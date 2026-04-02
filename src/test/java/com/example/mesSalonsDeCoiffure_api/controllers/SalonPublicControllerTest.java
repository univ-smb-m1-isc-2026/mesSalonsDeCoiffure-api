package com.example.mesSalonsDeCoiffure_api.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc 
class SalonPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void quandVisiteurDemandeEmployes_alorsReponseOk() throws Exception {
        // Étant donné l'ID d'un salon (on suppose que le salon ID 1 existe dans ta base de test)
        Long salonId = 1L;

        // Quand on fait une requête GET publique
        mockMvc.perform(get("/api/salons/" + salonId + "/employes"))
                // Alors on attend un code 200 OK (Pas de 403 Forbidden !)
                .andExpect(status().isOk())
                // Et on s'attend à recevoir une liste (un tableau JSON)
                .andExpect(jsonPath("$").isArray());
    }
}