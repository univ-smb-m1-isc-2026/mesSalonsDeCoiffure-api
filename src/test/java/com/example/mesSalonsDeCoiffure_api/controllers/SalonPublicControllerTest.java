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

    private final MockMvc mockMvc;

    // Dans les tests, JUnit exige un @Autowired sur le constructeur
    @Autowired
    public SalonPublicControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void quandVisiteurDemandeEmployes_alorsReponseOk() throws Exception {
        Long salonId = 1L;

        mockMvc.perform(get("/api/salons/" + salonId + "/employes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}