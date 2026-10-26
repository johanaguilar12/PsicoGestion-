package mx.uady.psicogestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface JpaSecretariaRepository extends JpaRepository<SecretariaEntity, Integer> {
    Optional<SecretariaEntity> findByCorreoElectronico(String correoElectronico);
    Optional<SecretariaEntity> findByTelefono(String telefono);

    @Query("SELECT s FROM SecretariaEntity s WHERE LOWER(s.correoElectronico) = LOWER(:identificador) OR s.telefono = :identificador")
    Optional<SecretariaEntity> findByIdentificador(@Param("identificador") String identificador);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE SecretariaEntity s SET s.activo = CASE WHEN s.activo = true THEN false ELSE true END WHERE s.idSecretaria = :id")
    void alternarEstatus(@Param("id") Integer id);
}