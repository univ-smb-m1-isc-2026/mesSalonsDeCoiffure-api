package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.dto.SalonDTO;
import com.example.mesSalonsDeCoiffure_api.entities.Creneau;
import com.example.mesSalonsDeCoiffure_api.entities.Employe;
import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.entities.Salon;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // On utilise Mockito pur (ultra rapide, pas de Spring Boot à charger)
class AdminControllerTest {

    @Mock private SalonRepository salonRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private EmployeRepository employeRepository;
    @Mock private PrestationRepository prestationRepository;
    @Mock private CreneauRepository creneauRepository;
    
    // On simule l'objet Authentication (le token JWT)
    @Mock private Authentication authentication;

    @InjectMocks
    private AdminController adminController;

    @Test
    void quandGetMesSalons_alorsRetourneListeSalons() {
        // 1. ARRANGEMENT
        Utilisateur gerant = new Utilisateur(); 
        gerant.setId(1L);
        
        Salon salon = new Salon(); 
        salon.setNom("Mon Super Salon");
        
        // On simule un gérant connecté
        when(authentication.getName()).thenReturn("gerant@test.com");
        when(utilisateurRepository.findByEmail("gerant@test.com")).thenReturn(Optional.of(gerant));
        when(salonRepository.findByGerantId(1L)).thenReturn(List.of(salon));

        // 2. ACTION
        List<Salon> resultats = adminController.getMesSalons(authentication);

        // 3. ASSERTION
        assertEquals(1, resultats.size());
        assertEquals("Mon Super Salon", resultats.get(0).getNom());
    }

