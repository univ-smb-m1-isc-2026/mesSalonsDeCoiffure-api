package com.example.mesSalonsDeCoiffure_api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mesSalonsDeCoiffure_api.entities.Salon;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.SalonRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;

@RestController
@RequestMapping("/api/admin/salons")
public class AdminController {

    @Autowired
    private SalonRepository salonRepository;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private com.example.mesSalonsDeCoiffure_api.repositories.EmployeRepository employeRepository;
    
    @Autowired
    private com.example.mesSalonsDeCoiffure_api.repositories.PrestationRepository prestationRepository;

    @Autowired
    private com.example.mesSalonsDeCoiffure_api.repositories.CreneauRepository creneauRepository;

    // 1. Récupérer UNIQUEMENT les salons du gérant connecté
    @GetMapping
    public List<Salon> getMesSalons(Authentication authentication) {
        String emailGerant = authentication.getName(); 
        
        Utilisateur gerant = utilisateurRepository.findByEmail(emailGerant)
            .orElseThrow(() -> new RuntimeException("🚨 ERREUR BDD : Impossible de trouver le gérant avec l'email : " + emailGerant));
        
        return salonRepository.findByGerantId(gerant.getId());
    }

    // 2. Créer un salon et l'attribuer automatiquement au gérant connecté
    @PostMapping
    public Salon creerMonSalon(@RequestBody Salon nouveauSalon, Authentication authentication) {
        String emailGerant = authentication.getName();
        
        System.out.println("➡️ Tentative de création de salon par : " + emailGerant);

        Utilisateur gerant = utilisateurRepository.findByEmail(emailGerant)
            .orElseThrow(() -> new RuntimeException("🚨 ERREUR BDD : Impossible de trouver le gérant avec l'email : " + emailGerant));
        
        nouveauSalon.setGerant(gerant);
        return salonRepository.save(nouveauSalon);
    }

    // 3. Modifier un salon existant
    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    public Salon modifierMonSalon(@org.springframework.web.bind.annotation.PathVariable Long id, @RequestBody Salon salonModifie, Authentication authentication) {
        String emailGerant = authentication.getName();
        Utilisateur gerant = utilisateurRepository.findByEmail(emailGerant).orElseThrow();

        // On cherche le salon en base de données
        Salon salonExistant = salonRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("🚨 Salon introuvable"));

        // SÉCURITÉ : On vérifie que ce salon appartient bien au gérant connecté !
        if (!salonExistant.getGerant().getId().equals(gerant.getId())) {
            throw new RuntimeException("🚨 Non autorisé à modifier ce salon !");
        }

        // On met à jour les informations
        salonExistant.setNom(salonModifie.getNom());
        salonExistant.setAdresse(salonModifie.getAdresse());
        salonExistant.setLatitude(salonModifie.getLatitude());
        salonExistant.setLongitude(salonModifie.getLongitude());

