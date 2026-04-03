package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.dto.UserUpdateDTO;
import com.example.mesSalonsDeCoiffure_api.entities.RendezVous;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<Utilisateur> getMonProfil(Authentication authentication) {
        return ResponseEntity.ok(userService.getMonProfil(authentication.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<Utilisateur> modifierMonProfil(@RequestBody UserUpdateDTO modifications, Authentication authentication) {
        return ResponseEntity.ok(userService.modifierMonProfil(modifications, authentication.getName()));
    }

    @GetMapping("/me/reservations")
    public ResponseEntity<List<RendezVous>> getMesRendezVous(Authentication authentication) {
        return ResponseEntity.ok(userService.getMesRendezVous(authentication.getName()));
    }
}