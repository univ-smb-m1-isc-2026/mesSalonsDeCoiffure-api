package com.example.mesSalonsDeCoiffure_api.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👇 Le champ combiné Date + Heure
    private LocalDateTime dateHeureDebut; 
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    // 👇 On cache les infos sensibles et les boucles
    @JsonIgnoreProperties({"motDePasse", "role", "reservations"}) 
    private Utilisateur client;
    
    @ManyToOne
    @JoinColumn(name = "salon_id")
    // 👇 On évite de charger tout le catalogue du salon pour un simple RDV
    @JsonIgnoreProperties({"employes", "prestations"}) 
    private Salon salon;
    
    @ManyToOne
    @JoinColumn(name = "employe_id")
    @JsonIgnoreProperties({"salon", "prestations"})
    private Employe employe;
    
    @ManyToOne
    @JoinColumn(name = "prestation_id")
    @JsonIgnoreProperties({"salon"})
    private Prestation prestation;

    // --- GETTERS & SETTERS (Génère-les ou mets-les à jour) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDateHeureDebut() { return dateHeureDebut; }
    public void setDateHeureDebut(LocalDateTime dateHeureDebut) { this.dateHeureDebut = dateHeureDebut; }
    public Utilisateur getClient() { return client; }
    public void setClient(Utilisateur client) { this.client = client; }
    public Salon getSalon() { return salon; }
    public void setSalon(Salon salon) { this.salon = salon; }
    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
    public Prestation getPrestation() { return prestation; }
    public void setPrestation(Prestation prestation) { this.prestation = prestation; }
}