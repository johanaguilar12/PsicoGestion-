
package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import mx.uady.psicogestion.domain.entities.EstadoCitaEnum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
public class CitaConcurrencyTest {

    @Autowired
    private AgendarCitaUseCase agendarCitaUseCase;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final int TOTAL = 20;
    private static final int TERAPEUTA = 6;
    private static final int SALA = 3;

    private static final LocalDate FECHA =
            LocalDate.of(2026, 11, 18);

    private static final LocalTime INICIO =
            LocalTime.of(11, 0);

    private static final LocalTime FIN =
            LocalTime.of(12, 0);

    private int contarCitasActivas() {

        String sql = """
            SELECT COUNT(DISTINCT c.id_cita)
            FROM cita c
            JOIN cita_bloque cb
                ON cb.id_cita = c.id_cita
            JOIN bloque_horario b
                ON b.id_bloque = cb.id_bloque
            WHERE b.fecha = ?
              AND b.hora_inicio < ?
              AND b.hora_fin > ?
              AND (c.id_terapeuta = ? OR c.id_sala = ?)
              AND c.estado <> 'CANCELADA'
            """;

        return jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                FECHA,
                FIN,
                INICIO,
                TERAPEUTA,
                SALA
        );
    }

    @Test
    void validarAntiColisionConcurrente()
            throws InterruptedException {

        // Protección: impedir escrituras fuera de QA.
        String baseActual =
                jdbcTemplate.queryForObject(
                        "SELECT DATABASE()", String.class
                );

        assertEquals(
            "psicogestion",
            baseActual,
            "La prueba debe ejecutarse únicamente en la BD psicogestion"
        );

        // El horario debe estar libre antes de empezar.
        assertEquals(
                0,
                contarCitasActivas(),
                "Limpia las reservas anteriores antes de repetir"
        );

        ExecutorService executor =
                Executors.newFixedThreadPool(TOTAL);

        CountDownLatch barreraInicio =
                new CountDownLatch(1);

        CountDownLatch finalizados =
                new CountDownLatch(TOTAL);

        AtomicInteger exitosas = new AtomicInteger();
        AtomicInteger rechazadas = new AtomicInteger();
        AtomicInteger errores = new AtomicInteger();

        System.out.println(
                "========== INICIO CPE-02 =========="
        );

        try {
            for (int i = 0; i < TOTAL; i++) {

                final int pacienteId = 93001 + i;

                executor.submit(() -> {

                    try {
                        barreraInicio.await();

                        Cita cita = new Cita();
                        cita.setIdPaciente(pacienteId);
                        cita.setIdTerapeuta(TERAPEUTA);
                        cita.setIdSala(SALA);
                        cita.setEstado(
                                EstadoCitaEnum.PENDIENTE
                        );
                        cita.setNotas("QA - junit cpe02");

                        BloqueHorario horario =
                                new BloqueHorario();

                        horario.setFecha(FECHA);
                        horario.setHoraInicio(INICIO);
                        horario.setHoraFin(FIN);

                        agendarCitaUseCase.ejecutar(
                                cita,
                                horario,
                                "test@example.com",
                                "9999999999"
                        );

                        exitosas.incrementAndGet();

                        System.out.println(
                                "CREADA: paciente " + pacienteId
                        );

                    } catch (IllegalStateException e) {

                        rechazadas.incrementAndGet();

                        System.out.println(
                                "COLISION: paciente " + pacienteId
                        );

                    } catch (Exception e) {

                        errores.incrementAndGet();

                        System.err.println(
                                "ERROR: paciente " + pacienteId
                                        + " - " + e
                        );

                    } finally {
                        finalizados.countDown();
                    }
                });
            }

            long inicio = System.nanoTime();

            // Lanzar todas las operaciones.
            barreraInicio.countDown();

            boolean terminaron =
                    finalizados.await(
                            40, TimeUnit.SECONDS
                    );

            long duracionMs =
                    TimeUnit.NANOSECONDS.toMillis(
                            System.nanoTime() - inicio
                    );

            System.out.println(
                    "\n========== RESULTADOS =========="
            );

            System.out.println(
                    "Solicitudes: " + TOTAL
            );

            System.out.println(
                    "Citas creadas: " + exitosas.get()
            );

            System.out.println(
                    "Colisiones rechazadas: "
                            + rechazadas.get()
            );

            System.out.println(
                    "Errores inesperados: " + errores.get()
            );

            System.out.println(
                    "Duracion total: " + duracionMs + " ms"
            );

            assertTrue(
                    terminaron,
                    "Las solicitudes no finalizaron en 40 segundos"
            );

            int guardadas = contarCitasActivas();

            System.out.println(
                    "Citas activas en MySQL: " + guardadas
            );

            // Validar todos los resultados.
            assertEquals(
                    0, errores.get(),
                    "No debe haber errores de MySQL ni Hikari"
            );

            assertEquals(
                    1, exitosas.get(),
                    "Solo una solicitud debe crear la cita"
            );

            assertEquals(
                    19, rechazadas.get(),
                    "Las otras 19 deben ser rechazadas"
            );

            assertEquals(
                    1, guardadas,
                    "MySQL debe contener una sola reserva"
            );

            System.out.println(
                    "CPE-02: APROBADA"
            );

        } finally {
            executor.shutdownNow();
        }
    }
}
