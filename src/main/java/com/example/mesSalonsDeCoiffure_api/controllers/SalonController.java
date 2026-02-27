package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.Salon;
import com.example.mesSalonsDeCoiffure_api.repositories.SalonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salons")
@CrossOrigin(origins = "*") // ⚠️ TRES IMPORTANT : Autorise Angular (qui tourne sur un autre port) à appeler cette API
public class SalonController {

    @Autowired
    private SalonRepository salonRepository;

    // 1. Récupérer tous les salons (pour la liste et la carte)
    @GetMapping
    public List<Salon> getAllSalons() {
        return salonRepository.findAll();
    }

    // 2. Créer un nouveau salon (Pour la partie Admin)
    @PostMapping
    public Salon createSalon(@RequestBody Salon salon) {
        return salonRepository.save(salon);
    }

    // 3. Récupérer un salon spécifique par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Salon> getSalonById(@PathVariable Long id) {
        return salonRepository.findById(id)
                .map(salon -> ResponseEntity.ok().body(salon))
                .orElse(ResponseEntity.notFound().build());
    }
}