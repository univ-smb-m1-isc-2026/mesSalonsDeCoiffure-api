package com.example.mesSalonsDeCoiffure_api.repositories;

import com.example.mesSalonsDeCoiffure_api.entities.Reservation;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest 
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReservationRepositoryTest {

    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;

    // Dans les tests, JUnit exige un @Autowired sur le constructeur
    @Autowired
    public ReservationRepositoryTest(ReservationRepository reservationRepository, UtilisateurRepository utilisateurRepository) {
        this.reservationRepository = reservationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Test
    void quandRechercheParTelephone_alorsRetourneLesBonnesReservations() {
        // 1. ARRANGEMENT
        Utilisateur client = new Utilisateur();
        client.setNom("Dupont");
        client.setTelephone("0600000000");
        utilisateurRepository.save(client);

        Reservation rdv = new Reservation();
        rdv.setDateHeureDebut(LocalDateTime.now().plusDays(1));
        rdv.setClient(client);
        reservationRepository.save(rdv);

        // 2. ACTION
        List<Reservation> resultats = reservationRepository.findByClientTelephoneOrderByDateHeureDebutDesc("0600000000");

        // 3. ASSERTION
        assertEquals(1, resultats.size(), "On devrait trouver 1 rendez-vous pour ce numéro");
        assertEquals("0600000000", resultats.get(0).getClient().getTelephone());
    }
}