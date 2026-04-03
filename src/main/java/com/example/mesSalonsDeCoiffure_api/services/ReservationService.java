package com.example.mesSalonsDeCoiffure_api.services;

import com.example.mesSalonsDeCoiffure_api.dto.CreneauDTO;
import com.example.mesSalonsDeCoiffure_api.dto.CreneauDisponibleDTO;
import com.example.mesSalonsDeCoiffure_api.dto.ReservationRequestDTO;
import com.example.mesSalonsDeCoiffure_api.entities.Creneau;
import com.example.mesSalonsDeCoiffure_api.entities.Employe;
import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.entities.RendezVous;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.CreneauRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.EmployeRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.PrestationRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.RendezVousRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {

    private final RendezVousRepository rendezVousRepository;
    private final EmployeRepository employeRepository;
    private final PrestationRepository prestationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CreneauRepository creneauRepository;

    public ReservationService(RendezVousRepository rendezVousRepository,
                              EmployeRepository employeRepository,
                              PrestationRepository prestationRepository,
                              UtilisateurRepository utilisateurRepository,
                              CreneauRepository creneauRepository) {
        this.rendezVousRepository = rendezVousRepository;
        this.employeRepository = employeRepository;
        this.prestationRepository = prestationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.creneauRepository = creneauRepository;
    }

    public List<CreneauDisponibleDTO> calculerCreneauxDisponibles(Long employeId, Long prestationId, LocalDate dateRecherche) {
        String jourRecherche = traduireJourEnFrancais(dateRecherche.getDayOfWeek().name());
        Prestation prestation = prestationRepository.findById(prestationId).orElseThrow();
        
        List<Long> idsEmployes = determinerIdsEmployes(employeId, prestation.getSalon().getId());
        List<CreneauDisponibleDTO> possibleSlots = new ArrayList<>();

        for (Long idEmp : idsEmployes) {
            possibleSlots.addAll(genererDisposPourUnEmploye(idEmp, prestation, dateRecherche, jourRecherche));
        }
        return possibleSlots;
    }

    // --- MÉTHODES PRIVÉES DE SOUTIEN (Réduisent la complexité) ---

    private List<CreneauDisponibleDTO> genererDisposPourUnEmploye(Long idEmp, Prestation prestation, LocalDate date, String jour) {
        List<CreneauDisponibleDTO> slots = new ArrayList<>();
        List<Creneau> plannings = creneauRepository.findByEmployeIdAndJourSemaine(idEmp, jour);
        List<RendezVous> rdvs = rendezVousRepository.findRendezVousByEmployeAndDate(idEmp, date.atStartOfDay(), date.atTime(23, 59));
        
        // On récupère l'employé une seule fois ici pour optimiser
        Employe emp = employeRepository.findById(idEmp).orElseThrow();

        for (Creneau tranche : plannings) {
            slots.addAll(decouperTrancheEnSlots(tranche, rdvs, prestation, date, emp));
        }
        return slots;
    }

    private List<CreneauDisponibleDTO> decouperTrancheEnSlots(Creneau tranche, List<RendezVous> rdvs, Prestation p, LocalDate date, Employe emp) {
        List<CreneauDisponibleDTO> slots = new ArrayList<>();
        LocalTime courant = tranche.getHeureDebut();
        int duree = p.getDureeMinutes();

        while (!courant.plusMinutes(duree).isAfter(tranche.getHeureFin())) {
            LocalDateTime slotDebut = LocalDateTime.of(date, courant);
            LocalDateTime slotFin = slotDebut.plusMinutes(duree);

            if (!isEnConflit(slotDebut, slotFin, rdvs)) {
                slots.add(new CreneauDisponibleDTO(slotDebut, slotFin, emp.getId(), emp.getNom()));
            }
            courant = courant.plusMinutes(15);
        }
        return slots;
    }

    private boolean isEnConflit(LocalDateTime debut, LocalDateTime fin, List<RendezVous> rdvs) {
        return rdvs.stream().anyMatch(rdv -> 
            debut.isBefore(rdv.getDateHeureFin()) && fin.isAfter(rdv.getDateHeureDebut())
        );
    }

    private String traduireJourEnFrancais(String jourJava) {
        return switch (jourJava) {
            case "MONDAY" -> "LUNDI";
            case "TUESDAY" -> "MARDI";
            case "WEDNESDAY" -> "MERCREDI";
            case "THURSDAY" -> "JEUDI";
            case "FRIDAY" -> "VENDREDI";
            case "SATURDAY" -> "SAMEDI";
            case "SUNDAY" -> "DIMANCHE";
            default -> jourJava;
        };
    }

    private List<Long> determinerIdsEmployes(Long employeId, Long salonId) {
        if (employeId != null) return List.of(employeId);
        
        List<Long> ids = new ArrayList<>();
        employeRepository.findBySalonId(salonId).forEach(e -> ids.add(e.getId()));
        return ids;
    }

    // --- MÉTHODES DE RÉSERVATION ---

    public RendezVous enregistrerRendezVous(ReservationRequestDTO demande, String emailClient) {
        Utilisateur client = utilisateurRepository.findByEmail(emailClient).orElseThrow();
        Employe employe = employeRepository.findById(demande.getEmployeId()).orElseThrow();
        Prestation prestation = prestationRepository.findById(demande.getPrestationId()).orElseThrow();

        RendezVous nouveauRdv = new RendezVous();
        nouveauRdv.setClient(client);
        nouveauRdv.setEmploye(employe);
        nouveauRdv.setPrestation(prestation);
        nouveauRdv.setSalon(prestation.getSalon());

        LocalDateTime dateHeure = LocalDateTime.of(demande.getDate(), demande.getHeureDebut());
        nouveauRdv.setDateHeureDebut(dateHeure);
        nouveauRdv.setDateHeureFin(dateHeure.plusMinutes(prestation.getDureeMinutes()));
        nouveauRdv.setStatut("CONFIRME");

        return rendezVousRepository.save(nouveauRdv);
    }

    public RendezVous deplacerRendezVous(Long id, ReservationRequestDTO demande, String emailClient) {
        RendezVous rdv = rendezVousRepository.findById(id).orElseThrow();
        if (!rdv.getClient().getEmail().equals(emailClient)) throw new RuntimeException("Non autorisé");

        Employe emp = employeRepository.findById(demande.getEmployeId()).orElseThrow();
        Prestation p = prestationRepository.findById(demande.getPrestationId()).orElseThrow();

        rdv.setEmploye(emp);
        rdv.setPrestation(p);
        LocalDateTime debut = LocalDateTime.of(demande.getDate(), demande.getHeureDebut());
        rdv.setDateHeureDebut(debut);
        rdv.setDateHeureFin(debut.plusMinutes(p.getDureeMinutes()));

        return rendezVousRepository.save(rdv);
    }
}