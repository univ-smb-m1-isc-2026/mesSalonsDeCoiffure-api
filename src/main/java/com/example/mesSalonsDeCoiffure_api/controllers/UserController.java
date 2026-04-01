package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.ReservationRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.ReservationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.mesSalonsDeCoiffure_api.entities.Reservation;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    // 1. Lire son propre profil
    @GetMapping("/me")
    public Utilisateur getMonProfil(Authentication authentication) {
        String email = authentication.getName(); // Extrait l'email du Token JWT
        return utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    // 2. Modifier son propre profil
    @PutMapping("/me")
    public Utilisateur modifierMonProfil(@RequestBody Utilisateur modifications, Authentication authentication) {
        String email = authentication.getName();
        Utilisateur userActuel = utilisateurRepository.findByEmail(email).orElseThrow();

        // On ne met à jour QUE les champs autorisés (On ne change pas l'email ou le mot de passe ici !)
        userActuel.setNom(modifications.getNom());
        userActuel.setTelephone(modifications.getTelephone());
        userActuel.setRappelsReguliers(modifications.isRappelsReguliers());
        userActuel.setNotifsWhatsapp(modifications.isNotifsWhatsapp());

        return utilisateurRepository.save(userActuel);
    }

    @GetMapping("/me/reservations")
    public List<Reservation> getMesRendezVous(Authentication authentication) {
        // 1. On identifie qui est connecté
        String email = authentication.getName();
        Utilisateur client = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 2. On utilise la méthode de recherche par téléphone qu'on a corrigée précédemment !
        return reservationRepository.findByClientTelephoneOrderByDateHeureDebutDesc(client.getTelephone());
    }
}