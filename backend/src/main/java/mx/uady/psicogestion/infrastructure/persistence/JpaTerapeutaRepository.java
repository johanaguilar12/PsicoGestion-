package mx.uady.psicogestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaTerapeutaRepository extends JpaRepository<TerapeutaEntity, Integer> {
    Optional<TerapeutaEntity> findByCedulaProfesional(String cedula);
    Optional<TerapeutaEntity> findByCorreoElectronico(String correo);
    Optional<TerapeutaEntity> findByTelefono(String telefono);

    List<TerapeutaEntity> findByActivoTrue();


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE TerapeutaEntity t SET t.activo = CASE WHEN t.activo = true THEN false ELSE true END WHERE t.idTerapeuta = :id")
    void desactivarTerapeuta(@Param("id") Integer id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("DELETE FROM TerapeutaEntity t WHERE t.idTerapeuta = :id")
    void eliminarTerapeuta(@Param("id") Integer id);
}