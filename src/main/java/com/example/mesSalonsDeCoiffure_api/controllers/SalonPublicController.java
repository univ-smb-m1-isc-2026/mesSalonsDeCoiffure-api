package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.Employe;
import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.repositories.EmployeRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.PrestationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salons")
public class SalonPublicController {

    private final EmployeRepository employeRepository;
    private final PrestationRepository prestationRepository;

    public SalonPublicController(EmployeRepository employeRepository, PrestationRepository prestationRepository) {
        this.employeRepository = employeRepository;
        this.prestationRepository = prestationRepository;
    }

    @GetMapping("/{salonId}/employes")
    public List<Employe> getEmployesDuSalon(@PathVariable Long salonId) {
        return employeRepository.findBySalonId(salonId);
    }

    @GetMapping("/{salonId}/prestations")
    public List<Prestation> getPrestationsDuSalon(@PathVariable Long salonId) {
        return prestationRepository.findBySalonId(salonId);
    }
}