        return salonRepository.save(salonExistant);
    }

    // 4. Supprimer un salon
    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<?> supprimerMonSalon(@org.springframework.web.bind.annotation.PathVariable Long id, Authentication authentication) {
        String emailGerant = authentication.getName();
        Utilisateur gerant = utilisateurRepository.findByEmail(emailGerant).orElseThrow();

        // 1. On cherche le salon
        Salon salonExistant = salonRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("🚨 Salon introuvable"));

        // 2. SÉCURITÉ : On vérifie que c'est bien LE SIEN
        if (!salonExistant.getGerant().getId().equals(gerant.getId())) {
            return org.springframework.http.ResponseEntity.status(403).body("{\"erreur\": \"Non autorisé à supprimer ce salon !\"}");
        }

        // 3. On supprime
        salonRepository.delete(salonExistant);
        
        // On renvoie un message de succès au format JSON (Angular préfère ça)
        return org.springframework.http.ResponseEntity.ok().body("{\"message\": \"Salon supprimé avec succès\"}");
    }


    // --- GESTION DES EMPLOYÉS ---
    
    @GetMapping("/{salonId}/employes")
    public List<com.example.mesSalonsDeCoiffure_api.entities.Employe> getEmployes(@PathVariable Long salonId) {
        return employeRepository.findBySalonId(salonId);
    }

    @PostMapping("/{salonId}/employes")
    public com.example.mesSalonsDeCoiffure_api.entities.Employe addEmploye(@PathVariable Long salonId, @RequestBody com.example.mesSalonsDeCoiffure_api.entities.Employe employe) {
        Salon salon = salonRepository.findById(salonId).orElseThrow();
        employe.setSalon(salon);
        return employeRepository.save(employe);
    }

    @PutMapping("/employes/{employeId}")
    public com.example.mesSalonsDeCoiffure_api.entities.Employe updateEmploye(@PathVariable Long employeId, @RequestBody com.example.mesSalonsDeCoiffure_api.entities.Employe employeDetails) {
        com.example.mesSalonsDeCoiffure_api.entities.Employe employe = employeRepository.findById(employeId).orElseThrow();
        employe.setNom(employeDetails.getNom());
        return employeRepository.save(employe);
    }

    @DeleteMapping("/employes/{employeId}")
    public org.springframework.http.ResponseEntity<?> deleteEmploye(@PathVariable Long employeId) {
        employeRepository.deleteById(employeId);
        return org.springframework.http.ResponseEntity.ok().build();
    }

    // --- GESTION DES PRESTATIONS ---
    
    @GetMapping("/{salonId}/prestations")
    public List<com.example.mesSalonsDeCoiffure_api.entities.Prestation> getPrestations(@PathVariable Long salonId) {
        return prestationRepository.findBySalonId(salonId);
    }

    @PostMapping("/{salonId}/prestations")
    public com.example.mesSalonsDeCoiffure_api.entities.Prestation addPrestation(@PathVariable Long salonId, @RequestBody com.example.mesSalonsDeCoiffure_api.entities.Prestation prestation) {
        Salon salon = salonRepository.findById(salonId).orElseThrow();
        prestation.setSalon(salon);
        return prestationRepository.save(prestation);
    }

    @PutMapping("/prestations/{prestationId}")
    public com.example.mesSalonsDeCoiffure_api.entities.Prestation updatePrestation(@PathVariable Long prestationId, @RequestBody com.example.mesSalonsDeCoiffure_api.entities.Prestation details) {
        com.example.mesSalonsDeCoiffure_api.entities.Prestation prestation = prestationRepository.findById(prestationId).orElseThrow();
        prestation.setNom(details.getNom());
        prestation.setDureeMinutes(details.getDureeMinutes());
        prestation.setPrix(details.getPrix());
        return prestationRepository.save(prestation);
    }

    @DeleteMapping("/prestations/{prestationId}")
    public org.springframework.http.ResponseEntity<?> deletePrestation(@PathVariable Long prestationId) {
        prestationRepository.deleteById(prestationId);
        return org.springframework.http.ResponseEntity.ok().build();
    }


    // --- GESTION DES COMPÉTENCES (Prestations par Employé) ---

    @PostMapping("/employes/{employeId}/prestations/{prestationId}")
    public com.example.mesSalonsDeCoiffure_api.entities.Employe assignerPrestation(@PathVariable Long employeId, @PathVariable Long prestationId) {
        com.example.mesSalonsDeCoiffure_api.entities.Employe employe = employeRepository.findById(employeId).orElseThrow();
        com.example.mesSalonsDeCoiffure_api.entities.Prestation prestation = prestationRepository.findById(prestationId).orElseThrow();
        
        // Si l'employé n'a pas déjà cette prestation, on l'ajoute
        if (!employe.getPrestations().contains(prestation)) {
            employe.getPrestations().add(prestation);
        }
        return employeRepository.save(employe);
    }

    @DeleteMapping("/employes/{employeId}/prestations/{prestationId}")
    public com.example.mesSalonsDeCoiffure_api.entities.Employe retirerPrestation(@PathVariable Long employeId, @PathVariable Long prestationId) {
        com.example.mesSalonsDeCoiffure_api.entities.Employe employe = employeRepository.findById(employeId).orElseThrow();
        com.example.mesSalonsDeCoiffure_api.entities.Prestation prestation = prestationRepository.findById(prestationId).orElseThrow();
        
        employe.getPrestations().remove(prestation);
        return employeRepository.save(employe);
    }


// Récupérer tous les créneaux d'un salon
    @GetMapping("/{salonId}/creneaux")
    public java.util.List<com.example.mesSalonsDeCoiffure_api.entities.Creneau> getCreneaux(@PathVariable Long salonId) {
        // 👇 On utilise le nouveau nom de la méthode ici aussi
        return creneauRepository.findByEmployeSalonIdOrderByJourSemaineAscHeureDebutAsc(salonId);
    }

    // Ajouter un créneau manuellement
    @PostMapping("/employes/{employeId}/creneaux")
    public com.example.mesSalonsDeCoiffure_api.entities.Creneau addCreneau(@PathVariable Long employeId, @RequestBody com.example.mesSalonsDeCoiffure_api.entities.Creneau creneau) {
        com.example.mesSalonsDeCoiffure_api.entities.Employe employe = employeRepository.findById(employeId).orElseThrow();
        creneau.setEmploye(employe);
        creneau.setStatut("DISPONIBLE");
        return creneauRepository.save(creneau);
    }

    // Supprimer un créneau
    @DeleteMapping("/creneaux/{creneauId}")
    public org.springframework.http.ResponseEntity<?> deleteCreneau(@PathVariable Long creneauId) {
        creneauRepository.deleteById(creneauId);
        return org.springframework.http.ResponseEntity.ok().build();
    }
}