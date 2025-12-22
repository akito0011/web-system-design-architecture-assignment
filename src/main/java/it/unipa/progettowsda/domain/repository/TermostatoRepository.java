package it.unipa.progettowsda.domain.repository;
import it.unipa.progettowsda.domain.entity.Termostato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TermostatoRepository extends JpaRepository<Termostato, Integer> {
    List<Termostato> findByCameraId(Integer idCamera);
}
