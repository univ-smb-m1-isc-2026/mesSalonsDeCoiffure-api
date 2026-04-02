package com.example.mesSalonsDeCoiffure_api.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// Imports de tes Entités
import com.example.mesSalonsDeCoiffure_api.entities.Creneau;
import com.example.mesSalonsDeCoiffure_api.entities.Employe;
import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.entities.Salon;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;

// Imports de tes DTOs (Sécurité)
import com.example.mesSalonsDeCoiffure_api.dto.CreneauDTO;
import com.example.mesSalonsDeCoiffure_api.dto.EmployeDTO;
import com.example.mesSalonsDeCoiffure_api.dto.PrestationDTO;
import com.example.mesSalonsDeCoiffure_api.dto.SalonDTO;

// Imports de tes Repositories
import com.example.mesSalonsDeCoiffure_api.repositories.CreneauRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.EmployeRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.PrestationRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.SalonRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;

@RestController
@RequestMapping("/api/admin/salons")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    // Injection propre par constructeur (Adieu @Autowired !)
    private final SalonRepository salonRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EmployeRepository employeRepository;
    private final PrestationRepository prestationRepository;
    private final CreneauRepository creneauRepository;

    public AdminController(SalonRepository salonRepository, 
                           UtilisateurRepository utilisateurRepository,
                           EmployeRepository employeRepository, 
                           PrestationRepository prestationRepository, 
                           CreneauRepository creneauRepository) {
        this.salonRepository = salonRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.employeRepository = employeRepository;
        this.prestationRepository = prestationRepository;
        this.creneauRepository = creneauRepository;
    }

    // 1. Récupérer UNIQUEMENT les salons du gérant connecté
    @GetMapping
    public List<Salon> getMesSalons(Authentication authentication) {
        String emailGerant = authentication.getName(); 
        Utilisateur gerant = utilisateurRepository.findByEmail(emailGerant)
            .orElseThrow(() -> new IllegalArgumentException("Impossible de trouver le gérant"));
        
        return salonRepository.findByGerantId(gerant.getId());
    }

    // 2. Créer un salon (Sécurisé avec SalonDTO)
    @PostMapping
    public Salon creerMonSalon(@RequestBody SalonDTO salonDTO, Authentication authentication) {
        String emailGerant = authentication.getName();
        
        // Formatage propre du log (plus de concaténation)
        log.info("➡️ Tentative de création de salon par : {}", emailGerant);

        Utilisateur gerant = utilisateurRepository.findByEmail(emailGerant)
            .orElseThrow(() -> new IllegalArgumentException("Impossible de trouver le gérant"));
        
        Salon nouveauSalon = new Salon();
        nouveauSalon.setNom(salonDTO.getNom());
        nouveauSalon.setAdresse(salonDTO.getAdresse());
        nouveauSalon.setLatitude(salonDTO.getLatitude());
        nouveauSalon.setLongitude(salonDTO.getLongitude());
        nouveauSalon.setGerant(gerant);
        
        return salonRepository.save(nouveauSalon);
    }

    // 3. Modifier un salon existant (Sécurisé avec SalonDTO)
    @PutMapping("/{id}")
    public Salon modifierMonSalon(@PathVariable Long id, @RequestBody SalonDTO salonDTO, Authentication authentication) {
        String emailGerant = authentication.getName();
        Utilisateur gerant = utilisateurRepository.findByEmail(emailGerant).orElseThrow();

        Salon salonExistant = salonRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Salon introuvable"));

        if (!salonExistant.getGerant().getId().equals(gerant.getId())) {
            throw new IllegalArgumentException("Non autorisé à modifier ce salon !");
        }

        salonExistant.setNom(salonDTO.getNom());
        salonExistant.setAdresse(salonDTO.getAdresse());
        salonExistant.setLatitude(salonDTO.getLatitude());
        salonExistant.setLongitude(salonDTO.getLongitude());

        return salonRepository.save(salonExistant);
    }

    // 4. Supprimer un salon (Correction du ResponseEntity<?>)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerMonSalon(@PathVariable Long id, Authentication authentication) {
        String emailGerant = authentication.getName();
        Utilisateur gerant = utilisateurRepository.findByEmail(emailGerant).orElseThrow();

        Salon salonExistant = salonRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Salon introuvable"));

        if (!salonExistant.getGerant().getId().equals(gerant.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"erreur\": \"Non autorisé à supprimer ce salon !\"}");
        }

        salonRepository.delete(salonExistant);
        return ResponseEntity.ok().body("{\"message\": \"Salon supprimé avec succès\"}");
    }

    // --- GESTION DES EMPLOYÉS ---
    
    @GetMapping("/{salonId}/employes")
    public List<Employe> getEmployes(@PathVariable Long salonId) {
        return employeRepository.findBySalonId(salonId);
    }

    @PostMapping("/{salonId}/employes")
    public Employe addEmploye(@PathVariable Long salonId, @RequestBody EmployeDTO employeDTO) {
        Salon salon = salonRepository.findById(salonId).orElseThrow();
        
        Employe employe = new Employe();
        employe.setNom(employeDTO.getNom());
        employe.setSalon(salon);
        
        return employeRepository.save(employe);
    }

    @PutMapping("/employes/{employeId}")
    public Employe updateEmploye(@PathVariable Long employeId, @RequestBody EmployeDTO employeDTO) {
        Employe employe = employeRepository.findById(employeId).orElseThrow();
        employe.setNom(employeDTO.getNom());
        return employeRepository.save(employe);
    }

    @DeleteMapping("/employes/{employeId}")
    public ResponseEntity<Void> deleteEmploye(@PathVariable Long employeId) {
        employeRepository.deleteById(employeId);
        return ResponseEntity.ok().build();
    }

    // --- GESTION DES PRESTATIONS ---
    
    @GetMapping("/{salonId}/prestations")
    public List<Prestation> getPrestations(@PathVariable Long salonId) {
        return prestationRepository.findBySalonId(salonId);
    }

    @PostMapping("/{salonId}/prestations")
    public Prestation addPrestation(@PathVariable Long salonId, @RequestBody PrestationDTO prestationDTO) {
        Salon salon = salonRepository.findById(salonId).orElseThrow();
        
        Prestation prestation = new Prestation();
        prestation.setNom(prestationDTO.getNom());
        prestation.setDureeMinutes(prestationDTO.getDureeMinutes());
        prestation.setPrix(prestationDTO.getPrix());
        prestation.setSalon(salon);
        
        return prestationRepository.save(prestation);
    }

    @PutMapping("/prestations/{prestationId}")
    public Prestation updatePrestation(@PathVariable Long prestationId, @RequestBody PrestationDTO prestationDTO) {
        Prestation prestation = prestationRepository.findById(prestationId).orElseThrow();
        prestation.setNom(prestationDTO.getNom());
        prestation.setDureeMinutes(prestationDTO.getDureeMinutes());
        prestation.setPrix(prestationDTO.getPrix());
        return prestationRepository.save(prestation);
    }

    @DeleteMapping("/prestations/{prestationId}")
    public ResponseEntity<Void> deletePrestation(@PathVariable Long prestationId) {
        prestationRepository.deleteById(prestationId);
        return ResponseEntity.ok().build();
    }

    // --- GESTION DES COMPÉTENCES ---

    @PostMapping("/employes/{employeId}/prestations/{prestationId}")
    public Employe assignerPrestation(@PathVariable Long employeId, @PathVariable Long prestationId) {
        Employe employe = employeRepository.findById(employeId).orElseThrow();
        Prestation prestation = prestationRepository.findById(prestationId).orElseThrow();
        
        if (!employe.getPrestations().contains(prestation)) {
            employe.getPrestations().add(prestation);
        }
        return employeRepository.save(employe);
    }

    @DeleteMapping("/employes/{employeId}/prestations/{prestationId}")
    public Employe retirerPrestation(@PathVariable Long employeId, @PathVariable Long prestationId) {
        Employe employe = employeRepository.findById(employeId).orElseThrow();
        Prestation prestation = prestationRepository.findById(prestationId).orElseThrow();
        
        employe.getPrestations().remove(prestation);
        return employeRepository.save(employe);
    }

    // --- GESTION DES CRÉNEAUX ---

    @GetMapping("/{salonId}/creneaux")
    public List<Creneau> getCreneaux(@PathVariable Long salonId) {
        return creneauRepository.findByEmployeSalonIdOrderByJourSemaineAscHeureDebutAsc(salonId);
    }

    @PostMapping("/employes/{employeId}/creneaux")
    public Creneau addCreneau(@PathVariable Long employeId, @RequestBody CreneauDTO creneauDTO) {
        Employe employe = employeRepository.findById(employeId).orElseThrow();
        
        Creneau creneau = new Creneau();
        creneau.setJourSemaine(creneauDTO.getJourSemaine());
        creneau.setHeureDebut(creneauDTO.getHeureDebut());
        creneau.setEmploye(employe);
        creneau.setStatut("DISPONIBLE");
        
        return creneauRepository.save(creneau);
    }

    @DeleteMapping("/creneaux/{creneauId}")
    public ResponseEntity<Void> deleteCreneau(@PathVariable Long creneauId) {
        creneauRepository.deleteById(creneauId);
        return ResponseEntity.ok().build();
    }
}