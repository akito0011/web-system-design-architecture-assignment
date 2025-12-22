package it.unipa.progettowsda.domain.repository;
import it.unipa.progettowsda.domain.entity.Camera;
import it.unipa.progettowsda.domain.entity.enumerazioni.StatoCamera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Integer> {
    List<Camera> findByStato(StatoCamera stato);

    @Query("SELECT DISTINCT c FROM Camera c " +
            "JOIN FETCH c.struttura s " +
            "LEFT JOIN FETCH s.serviziDisponibili " +
            "WHERE s.citta = :citta " +
            "AND c.capienza >= :ospiti " +
            "AND c.stato != 'MANUTENZIONE' " +
            "AND c.id NOT IN (" +
            "SELECT p.camera.id FROM Prenotazione p " +
            "WHERE p.stato != 'CANCELLATA' " +
            "AND ((p.dataCheckin < :checkout) AND (p.dataCheckout > :checkin))" +
            ")")
    List<Camera> findCamereDisponibili(
            @Param("citta") String citta,
            @Param("ospiti") Integer ospiti,
            @Param("checkin") LocalDate checkin,
            @Param("checkout") LocalDate checkout
    );
}
