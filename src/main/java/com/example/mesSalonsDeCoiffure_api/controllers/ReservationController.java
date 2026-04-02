package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.*;
import com.example.mesSalonsDeCoiffure_api.repositories.*;
import com.example.mesSalonsDeCoiffure_api.dto.DemandeReservationDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final CreneauRepository creneauRepository;
    private final ReservationRepository reservationRepository;
    private final PrestationRepository prestationRepository;
    private final EmployeRepository employeRepository;
    private final SalonRepository salonRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ReservationController(CreneauRepository creneauRepository, ReservationRepository reservationRepository,
                                 PrestationRepository prestationRepository, EmployeRepository employeRepository,
                                 SalonRepository salonRepository, UtilisateurRepository utilisateurRepository) {
        this.creneauRepository = creneauRepository;
        this.reservationRepository = reservationRepository;
        this.prestationRepository = prestationRepository;
        this.employeRepository = employeRepository;
        this.salonRepository = salonRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping("/disponibles")
    public List<Creneau> getDisponibilites(
            @RequestParam Long prestationId,
            @RequestParam String date,
            @RequestParam(required = false) Long employeId) {

       LocalDate dateChoisie = LocalDate.parse(date);
        
        String jourSemaineBrut = dateChoisie.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.FRANCE)
                .toUpperCase(); 
        
        String jourSemaineSansAccent = java.text.Normalizer.normalize(jourSemaineBrut, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        Prestation prestation = prestationRepository.findById(prestationId).orElseThrow();
        Long salonId = prestation.getSalon().getId();
        final String jourRecherche = jourSemaineSansAccent;

        // SonarQube Fix: Remplacement de .collect(Collectors.toList()) par .toList()
        List<Creneau> creneauxDuJour = creneauRepository.findByEmployeSalonIdOrderByJourSemaineAscHeureDebutAsc(salonId)
                .stream()
                .filter(c -> c.getJourSemaine().equals(jourRecherche))
                .toList();

        if (employeId != null) {
            creneauxDuJour = creneauxDuJour.stream()
                    .filter(c -> c.getEmploye().getId().equals(employeId))
                    .toList();
        } else {
            creneauxDuJour = creneauxDuJour.stream()
                    .filter(c -> c.getEmploye().getPrestations().stream().anyMatch(p -> p.getId().equals(prestationId)))
                    .toList();
        }

        List<Creneau> creneauxLibres = new ArrayList<>();
        
        for (Creneau creneau : creneauxDuJour) {
            List<Reservation> rdvDeLemploye = reservationRepository.findByEmployeIdAndDate(creneau.getEmploye().getId(), dateChoisie);
            
            boolean estOccupe = rdvDeLemploye.stream()
                .anyMatch(rdv -> rdv.getDateHeureDebut().toLocalTime().equals(creneau.getHeureDebut()));
            
            if (!estOccupe) {
                creneauxLibres.add(creneau);
            }
        }

        return creneauxLibres;
    }

    @PostMapping("/reserver")
    public Reservation reserver(@RequestBody DemandeReservationDTO demande, Authentication authentication) {
        Salon salon = salonRepository.findById(demande.getSalonId()).orElseThrow();
        Employe employe = employeRepository.findById(demande.getEmployeId()).orElseThrow();
        Prestation prestation = prestationRepository.findById(demande.getPrestationId()).orElseThrow();

        String emailClient = authentication.getName();
        Utilisateur client = utilisateurRepository.findByEmail(emailClient)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        Reservation nouvelleReservation = new Reservation();
        LocalDateTime dateHeure = LocalDateTime.of(demande.getDate(), demande.getHeureDebut());
        nouvelleReservation.setDateHeureDebut(dateHeure);
        
        nouvelleReservation.setClient(client);
        nouvelleReservation.setSalon(salon);
        nouvelleReservation.setEmploye(employe);
        nouvelleReservation.setPrestation(prestation);

        return reservationRepository.save(nouvelleReservation);
    }
}