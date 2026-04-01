package com.example.mesSalonsDeCoiffure_api.dto;

public class DemandeReservationDTO {
    private Long salonId;
    private Long prestationId;
    private Long employeId;
    private java.time.LocalDate date;
    private java.time.LocalTime heureDebut;
    private String nomClient;
    private String telephoneClient;

    // --- GETTERS ET SETTERS ---
    public Long getSalonId() { return salonId; }
    public void setSalonId(Long salonId) { this.salonId = salonId; }
    public Long getPrestationId() { return prestationId; }
    public void setPrestationId(Long prestationId) { this.prestationId = prestationId; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public java.time.LocalDate getDate() { return date; }
    public void setDate(java.time.LocalDate date) { this.date = date; }
    public java.time.LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(java.time.LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }
    public String getTelephoneClient() { return telephoneClient; }
    public void setTelephoneClient(String telephoneClient) { this.telephoneClient = telephoneClient; }
}
