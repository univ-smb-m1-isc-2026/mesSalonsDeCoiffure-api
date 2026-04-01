package com.example.mesSalonsDeCoiffure_api.repositories;

import com.example.mesSalonsDeCoiffure_api.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // 👇 On utilise une requête SQL explicite (HQL) pour être sûr qu'il n'y ait aucun doute
    // sur la façon d'extraire la date à partir du LocalDateTime pour la comparer !
    @Query("SELECT r FROM Reservation r WHERE r.employe.id = :employeId AND CAST(r.dateHeureDebut AS date) = :date")
    List<Reservation> findByEmployeIdAndDate(@Param("employeId") Long employeId, @Param("date") LocalDate date);
    
    // 👇 On navigue dans l'objet Client, puis on trie sur le nouveau champ dateHeureDebut
    List<Reservation> findByClientTelephoneOrderByDateHeureDebutDesc(String telephone);
}