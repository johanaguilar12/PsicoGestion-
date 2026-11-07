
package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import mx.uady.psicogestion.domain.entities.EstadoCitaEnum;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
public class CasosConcurrenciaJUnitTest {

        @Autowired
        private AgendarCitaUseCase useCase;

        @Autowired
        private JdbcTemplate jdbc;

        @Autowired
        private PlatformTransactionManager transactionManager;

        private static final LocalDate FECHA_CPE05 = LocalDate.of(2027, 2, 10);

        private static final LocalDate FECHA_CPE06 = LocalDate.of(2027, 2, 11);

        private static final int T1 = 6;
        private static final int T2 = 5;

        private static final int S1 = 3;
        private static final int S2 = 2;

        private static final LocalDate NOV18 = LocalDate.of(2026, 11, 18);

        private static final LocalDate NOV19 = LocalDate.of(2026, 11, 19);

        private static final LocalDate NOV20 = LocalDate.of(2026, 11, 20);

        private static final LocalDate NOV23 = LocalDate.of(2026, 11, 23);

        private String marca;
        private volatile boolean limpiezaSegura;

        private final AtomicInteger aceptadas = new AtomicInteger();

        private final AtomicInteger rechazadas = new AtomicInteger();

        // =====================================================
        // PREPARACION
        // =====================================================

        @Test
        void diagnosticarConexionTiDB() {

        Map<String, Object> conexion = jdbc.queryForMap("""
                SELECT
                DATABASE() AS base_datos,
                USER() AS usuario,
                @@hostname AS servidor,
                @@port AS puerto
                """);

        System.out.println("CONEXION JAVA:");
        System.out.println(conexion);

        List<String> tablas = jdbc.queryForList("""
                SELECT TABLE_NAME
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = DATABASE()
                AND TABLE_NAME = 'bloqueo_reserva'
                """, String.class);

        System.out.println("TABLA BLOQUEO: " + tablas);
        }
        @BeforeEach
        void preparar() {

                limpiezaSegura = true;

                marca = "QA-JUNIT-" +
                                UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8);

                reiniciarContadores();

                // Comprobar la base configurada.
                String base = jdbc.queryForObject(
                                "SELECT DATABASE()",
                                String.class);

                assertEquals(
                                "psicogestion",
                                base,
                                "La BD debe ser psicogestion del entorno QA");

                // Comprobar los 20 pacientes de carga.
                Integer pacientes = jdbc.queryForObject(
                                """
                                                SELECT COUNT(*)
                                                FROM paciente
                                                WHERE id_paciente BETWEEN 93001 AND 93020
                                                  AND activo = 1
                                                """,
                                Integer.class);

                assertEquals(
                                20,
                                pacientes,
                                "Faltan pacientes activos de prueba");

