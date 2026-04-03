package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.dto.ReservationRequestDTO;
import com.example.mesSalonsDeCoiffure_api.services.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    // 🌟 On n'injecte QUE le Service ! Plus aucun Repository.
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/disponibles")
    public ResponseEntity<?> getDisponibilites(
            @RequestParam Long prestationId,
            @RequestParam String date,
            @RequestParam(required = false) Long employeId) {

        LocalDate dateChoisie = LocalDate.parse(date);
        
        // Le Service fait tout le travail compliqué (boucles, accents, base de données...)
        return ResponseEntity.ok(reservationService.calculerCreneauxDisponibles(employeId, prestationId, dateChoisie));
    }

    @PostMapping("/reserver")
    public ResponseEntity<?> reserver(@RequestBody ReservationRequestDTO demande, Authentication authentication) {
        
        // 1. On extrait l'email de la personne connectée grâce au Token JWT
        String emailClient = authentication.getName(); 
        
        // 2. On passe la demande ET l'email au service !
        return ResponseEntity.ok(reservationService.enregistrerRendezVous(demande, emailClient));
    }

    // 🌟 L'AJOUT EST ICI : La route pour attraper ton this.http.put() d'Angular 🌟
    @PutMapping("/{id}/deplacer")
    public ResponseEntity<?> deplacerReservation(
            @PathVariable Long id, 
            @RequestBody ReservationRequestDTO demande, 
            Authentication authentication) {
        
        // Sécurité : On récupère l'email pour s'assurer que seul le vrai client modifie son RDV
        String emailClient = authentication.getName();
        
        return ResponseEntity.ok(reservationService.deplacerRendezVous(id, demande, emailClient));
    }
}