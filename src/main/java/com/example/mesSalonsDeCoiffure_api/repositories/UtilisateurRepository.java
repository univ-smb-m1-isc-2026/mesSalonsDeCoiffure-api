package com.example.mesSalonsDeCoiffure_api.repositories;

import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    // Spring Boot comprend tout seul cette phrase ! Il va faire un "SELECT * WHERE email = ?"
    Optional<Utilisateur> findByEmail(String email);
}