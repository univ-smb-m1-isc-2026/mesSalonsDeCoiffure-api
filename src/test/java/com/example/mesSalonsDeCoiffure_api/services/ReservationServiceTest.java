package com.example.mesSalonsDeCoiffure_api.services;

import com.example.mesSalonsDeCoiffure_api.dto.CreneauDisponibleDTO;
import com.example.mesSalonsDeCoiffure_api.dto.ReservationRequestDTO;
import com.example.mesSalonsDeCoiffure_api.entities.*;
import com.example.mesSalonsDeCoiffure_api.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private RendezVousRepository rendezVousRepository;
    @Mock
    private EmployeRepository employeRepository;
    @Mock
    private PrestationRepository prestationRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private CreneauRepository creneauRepository; // 🌟 AJOUT DU MOCK MANQUANT

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void quandEnregistrerRendezVous_alorsSauvegardeEnBase() {
        // 1. ARRANGEMENT
        String emailClient = "client@test.com";
        ReservationRequestDTO demande = new ReservationRequestDTO();
        demande.setEmployeId(1L);
        demande.setPrestationId(2L);
        demande.setDate(LocalDate.of(2026, 5, 10));
        demande.setHeureDebut(LocalTime.of(10, 0));

        Utilisateur client = new Utilisateur(); client.setEmail(emailClient);
        Employe employe = new Employe(); employe.setId(1L);
        Prestation prestation = new Prestation(); 
        prestation.setId(2L); 
        prestation.setDureeMinutes(30);
        prestation.setSalon(new Salon()); // Pour éviter un NullPointerException si le service appelle getSalon()

        when(utilisateurRepository.findByEmail(emailClient)).thenReturn(Optional.of(client));
        when(employeRepository.findById(1L)).thenReturn(Optional.of(employe));
        when(prestationRepository.findById(2L)).thenReturn(Optional.of(prestation));
        when(rendezVousRepository.save(any(RendezVous.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACTION
        RendezVous resultat = reservationService.enregistrerRendezVous(demande, emailClient);

        // 3. ASSERTION
        assertNotNull(resultat);
        assertEquals(LocalDateTime.of(2026, 5, 10, 10, 0), resultat.getDateHeureDebut());
        assertEquals("CONFIRME", resultat.getStatut());
        verify(rendezVousRepository, times(1)).save(any(RendezVous.class));
    }

   @Test
    void quandCalculerCreneauxDisponibles_alorsRespectePlanningEtRendezVous() {
        // 1. ARRANGEMENT
        Long employeId = 1L;
        Long prestationId = 1L;
        LocalDate dateTest = LocalDate.of(2026, 5, 10); // Dimanche
        String jourAttendu = "DIMANCHE"; 

        // Objet Employe indispensable pour le .get() dans le service
        Employe employe = new Employe();
        employe.setId(employeId);
        employe.setNom("Jean le Coiffeur");

        Prestation prestation = new Prestation();
        prestation.setDureeMinutes(30);
        Salon salon = new Salon();
        salon.setId(1L);
        prestation.setSalon(salon);

        Creneau planning = new Creneau();
        planning.setHeureDebut(LocalTime.of(9, 0));
        planning.setHeureFin(LocalTime.of(11, 0));
        planning.setJourSemaine(jourAttendu);

        RendezVous rdvExistant = new RendezVous();
        rdvExistant.setDateHeureDebut(LocalDateTime.of(2026, 5, 10, 10, 0));
        rdvExistant.setDateHeureFin(LocalDateTime.of(2026, 5, 10, 10, 30));

        // Mocks
        when(prestationRepository.findById(prestationId)).thenReturn(Optional.of(prestation));
        
        // 🌟 LA CORRECTION EST ICI : On mocke aussi le findById de l'employé
        when(employeRepository.findById(employeId)).thenReturn(Optional.of(employe));
        
        when(creneauRepository.findByEmployeIdAndJourSemaine(employeId, jourAttendu))
                .thenReturn(List.of(planning));
                
        when(rendezVousRepository.findRendezVousByEmployeAndDate(eq(employeId), any(), any()))
                .thenReturn(List.of(rdvExistant));

        // 2. ACTION
        List<CreneauDisponibleDTO> creneaux = reservationService.calculerCreneauxDisponibles(employeId, prestationId, dateTest);

        // 3. ASSERTION
        assertNotNull(creneaux);
        assertFalse(creneaux.isEmpty());
        
        // Vérifions que le créneau de 09:00 est bien présent
        assertTrue(creneaux.stream().anyMatch(c -> c.getHeureDebut().toLocalTime().equals(LocalTime.of(9, 0))));
        
        // Vérifions que le créneau de 10:00 est ABSENT
        boolean dixHeuresPresent = creneaux.stream()
                .anyMatch(c -> c.getHeureDebut().toLocalTime().equals(LocalTime.of(10, 0)));
        assertFalse(dixHeuresPresent, "Le créneau de 10:00 devrait être indisponible");
    }
}