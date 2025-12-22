package it.unipa.progettowsda.domain.repository;

import it.unipa.progettowsda.domain.entity.Utente;
import it.unipa.progettowsda.domain.entity.enumerazioni.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    Optional<Utente> findByEmail(String email);

    boolean existsByEmail(String email);

    // METODO NUOVO AGGIUNTO
    List<Utente> findByRuolo(Ruolo ruolo);
}