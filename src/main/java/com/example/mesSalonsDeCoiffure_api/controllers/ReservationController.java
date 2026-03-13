package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.dto.CreneauDisponibleDTO;
import com.example.mesSalonsDeCoiffure_api.dto.ReservationRequestDTO;
import com.example.mesSalonsDeCoiffure_api.entities.RendezVous;
import com.example.mesSalonsDeCoiffure_api.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // 1. Chercher les disponibilités
    @GetMapping("/disponibles")
    public List<CreneauDisponibleDTO> getCreneauxDisponibles(
            @RequestParam Long employeId,
            @RequestParam Long prestationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        return reservationService.calculerCreneauxDisponibles(employeId, prestationId, date);
    }

    // 2. Valider et créer le rendez-vous !
    @PostMapping("/reserver")
    public RendezVous reserverCreneau(@RequestBody ReservationRequestDTO demande) {
        return reservationService.enregistrerRendezVous(demande);
    }
}