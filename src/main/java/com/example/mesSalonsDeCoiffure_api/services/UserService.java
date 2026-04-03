package com.example.mesSalonsDeCoiffure_api.services;

import com.example.mesSalonsDeCoiffure_api.dto.UserUpdateDTO;
import com.example.mesSalonsDeCoiffure_api.entities.RendezVous; // Vérifie que c'est bien RendezVous et pas Reservation
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.RendezVousRepository; // Idem ici
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UtilisateurRepository utilisateurRepository;
    private final RendezVousRepository rendezVousRepository;

    public UserService(UtilisateurRepository utilisateurRepository, RendezVousRepository rendezVousRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.rendezVousRepository = rendezVousRepository;
    }

    public Utilisateur getMonProfil(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public Utilisateur modifierMonProfil(UserUpdateDTO modifications, String email) {
        Utilisateur userActuel = getMonProfil(email);

        userActuel.setNom(modifications.getNom());
        // On n'oublie pas le prénom !
        userActuel.setPrenom(modifications.getPrenom()); 
        userActuel.setTelephone(modifications.getTelephone());
        userActuel.setRappelsReguliers(modifications.isRappelsReguliers());
        userActuel.setNotifsWhatsapp(modifications.isNotifsWhatsapp());

        return utilisateurRepository.save(userActuel);
    }

    public List<RendezVous> getMesRendezVous(String email) {
        Utilisateur client = getMonProfil(email);
        
        // 🌟 CORRECTION DU BUG 🌟
        // On cherche les RDV via l'ID unique du client, pas via son téléphone fluctuant.
        // Assure-toi que cette méthode existe dans ton RendezVousRepository !
        return rendezVousRepository.findByClientIdOrderByDateHeureDebutDesc(client.getId());
    }
}