package com.example.mesSalonsDeCoiffure_api.dto;

public class AuthResponseDTO {
    private String token;
    private String role;
    private String message;

    public AuthResponseDTO(String token, String role, String message) {
        this.token = token;
        this.role = role;
        this.message = message;
    }

    // Getters
    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
}