package com.example.mesSalonsDeCoiffure_api.services;

import com.example.mesSalonsDeCoiffure_api.dto.CreneauDisponibleDTO;
import com.example.mesSalonsDeCoiffure_api.dto.ReservationRequestDTO;
import com.example.mesSalonsDeCoiffure_api.entities.Employe;
import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.entities.RendezVous;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
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

    public ReservationService(RendezVousRepository rendezVousRepository, EmployeRepository employeRepository, 
                              PrestationRepository prestationRepository, UtilisateurRepository utilisateurRepository) {
        this.rendezVousRepository = rendezVousRepository;
        this.employeRepository = employeRepository;
        this.prestationRepository = prestationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<CreneauDisponibleDTO> calculerCreneauxDisponibles(Long employeId, Long prestationId, LocalDate dateRecherche) {
        
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        Prestation prestation = prestationRepository.findById(prestationId)
                .orElseThrow(() -> new RuntimeException("Prestation introuvable"));

        LocalDateTime debutJournee = LocalDateTime.of(dateRecherche, LocalTime.of(9, 0));
        LocalDateTime finJournee = LocalDateTime.of(dateRecherche, LocalTime.of(18, 0));

        List<RendezVous> rdvExistants = rendezVousRepository.findRendezVousByEmployeAndDate(employeId, debutJournee, finJournee);
        List<CreneauDisponibleDTO> creneauxLibres = new ArrayList<>();
        LocalDateTime heureTestee = debutJournee;

        while (heureTestee.plusMinutes(prestation.getDureeMinutes()).isBefore(finJournee) || 
               heureTestee.plusMinutes(prestation.getDureeMinutes()).isEqual(finJournee)) {
            
            LocalDateTime finTestee = heureTestee.plusMinutes(prestation.getDureeMinutes());
            boolean conflit = false;

            for (RendezVous rdv : rdvExistants) {
                if (heureTestee.isBefore(rdv.getDateHeureFin()) && finTestee.isAfter(rdv.getDateHeureDebut())) {
                    conflit = true;
                    break;
                }
            }

            if (!conflit) {
                creneauxLibres.add(new CreneauDisponibleDTO(heureTestee, finTestee, employe.getId(), employe.getNom()));
            }
            heureTestee = heureTestee.plusMinutes(15);
        }
        return creneauxLibres;
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
        
        // 🌟 L'ASSEMBLAGE MAGIQUE EST ICI 🌟
        // On fusionne la LocalDate et la LocalTime envoyées par Angular
        LocalDateTime dateHeure = LocalDateTime.of(demande.getDate(), demande.getHeureDebut());
        nouveauRdv.setDateHeureDebut(dateHeure);
        
        LocalDateTime heureFin = dateHeure.plusMinutes(prestation.getDureeMinutes());
        nouveauRdv.setDateHeureFin(heureFin);
        nouveauRdv.setStatut("CONFIRME");

        return rendezVousRepository.save(nouveauRdv);
    }
}