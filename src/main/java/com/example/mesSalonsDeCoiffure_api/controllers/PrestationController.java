package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.entities.Salon;
import com.example.mesSalonsDeCoiffure_api.repositories.PrestationRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.SalonRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestations")
public class PrestationController {

    private final PrestationRepository prestationRepository;
    private final SalonRepository salonRepository;

    public PrestationController(PrestationRepository prestationRepository, SalonRepository salonRepository) {
        this.prestationRepository = prestationRepository;
        this.salonRepository = salonRepository;
    }

    @PostMapping("/salon/{salonId}")
    public Prestation ajouterPrestation(@PathVariable Long salonId, @RequestBody Prestation prestation) {
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon non trouvé !"));
        
        prestation.setSalon(salon);
        return prestationRepository.save(prestation);
    }

    @GetMapping
    public List<Prestation> getAllPrestations() {
        return prestationRepository.findAll();
    }
}