package com.example.mesSalonsDeCoiffure_api.entities;

import jakarta.persistence.*;

@Entity
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    
    // Crucial pour la User Story "Notification WhatsApp"
    private String telephone; 

    // 👇 Nouveaux champs pour la sécurité 👇
    private String motDePasse;
    
    // Le rôle définira les droits. Ex: "USER" ou "ADMIN"
    private String role;

    private boolean rappelsReguliers = false;
    private boolean notifsWhatsapp = false;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isRappelsReguliers() { return rappelsReguliers; }
    public void setRappelsReguliers(boolean rappelsReguliers) { this.rappelsReguliers = rappelsReguliers; }

    public boolean isNotifsWhatsapp() { return notifsWhatsapp; }
    public void setNotifsWhatsapp(boolean notifsWhatsapp) { this.notifsWhatsapp = notifsWhatsapp; }
}