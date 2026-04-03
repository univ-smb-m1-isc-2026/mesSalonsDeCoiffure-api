package com.example.mesSalonsDeCoiffure_api.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class CreneauDTO {
    // Pour l'Admin (Plage de travail)
    private String jourSemaine;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    // Pour la Réservation (Le créneau précis de 15min calculé)
    // On utilise LocalDateTime pour inclure la date du jour choisie
    private LocalDateTime dateHeureDebut; 

    // Constructeur vide (pour Jackson/JSON)
    public CreneauDTO() {}

    // Constructeur pratique pour le calcul des dispos
    public CreneauDTO(LocalDateTime dateHeureDebut, LocalTime heureFin) {
        this.dateHeureDebut = dateHeureDebut;
        this.heureFin = heureFin;
    }

    // --- Getters et Setters ---

    public String getJourSemaine() { return jourSemaine; }
    public void setJourSemaine(String jourSemaine) { this.jourSemaine = jourSemaine; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public LocalDateTime getDateHeureDebut() { return dateHeureDebut; }
    public void setDateHeureDebut(LocalDateTime dateHeureDebut) { this.dateHeureDebut = dateHeureDebut; }
}