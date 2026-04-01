package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.Employe;
import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.repositories.EmployeRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.PrestationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salons")
@CrossOrigin(origins = "*")
public class SalonPublicController {

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private PrestationRepository prestationRepository;

    // 1. Fournir la liste des employés aux clients
    @GetMapping("/{salonId}/employes")
    public List<Employe> getEmployesDuSalon(@PathVariable Long salonId) {
        return employeRepository.findBySalonId(salonId);
    }

    // 2. Fournir la liste des prestations aux clients
    @GetMapping("/{salonId}/prestations")
    public List<Prestation> getPrestationsDuSalon(@PathVariable Long salonId) {
        return prestationRepository.findBySalonId(salonId);
    }
}