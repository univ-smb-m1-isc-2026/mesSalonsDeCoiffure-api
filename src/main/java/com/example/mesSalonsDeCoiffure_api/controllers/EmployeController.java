package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.Employe;
import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.entities.Salon;
import com.example.mesSalonsDeCoiffure_api.repositories.EmployeRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.PrestationRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.SalonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employes")
@CrossOrigin(origins = "*")
public class EmployeController {

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private SalonRepository salonRepository;

    @Autowired
    private PrestationRepository prestationRepository;

    // 1. Embaucher un employé dans un salon
    @PostMapping("/salon/{salonId}")
    public Employe ajouterEmploye(@PathVariable Long salonId, @RequestBody Employe employe) {
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon non trouvé"));
        employe.setSalon(salon);
        return employeRepository.save(employe);
    }

    // 2. Assigner une prestation (compétence) à un employé
    @PostMapping("/{employeId}/prestations/{prestationId}")
    public Employe assignerPrestation(@PathVariable Long employeId, @PathVariable Long prestationId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));
        
        Prestation prestation = prestationRepository.findById(prestationId)
                .orElseThrow(() -> new RuntimeException("Prestation non trouvée"));

        // On ajoute la prestation à la liste de l'employé
        employe.getPrestationsProposees().add(prestation);
        return employeRepository.save(employe);
    }
}