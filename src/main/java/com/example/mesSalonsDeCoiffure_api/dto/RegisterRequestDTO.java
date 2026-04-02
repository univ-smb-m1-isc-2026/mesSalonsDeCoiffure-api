package com.example.mesSalonsDeCoiffure_api.dto;

public class RegisterRequestDTO {
    private String nom;
    private String email;
    private String motDePasse;
    private String telephone;

    // Génère les Getters et Setters ici (ou utilise Lombok @Data)
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}