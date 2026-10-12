package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.application.ports.out.ICitaRepository;
import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import mx.uady.psicogestion.domain.entities.EstadoCitaEnum;
import mx.uady.psicogestion.infrastructure.persistence.CitaEntity;
import mx.uady.psicogestion.infrastructure.persistence.JpaCitaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Component
public class CitaRepositoryAdapter implements ICitaRepository {

    private final JpaCitaRepository jpaRepository;
    private final JdbcTemplate jdbcTemplate;

    public CitaRepositoryAdapter(JpaCitaRepository jpaRepository, JdbcTemplate jdbcTemplate) {
        this.jpaRepository = jpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Cita guardar(Cita cita, BloqueHorario horario) {
        // 1. Guardar la cita principal
        CitaEntity entity = new CitaEntity();
        entity.setIdPaciente(cita.getIdPaciente());
        entity.setIdTerapeuta(cita.getIdTerapeuta());
        entity.setIdSala(cita.getIdSala());
        entity.setFolio(cita.getFolio());
        entity.setEstado(cita.getEstado().name());

        CitaEntity guardado = jpaRepository.save(entity);
        cita.setIdCita(guardado.getIdCita());

        // 2. Guardar el bloque horario y la tabla pivote
        if (horario != null) {
            jdbcTemplate.update("INSERT INTO bloque_horario (fecha, hora_inicio, hora_fin) VALUES (?, ?, ?)",
                    horario.getFecha(), horario.getHoraInicio(), horario.getHoraFin());

            Integer idBloque = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

            jdbcTemplate.update("INSERT INTO cita_bloque (id_cita, id_bloque) VALUES (?, ?)",
                    cita.getIdCita(), idBloque);
        }

        return cita;
    }

    @Override
    @Transactional
    public boolean buscarColisiones(Integer idTerapeuta, Integer idSala, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        long colisiones = jpaRepository.contarColisionesConBloqueoPesimista(idTerapeuta, idSala, fecha, horaInicio, horaFin);
        return colisiones > 0;
    }

    @Override
    public Optional<Cita> buscarPorFolio(String folio) {
        return jpaRepository.findByFolio(folio).map(e -> {
            Cita c = new Cita();
            c.setIdCita(e.getIdCita());
            c.setIdPaciente(e.getIdPaciente());
            c.setIdTerapeuta(e.getIdTerapeuta());
            c.setIdSala(e.getIdSala());
            c.setFolio(e.getFolio());
            c.setEstado(EstadoCitaEnum.valueOf(e.getEstado()));
            return c;
        });
    }

    @Override
    public Cita actualizar(Cita cita) {
        return guardar(cita, null);
    }
}