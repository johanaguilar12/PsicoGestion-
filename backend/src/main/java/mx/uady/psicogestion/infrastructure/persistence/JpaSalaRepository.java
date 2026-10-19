package mx.uady.psicogestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaSalaRepository extends JpaRepository<SalaEntity, Integer> {
    Optional<SalaEntity> findByNombreSala(String nombreSala);
}