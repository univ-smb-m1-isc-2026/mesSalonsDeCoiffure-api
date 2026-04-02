package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.Employe;
import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.entities.Salon;
import com.example.mesSalonsDeCoiffure_api.repositories.EmployeRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.PrestationRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.SalonRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employes")
public class EmployeController {

    private final EmployeRepository employeRepository;
    private final SalonRepository salonRepository;
    private final PrestationRepository prestationRepository;

    public EmployeController(EmployeRepository employeRepository, SalonRepository salonRepository, PrestationRepository prestationRepository) {
        this.employeRepository = employeRepository;
        this.salonRepository = salonRepository;
        this.prestationRepository = prestationRepository;
    }

    @PostMapping("/salon/{salonId}")
    public Employe ajouterEmploye(@PathVariable Long salonId, @RequestBody Employe employe) {
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon non trouvé"));
        employe.setSalon(salon);
        return employeRepository.save(employe);
    }

    @PostMapping("/{employeId}/prestations/{prestationId}")
    public Employe assignerPrestation(@PathVariable Long employeId, @PathVariable Long prestationId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));
        
        Prestation prestation = prestationRepository.findById(prestationId)
                .orElseThrow(() -> new RuntimeException("Prestation non trouvée"));

        employe.getPrestations().add(prestation);
        return employeRepository.save(employe);
    }
}