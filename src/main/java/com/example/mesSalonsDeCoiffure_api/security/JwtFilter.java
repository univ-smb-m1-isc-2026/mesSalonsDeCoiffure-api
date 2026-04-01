package com.example.mesSalonsDeCoiffure_api.security;

import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        System.out.println("\n=== 🔍 NOUVELLE REQUETE : " + request.getMethod() + " " + request.getRequestURI() + " ===");
        
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ ÉTAPE 1 : Aucun token Bearer trouvé dans la requête.");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("✅ ÉTAPE 1 : Token Bearer trouvé !");
        final String jwt = authHeader.substring(7);
        
        try {
            String email = jwtService.extractEmail(jwt);
            System.out.println("✅ ÉTAPE 2 : Email extrait du token -> " + email);
            
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.validateToken(jwt)) {
                    System.out.println("✅ ÉTAPE 3 : Token cryptographiquement valide.");
                    
                    Optional<Utilisateur> userOpt = utilisateurRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        Utilisateur user = userOpt.get();
                        
                        String role = user.getRole();
                        if (role == null) role = "USER"; // Sécurité anti-crash
                        role = role.toUpperCase();
                        
                        if (!role.startsWith("ROLE_")) {
                            role = "ROLE_" + role;
                        }
                        System.out.println("✅ ÉTAPE 4 : Utilisateur trouvé en BDD. Rôle final attribué -> " + role);
                        
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                user.getEmail(), null, Collections.singletonList(new SimpleGrantedAuthority(role))
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("🔓 ÉTAPE 5 : Accès autorisé par le filtre, passage à la vérification de la route...");
                    } else {
                        System.out.println("❌ ÉTAPE 4 : L'utilisateur n'existe pas dans PostgreSQL !");
                    }
                } else {
                    System.out.println("❌ ÉTAPE 3 : Le token est invalide (ancienne clé ou expiré).");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ ERREUR CRITIQUE DANS LE FILTRE : " + e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}