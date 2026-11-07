
package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.domain.entities.BloqueHorario;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
public class BloqueoReservaService {

    private final JdbcTemplate jdbc;

    public BloqueoReservaService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record Solicitud(
            Integer paciente,
            Integer terapeuta,
            Integer sala,
            BloqueHorario horario
    ) {}

    private record Clave(
            String tipo,
            Integer id,
            LocalDate fecha,
            LocalTime hora
    ) {}

    public void validarHorario(BloqueHorario h) {
        if (h == null || h.getFecha() == null ||
                h.getHoraInicio() == null ||
                h.getHoraFin() == null) {
            throw new IllegalArgumentException(
                    "La fecha y las horas son obligatorias."
            );
        }

        LocalTime inicio = h.getHoraInicio();
        LocalTime fin = h.getHoraFin();

        if (inicio.getSecond() != 0 ||
                inicio.getNano() != 0 ||
                (inicio.getMinute() != 0 &&
                 inicio.getMinute() != 30)) {
            throw new IllegalArgumentException(
                    "Las citas deben comenzar en :00 o :30."
            );
        }

        if (!inicio.plusHours(1).equals(fin)) {
            throw new IllegalArgumentException(
                    "Cada cita debe durar exactamente una hora."
            );
        }

        if (!h.validarRangoOperativo()) {
            throw new IllegalArgumentException(
                    "Horario operativo inválido."
            );
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void bloquear(Collection<Solicitud> solicitudes) {

        List<Clave> claves = new ArrayList<>();

        for (Solicitud s : solicitudes) {
            validarHorario(s.horario());

            if (s.paciente() == null ||
                    s.terapeuta() == null ||
                    s.sala() == null) {
                throw new IllegalArgumentException(
                        "Los identificadores son obligatorios."
                );
            }

            LocalDate fecha = s.horario().getFecha();
            LocalTime inicio = s.horario().getHoraInicio();

            // Una hora incluye dos bloques de 30 minutos.
            for (int i = 0; i < 2; i++) {
                LocalTime franja = inicio.plusMinutes(30L * i);

                claves.add(new Clave(
                        "PACIENTE", s.paciente(), fecha, franja
                ));

                claves.add(new Clave(
                        "TERAPEUTA", s.terapeuta(), fecha, franja
                ));

                claves.add(new Clave(
                        "SALA", s.sala(), fecha, franja
                ));
            }
        }

        // Todas las transacciones usan el mismo orden.
        claves = claves.stream()
                .distinct()
                .sorted(
                        Comparator.comparing(Clave::tipo)
                                .thenComparing(Clave::id)
                                .thenComparing(Clave::fecha)
                                .thenComparing(Clave::hora)
                )
                .toList();

        for (Clave clave : claves) {

            // Crear fila si todavía no existe.
            jdbc.update(
                    """
                    INSERT IGNORE INTO bloqueo_reserva
                      (tipo_recurso, id_recurso, fecha, hora)
                    VALUES (?, ?, ?, ?)
                    """,
                    clave.tipo(),
                    clave.id(),
                    clave.fecha(),
                    clave.hora()
            );

            // Mantener bloqueada la clave hasta COMMIT/ROLLBACK.
            Integer bloqueada = jdbc.queryForObject(
                    """
                    SELECT id_recurso
                    FROM bloqueo_reserva
                    WHERE tipo_recurso = ?
                      AND id_recurso = ?
                      AND fecha = ?
                      AND hora = ?
                    FOR UPDATE
                    """,
                    Integer.class,
                    clave.tipo(),
                    clave.id(),
                    clave.fecha(),
                    clave.hora()
            );

            if (!clave.id().equals(bloqueada)) {
                throw new IllegalStateException(
                        "No se pudo bloquear la franja."
                );
            }
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void bloquear(Solicitud... solicitudes) {
        bloquear(List.of(solicitudes));
    }
}
