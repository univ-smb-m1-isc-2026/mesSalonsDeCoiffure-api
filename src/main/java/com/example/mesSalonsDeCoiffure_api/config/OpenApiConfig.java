package com.example.mesSalonsDeCoiffure_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API - Mes Salons de Coiffure",
        version = "1.0",
        description = "Documentation complète de l'API REST pour la gestion des salons, employés et rendez-vous."
    ),
    // Applique la sécurité par défaut à TOUTES les routes dans Swagger
    security = @SecurityRequirement(name = "Bearer Authentication") 
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Connectez-vous avec /api/auth/login, copiez le token, et collez-le ici."
)
public class OpenApiConfig {
}