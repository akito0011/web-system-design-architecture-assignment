package it.unipa.progettowsda.domain.repository;
import it.unipa.progettowsda.domain.entity.Tapparella;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TapparellaRepository extends JpaRepository<Tapparella, Integer> {
    List<Tapparella> findByCameraId(Integer idCamera);

    Integer id(Integer id);
}
