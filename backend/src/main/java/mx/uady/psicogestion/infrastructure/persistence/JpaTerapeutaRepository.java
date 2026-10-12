package mx.uady.psicogestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaTerapeutaRepository extends JpaRepository<TerapeutaEntity, Integer> {
    Optional<TerapeutaEntity> findByCedulaProfesional(String cedula);
    Optional<TerapeutaEntity> findByCorreoElectronico(String correo);
    Optional<TerapeutaEntity> findByTelefono(String telefono);
}