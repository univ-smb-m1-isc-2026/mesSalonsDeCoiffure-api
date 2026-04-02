package com.example.mesSalonsDeCoiffure_api.dto;
import java.time.LocalTime;

public class CreneauDTO {
    private String jourSemaine;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    // Getters et Setters
    public String getJourSemaine() { return jourSemaine; }
    public void setJourSemaine(String jourSemaine) { this.jourSemaine = jourSemaine; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
}