package it.unipa.progettowsda.domain.repository;
import it.unipa.progettowsda.domain.entity.Camera;
import it.unipa.progettowsda.domain.entity.Prenotazione;
import it.unipa.progettowsda.domain.entity.Utente;
import it.unipa.progettowsda.domain.entity.enumerazioni.StatoPrenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Integer> {
    // Trova le prenotazioni di un utente specifico
    List<Prenotazione> findByUtenteId(Integer idUtente);

    //DASHBOARD GESTORE

    // Arrivi di oggi: (Data Checkin = Oggi AND Stato = CONFERMATA)
    List<Prenotazione> findByDataCheckinAndStato(LocalDate dataCheckin, StatoPrenotazione stato);

    //Partenze di oggi: (Data Checkout = Oggi AND Stato = IN_CORSO)
    List<Prenotazione> findByDataCheckoutAndStato(LocalDate dataCheckout, StatoPrenotazione stato);

    //Ospiti attualmente in struttura: (Stato = IN_CORSO)
    List<Prenotazione> findByStato(StatoPrenotazione stato);

    // Per il report XML della giornata: (Arrivi previsti + Arrivi già check-innati oggi)
    // Questa query prende tutte le prenotazioni che hanno checkin oggi, indipendentemente dallo stato (purché non cancellate)
    @Query("SELECT p FROM Prenotazione p WHERE p.dataCheckin = :data AND p.stato != 'CANCELLATA'")
    List<Prenotazione> findAllByDataCheckin(LocalDate data);

    // Funzione per avere lista di tutte le prenotazioni valide e non cancellate
    @Query("SELECT p FROM Prenotazione p WHERE p.camera = :camera " +
            "AND p.stato != 'CANCELLATA' " +
            "AND (p.dataCheckin < :checkout AND p.dataCheckout > :checkin)")
    List<Prenotazione> findSovrapposizioni(Camera camera, LocalDate checkin, LocalDate checkout);
}