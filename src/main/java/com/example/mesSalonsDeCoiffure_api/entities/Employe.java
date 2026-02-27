package com.example.mesSalonsDeCoiffure_api.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    // Un employé peut faire plusieurs prestations, et une prestation est faite par plusieurs employés
    @ManyToMany
    @JoinTable(
        name = "employe_prestation",
        joinColumns = @JoinColumn(name = "employe_id"),
        inverseJoinColumns = @JoinColumn(name = "prestation_id")
    )
    private List<Prestation> prestationsProposees = new java.util.ArrayList<>();

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public Salon getSalon() { return salon; }
    public void setSalon(Salon salon) {this.salon = salon;}

    public List<Prestation> getPrestationsProposees(){ return prestationsProposees; }
    public void setPrestationsProposees(List<Prestation> prestationsProposees) {this.prestationsProposees = prestationsProposees;}
    

}