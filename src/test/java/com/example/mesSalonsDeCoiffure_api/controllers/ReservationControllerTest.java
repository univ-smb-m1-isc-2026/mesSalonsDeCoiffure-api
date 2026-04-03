package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.dto.CreneauDisponibleDTO;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

        // ✅ Utilisation du type précis List<CreneauDisponibleDTO>
        when(reservationService.calculerCreneauxDisponibles(employeId, prestationId, dateChoisie))
                .thenReturn(new ArrayList<>()); 

        // 2. ACTION
        ResponseEntity<List<CreneauDisponibleDTO>> response = reservationController.getDisponibilites(prestationId, dateString, employeId);

        // 3. ASSERTION
        assertEquals(200, response.getStatusCode().value());
        verify(reservationService, times(1)).calculerCreneauxDisponibles(employeId, prestationId, dateChoisie);
    }

    @Test
    void quandReserver_alorsAppelleServiceEtRetourne201() { // 🌟 Renommé en 201
        // 1. ARRANGEMENT
        ReservationRequestDTO demande = new ReservationRequestDTO();
        RendezVous rdvCree = new RendezVous();
        rdvCree.setId(99L);

        when(authentication.getName()).thenReturn("client@test.com");
        
        when(reservationService.enregistrerRendezVous(any(ReservationRequestDTO.class), eq("client@test.com")))
            .thenReturn(rdvCree);

        // 2. ACTION
        ResponseEntity<RendezVous> response = reservationController.reserver(demande, authentication);

        // 3. ASSERTION
        // 🌟 CORRECTION : On attend maintenant 201 (Created)
        assertEquals(201, response.getStatusCode().value()); 
        
        assertNotNull(response.getBody());
        assertEquals(99L, response.getBody().getId());
        verify(reservationService, times(1)).enregistrerRendezVous(demande, "client@test.com");
    }

    @Test
    void quandDeplacer_alorsAppelleServiceEtRetourne200() {
        // 1. ARRANGEMENT
        Long rdvId = 4L;
        ReservationRequestDTO demande = new ReservationRequestDTO();
        RendezVous rdvModifie = new RendezVous();
        rdvModifie.setId(rdvId);

        when(authentication.getName()).thenReturn("client@test.com");
        when(reservationService.deplacerRendezVous(eq(rdvId), any(ReservationRequestDTO.class), eq("client@test.com")))
            .thenReturn(rdvModifie);

        // 2. ACTION
        ResponseEntity<RendezVous> response = reservationController.deplacerReservation(rdvId, demande, authentication);

        // 3. ASSERTION
        assertEquals(200, response.getStatusCode().value());
        assertEquals(rdvId, response.getBody().getId());
        verify(reservationService, times(1)).deplacerRendezVous(rdvId, demande, "client@test.com");
    }
}