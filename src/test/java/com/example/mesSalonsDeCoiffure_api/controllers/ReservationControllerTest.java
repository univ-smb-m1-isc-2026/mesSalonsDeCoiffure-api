package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.dto.ReservationRequestDTO;
import com.example.mesSalonsDeCoiffure_api.entities.RendezVous;
import com.example.mesSalonsDeCoiffure_api.services.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReservationController reservationController;

    @Test
    void quandGetDisponibilites_alorsAppelleServiceEtRetourne200() {
        // 1. ARRANGEMENT
        Long employeId = 1L;
        Long prestationId = 2L;
        String dateString = "2026-05-10";
        LocalDate dateChoisie = LocalDate.parse(dateString);

        when(reservationService.calculerCreneauxDisponibles(employeId, prestationId, dateChoisie))
                .thenReturn(new ArrayList<>()); // On simule une liste vide renvoyée par le service

        // 2. ACTION
        ResponseEntity<?> response = reservationController.getDisponibilites(prestationId, dateString, employeId);

        // 3. ASSERTION
        assertEquals(200, response.getStatusCode().value());
        verify(reservationService, times(1)).calculerCreneauxDisponibles(employeId, prestationId, dateChoisie);
    }

    @Test
    void quandReserver_alorsAppelleServiceEtRetourne200() {
        // 1. ARRANGEMENT
        ReservationRequestDTO demande = new ReservationRequestDTO();
        RendezVous rdvCree = new RendezVous();
        rdvCree.setId(99L);

        when(reservationService.enregistrerRendezVous(any(ReservationRequestDTO.class))).thenReturn(rdvCree);

        // 2. ACTION
        ResponseEntity<?> response = reservationController.reserver(demande, authentication);

        // 3. ASSERTION
        assertEquals(200, response.getStatusCode().value());
        verify(reservationService, times(1)).enregistrerRendezVous(demande);
    }
}