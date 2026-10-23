package mx.uady.psicogestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface JpaCitaRepository extends JpaRepository<CitaEntity, Integer> {

    Optional<CitaEntity> findByFolio(String folio);

    @Query(value = "SELECT COUNT(c.id_cita) FROM cita c " +
            "JOIN cita_bloque cb ON c.id_cita = cb.id_cita " +
            "JOIN bloque_horario bh ON cb.id_bloque = bh.id_bloque " +
            "WHERE (c.id_terapeuta = :idTerapeuta OR c.id_sala = :idSala) " +
            "AND bh.fecha = :fecha " +
            "AND (bh.hora_inicio < :horaFin AND bh.hora_fin > :horaInicio) " +
            "AND c.estado != 'CANCELADA' FOR UPDATE", nativeQuery = true)
    long contarColisionesConBloqueoPesimista(
            @Param("idTerapeuta") Integer idTerapeuta,
            @Param("idSala") Integer idSala,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );

    @Modifying
    @Transactional
    @Query("UPDATE CitaEntity c SET c.estado = :estado WHERE c.idCita = :id")
    void actualizarEstado(@Param("id") Integer id, @Param("estado") String estado);
}