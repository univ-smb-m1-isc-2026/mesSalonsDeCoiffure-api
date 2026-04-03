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
        // Idéalement, on passe l'email du client (authentication.getName()) au service
        // pour qu'il soit sûr de l'identité de la personne qui réserve !
        
        return ResponseEntity.ok(reservationService.enregistrerRendezVous(demande));
    }
}