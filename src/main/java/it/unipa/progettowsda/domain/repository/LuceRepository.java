package it.unipa.progettowsda.domain.repository;
import it.unipa.progettowsda.domain.entity.Luce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LuceRepository extends JpaRepository<Luce, Integer> {
    List<Luce> findByCameraId(Integer idCamera);
}
