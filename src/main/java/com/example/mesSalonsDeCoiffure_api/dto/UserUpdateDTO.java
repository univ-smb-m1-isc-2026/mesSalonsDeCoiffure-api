package com.example.mesSalonsDeCoiffure_api.dto;

public class UserUpdateDTO {
    private String nom;
    private String telephone;
    private boolean rappelsReguliers;
    private boolean notifsWhatsapp;

    // Génère les Getters et Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public boolean isRappelsReguliers() { return rappelsReguliers; }
    public void setRappelsReguliers(boolean rappelsReguliers) { this.rappelsReguliers = rappelsReguliers; }
    public boolean isNotifsWhatsapp() { return notifsWhatsapp; }
    public void setNotifsWhatsapp(boolean notifsWhatsapp) { this.notifsWhatsapp = notifsWhatsapp; }
}