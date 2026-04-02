package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.dto.AuthRequestDTO;
import com.example.mesSalonsDeCoiffure_api.dto.AuthResponseDTO;
import com.example.mesSalonsDeCoiffure_api.dto.RegisterRequestDTO;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;
import com.example.mesSalonsDeCoiffure_api.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Injection par constructeur (Spring l'injecte automatiquement, plus besoin de @Autowired)
    public AuthController(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponseDTO(null, null, "Cet email est déjà utilisé."));
        }

        // On transfère les données du DTO vers une vraie Entité Utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole("USER"); 
        
        utilisateurRepository.save(utilisateur);
        
        return ResponseEntity.ok(new AuthResponseDTO(null, utilisateur.getRole(), "Compte créé avec succès !"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        Optional<Utilisateur> userOpt = utilisateurRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            if (passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
                String token = jwtService.generateToken(user.getEmail(), user.getRole());
                return ResponseEntity.ok(new AuthResponseDTO(token, user.getRole(), "Connexion réussie"));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponseDTO(null, null, "Email ou mot de passe incorrect."));
    }
}