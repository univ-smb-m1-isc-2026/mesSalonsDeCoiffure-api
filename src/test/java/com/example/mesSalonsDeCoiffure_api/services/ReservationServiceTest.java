package com.example.mesSalonsDeCoiffure_api.services;

import com.example.mesSalonsDeCoiffure_api.dto.ReservationRequestDTO;
import com.example.mesSalonsDeCoiffure_api.entities.Employe;
import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.entities.RendezVous;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.EmployeRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.PrestationRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.RendezVousRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Active Mockito pour créer des faux Repositories
class ReservationServiceTest {

    @Mock
    private RendezVousRepository rendezVousRepository;
    @Mock
    private EmployeRepository employeRepository;
    @Mock
    private PrestationRepository prestationRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks // Injecte les faux Repositories dans le vrai Service
    private ReservationService reservationService;

 @Test
    void quandEnregistrerRendezVous_alorsSauvegardeEnBase() {
        // 1. ARRANGEMENT
        String emailClient = "client@test.com";
        
        ReservationRequestDTO demande = new ReservationRequestDTO();
        demande.setEmployeId(1L);
        demande.setPrestationId(2L);
        // On utilise les nouveaux champs séparés
        demande.setDate(java.time.LocalDate.of(2026, 5, 10));
        demande.setHeureDebut(java.time.LocalTime.of(10, 0));

        Utilisateur client = new Utilisateur(); client.setId(10L); client.setEmail(emailClient);
        Employe employe = new Employe(); employe.setId(1L);
        Prestation prestation = new Prestation(); prestation.setId(2L); prestation.setDureeMinutes(30);

        // On cherche par email maintenant !
        when(utilisateurRepository.findByEmail(emailClient)).thenReturn(java.util.Optional.of(client));
        when(employeRepository.findById(1L)).thenReturn(java.util.Optional.of(employe));
        when(prestationRepository.findById(2L)).thenReturn(java.util.Optional.of(prestation));
        when(rendezVousRepository.save(any(RendezVous.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACTION (On passe les deux paramètres)
        RendezVous resultat = reservationService.enregistrerRendezVous(demande, emailClient);

        // 3. ASSERTION
        assertNotNull(resultat);
        assertEquals(client, resultat.getClient());
        assertEquals(java.time.LocalDateTime.of(2026, 5, 10, 10, 0), resultat.getDateHeureDebut());
        assertEquals(java.time.LocalDateTime.of(2026, 5, 10, 10, 30), resultat.getDateHeureFin());
        assertEquals("CONFIRME", resultat.getStatut());
    }

@Test
    void quandCalculerCreneauxDisponibles_alorsRetourneListeCreneauxLibres() {
        // 1. ARRANGEMENT
        Long employeId = 1L;
        Long prestationId = 1L;
        java.time.LocalDate dateTest = java.time.LocalDate.of(2026, 5, 10);

        Employe employe = new Employe();
        employe.setId(employeId);
        employe.setNom("Jean le Coiffeur");

        Prestation prestation = new Prestation();
        prestation.setDureeMinutes(30);

        // 🌟 L'ASTUCE EST ICI : On utilise Mockito pour forcer le RendezVous ! 🌟
        RendezVous rdvExistant = mock(RendezVous.class);
        when(rdvExistant.getDateHeureDebut()).thenReturn(LocalDateTime.of(2026, 5, 10, 10, 0));
        when(rdvExistant.getDateHeureFin()).thenReturn(LocalDateTime.of(2026, 5, 10, 10, 30));

        when(employeRepository.findById(employeId)).thenReturn(Optional.of(employe));
        when(prestationRepository.findById(prestationId)).thenReturn(Optional.of(prestation));
        when(rendezVousRepository.findRendezVousByEmployeAndDate(eq(employeId), any(), any()))
                .thenReturn(java.util.List.of(rdvExistant));

        // 2. ACTION
        java.util.List<com.example.mesSalonsDeCoiffure_api.dto.CreneauDisponibleDTO> creneaux = 
                reservationService.calculerCreneauxDisponibles(employeId, prestationId, dateTest);

        // 3. ASSERTION
        assertFalse(creneaux.isEmpty(), "La liste des créneaux ne doit pas être vide");
        assertEquals(LocalDateTime.of(2026, 5, 10, 9, 0), creneaux.get(0).getHeureDebut());
        
        boolean creneau10hPresent = creneaux.stream()
                .anyMatch(c -> c.getHeureDebut().equals(LocalDateTime.of(2026, 5, 10, 10, 0)));
        assertFalse(creneau10hPresent, "Le créneau de 10h00 devrait être masqué car déjà réservé !");
    }
}