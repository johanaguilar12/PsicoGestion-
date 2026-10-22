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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        CitaEntity entity = new CitaEntity();
        if (cita.getIdCita() != null) entity.setIdCita(cita.getIdCita());
        entity.setIdPaciente(cita.getIdPaciente());
        entity.setIdTerapeuta(cita.getIdTerapeuta());
        entity.setIdSala(cita.getIdSala());
        entity.setFolio(cita.getFolio());
        entity.setNotas(cita.getNotas());
        entity.setEstado(cita.getEstado().name());

        CitaEntity guardado = jpaRepository.save(entity);
        cita.setIdCita(guardado.getIdCita());

        if (horario != null) {
            List<Integer> bloquesViejos = jdbcTemplate.queryForList("SELECT id_bloque FROM cita_bloque WHERE id_cita = ?", Integer.class, cita.getIdCita());
            for (Integer viejoId : bloquesViejos) {
                jdbcTemplate.update("DELETE FROM disponibilidad_sala WHERE id_bloque = ?", viejoId);
                jdbcTemplate.update("DELETE FROM disponibilidad_terapeuta WHERE id_bloque = ?", viejoId);
                jdbcTemplate.update("DELETE FROM cita_bloque WHERE id_bloque = ?", viejoId);
                jdbcTemplate.update("DELETE FROM bloque_horario WHERE id_bloque = ?", viejoId);
            }

            jdbcTemplate.update("INSERT INTO bloque_horario (fecha, hora_inicio, hora_fin) VALUES (?, ?, ?)",
                    horario.getFecha(), horario.getHoraInicio(), horario.getHoraFin());
            Integer idBloque = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

            jdbcTemplate.update("INSERT INTO cita_bloque (id_cita, id_bloque) VALUES (?, ?)", cita.getIdCita(), idBloque);

            jdbcTemplate.update("INSERT INTO disponibilidad_sala (id_sala, id_bloque) VALUES (?, ?)", cita.getIdSala(), idBloque);
            jdbcTemplate.update("INSERT INTO disponibilidad_terapeuta (id_terapeuta, id_bloque) VALUES (?, ?)", cita.getIdTerapeuta(), idBloque);
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
        return jpaRepository.findByFolio(folio).map(this::mapearADominio);
    }

    @Override
    public Optional<Cita> buscarPorId(Integer idCita) {
        return jpaRepository.findById(idCita).map(this::mapearADominio);
    }

    @Override
    public Cita actualizar(Cita cita) {
        return guardar(cita, null);
    }

    @Override
    public List<Cita> obtenerTodas() {
        return jpaRepository.findAll().stream().map(this::mapearADominio).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> obtenerTodasConDetalles() {
        String sql = "SELECT c.id_cita, c.folio, c.estado, c.notas, " +
                "COALESCE(p.nombre_completo, 'Desconocido') as paciente, " +
                "COALESCE(t.nombre_completo, 'Sin asignar') as terapeuta, " +
                "COALESCE(s.nombre_sala, 'Sin sala') as sala, " +
                "bh.fecha, bh.hora_inicio, bh.hora_fin " +
                "FROM cita c " +
                "LEFT JOIN paciente p ON c.id_paciente = p.id_paciente " +
                "LEFT JOIN terapeuta t ON c.id_terapeuta = t.id_terapeuta " +
                "LEFT JOIN sala s ON c.id_sala = s.id_sala " +
                "LEFT JOIN cita_bloque cb ON c.id_cita = cb.id_cita " +
                "LEFT JOIN bloque_horario bh ON cb.id_bloque = bh.id_bloque";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    @Transactional
    public void cambiarEstado(Integer idCita, EstadoCitaEnum estado) {
        jpaRepository.actualizarEstado(idCita, estado.name());
    }

    private Cita mapearADominio(CitaEntity e) {
        Cita c = new Cita();
        c.setIdCita(e.getIdCita());
        c.setIdPaciente(e.getIdPaciente());
        c.setIdTerapeuta(e.getIdTerapeuta());
        c.setIdSala(e.getIdSala());
        c.setFolio(e.getFolio());
        c.setNotas(e.getNotas());
        c.setEstado(EstadoCitaEnum.valueOf(e.getEstado().toUpperCase()));
        return c;
    }
}