package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.dto.CreneauDisponibleDTO;
import com.example.mesSalonsDeCoiffure_api.dto.ReservationRequestDTO;
import com.example.mesSalonsDeCoiffure_api.entities.RendezVous;
import com.example.mesSalonsDeCoiffure_api.services.ReservationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // ✅ Correction : Remplacement de <?> par <List<CreneauDisponibleDTO>>
    @GetMapping("/disponibles")
    public ResponseEntity<List<CreneauDisponibleDTO>> getDisponibilites(
            @RequestParam Long prestationId,
            @RequestParam String date,
            @RequestParam(required = false) Long employeId) {

        LocalDate dateChoisie = LocalDate.parse(date);
        List<CreneauDisponibleDTO> creneaux = reservationService.calculerCreneauxDisponibles(employeId, prestationId, dateChoisie);
        
        return ResponseEntity.ok(creneaux);
    }

    @PostMapping("/reserver")
    public ResponseEntity<RendezVous> reserver(@RequestBody ReservationRequestDTO demande, Authentication auth) {
        RendezVous rdv = reservationService.enregistrerRendezVous(demande, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(rdv);
    }

    // ✅ Correction : Remplacement de <?> par <RendezVous>
    @PutMapping("/{id}/deplacer")
    public ResponseEntity<RendezVous> deplacerReservation(
            @PathVariable Long id, 
            @RequestBody ReservationRequestDTO demande, 
            Authentication authentication) {
        
        String emailClient = authentication.getName();
        RendezVous rdvMisAJour = reservationService.deplacerRendezVous(id, demande, emailClient);
        
        return ResponseEntity.ok(rdvMisAJour);
    }
}