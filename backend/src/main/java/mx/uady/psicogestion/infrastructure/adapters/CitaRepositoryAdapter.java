
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
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CitaRepositoryAdapter implements ICitaRepository {

    private final JpaCitaRepository jpaRepository;
    private final JdbcTemplate jdbcTemplate;

    public CitaRepositoryAdapter(
            JpaCitaRepository jpaRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.jpaRepository = jpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================
    // GUARDAR CITA Y BLOQUE HORARIO
    // =========================================================

    @Override
    @Transactional
    public Cita guardar(Cita cita, BloqueHorario horario) {

        CitaEntity entity = new CitaEntity();

        if (cita.getIdCita() != null) {
            entity.setIdCita(cita.getIdCita());
        }

        entity.setIdPaciente(cita.getIdPaciente());
        entity.setIdTerapeuta(cita.getIdTerapeuta());
        entity.setIdSala(cita.getIdSala());
        entity.setFolio(cita.getFolio());
        entity.setNotas(cita.getNotas());
        entity.setEstado(cita.getEstado().name());

        CitaEntity guardado = jpaRepository.save(entity);
        cita.setIdCita(guardado.getIdCita());

        if (horario != null) {

            List<Integer> bloquesViejos =
                    jdbcTemplate.queryForList(
                            """
                            SELECT id_bloque
                            FROM cita_bloque
                            WHERE id_cita = ?
                            """,
                            Integer.class,
                            cita.getIdCita()
                    );

            // Eliminar relaciones de bloques anteriores.
            for (Integer viejoId : bloquesViejos) {

                jdbcTemplate.update(
                        """
                        DELETE FROM disponibilidad_sala
                        WHERE id_bloque = ?
                        """,
                        viejoId
                );

                jdbcTemplate.update(
                        """
                        DELETE FROM disponibilidad_terapeuta
                        WHERE id_bloque = ?
                        """,
                        viejoId
                );

                jdbcTemplate.update(
                        """
                        DELETE FROM cita_bloque
                        WHERE id_cita = ? AND id_bloque = ?
                        """,
                        cita.getIdCita(),
                        viejoId
                );

                jdbcTemplate.update(
                        """
                        DELETE FROM bloque_horario
                        WHERE id_bloque = ?
                        """,
                        viejoId
                );
            }

            // Insertar el nuevo bloque.
            jdbcTemplate.update(
                    """
                    INSERT INTO bloque_horario
                        (fecha, hora_inicio, hora_fin)
                    VALUES (?, ?, ?)
                    """,
                    horario.getFecha(),
                    horario.getHoraInicio(),
                    horario.getHoraFin()
            );

            Integer idBloque = jdbcTemplate.queryForObject(
                    "SELECT LAST_INSERT_ID()",
                    Integer.class
            );

            if (idBloque == null) {
                throw new IllegalStateException(
                        "No se pudo obtener el ID del bloque horario."
                );
            }

            // Asociar la cita al bloque.
            jdbcTemplate.update(
                    """
                    INSERT INTO cita_bloque
                        (id_cita, id_bloque)
                    VALUES (?, ?)
                    """,
                    cita.getIdCita(),
                    idBloque
            );

            // Registrar disponibilidad de la sala.
            jdbcTemplate.update(
                    """
                    INSERT INTO disponibilidad_sala
                        (id_sala, id_bloque)
                    VALUES (?, ?)
                    """,
                    cita.getIdSala(),
                    idBloque
            );

            // Registrar disponibilidad del terapeuta.
            jdbcTemplate.update(
                    """
                    INSERT INTO disponibilidad_terapeuta
                        (id_terapeuta, id_bloque)
                    VALUES (?, ?)
                    """,
                    cita.getIdTerapeuta(),
                    idBloque
            );
        }

        return cita;
    }

    // =========================================================
    // BUSCAR COLISIONES
    // =========================================================

    @Override
    public boolean buscarColisiones(
            Integer idTerapeuta,
            Integer idSala,
            Integer idPaciente,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            Integer idCitaExcluida
    ) {

        String sql = """
                SELECT COUNT(DISTINCT c.id_cita)
                FROM cita c
                JOIN cita_bloque cb
                    ON c.id_cita = cb.id_cita
                JOIN bloque_horario bh
                    ON cb.id_bloque = bh.id_bloque
                WHERE (
                    c.id_terapeuta = ?
                    OR c.id_sala = ?
                    OR c.id_paciente = ?
                )
                AND bh.fecha = ?
                AND bh.hora_inicio < ?
                AND bh.hora_fin > ?
                AND c.estado <> 'CANCELADA'
                AND c.id_cita <> ?
                """;

        Long cantidad = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                idTerapeuta,
                idSala,
                idPaciente,
                fecha,
                horaFin,
                horaInicio,
                idCitaExcluida
        );

        return cantidad != null && cantidad > 0;
    }

    // =========================================================
    // BLOQUEAR RECURSOS - CREACION
    // =========================================================

    @Override
    public void bloquearRecursos(
            Integer idPaciente,
            Integer idTerapeuta,
            Integer idSala
    ) {

        bloquearRecursos(
                List.of(idPaciente),
                List.of(idTerapeuta),
                List.of(idSala)
        );
    }

    // =========================================================
    // BLOQUEAR RECURSOS - CREACION Y MODIFICACION
    // =========================================================

    @Override
    public void bloquearRecursos(
            List<Integer> pacientes,
            List<Integer> terapeutas,
            List<Integer> salas
    ) {

        // 1. Bloquear pacientes en orden ascendente.
        for (Integer id : ordenarIds(pacientes)) {

            jdbcTemplate.queryForObject(
                    """
                    SELECT id_paciente
                    FROM paciente
                    WHERE id_paciente = ?
                    FOR UPDATE
                    """,
                    Integer.class,
                    id
            );
        }

        // 2. Bloquear terapeutas en orden ascendente.
        for (Integer id : ordenarIds(terapeutas)) {

            jdbcTemplate.queryForObject(
                    """
                    SELECT id_terapeuta
                    FROM terapeuta
                    WHERE id_terapeuta = ?
                    FOR UPDATE
                    """,
                    Integer.class,
                    id
            );
        }

        // 3. Bloquear salas en orden ascendente.
        for (Integer id : ordenarIds(salas)) {

            jdbcTemplate.queryForObject(
                    """
                    SELECT id_sala
                    FROM sala
                    WHERE id_sala = ?
                    FOR UPDATE
                    """,
                    Integer.class,
                    id
            );
        }
    }

    private List<Integer> ordenarIds(List<Integer> ids) {

        if (ids == null || ids.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "Los identificadores de recursos son obligatorios."
            );
        }

        return ids.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // =========================================================
    // BLOQUEAR UNA CITA EXISTENTE
    // =========================================================

    @Override
    public Optional<Cita> bloquearCita(Integer idCita) {

        List<Cita> citas = jdbcTemplate.query(
                """
                SELECT
                    id_cita,
                    id_paciente,
                    id_terapeuta,
                    id_sala,
                    folio,
                    notas,
                    estado
                FROM cita
                WHERE id_cita = ?
                FOR UPDATE
                """,
                (rs, rowNum) -> {

                    Cita cita = new Cita();

                    cita.setIdCita(
                            rs.getInt("id_cita")
                    );

                    cita.setIdPaciente(
                            rs.getInt("id_paciente")
                    );

                    cita.setIdTerapeuta(
                            rs.getInt("id_terapeuta")
                    );

                    cita.setIdSala(
                            rs.getInt("id_sala")
                    );

                    cita.setFolio(
                            rs.getString("folio")
                    );

                    cita.setNotas(
                            rs.getString("notas")
                    );

                    cita.setEstado(
                            EstadoCitaEnum.valueOf(
                                    rs.getString("estado")
                                            .toUpperCase()
                            )
                    );

                    return cita;
                },
                idCita
        );

        return citas.stream().findFirst();
    }

    // =========================================================
    // BUSCAR CITA POR FOLIO
    // =========================================================

    @Override
    public Optional<Cita> buscarPorFolio(String folio) {

        return jpaRepository.findByFolio(folio)
                .map(this::mapearADominio);
    }

    // =========================================================
    // BUSCAR CITA POR ID
    // =========================================================

    @Override
    public Optional<Cita> buscarPorId(Integer idCita) {

        return jpaRepository.findById(idCita)
                .map(this::mapearADominio);
    }

    // =========================================================
    // ACTUALIZAR CITA
    // =========================================================

    @Override
    public Cita actualizar(Cita cita) {

        return guardar(cita, null);
    }

    // =========================================================
    // OBTENER TODAS LAS CITAS
    // =========================================================

    @Override
    public List<Cita> obtenerTodas() {

        return jpaRepository.findAll()
                .stream()
                .map(this::mapearADominio)
                .collect(Collectors.toList());
    }

    // =========================================================
    // OBTENER TODAS LAS CITAS CON DETALLES
    // =========================================================

    @Override
    public List<Map<String, Object>> obtenerTodasConDetalles() {

        String sql = """
                SELECT
                    c.id_cita,
                    c.folio,
                    c.estado,
                    c.notas,
                    COALESCE(
                        p.nombre_completo,
                        'Desconocido'
                    ) AS paciente,
                    COALESCE(
                        t.nombre_completo,
                        'Sin asignar'
                    ) AS terapeuta,
                    COALESCE(
                        s.nombre_sala,
                        'Sin sala'
                    ) AS sala,
                    bh.fecha,
                    bh.hora_inicio,
                    bh.hora_fin
                FROM cita c
                LEFT JOIN paciente p
                    ON c.id_paciente = p.id_paciente
                LEFT JOIN terapeuta t
                    ON c.id_terapeuta = t.id_terapeuta
                LEFT JOIN sala s
                    ON c.id_sala = s.id_sala
                LEFT JOIN cita_bloque cb
                    ON c.id_cita = cb.id_cita
                LEFT JOIN bloque_horario bh
                    ON cb.id_bloque = bh.id_bloque
                """;

        return jdbcTemplate.queryForList(sql);
    }

    // =========================================================
    // CAMBIAR ESTADO DE UNA CITA
    // =========================================================

    @Override
    @Transactional
    public void cambiarEstado(
            Integer idCita,
            EstadoCitaEnum estado
    ) {

        jpaRepository.actualizarEstado(
                idCita,
                estado.name()
        );
    }

    // =========================================================
    // MAPEAR ENTIDAD JPA A DOMINIO
    // =========================================================

    private Cita mapearADominio(CitaEntity e) {

        Cita cita = new Cita();

        cita.setIdCita(e.getIdCita());
        cita.setIdPaciente(e.getIdPaciente());
        cita.setIdTerapeuta(e.getIdTerapeuta());
        cita.setIdSala(e.getIdSala());
        cita.setFolio(e.getFolio());
        cita.setNotas(e.getNotas());

        cita.setEstado(
                EstadoCitaEnum.valueOf(
                        e.getEstado().toUpperCase()
                )
        );

        return cita;
    }

    @Override
    public Optional<BloqueHorario> buscarHorarioPorCita(Integer idCita) {

        List<BloqueHorario> horarios = jdbcTemplate.query(
                """
                SELECT bh.id_bloque,
                    bh.fecha,
                    bh.hora_inicio,
                    bh.hora_fin
                FROM cita_bloque cb
                JOIN bloque_horario bh
                ON bh.id_bloque = cb.id_bloque
                WHERE cb.id_cita = ?
                """,
                (rs, fila) -> {
                    BloqueHorario h = new BloqueHorario();
                    h.setIdBloque(rs.getInt("id_bloque"));
                    h.setFecha(
                            rs.getDate("fecha").toLocalDate()
                    );
                    h.setHoraInicio(
                            rs.getTime("hora_inicio").toLocalTime()
                    );
                    h.setHoraFin(
                            rs.getTime("hora_fin").toLocalTime()
                    );
                    return h;
                },
                idCita
        );

        if (horarios.size() > 1) {
            throw new IllegalStateException(
                    "La cita tiene más de un bloque horario."
            );
        }

        return horarios.stream().findFirst();
    }
}
