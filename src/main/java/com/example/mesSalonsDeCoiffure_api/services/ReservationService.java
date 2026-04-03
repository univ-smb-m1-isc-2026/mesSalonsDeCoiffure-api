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

    // Remplacement de @Autowired par champs 'final' et constructeur
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

   // Dans ReservationService.java
public List<CreneauDisponibleDTO> calculerCreneauxDisponibles(Long employeId, Long prestationId, LocalDate dateRecherche) {
    // 1. TRADUCTION DU JOUR (Java English -> Ta BDD Français)
    String jourJava = dateRecherche.getDayOfWeek().name();
    String jourRecherche = switch (jourJava) {
        case "MONDAY" -> "LUNDI";
        case "TUESDAY" -> "MARDI";
        case "WEDNESDAY" -> "MERCREDI";
        case "THURSDAY" -> "JEUDI";
        case "FRIDAY" -> "VENDREDI";
        case "SATURDAY" -> "SAMEDI";
        case "SUNDAY" -> "DIMANCHE";
        default -> jourJava;
    };

    Prestation prestation = prestationRepository.findById(prestationId).orElseThrow();
    int duree = prestation.getDureeMinutes();
    List<CreneauDisponibleDTO> possibleSlots = new ArrayList<>();

    // 2. GESTION DU MODE "PEU IMPORTE"
    // Si employeId est null, on récupère tous les employés du salon de la prestation
    List<Long> idsEmployes = new ArrayList<>();
    if (employeId == null) {
        employeRepository.findBySalonId(prestation.getSalon().getId())
                .forEach(e -> idsEmployes.add(e.getId()));
    } else {
        idsEmployes.add(employeId);
    }

    // 3. BOUCLE SUR CHAQUE EMPLOYÉ CONCERNÉ
    for (Long idEmp : idsEmployes) {
        List<Creneau> planning = creneauRepository.findByEmployeIdAndJourSemaine(idEmp, jourRecherche);
        
        LocalDateTime debutJour = dateRecherche.atStartOfDay();
        LocalDateTime finJour = dateRecherche.atTime(23, 59);
        List<RendezVous> rdvExistants = rendezVousRepository.findRendezVousByEmployeAndDate(idEmp, debutJour, finJour);

        for (Creneau tranche : planning) {
            LocalTime courant = tranche.getHeureDebut();
            LocalTime limite = tranche.getHeureFin();

            while (courant.plusMinutes(duree).isBefore(limite) || courant.plusMinutes(duree).equals(limite)) {
                LocalDateTime slotDebut = LocalDateTime.of(dateRecherche, courant);
                LocalDateTime slotFin = slotDebut.plusMinutes(duree);

                boolean conflit = false;
                for (RendezVous rdv : rdvExistants) {
                    if (slotDebut.isBefore(rdv.getDateHeureFin()) && slotFin.isAfter(rdv.getDateHeureDebut())) {
                        conflit = true;
                        break;
                    }
                }

                if (!conflit) {
                    // On récupère le nom de l'employé pour le DTO
                    Employe e = employeRepository.findById(idEmp).get();
                    possibleSlots.add(new CreneauDisponibleDTO(slotDebut, slotFin, idEmp, e.getNom()));
                }
                courant = courant.plusMinutes(15);
            }
        }
    }
    return possibleSlots;
}
    // N'oublie pas d'ajouter le paramètre emailClient si ce n'est pas déjà fait !
    public RendezVous enregistrerRendezVous(ReservationRequestDTO demande, String emailClient) {
        
        Utilisateur client = utilisateurRepository.findByEmail(emailClient)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
                
        Employe employe = employeRepository.findById(demande.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
                
        Prestation prestation = prestationRepository.findById(demande.getPrestationId())
                .orElseThrow(() -> new RuntimeException("Prestation introuvable"));

        RendezVous nouveauRdv = new RendezVous();
        nouveauRdv.setClient(client);
        nouveauRdv.setEmploye(employe);
        nouveauRdv.setPrestation(prestation);
        nouveauRdv.setSalon(prestation.getSalon());
        
        // 🌟 L'ASSEMBLAGE MAGIQUE EST ICI 🌟
        // On fusionne la LocalDate et la LocalTime envoyées par Angular
        LocalDateTime dateHeure = LocalDateTime.of(demande.getDate(), demande.getHeureDebut());
        nouveauRdv.setDateHeureDebut(dateHeure);
        
        LocalDateTime heureFin = dateHeure.plusMinutes(prestation.getDureeMinutes());
        nouveauRdv.setDateHeureFin(heureFin);
        nouveauRdv.setStatut("CONFIRME");

        return rendezVousRepository.save(nouveauRdv);
    }

public RendezVous deplacerRendezVous(Long id, ReservationRequestDTO demande, String emailClient) {
    RendezVous rdvExistant = rendezVousRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable"));
            
    if (!rdvExistant.getClient().getEmail().equals(emailClient)) {
        throw new RuntimeException("Non autorisé");
    }
            
    Employe nouvelEmploye = employeRepository.findById(demande.getEmployeId()).orElseThrow();
    Prestation presta = prestationRepository.findById(demande.getPrestationId()).orElseThrow();

    rdvExistant.setEmploye(nouvelEmploye);
    rdvExistant.setPrestation(presta);
    
    LocalDateTime nouvelleDateHeure = LocalDateTime.of(demande.getDate(), demande.getHeureDebut());
    rdvExistant.setDateHeureDebut(nouvelleDateHeure);
    rdvExistant.setDateHeureFin(nouvelleDateHeure.plusMinutes(presta.getDureeMinutes()));

    return rendezVousRepository.save(rdvExistant);
}
}