package it.unipa.progettowsda.domain.repository;

import it.unipa.progettowsda.domain.entity.Struttura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrutturaRepository extends JpaRepository<Struttura, Integer> {
}
