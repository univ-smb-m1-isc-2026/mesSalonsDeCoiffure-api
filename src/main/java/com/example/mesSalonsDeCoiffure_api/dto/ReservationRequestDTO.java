package com.example.mesSalonsDeCoiffure_api.dto;

import java.time.LocalDateTime;

public class ReservationRequestDTO {
    private Long utilisateurId; // Le profil de l'utilisateur qui réserve
    private Long employeId;     // Le coiffeur choisi
    private Long prestationId;  // La coupe choisie
    private LocalDateTime dateHeureDebut; // Le créneau choisi

    // --- Getters et Setters ---
    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public Long getPrestationId() { return prestationId; }
    public void setPrestationId(Long prestationId) { this.prestationId = prestationId; }

    public LocalDateTime getDateHeureDebut() { return dateHeureDebut; }
    public void setDateHeureDebut(LocalDateTime dateHeureDebut) { this.dateHeureDebut = dateHeureDebut; }
}