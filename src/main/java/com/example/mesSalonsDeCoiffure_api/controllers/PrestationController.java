package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.Prestation;
import com.example.mesSalonsDeCoiffure_api.entities.Salon;
import com.example.mesSalonsDeCoiffure_api.repositories.PrestationRepository;
import com.example.mesSalonsDeCoiffure_api.repositories.SalonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestations")
@CrossOrigin(origins = "*")
public class PrestationController {

    @Autowired
    private PrestationRepository prestationRepository;

    @Autowired
    private SalonRepository salonRepository;

    // Ajouter une prestation à un salon spécifique
    @PostMapping("/salon/{salonId}")
    public Prestation ajouterPrestation(@PathVariable Long salonId, @RequestBody Prestation prestation) {
        // 1. On cherche le salon en base de données
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon non trouvé !"));
        
        // 2. On lie le salon à la prestation
        prestation.setSalon(salon);
        
        // 3. On sauvegarde la prestation
        return prestationRepository.save(prestation);
    }

    // Récupérer toutes les prestations
    @GetMapping
    public List<Prestation> getAllPrestations() {
        return prestationRepository.findAll();
    }
}