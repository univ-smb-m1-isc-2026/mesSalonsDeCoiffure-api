package com.example.mesSalonsDeCoiffure_api.repositories;

import com.example.mesSalonsDeCoiffure_api.entities.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CreneauRepository extends JpaRepository<Creneau, Long> {
    
    // Pour l'affichage Admin
    List<Creneau> findByEmployeSalonIdOrderByJourSemaineAscHeureDebutAsc(Long salonId);

    // 🌟 INDISPENSABLE pour le calcul des disponibilités 🌟
    List<Creneau> findByEmployeIdAndJourSemaine(Long employeId, String jourSemaine);
}