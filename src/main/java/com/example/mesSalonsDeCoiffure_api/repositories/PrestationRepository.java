package com.example.mesSalonsDeCoiffure_api.repositories;
import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrestationRepository extends JpaRepository<Prestation, Long> {
    List<Prestation> findBySalonId(Long salonId);
}