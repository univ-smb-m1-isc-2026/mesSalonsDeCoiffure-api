package com.example.mesSalonsDeCoiffure_api.repositories;

import com.example.mesSalonsDeCoiffure_api.entities.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CreneauRepository extends JpaRepository<Creneau, Long> {
    
    // 👇 On remplace "Date" par "JourSemaine" dans le nom de la méthode
    List<Creneau> findByEmployeSalonIdOrderByJourSemaineAscHeureDebutAsc(Long salonId);
}