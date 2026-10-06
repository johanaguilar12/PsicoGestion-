package mx.uady.psicogestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaSecretariaRepository extends JpaRepository<SecretariaEntity, Integer> {
    Optional<SecretariaEntity> findByCorreoElectronico(String correoElectronico);
    Optional<SecretariaEntity> findByTelefono(String telefono);
}