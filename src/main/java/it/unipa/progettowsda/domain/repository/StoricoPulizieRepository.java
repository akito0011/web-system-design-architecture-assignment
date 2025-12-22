package it.unipa.progettowsda.domain.repository;

import it.unipa.progettowsda.domain.entity.StoricoPulizie;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoricoPulizieRepository extends JpaRepository<StoricoPulizie, Integer> {
    // Ci serve per mostrare la lista ordinata dalla più recente
    List<StoricoPulizie> findAll(Sort sort);
}