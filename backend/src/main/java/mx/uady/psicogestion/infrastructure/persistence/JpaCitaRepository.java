package mx.uady.psicogestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface JpaCitaRepository extends JpaRepository<CitaEntity, Integer> {

    Optional<CitaEntity> findByFolio(String folio);

    // Bloqueo pesimista: Evita dobles reservas leyendo y bloqueando los horarios en la BD
    @Query(value = "SELECT COUNT(c.id_cita) FROM cita c " +
            "JOIN cita_bloque cb ON c.id_cita = cb.id_cita " +
            "JOIN bloque_horario bh ON cb.id_bloque = bh.id_bloque " +
            "WHERE (c.id_terapeuta = :idTerapeuta OR c.id_sala = :idSala) " +
            "AND bh.fecha = :fecha " +
            "AND (bh.hora_inicio < :horaFin AND bh.hora_fin > :horaInicio) " +
            "AND c.estado != 'Cancelada' FOR UPDATE", nativeQuery = true)
    long contarColisionesConBloqueoPesimista(
            @Param("idTerapeuta") Integer idTerapeuta,
            @Param("idSala") Integer idSala,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );
}