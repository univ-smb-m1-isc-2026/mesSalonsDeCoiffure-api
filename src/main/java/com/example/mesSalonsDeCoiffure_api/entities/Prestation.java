package com.example.mesSalonsDeCoiffure_api.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class Prestation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private Integer dureeMinutes; // Ex: 30, 60
    private Double prix;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(Integer dureeMinutes) { this.dureeMinutes = dureeMinutes; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public Salon getSalon() { return salon; }
    public void setSalon(Salon salon) {this.salon = salon;}
}