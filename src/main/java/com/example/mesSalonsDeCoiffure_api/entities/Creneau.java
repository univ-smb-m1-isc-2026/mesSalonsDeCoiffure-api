package com.example.mesSalonsDeCoiffure_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
public class Creneau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le jour (LUNDI, MARDI, etc.) au lieu d'une date fixe
    private String jourSemaine; 
    
    private LocalTime heureDebut;
    private LocalTime heureFin;
    // Le statut qui avait disparu !
    private String statut = "DISPONIBLE";

    @ManyToOne
    @JoinColumn(name = "employe_id")
    @JsonIgnoreProperties({"salon", "prestations"}) // Évite les boucles JSON
    private Employe employe;

    // --- GETTERS ET SETTERS ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJourSemaine() { return jourSemaine; }
    public void setJourSemaine(String jourSemaine) { this.jourSemaine = jourSemaine; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
}