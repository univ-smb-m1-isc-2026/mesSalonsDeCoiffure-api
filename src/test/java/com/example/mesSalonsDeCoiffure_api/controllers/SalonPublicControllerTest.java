package com.example.mesSalonsDeCoiffure_api.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 🌟 LES VRAIS IMPORTS SPRING BOOT 4 🌟
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class SalonPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void quandVisiteurDemandeEmployes_alorsReponseOk() throws Exception {
        Long salonId = 1L;

        mockMvc.perform(get("/api/salons/" + salonId + "/employes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}