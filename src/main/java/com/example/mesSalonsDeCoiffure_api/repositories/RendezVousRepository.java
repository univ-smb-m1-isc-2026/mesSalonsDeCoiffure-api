package com.example.mesSalonsDeCoiffure_api.repositories;

import com.example.mesSalonsDeCoiffure_api.entities.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    
    // C'est CETTE méthode qui manquait ! 
    // Elle cherche les RDV d'un employé précis sur une journée précise.
    @Query("SELECT r FROM RendezVous r WHERE r.employe.id = :employeId AND r.dateHeureDebut >= :debutJournee AND r.dateHeureFin <= :finJournee AND r.statut != 'ANNULE'")
    List<RendezVous> findRendezVousByEmployeAndDate(
            @Param("employeId") Long employeId, 
            @Param("debutJournee") LocalDateTime debutJournee, 
            @Param("finJournee") LocalDateTime finJournee
    );
}