                System.out.println(
                                "\n========== PREPARACION ==========");
                System.out.println("Base: " + base);
                System.out.println("Marca: " + marca);
        }

        private void reiniciarContadores() {
                aceptadas.set(0);
                rechazadas.set(0);
        }

        // =====================================================
        // LIMPIEZA DE REGISTROS DE PRUEBA
        // =====================================================

        @AfterEach
        void limpiar() {

                if (marca == null || !limpiezaSegura) {
                        System.out.println(
                                        "Limpieza omitida: revisar los hilos " +
                                                        "y las citas de prueba manualmente.");
                        return;
                }

                TransactionTemplate tx = new TransactionTemplate(transactionManager);

                tx.execute(status -> {

                        // Solo citas de esta ejecución.
                        List<Integer> ids = jdbc.queryForList(
                                        """
                                                        SELECT id_cita
                                                        FROM cita
                                                        WHERE notas = ?
                                                          AND id_paciente BETWEEN 93001 AND 93020
                                                          AND id_terapeuta IN (5, 6)
                                                          AND id_sala IN (2, 3)
                                                        """,
                                        Integer.class,
                                        marca);

                        for (Integer idCita : ids) {

                                List<Integer> bloques = jdbc.queryForList(
                                                """
                                                                SELECT id_bloque
                                                                FROM cita_bloque
                                                                WHERE id_cita = ?
                                                                """,
                                                Integer.class,
                                                idCita);

                                for (Integer idBloque : bloques) {

                                        jdbc.update(
                                                        "DELETE FROM disponibilidad_sala " +
                                                                        "WHERE id_bloque = ?",
                                                        idBloque);

                                        jdbc.update(
                                                        "DELETE FROM disponibilidad_terapeuta " +
                                                                        "WHERE id_bloque = ?",
                                                        idBloque);

                                        jdbc.update(
                                                        "DELETE FROM cita_bloque " +
                                                                        "WHERE id_cita = ? AND id_bloque = ?",
                                                        idCita,
                                                        idBloque);

                                        // No eliminar un bloque compartido.
                                        Integer referencias = jdbc.queryForObject(
                                                        """
                                                                        SELECT COUNT(*)
                                                                        FROM cita_bloque
                                                                        WHERE id_bloque = ?
                                                                        """,
                                                        Integer.class,
                                                        idBloque);

                                        if (referencias != null &&
                                                        referencias == 0) {
                                                jdbc.update(
                                                                "DELETE FROM bloque_horario " +
                                                                                "WHERE id_bloque = ?",
                                                                idBloque);
                                        }
                                }

                                jdbc.update(
                                                "DELETE FROM cita WHERE id_cita = ?",
                                                idCita);
                        }

                        System.out.println(
                                        "Limpieza completada. Citas retiradas: "
                                                        + ids.size());

                        return null;
                });
        }

        // =====================================================
        // CONSTRUCCION DE DATOS
        // =====================================================

        private BloqueHorario horario(
                        LocalDate fecha,
                        int hora) {

                BloqueHorario h = new BloqueHorario();
                h.setFecha(fecha);
                h.setHoraInicio(LocalTime.of(hora, 0));
                h.setHoraFin(LocalTime.of(hora + 1, 0));

                return h;
        }

        private Cita datos(
                        int paciente,
                        int terapeuta,
                        int sala) {

                Cita cita = new Cita();
                cita.setIdPaciente(paciente);
                cita.setIdTerapeuta(terapeuta);
                cita.setIdSala(sala);
                cita.setEstado(EstadoCitaEnum.PENDIENTE);
                cita.setNotas(marca);

                return cita;
        }

        private Cita crear(
                        int paciente,
                        int terapeuta,
                        int sala,
                        LocalDate fecha,
                        int hora) {

                return useCase.ejecutar(
                                datos(paciente, terapeuta, sala),
                                horario(fecha, hora),
                                "test@example.com",
                                "9999999999");
        }

        // =====================================================
        // OPERACIONES Y CONTEO
        // =====================================================

        private boolean esColision(IllegalStateException e) {

                return e.getMessage() != null
                                && e.getMessage().contains("Horario ocupado");
        }

        private boolean intentarCrear(
                        int paciente,
                        int terapeuta,
                        int sala,
                        LocalDate fecha,
                        int hora) {

                try {
                        crear(
                                        paciente,
                                        terapeuta,
                                        sala,
                                        fecha,
                                        hora);

                        aceptadas.incrementAndGet();
                        return true;

                } catch (IllegalStateException e) {

                        if (!esColision(e)) {
                                throw e;
                        }

                        rechazadas.incrementAndGet();
                        return false;
                }
        }

        private boolean intentarModificar(
                        int idCita,
                        int paciente,
                        int terapeuta,
                        int sala,
                        LocalDate fecha,
                        int hora) {

                try {

                        useCase.modificar(
                                        idCita,
                                        datos(paciente, terapeuta, sala),
                                        horario(fecha, hora));

                        aceptadas.incrementAndGet();
                        return true;

                } catch (IllegalStateException e) {

                        if (!esColision(e)) {
                                throw e;
                        }

                        rechazadas.incrementAndGet();
                        return false;
                }
        }

        private int contar(
                        LocalDate fecha,
                        int hora,
                        int terapeuta,
                        int sala) {

                Integer cantidad = jdbc.queryForObject(
                                """
                                                SELECT COUNT(DISTINCT c.id_cita)
                                                FROM cita c
                                                JOIN cita_bloque cb
                                                    ON c.id_cita = cb.id_cita
                                                JOIN bloque_horario bh
                                                    ON bh.id_bloque = cb.id_bloque
                                                WHERE bh.fecha = ?
                                                  AND bh.hora_inicio < ?
                                                  AND bh.hora_fin > ?
                                                  AND (
                                                      c.id_terapeuta = ?
                                                      OR c.id_sala = ?
                                                  )
                                                  AND c.estado <> 'CANCELADA'
                                                """,
                                Integer.class,
                                fecha,
                                LocalTime.of(hora + 1, 0),
                                LocalTime.of(hora, 0),
                                terapeuta,
                                sala);

                return cantidad == null ? 0 : cantidad;
        }

        private void exigirLibre(
                        LocalDate fecha,
                        int hora,
                        int terapeuta,
                        int sala) {

                assertEquals(
                                0,
                                contar(fecha, hora, terapeuta, sala),
                                "Horario ocupado previamente: "
                                                + fecha + " " + hora + ":00");
        }

        private int contarMarcadas() {

                Integer total = jdbc.queryForObject(
                                """
                                                SELECT COUNT(*)
                                                FROM cita
                                                WHERE notas = ?
                                                """,
                                Integer.class,
                                marca);

                return total == null ? 0 : total;
        }

        private String estado(Integer idCita) {

                return jdbc.queryForObject(
                                """
                                                SELECT estado
                                                FROM cita
                                                WHERE id_cita = ?
                                                """,
                                String.class,
                                idCita);
        }

        private int horaDeCita(Integer idCita) {

                Integer hora = jdbc.queryForObject(
                                """
                                                SELECT HOUR(bh.hora_inicio)
                                                FROM cita_bloque cb
                                                JOIN bloque_horario bh
                                                    ON bh.id_bloque = cb.id_bloque
                                                WHERE cb.id_cita = ?
                                                """,
                                Integer.class,
                                idCita);

                assertNotNull(hora);
                return hora;
        }

        private int bloquesDeCita(Integer idCita) {

                Integer bloques = jdbc.queryForObject(
                                """
                                                SELECT COUNT(*)
                                                FROM cita_bloque
                                                WHERE id_cita = ?
                                                """,
                                Integer.class,
                                idCita);

                return bloques == null ? 0 : bloques;
        }

        // =====================================================
        // EJECUCION CONCURRENTE
        // =====================================================

        private record Resultado(
                        long duracionMs,
                        long p95Ms,
                        long maxMs) {
        }

        private Resultado ejecutarConcurrentes(
                        List<Runnable> trabajos) throws Exception {

                int total = trabajos.size();

                ExecutorService executor = Executors.newFixedThreadPool(total);

                CountDownLatch preparados = new CountDownLatch(total);

                CountDownLatch inicio = new CountDownLatch(1);

                List<Future<?>> futures = new ArrayList<>();

                List<Long> latencias = Collections.synchronizedList(
                                new ArrayList<>());

                boolean completaron = false;
                long tiempoInicio = System.nanoTime();

                try {

                        for (Runnable trabajo : trabajos) {

                                futures.add(executor.submit(() -> {

                                        preparados.countDown();
                                        inicio.await();

                                        long inicioOperacion = System.nanoTime();

                                        try {
                                                trabajo.run();
                                        } finally {

                                                long ms = TimeUnit.NANOSECONDS.toMillis(
                                                                System.nanoTime()
                                                                                - inicioOperacion);

                                                latencias.add(ms);
                                        }

                                        return null;
                                }));
                        }

                        assertTrue(
                                        preparados.await(
                                                        20, TimeUnit.SECONDS),
                                        "No iniciaron todos los hilos");

                        inicio.countDown();

                        // Tiempo límite global, no 90 s por hilo.
                        long limite = System.nanoTime()
                                        + TimeUnit.SECONDS.toNanos(90);

                        List<Throwable> errores = new ArrayList<>();

                        for (Future<?> future : futures) {

                                long restante = limite - System.nanoTime();

                                if (restante <= 0) {
                                        throw new TimeoutException(
                                                        "Tiempo global agotado");
                                }

                                try {
                                        future.get(
                                                        restante,
                                                        TimeUnit.NANOSECONDS);

                                } catch (ExecutionException e) {
                                        errores.add(e.getCause());
                                }
                        }

                        completaron = true;

                        long duracion = TimeUnit.NANOSECONDS.toMillis(
                                        System.nanoTime() - tiempoInicio);

                        List<Long> ordenadas = new ArrayList<>(latencias);

                        Collections.sort(ordenadas);

                        assertEquals(
                                        total,
                                        ordenadas.size(),
                                        "Faltan mediciones de latencia");

                        int indiceP95 = (int) Math.ceil(total * 0.95) - 1;

                        long p95 = ordenadas.get(indiceP95);

                        long max = ordenadas.get(total - 1);

                        System.out.println(
                                        "\n========== RESULTADOS ==========");

                        System.out.println(
                                        "Operaciones: " + total);
                        System.out.println(
                                        "Aceptadas: " + aceptadas.get());
                        System.out.println(
                                        "Rechazadas: " + rechazadas.get());
                        System.out.println(
                                        "Errores inesperados: " + errores.size());
                        System.out.println(
                                        "Duracion total: " + duracion + " ms");
                        System.out.println(
                                        "p95 Java: " + p95 + " ms");
                        System.out.println(
                                        "Maximo Java: " + max + " ms");

                        for (Throwable error : errores) {
                                System.err.println("\n===== ERROR COMPLETO =====");
                                error.printStackTrace(System.err);
                        }

                        assertTrue(
                                        errores.isEmpty(),
                                        "Existen errores inesperados: " + errores);

                        return new Resultado(
                                        duracion,
                                        p95,
                                        max);

                } catch (TimeoutException e) {

                        limpiezaSegura = false;

                        throw new AssertionError(
                                        "Operaciones bloqueadas o demasiado lentas",
                                        e);

                } finally {

                        inicio.countDown();

                        executor.shutdownNow();

                        if (!executor.awaitTermination(
                                        20, TimeUnit.SECONDS)) {
                                limpiezaSegura = false;
                        }

                        if (!completaron) {
                                limpiezaSegura = false;
                        }
                }
        }

        private void validarRendimiento(Resultado r) {

                assertTrue(
                                r.p95Ms() < 3000,
                                "El p95 Java supero 3 segundos: "
                                                + r.p95Ms() + " ms");

                assertTrue(
                                r.maxMs() < 3000,
                                "Una operacion supero 3 segundos: "
                                                + r.maxMs() + " ms");
        }

        // =====================================================
        // CPE-02: MISMO TERAPEUTA, DIFERENTES SALAS
        // =====================================================

        @Test
        @Timeout(180)
        void cpe02_colisionTerapeuta() throws Exception {

                System.out.println(
                                "\n===== CPE-02: COLISION TERAPEUTA =====");

                exigirLibre(NOV18, 12, T1, S1);
                exigirLibre(NOV18, 12, T1, S2);
                exigirLibre(NOV18, 12, T2, S1);
                exigirLibre(NOV18, 12, T2, S2);

                AtomicInteger salaGanadora = new AtomicInteger();

                Resultado resultado = ejecutarConcurrentes(
                                List.of(
                                                () -> {
                                                        if (intentarCrear(
                                                                        93001, T1, S1,
                                                                        NOV18, 12)) {
                                                                salaGanadora.set(S1);
                                                        }
                                                },
                                                () -> {
                                                        if (intentarCrear(
                                                                        93002, T1, S2,
                                                                        NOV18, 12)) {
                                                                salaGanadora.set(S2);
                                                        }
                                                }));

                assertEquals(1, aceptadas.get());
                assertEquals(1, rechazadas.get());

                int salaLibre = salaGanadora.get() == S1 ? S2 : S1;

                // Otro terapeuta sí puede utilizar la sala libre.
                crear(
                                93003, T2, salaLibre,
                                NOV18, 12);

                assertEquals(
                                2,
                                contarMarcadas(),
                                "Deben existir dos citas compatibles");

                System.out.println(
                                "CPE-02: INTEGRIDAD APROBADA");

                validarRendimiento(resultado);
        }

        // =====================================================
        // CPE-03: MISMA SALA, DIFERENTES TERAPEUTAS
        // =====================================================

        @Test
        @Timeout(180)
        void cpe03_colisionSala() throws Exception {

                System.out.println(
                                "\n===== CPE-03: COLISION SALA =====");

                exigirLibre(NOV18, 13, T1, S1);
                exigirLibre(NOV18, 13, T2, S1);
                exigirLibre(NOV18, 13, T1, S2);
                exigirLibre(NOV18, 13, T2, S2);

                AtomicInteger terapeutaGanador = new AtomicInteger();

                Resultado resultado = ejecutarConcurrentes(
                                List.of(
                                                () -> {
                                                        if (intentarCrear(
                                                                        93003, T1, S1,
                                                                        NOV18, 13)) {
                                                                terapeutaGanador.set(T1);
                                                        }
                                                },
                                                () -> {
                                                        if (intentarCrear(
                                                                        93004, T2, S1,
                                                                        NOV18, 13)) {
                                                                terapeutaGanador.set(T2);
                                                        }
                                                }));

                assertEquals(1, aceptadas.get());
                assertEquals(1, rechazadas.get());

                int terapeutaLibre = terapeutaGanador.get() == T1 ? T2 : T1;

                crear(
                                93005,
                                terapeutaLibre,
                                S2,
                                NOV18,
                                13);

                assertEquals(
                                2,
                                contarMarcadas(),
                                "Deben existir dos citas compatibles");

                System.out.println(
                                "CPE-03: INTEGRIDAD APROBADA");

                validarRendimiento(resultado);
        }

        // =====================================================
        // CPE-04: 20 RESERVAS EN HORARIOS DIFERENTES
        // =====================================================

        @Test
        @Timeout(180)
        void cpe04_sinColisiones() throws Exception {

                System.out.println(
                                "\n===== CPE-04: SIN COLISIONES =====");

                List<Runnable> trabajos = new ArrayList<>();

                for (int i = 0; i < 20; i++) {

                        final int paciente = 93001 + i;

                        final LocalDate fecha = i < 10 ? NOV19 : NOV20;

                        final int hora = 7 + (i % 10);

                        exigirLibre(
                                        fecha, hora, T1, S1);

                        trabajos.add(() -> intentarCrear(
                                        paciente,
                                        T1,
                                        S1,
                                        fecha,
                                        hora));
                }

                Resultado resultado = ejecutarConcurrentes(trabajos);

                assertEquals(20, aceptadas.get());
                assertEquals(0, rechazadas.get());
                assertEquals(20, contarMarcadas());

                System.out.println(
                                "CPE-04: INTEGRIDAD APROBADA");

                validarRendimiento(resultado);
        }

        // =====================================================
        // CPE-05: MODIFICACION Y CREACION SIMULTANEAS
        // =====================================================

        @Test
        @Timeout(180)
        void cpe05_modificarYCrear() throws Exception {

                System.out.println(
                                "\n===== CPE-05: MODIFICAR Y CREAR =====");

                exigirLibre(FECHA_CPE05, 9, T1, S1);
                exigirLibre(FECHA_CPE05, 10, T1, S1);

                // Preparar la cita original a las 9.
                Cita original = crear(
                                93001,
                                T1,
                                S1,
                                FECHA_CPE05,
                                9);

                int idOriginal = original.getIdCita();

                // La modificación y la creación compiten
                // por el mismo bloque de las 10.
                Resultado resultado = ejecutarConcurrentes(
                                List.of(
                                                () -> intentarModificar(
                                                                idOriginal,
                                                                93001,
                                                                T1,
                                                                S1,
                                                                FECHA_CPE05,
                                                                10),

                                                () -> intentarCrear(
                                                                93002,
                                                                T1,
                                                                S1,
                                                                FECHA_CPE05,
                                                                10)));

                assertEquals(1, aceptadas.get());
                assertEquals(1, rechazadas.get());

                // Solo una reserva en el destino.
                assertEquals(
                                1,
                                contar(FECHA_CPE05, 10, T1, S1));

                assertEquals(
                                "PENDIENTE",
                                estado(idOriginal));

                assertEquals(
                                1,
                                bloquesDeCita(idOriginal));

                int horaFinal = horaDeCita(idOriginal);

                assertTrue(
                                horaFinal == 9 || horaFinal == 10);

                if (horaFinal == 10) {
                        // Gano la modificación.
                        assertEquals(
                                        0,
                                        contar(FECHA_CPE05, 9, T1, S1));
                        assertEquals(1, contarMarcadas());

                        System.out.println(
                                        "Ganadora: MODIFICACION");

                } else {
                        // Gano la creación.
                        assertEquals(
                                        1,
                                        contar(FECHA_CPE05, 9, T1, S1));
                        assertEquals(2, contarMarcadas());

                        System.out.println(
                                        "Ganadora: CREACION");
                }

                System.out.println(
                                "CPE-05: INTEGRIDAD APROBADA");

                validarRendimiento(resultado);
        }

        // =====================================================
        // CPE-06: CANCELACION Y CREACION SIMULTANEAS
        // =====================================================

        @Test
        @Timeout(180)
        void cpe06_cancelarYCrear() throws Exception {

                System.out.println(
                                "\n===== CPE-06: CANCELAR Y CREAR =====");

                exigirLibre(FECHA_CPE06, 11, T1, S1);

                Cita original = crear(
                                93003,
                                T1,
                                S1,
                                FECHA_CPE06,
                                11);

                int idOriginal = original.getIdCita();

                AtomicInteger cancelaciones = new AtomicInteger();

                Resultado resultado = ejecutarConcurrentes(
                                List.of(
                                                () -> {
                                                        useCase.cancelarCita(idOriginal);
                                                        cancelaciones.incrementAndGet();
                                                },

                                                () -> intentarCrear(
                                                                93004,
                                                                T1,
                                                                S1,
                                                                FECHA_CPE06,
                                                                11)));

                // La cancelación siempre debe completarse.
                assertEquals(1, cancelaciones.get());

                assertEquals(
                                "CANCELADA",
                                estado(idOriginal));

                int activas = contar(FECHA_CPE06, 11, T1, S1);

                assertTrue(
                                activas <= 1,
                                "No debe haber citas duplicadas");

                assertEquals(
                                1,
                                aceptadas.get() + rechazadas.get(),
                                "La nueva reserva debe tener un resultado");

                if (aceptadas.get() == 1) {

                        // La reserva se ejecutó después de cancelar.
                        assertEquals(1, activas);

                        System.out.println(
                                        "Cancelación correcta; nueva cita creada.");

                } else {

                        // La reserva se intentó antes de cancelar.
                        assertEquals(1, rechazadas.get());
                        assertEquals(0, activas);

                        System.out.println(
                                        "Cancelación correcta; bloque liberado.");

                        // El horario debe poder utilizarse otra vez.
                        crear(
                                        93005,
                                        T1,
                                        S1,
                                        FECHA_CPE06,
                                        11);

                        assertEquals(
                                        1,
                                        contar(FECHA_CPE06, 11, T1, S1));
                }

                System.out.println(
                                "CPE-06: INTEGRIDAD APROBADA");

                validarRendimiento(resultado);
        }

        // =====================================================
        // CPE-07: ESTRES ESCALONADO 20 / 50 / 100
        // =====================================================

        @Test
        @Timeout(300)
        void cpe07_estresEscalonado() throws Exception {

                System.out.println(
                                "\n===== CPE-07: ESTRES ESCALONADO =====");

                // Verificar que los 40 bloques de trabajo
                // están disponibles antes de iniciar la carga.
                for (int dia = 24; dia <= 27; dia++) {
                        for (int hora = 7; hora <= 16; hora++) {
                                exigirLibre(
                                                LocalDate.of(2026, 11, dia),
                                                hora,
                                                T1,
                                                S1);
                        }
                }

                int[] niveles = { 20, 50, 100 };
                List<String> incumplimientos = new ArrayList<>();

                for (int nivel : niveles) {

                        reiniciarContadores();

                        System.out.println(
                                        "\n===== ETAPA: " + nivel +
                                                        " OPERACIONES =====");

                        List<Runnable> trabajos = new ArrayList<>();

                        for (int i = 0; i < nivel; i++) {

                                final int indice = i;

                                trabajos.add(() -> {

                                        // 20% de operaciones de escritura.
                                        if (indice % 5 == 0) {

                                                int numeroBloque = indice / 5;

                                                LocalDate fecha = LocalDate.of(
                                                                2026,
                                                                11,
                                                                24 + (numeroBloque % 4));

                                                int hora = 7 + (numeroBloque % 10);

                                                intentarCrear(
                                                                93001 + (indice % 20),
                                                                T1,
                                                                S1,
                                                                fecha,
                                                                hora);

                                        } else {

                                                // 80% consultas de agenda.
                                                List<?> agenda = useCase.listarTodasConDetalles();

                                                if (agenda == null) {
                                                        throw new IllegalStateException(
                                                                        "La agenda devolvió null");
                                                }
                                        }
                                });
                        }

                        Resultado resultado = ejecutarConcurrentes(trabajos);

                        System.out.println(
                                        "Etapa " + nivel +
                                                        ": creadas=" + aceptadas.get() +
                                                        ", colisiones=" + rechazadas.get());

                        if (resultado.p95Ms() >= 3000) {
                                incumplimientos.add(
                                                "Etapa " + nivel +
                                                                ": p95 = " + resultado.p95Ms() +
                                                                " ms (límite: 3000 ms)");
                        }
                }

                // Verificar superposiciones entre citas del test.
                Integer duplicados = jdbc.queryForObject(
                                """
                                                SELECT COUNT(*)
                                                FROM cita a
                                                JOIN cita_bloque ca
                                                    ON ca.id_cita = a.id_cita
                                                JOIN bloque_horario ha
                                                    ON ha.id_bloque = ca.id_bloque
                                                JOIN cita b
                                                    ON a.id_cita < b.id_cita
                                                JOIN cita_bloque cb
                                                    ON cb.id_cita = b.id_cita
                                                JOIN bloque_horario hb
                                                    ON hb.id_bloque = cb.id_bloque
                                                WHERE a.notas = ?
                                                  AND b.notas = ?
                                                  AND a.estado <> 'CANCELADA'
                                                  AND b.estado <> 'CANCELADA'
                                                  AND ha.fecha = hb.fecha
                                                  AND ha.hora_inicio < hb.hora_fin
                                                  AND ha.hora_fin > hb.hora_inicio
                                                  AND (
                                                      a.id_terapeuta = b.id_terapeuta
                                                      OR a.id_sala = b.id_sala
                                                      OR a.id_paciente = b.id_paciente
                                                  )
                                                """,
                                Integer.class,
                                marca,
                                marca);

                assertEquals(
                                0,
                                duplicados,
                                "Existen reservas duplicadas bajo estrés");

                assertTrue(
                                incumplimientos.isEmpty(),
                                "CPE-07 incumplió rendimiento:\n" +
                                                String.join("\n", incumplimientos));

                System.out.println(
                                "\nCPE-07: INTEGRIDAD APROBADA");

        }

        @Test
        @Timeout(180)
        void cpe08_colisionParcialMediaHora() throws Exception {

                LocalDate fecha = LocalDate.of(2027, 2, 15);

                exigirLibre(fecha, 7, T1, S1);
                exigirLibre(fecha, 8, T1, S1);

                ejecutarConcurrentes(List.of(
                                () -> intentarCrearConMinutos(
                                                93001, T1, S1, fecha, 7, 0),
                                () -> intentarCrearConMinutos(
                                                93002, T1, S1, fecha, 7, 30)));

                assertEquals(1, aceptadas.get());
                assertEquals(1, rechazadas.get());
                assertEquals(1, contarMarcadas());
        }

        @Test
        @Timeout(180)
        void cpe09_horariosContiguosSinColision() throws Exception {

                LocalDate fecha = LocalDate.of(2027, 2, 16);

                exigirLibre(fecha, 7, T1, S1);
                exigirLibre(fecha, 8, T1, S1);

                ejecutarConcurrentes(List.of(
                                () -> intentarCrearConMinutos(
                                                93001, T1, S1, fecha, 7, 0),
                                () -> intentarCrearConMinutos(
                                                93002, T1, S1, fecha, 8, 0)));

                assertEquals(2, aceptadas.get());
                assertEquals(0, rechazadas.get());
                assertEquals(2, contarMarcadas());
        }

        private void validarRendimientoP95(Resultado resultado) {
                assertTrue(
                                resultado.p95Ms() < 3000,
                                "El p95 Java superó 3 segundos: "
                                                + resultado.p95Ms() + " ms");
        }

        private boolean intentarCrearConMinutos(
                        int paciente,
                        int terapeuta,
                        int sala,
                        LocalDate fecha,
                        int hora,
                        int minuto) {
                BloqueHorario bloque = new BloqueHorario();

                bloque.setFecha(fecha);

                LocalTime inicio = LocalTime.of(hora, minuto);
                bloque.setHoraInicio(inicio);
                bloque.setHoraFin(inicio.plusHours(1));

                try {
                        useCase.ejecutar(
                                        datos(paciente, terapeuta, sala),
                                        bloque,
                                        "test@example.com",
                                        "9999999999");

                        aceptadas.incrementAndGet();
                        return true;

                } catch (IllegalStateException e) {

                        if (e.getMessage() != null &&
                                        e.getMessage().contains("Horario ocupado")) {
                                rechazadas.incrementAndGet();
                                return false;
                        }

                        throw e;
                }
        }

}
