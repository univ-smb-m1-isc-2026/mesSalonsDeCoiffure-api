package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.entities.Salon;
import com.example.mesSalonsDeCoiffure_api.repositories.SalonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salons")
public class SalonController {

    private final SalonRepository salonRepository;

    public SalonController(SalonRepository salonRepository) {
        this.salonRepository = salonRepository;
    }

    @GetMapping
    public List<Salon> getAllSalons() {
        return salonRepository.findAll();
    }

    @PostMapping
    public Salon createSalon(@RequestBody Salon salon) {
        return salonRepository.save(salon);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Salon> getSalonById(@PathVariable Long id) {
        return salonRepository.findById(id)
                .map(salon -> ResponseEntity.ok().body(salon))
                .orElse(ResponseEntity.notFound().build());
    }
}