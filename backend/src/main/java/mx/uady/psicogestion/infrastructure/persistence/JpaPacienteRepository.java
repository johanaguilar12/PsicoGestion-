package mx.uady.psicogestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPacienteRepository extends JpaRepository<PacienteEntity, Integer> {
    Optional<PacienteEntity> findByCurp(String curp);
    Optional<PacienteEntity> findByTelefono(String telefono);
    List<PacienteEntity> findByActivoTrue(); // Solo trae los que no han sido dados de baja
}