    @Test
    void quandCreerMonSalon_alorsSauvegardeSalon() {
        // 1. ARRANGEMENT
        SalonDTO dto = new SalonDTO();
        dto.setNom("Nouveau Salon");
        
        Utilisateur gerant = new Utilisateur(); 
        gerant.setId(1L);
        
        when(authentication.getName()).thenReturn("gerant@test.com");
        when(utilisateurRepository.findByEmail("gerant@test.com")).thenReturn(Optional.of(gerant));
        when(salonRepository.save(any(Salon.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACTION
        Salon resultat = adminController.creerMonSalon(dto, authentication);

        // 3. ASSERTION
        assertNotNull(resultat);
        assertEquals("Nouveau Salon", resultat.getNom());
        assertEquals(gerant, resultat.getGerant()); // On vérifie que le salon est bien attribué au gérant
    }
    
    @Test
    void quandSupprimerMonSalon_alorsSupprimeEtRetourne200() {
        // 1. ARRANGEMENT
        Utilisateur gerant = new Utilisateur(); 
        gerant.setId(1L);
        
        Salon salon = new Salon(); 
        salon.setId(10L); 
        salon.setGerant(gerant); // Le salon appartient bien à ce gérant
        
        when(authentication.getName()).thenReturn("gerant@test.com");
        when(utilisateurRepository.findByEmail("gerant@test.com")).thenReturn(Optional.of(gerant));
        when(salonRepository.findById(10L)).thenReturn(Optional.of(salon));

        // 2. ACTION
        ResponseEntity<String> response = adminController.supprimerMonSalon(10L, authentication);

        // 3. ASSERTION
        assertEquals(200, response.getStatusCode().value());
        // On vérifie que la méthode de suppression de la BDD a bien été appelée 1 fois
        verify(salonRepository, times(1)).delete(salon); 
    }

    @Test
    void quandModifierSalonNonAutorise_alorsLanceException() {
        // 1. ARRANGEMENT : Un pirate (ID 99) essaie de modifier le salon du gérant (ID 1)
        Utilisateur pirate = new Utilisateur(); pirate.setId(99L);
        Utilisateur vraiGerant = new Utilisateur(); vraiGerant.setId(1L);
        
        Salon salon = new Salon(); 
        salon.setId(10L);
        salon.setGerant(vraiGerant); // Le salon appartient au vrai gérant

        when(authentication.getName()).thenReturn("pirate@test.com");
        when(utilisateurRepository.findByEmail("pirate@test.com")).thenReturn(Optional.of(pirate));
        when(salonRepository.findById(10L)).thenReturn(Optional.of(salon));

        // 2 & 3. ACTION & ASSERTION : On vérifie que la sécurité bloque bien avec une Exception
        assertThrows(IllegalArgumentException.class, 
            () -> adminController.modifierMonSalon(10L, new SalonDTO(), authentication));
    }

    @Test
    void quandSupprimerSalonNonAutorise_alorsRetourneErreur403() {
        // 1. ARRANGEMENT : Même scénario, mais pour la suppression
        Utilisateur pirate = new Utilisateur(); pirate.setId(99L);
        Utilisateur vraiGerant = new Utilisateur(); vraiGerant.setId(1L);
        
        Salon salon = new Salon(); 
        salon.setId(10L);
        salon.setGerant(vraiGerant);

        when(authentication.getName()).thenReturn("pirate@test.com");
        when(utilisateurRepository.findByEmail("pirate@test.com")).thenReturn(Optional.of(pirate));
        when(salonRepository.findById(10L)).thenReturn(Optional.of(salon));

        // 2. ACTION
        ResponseEntity<String> response = adminController.supprimerMonSalon(10L, authentication);

        // 3. ASSERTION : On vérifie le code HTTP 403 Forbidden
        assertEquals(403, response.getStatusCode().value());
        // On vérifie que la méthode DELETE de la base de données n'a JAMAIS été appelée !
        verify(salonRepository, never()).delete(any()); 
    }

    @Test
    void quandModifierSalonIntrouvable_alorsLanceException() {
        // 1. ARRANGEMENT : Le gérant essaie de modifier un salon qui n'existe pas (ID 999)
        Utilisateur gerant = new Utilisateur(); gerant.setId(1L);
        
        when(authentication.getName()).thenReturn("gerant@test.com");
        when(utilisateurRepository.findByEmail("gerant@test.com")).thenReturn(Optional.of(gerant));
        when(salonRepository.findById(999L)).thenReturn(Optional.empty()); // BDD vide

        // 2 & 3. ACTION & ASSERTION : Le .orElseThrow doit s'activer
        assertThrows(IllegalArgumentException.class, 
            () -> adminController.modifierMonSalon(999L, new SalonDTO(), authentication));
    }

    @Test
    void quandAssignerPrestation_alorsTestLaBrancheIf() {
        // 1. ARRANGEMENT : Test du si
        Employe employe = new Employe();
        employe.setPrestations(new java.util.ArrayList<>()); // Liste vide au départ
        Prestation prestation = new Prestation();

        when(employeRepository.findById(1L)).thenReturn(Optional.of(employe));
        when(prestationRepository.findById(2L)).thenReturn(Optional.of(prestation));
        when(employeRepository.save(employe)).thenReturn(employe);

        // 2. ACTION
        Employe resultat = adminController.assignerPrestation(1L, 2L);

        // 3. ASSERTION
        assertTrue(resultat.getPrestations().contains(prestation));
        verify(employeRepository, times(1)).save(employe);
    }


    @Test
    void quandModifierSalonAutorise_alorsSauvegardeEtRetourneOk() {
        // 1. ARRANGEMENT
        Utilisateur vraiGerant = new Utilisateur(); vraiGerant.setId(1L);
        
        Salon salonExistant = new Salon(); 
        salonExistant.setId(10L);
        salonExistant.setGerant(vraiGerant);
        salonExistant.setNom("Ancien Nom");

        SalonDTO modifications = new SalonDTO();
        modifications.setNom("Nouveau Nom Super");

        when(authentication.getName()).thenReturn("gerant@test.com");
        when(utilisateurRepository.findByEmail("gerant@test.com")).thenReturn(Optional.of(vraiGerant));
        when(salonRepository.findById(10L)).thenReturn(Optional.of(salonExistant));
        
        // 🌟 LA LIGNE MAGIQUE QU'ON AVAIT OUBLIÉE 🌟
        // On dit à Mockito : "Si on te demande de sauvegarder un salon, renvoie ce même salon !"
        when(salonRepository.save(any(Salon.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACTION
        Salon resultat = adminController.modifierMonSalon(10L, modifications, authentication);

        // 3. ASSERTION
        assertNotNull(resultat, "Le résultat ne devrait pas être null !");
        assertEquals("Nouveau Nom Super", salonExistant.getNom()); 
        verify(salonRepository, times(1)).save(salonExistant);
    }

    @Test
    void quandAddEmploye_alorsSauvegardeEtRetourneEmploye() {
        // 1. ARRANGEMENT
        Salon salon = new Salon(); salon.setId(10L);
        
        com.example.mesSalonsDeCoiffure_api.dto.EmployeDTO employeDTO = new com.example.mesSalonsDeCoiffure_api.dto.EmployeDTO();
        employeDTO.setNom("Dupont"); // Pas de prénom car ton contrôleur ne l'utilise pas

        when(salonRepository.findById(10L)).thenReturn(Optional.of(salon));
        when(employeRepository.save(any(Employe.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACTION (Correspond exactement à ta méthode : pas de paramètre Authentication !)
        Employe resultat = adminController.addEmploye(10L, employeDTO);

        // 3. ASSERTION
        assertNotNull(resultat);
        assertEquals("Dupont", resultat.getNom());
    }

    @Test
    void quandAddPrestation_alorsSauvegardeEtRetournePrestation() {
        // 1. ARRANGEMENT
        Salon salon = new Salon(); salon.setId(10L);
        
        com.example.mesSalonsDeCoiffure_api.dto.PrestationDTO prestaDTO = new com.example.mesSalonsDeCoiffure_api.dto.PrestationDTO();
        prestaDTO.setNom("Coupe Homme");
        prestaDTO.setPrix(20.0);

        when(salonRepository.findById(10L)).thenReturn(Optional.of(salon));
        when(prestationRepository.save(any(Prestation.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACTION
        Prestation resultat = adminController.addPrestation(10L, prestaDTO);

        // 3. ASSERTION
        assertNotNull(resultat);
        assertEquals("Coupe Homme", resultat.getNom());
    }

    @Test
    void quandGetEmployes_alorsRetourneListe() {
        when(employeRepository.findBySalonId(10L)).thenReturn(List.of(new Employe()));
        List<Employe> resultats = adminController.getEmployes(10L);
        assertEquals(1, resultats.size());
    }

    @Test
    void quandUpdateEmploye_alorsModifieEtSauvegarde() {
        Employe employe = new Employe(); employe.setId(1L);
        com.example.mesSalonsDeCoiffure_api.dto.EmployeDTO dto = new com.example.mesSalonsDeCoiffure_api.dto.EmployeDTO();
        dto.setNom("Nouveau Nom");

        when(employeRepository.findById(1L)).thenReturn(Optional.of(employe));
        when(employeRepository.save(any(Employe.class))).thenAnswer(i -> i.getArguments()[0]);

        Employe resultat = adminController.updateEmploye(1L, dto);
        assertEquals("Nouveau Nom", resultat.getNom());
    }

    @Test
    void quandDeleteEmploye_alorsRetourne200() {
        ResponseEntity<Void> response = adminController.deleteEmploye(1L);
        assertEquals(200, response.getStatusCode().value());
        verify(employeRepository, times(1)).deleteById(1L);
    }

    @Test
    void quandUpdatePrestation_alorsModifieEtSauvegarde() {
        Prestation presta = new Prestation(); presta.setId(1L);
        com.example.mesSalonsDeCoiffure_api.dto.PrestationDTO dto = new com.example.mesSalonsDeCoiffure_api.dto.PrestationDTO();
        dto.setNom("Shampoing");

        when(prestationRepository.findById(1L)).thenReturn(Optional.of(presta));
        when(prestationRepository.save(any(Prestation.class))).thenAnswer(i -> i.getArguments()[0]);

        Prestation resultat = adminController.updatePrestation(1L, dto);
        assertEquals("Shampoing", resultat.getNom());
    }

    @Test
    void quandDeletePrestation_alorsRetourne200() {
        ResponseEntity<Void> response = adminController.deletePrestation(1L);
        assertEquals(200, response.getStatusCode().value());
        verify(prestationRepository, times(1)).deleteById(1L);
    }

    @Test
    void quandRetirerPrestation_alorsSupprimeDeLaListe() {
        Employe employe = new Employe(); 
        Prestation presta = new Prestation(); presta.setId(99L);
        employe.getPrestations().add(presta); // On lui donne la prestation

        when(employeRepository.findById(1L)).thenReturn(Optional.of(employe));
        when(prestationRepository.findById(99L)).thenReturn(Optional.of(presta));
        when(employeRepository.save(employe)).thenReturn(employe);

        Employe resultat = adminController.retirerPrestation(1L, 99L);
        assertTrue(resultat.getPrestations().isEmpty()); // La liste doit être vide maintenant
    }

    @Test
    void quandAddCreneau_alorsSauvegardeEtRetourneCreneau() {
        Employe employe = new Employe(); employe.setId(1L);
        com.example.mesSalonsDeCoiffure_api.dto.CreneauDTO dto = new com.example.mesSalonsDeCoiffure_api.dto.CreneauDTO();
        dto.setJourSemaine("LUNDI");

        when(employeRepository.findById(1L)).thenReturn(Optional.of(employe));
        when(creneauRepository.save(any(Creneau.class))).thenAnswer(i -> i.getArguments()[0]);

        Creneau resultat = adminController.addCreneau(1L, dto);
        assertEquals("DISPONIBLE", resultat.getStatut());
        assertEquals("LUNDI", resultat.getJourSemaine());
    }

    @Test
    void quandDeleteCreneau_alorsRetourne200() {
        ResponseEntity<Void> response = adminController.deleteCreneau(1L);
        assertEquals(200, response.getStatusCode().value());
        verify(creneauRepository, times(1)).deleteById(1L);
    }
}