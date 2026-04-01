package com.example.mesSalonsDeCoiffure_api.repositories;
import com.example.mesSalonsDeCoiffure_api.entities.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeRepository extends JpaRepository<Employe, Long> {
    List<Employe> findBySalonId(Long salonId);
}