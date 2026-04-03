package com.example.mesSalonsDeCoiffure_api.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationRequestDTO {
    // Plus besoin de utilisateurId, on le récupère via le Token JWT !
    
    private Long employeId;
    private Long prestationId;
    
    // 👇 On sépare la date et l'heure EXACTEMENT comme Angular les envoie !
    private LocalDate date;
    private LocalTime heureDebut; 

    // --- Getters et Setters ---
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public Long getPrestationId() { return prestationId; }
    public void setPrestationId(Long prestationId) { this.prestationId = prestationId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
}