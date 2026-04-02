package com.example.mesSalonsDeCoiffure_api.security;

import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    // 1. Remplacement de System.out par un Logger
    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    // 2. Remplacement de @Autowired par l'injection par constructeur (champs final)
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    public JwtFilter(JwtService jwtService, UtilisateurRepository utilisateurRepository) {
        this.jwtService = jwtService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("=== 🔍 NOUVELLE REQUETE : {} {} ===", request.getMethod(), request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");

        // Guard Clause : Si pas de token, on passe au filtre suivant et on s'arrête là
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("❌ ÉTAPE 1 : Aucun token Bearer trouvé dans la requête.");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("✅ ÉTAPE 1 : Token Bearer trouvé !");
        final String jwt = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(jwt);
            log.info("✅ ÉTAPE 2 : Email extrait du token -> {}", email);

            // Guard Clause : Si l'email est nul ou si l'utilisateur est déjà authentifié
            if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Guard Clause : Si le token est invalide
            if (!jwtService.validateToken(jwt)) {
                log.warn("❌ ÉTAPE 3 : Le token est invalide (ancienne clé ou expiré).");
                filterChain.doFilter(request, response);
                return;
            }
            log.info("✅ ÉTAPE 3 : Token cryptographiquement valide.");

            Optional<Utilisateur> userOpt = utilisateurRepository.findByEmail(email);
            
            // Guard Clause : Si l'utilisateur n'existe plus en BDD
            if (userOpt.isEmpty()) {
                log.warn("❌ ÉTAPE 4 : L'utilisateur n'existe pas dans PostgreSQL !");
                filterChain.doFilter(request, response);
                return;
            }

            // Arrivé ici, tout est valide !
            Utilisateur user = userOpt.get();
            String role = user.getRole();
            if (role == null) role = "USER"; // Sécurité anti-crash
            role = role.toUpperCase();

            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            
            log.info("✅ ÉTAPE 4 : Utilisateur trouvé en BDD. Rôle final attribué -> {}", role);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user.getEmail(), null, Collections.singletonList(new SimpleGrantedAuthority(role))
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("🔓 ÉTAPE 5 : Accès autorisé par le filtre, passage à la vérification de la route...");

        } catch (Exception e) {
            // Utilisation du logger pour capturer proprement la trace de l'erreur
            log.error("❌ ERREUR CRITIQUE DANS LE FILTRE : {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}