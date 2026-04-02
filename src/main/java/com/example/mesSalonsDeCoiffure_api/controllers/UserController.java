package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.Reservation;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.ReservationRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UtilisateurRepository utilisateurRepository;
    private final ReservationRepository reservationRepository;

    public UserController(UtilisateurRepository utilisateurRepository, ReservationRepository reservationRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/me")
    public Utilisateur getMonProfil(Authentication authentication) {
        String email = authentication.getName();
        return utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    @PutMapping("/me")
    public Utilisateur modifierMonProfil(@RequestBody Utilisateur modifications, Authentication authentication) {
        String email = authentication.getName();
        Utilisateur userActuel = utilisateurRepository.findByEmail(email).orElseThrow();

        userActuel.setNom(modifications.getNom());
        userActuel.setTelephone(modifications.getTelephone());
        userActuel.setRappelsReguliers(modifications.isRappelsReguliers());
        userActuel.setNotifsWhatsapp(modifications.isNotifsWhatsapp());

        return utilisateurRepository.save(userActuel);
    }

    @GetMapping("/me/reservations")
    public List<Reservation> getMesRendezVous(Authentication authentication) {
        String email = authentication.getName();
        Utilisateur client = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return reservationRepository.findByClientTelephoneOrderByDateHeureDebutDesc(client.getTelephone());
    }
}