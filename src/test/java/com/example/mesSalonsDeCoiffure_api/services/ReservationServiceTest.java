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
}