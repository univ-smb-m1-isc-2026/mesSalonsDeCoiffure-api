package com.example.mesSalonsDeCoiffure_api.repositories;

import com.example.mesSalonsDeCoiffure_api.entities.Reservation;
import com.example.mesSalonsDeCoiffure_api.entities.Utilisateur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest // 👈 Sans rien d'autre, il utilise H2 automatiquement !
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Test
    void quandRechercheParTelephone_alorsRetourneLesBonnesReservations() {
        Utilisateur client = new Utilisateur();
        client.setNom("Dupont");
        client.setTelephone("0600000000");
        utilisateurRepository.save(client);

        Reservation rdv = new Reservation();
        rdv.setDateHeureDebut(LocalDateTime.now().plusDays(1));
        rdv.setClient(client);
        reservationRepository.save(rdv);

        List<Reservation> resultats = reservationRepository.findByClientTelephoneOrderByDateHeureDebutDesc("0600000000");

        assertEquals(1, resultats.size(), "On devrait trouver 1 rendez-vous");
        assertEquals("0600000000", resultats.get(0).getClient().getTelephone());
    }
}