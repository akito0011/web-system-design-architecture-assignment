package it.unipa.progettowsda.domain.repository;
import it.unipa.progettowsda.domain.entity.Ospite;
import it.unipa.progettowsda.domain.entity.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OspiteRepository extends JpaRepository<Ospite, Integer> {
    List<Ospite> findByPrenotazione(Prenotazione prenotazione);
}