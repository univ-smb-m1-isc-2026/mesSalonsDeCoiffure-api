package com.example.mesSalonsDeCoiffure_api.dto;

import java.time.LocalDateTime;

public class CreneauDisponibleDTO {
    
    private LocalDateTime heureDebut;
    private LocalDateTime heureFin;
    private Long employeId;
    private String employeNom;

    // --- Constructeur vide (Obligatoire pour que Spring Boot lise le JSON) ---
    public CreneauDisponibleDTO() {
    }

    // --- Constructeur avec tous les paramètres (Pratique pour notre Service) ---
    public CreneauDisponibleDTO(LocalDateTime heureDebut, LocalDateTime heureFin, Long employeId, String employeNom) {
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.employeId = employeId;
        this.employeNom = employeNom;
    }

    // --- Getters et Setters ---
    
    public LocalDateTime getHeureDebut() { 
        return heureDebut; 
    }
    
    public void setHeureDebut(LocalDateTime heureDebut) { 
        this.heureDebut = heureDebut; 
    }

    public LocalDateTime getHeureFin() { 
        return heureFin; 
    }
    
    public void setHeureFin(LocalDateTime heureFin) { 
        this.heureFin = heureFin; 
    }

    public Long getEmployeId() { 
        return employeId; 
    }
    
    public void setEmployeId(Long employeId) { 
        this.employeId = employeId; 
    }

    public String getEmployeNom() { 
        return employeNom; 
    }
    
    public void setEmployeNom(String employeNom) { 
        this.employeNom = employeNom; 
    }
}