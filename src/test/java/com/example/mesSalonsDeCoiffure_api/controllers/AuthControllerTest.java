package com.example.mesSalonsDeCoiffure_api.controllers;

import com.example.mesSalonsDeCoiffure_api.dto.AuthRequestDTO;
import com.example.mesSalonsDeCoiffure_api.dto.RegisterRequestDTO;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import com.example.mesSalonsDeCoiffure_api.repositories.UtilisateurRepository;
import com.example.mesSalonsDeCoiffure_api.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UtilisateurRepository utilisateurRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtService jwtService;

    private ObjectMapper objectMapper = new ObjectMapper();

    // ==========================================
    // TESTS DU REGISTER (INSCRIPTION)
    // ==========================================

    @Test
    void quandEmailDejaUtilise_alorsRetourneErreur400() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("deja.pris@test.com");
        request.setMotDePasse("12345");

        when(utilisateurRepository.findByEmail(anyString())).thenReturn(Optional.of(new Utilisateur()));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cet email est déjà utilisé."));
    }

    @Test
    void quandInscriptionReussie_avecRoleSpecifique_alorsCreeCompte() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("nouveau@test.com");
        request.setMotDePasse("12345");
        request.setRole("ADMIN"); // Test de la branche 

        when(utilisateurRepository.findByEmail("nouveau@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("12345")).thenReturn("mdp_encode");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArguments()[0]);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void quandInscriptionReussie_sansRole_alorsRoleUserParDefaut() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("client@test.com");
        request.setMotDePasse("12345");
        // Pas de rôle défini (null), on teste la branche "else"

        when(utilisateurRepository.findByEmail("client@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("12345")).thenReturn("mdp_encode");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArguments()[0]);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // ==========================================
    // TESTS DU LOGIN (CONNEXION)
    // ==========================================

    @Test
    void quandLoginValide_alorsRetourneToken() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("valid@test.com");
        request.setMotDePasse("monMdpSecret");

        Utilisateur user = new Utilisateur();
        user.setEmail("valid@test.com");
        user.setMotDePasse("mdp_hash_en_bdd");
        user.setRole("USER");

        when(utilisateurRepository.findByEmail("valid@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("monMdpSecret", "mdp_hash_en_bdd")).thenReturn(true);
        when(jwtService.generateToken("valid@test.com", "USER")).thenReturn("super-token-jwt");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("super-token-jwt"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void quandLoginMauvaisMotDePasse_alorsRetourne401() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("valid@test.com");
        request.setMotDePasse("faux_mdp");

        Utilisateur user = new Utilisateur();
        user.setMotDePasse("mdp_hash_en_bdd");

        when(utilisateurRepository.findByEmail("valid@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("faux_mdp", "mdp_hash_en_bdd")).thenReturn(false); // Refusé !

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email ou mot de passe incorrect."));
    }

    @Test
    void quandLoginEmailIntrouvable_alorsRetourne401() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("inconnu@test.com");
        request.setMotDePasse("12345");

        // L'email n'existe pas en base de données
        when(utilisateurRepository.findByEmail("inconnu@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}