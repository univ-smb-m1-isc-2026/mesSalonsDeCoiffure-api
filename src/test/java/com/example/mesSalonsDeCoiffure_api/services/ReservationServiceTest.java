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
    void quandEnregistrerRendezVous_alorsSauvegardeReussie() {
        // 1. ARRANGEMENT (On prépare les fausses données)
        ReservationRequestDTO demande = new ReservationRequestDTO();
        demande.setUtilisateurId(1L);
        demande.setEmployeId(2L);
        demande.setPrestationId(3L);
        demande.setDateHeureDebut(LocalDateTime.of(2026, 5, 10, 14, 0));

        Utilisateur fauxClient = new Utilisateur(); fauxClient.setId(1L);
        Employe fauxEmploye = new Employe(); fauxEmploye.setId(2L);
        Prestation faussePrestation = new Prestation(); faussePrestation.setDureeMinutes(30);

        // On dicte à Mockito ce qu'il doit répondre
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(fauxClient));
        when(employeRepository.findById(2L)).thenReturn(Optional.of(fauxEmploye));
        when(prestationRepository.findById(3L)).thenReturn(Optional.of(faussePrestation));
        
        // Quand on sauvegarde, on renvoie simplement l'objet sauvegardé
        when(rendezVousRepository.save(any(RendezVous.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACTION (On lance la méthode)
        RendezVous resultat = reservationService.enregistrerRendezVous(demande);

        // 3. ASSERTION (On vérifie que la logique a bien marché)
        assertNotNull(resultat);
        assertEquals("CONFIRME", resultat.getStatut());
        assertEquals(LocalDateTime.of(2026, 5, 10, 14, 30), resultat.getDateHeureFin()); // 14h00 + 30min
        
        // On vérifie que la méthode save() a bien été appelée exactement 1 fois
        verify(rendezVousRepository, times(1)).save(any(RendezVous.class));
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