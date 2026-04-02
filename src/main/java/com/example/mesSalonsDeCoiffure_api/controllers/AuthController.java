package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.dto.AuthRequestDTO;
import com.example.mesSalonsDeCoiffure_api.dto.AuthResponseDTO;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;
import com.example.mesSalonsDeCoiffure_api.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
 // Autorise Angular à faire des requêtes
public class AuthController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;

    /**
     * API 1 : REGISTER (Créer un compte)
     * URL : POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody Utilisateur utilisateur) {
        
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponseDTO(null, null, "Cet email est déjà utilisé."));
        }

        // Hacher le mot de passe pour la sécurité
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        
        // Par défaut, c'est un utilisateur normal (sauf si on précise ADMIN)
        if (utilisateur.getRole() == null || utilisateur.getRole().isEmpty()) {
            utilisateur.setRole("USER"); 
        }
        
        // Sauvegarder en base de données
        utilisateurRepository.save(utilisateur);
        
        return ResponseEntity.ok(new AuthResponseDTO(null, utilisateur.getRole(), "Compte créé avec succès !"));
    }

    /**
     * API 2 : LOGIN (Se connecter)
     * URL : POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        
        // Chercher l'utilisateur par son email
        Optional<Utilisateur> userOpt = utilisateurRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            
            // Comparer le mot de passe tapé avec celui haché en base de données
            if (passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
                
                // C'est le bon mot de passe ! On génère le badge VIP (Token JWT)
                String token = jwtService.generateToken(user.getEmail(), user.getRole());
                
                // On renvoie le token au Frontend
                return ResponseEntity.ok(new AuthResponseDTO(token, user.getRole(), "Connexion réussie"));
            }
        }
        
        // Si email ou mot de passe incorrect
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponseDTO(null, null, "Email ou mot de passe incorrect."));
    }
}