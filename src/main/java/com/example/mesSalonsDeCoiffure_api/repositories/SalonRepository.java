package com.example.mesSalonsDeCoiffure_api.repositories;

import com.example.mesSalonsDeCoiffure_api.entities.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalonRepository extends JpaRepository<Salon, Long> {
    // Spring Data JPA génère automatiquement les méthodes findById, findAll, save, delete...
    
    // Tu peux aussi ajouter des requêtes personnalisées (ex: chercher par nom)
    // List<Salon> findByNomContainingIgnoreCase(String nom);
}