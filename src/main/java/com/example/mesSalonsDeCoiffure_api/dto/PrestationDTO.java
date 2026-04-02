package com.example.mesSalonsDeCoiffure_api.dto;

public class PrestationDTO {
    private String nom;
    private int dureeMinutes;
    private double prix;

    // Getters et Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(int dureeMinutes) { this.dureeMinutes = dureeMinutes; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
}