package it.unipa.progettowsda.domain.repository;
import it.unipa.progettowsda.domain.entity.Servizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServizioRepository extends JpaRepository<Servizio, Integer> {
}