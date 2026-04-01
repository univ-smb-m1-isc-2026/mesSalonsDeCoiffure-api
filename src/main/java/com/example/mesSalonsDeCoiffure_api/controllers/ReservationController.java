package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.Creneau;
import com.example.mesSalonsDeCoiffure_api.entities.Employe;
import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.entities.Reservation;
import com.example.mesSalonsDeCoiffure_api.entities.Salon;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.CreneauRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.EmployeRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.PrestationRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.ReservationRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.SalonRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;
import com.example.mesSalonsDeCoiffure_api.dto.DemandeReservationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.time.LocalDateTime;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private CreneauRepository creneauRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PrestationRepository prestationRepository;
    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private SalonRepository salonRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // ==========================================
    // 1. RECHERCHE DES DISPONIBILITÉS
    // ==========================================
    @GetMapping("/disponibles")
    public List<Creneau> getDisponibilites(
            @RequestParam Long prestationId,
            @RequestParam String date,
            @RequestParam(required = false) Long employeId) {

       LocalDate dateChoisie = LocalDate.parse(date);
        
        // 1. Traduire la date en Jour de la Semaine (ex: "MARDI")
        String jourSemaineBrut = dateChoisie.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.FRANCE)
                .toUpperCase(); 
        
        // On enlève les accents éventuels (ex: "Mardi" -> "MARDI")
        String jourSemaineSansAccent = java.text.Normalizer.normalize(jourSemaineBrut, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        Prestation prestation = prestationRepository.findById(prestationId).orElseThrow();
        Long salonId = prestation.getSalon().getId();

        // 👇 CORRECTION ICI : On crée une copie 100% finale pour faire plaisir à Java
        final String jourRecherche = jourSemaineSansAccent;

        // 2. Récupérer l'emploi du temps habituel du salon pour CE JOUR LÀ
        List<Creneau> creneauxDuJour = creneauRepository.findByEmployeSalonIdOrderByJourSemaineAscHeureDebutAsc(salonId)
                .stream()
                .filter(c -> c.getJourSemaine().equals(jourRecherche)) // 👈 On utilise la nouvelle variable
                .collect(Collectors.toList());

        // Si le client veut un coiffeur précis, on filtre l'emploi du temps
        if (employeId != null) {
            creneauxDuJour = creneauxDuJour.stream()
                    .filter(c -> c.getEmploye().getId().equals(employeId))
                    .collect(Collectors.toList());
        } else {
            // "Peu m'importe" -> On garde uniquement les créneaux des employés qui SAVENT faire la prestation
            creneauxDuJour = creneauxDuJour.stream()
                    .filter(c -> c.getEmploye().getPrestations().stream().anyMatch(p -> p.getId().equals(prestationId)))
                    .collect(Collectors.toList());
        }

        // 3. Vérifier les RDV déjà pris pour éliminer les créneaux occupés
        List<Creneau> creneauxLibres = new ArrayList<>();
        
        for (Creneau creneau : creneauxDuJour) {
            List<Reservation> rdvDeLemploye = reservationRepository.findByEmployeIdAndDate(creneau.getEmploye().getId(), dateChoisie);
            
            // Est-ce qu'une réservation existe déjà à cette heure précise ?
            boolean estOccupe = rdvDeLemploye.stream()
                .anyMatch(rdv -> rdv.getDateHeureDebut().toLocalTime().equals(creneau.getHeureDebut()));
            
            if (!estOccupe) {
                creneauxLibres.add(creneau);
            }
        }

        return creneauxLibres;
    }

    // ==========================================
    // 2. SAUVEGARDER LA RÉSERVATION
    // ==========================================
    @PostMapping("/reserver")
    public Reservation reserver(@RequestBody DemandeReservationDTO demande, Authentication authentication) {
        
        // 1. On récupère les éléments du salon
        Salon salon = salonRepository.findById(demande.getSalonId()).orElseThrow();
        Employe employe = employeRepository.findById(demande.getEmployeId()).orElseThrow();
        Prestation prestation = prestationRepository.findById(demande.getPrestationId()).orElseThrow();

        // 👇 2. On récupère le VRAI client connecté grâce à son Token !
        String emailClient = authentication.getName();
        Utilisateur client = utilisateurRepository.findByEmail(emailClient)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        Reservation nouvelleReservation = new Reservation();
        
        // 👇 3. On fusionne la Date et l'Heure envoyées par Angular en un seul LocalDateTime
        LocalDateTime dateHeure = LocalDateTime.of(demande.getDate(), demande.getHeureDebut());
        nouvelleReservation.setDateHeureDebut(dateHeure);
        
        // 4. On lie les vraies entités
        nouvelleReservation.setClient(client);
        nouvelleReservation.setSalon(salon);
        nouvelleReservation.setEmploye(employe);
        nouvelleReservation.setPrestation(prestation);

        return reservationRepository.save(nouvelleReservation);
    }
}