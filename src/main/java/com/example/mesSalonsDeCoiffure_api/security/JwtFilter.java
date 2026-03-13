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
        
        // 1. On cherche le header "Authorization"
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // Pas de token ? On passe au filtre suivant (qui bloquera si la route est protégée)
        }

        // 2. On extrait le token (en enlevant les 7 premiers caractères "Bearer ")
        final String jwt = authHeader.substring(7);
        final String email = jwtService.extractEmail(jwt);

        // 3. Si on a un email et que l'utilisateur n'est pas encore identifié dans le contexte actuel
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // On vérifie que le token est bien valide
            if (jwtService.validateToken(jwt)) {
                
                Optional<Utilisateur> userOpt = utilisateurRepository.findByEmail(email);
                
                if (userOpt.isPresent()) {
                    Utilisateur user = userOpt.get();
                    
                    // On dit officiellement à Spring Security : "Cet utilisateur est connecté et voici son rôle"
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, 
                            null, 
                            Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
                    );
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        
        // On continue la chaîne des filtres
        filterChain.doFilter(request, response);
    }
}