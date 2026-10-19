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
public interface JpaPacienteRepository extends JpaRepository<PacienteEntity, Integer> {

    Optional<PacienteEntity> findByCurp(String curp);

    Optional<PacienteEntity> findByTelefono(String telefono);

    List<PacienteEntity> findByActivoTrue();


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE PacienteEntity p SET p.activo = false WHERE p.idPaciente = :id")
    void desactivarPacienteFuerzaBruta(@Param("id") Integer id);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("DELETE FROM PacienteEntity p WHERE p.idPaciente = :id")
    void eliminarPacienteFuerzaBruta(@Param("id") Integer id);
}