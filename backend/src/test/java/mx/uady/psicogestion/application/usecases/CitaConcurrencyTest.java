package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import mx.uady.psicogestion.domain.entities.EstadoCitaEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CitaConcurrencyTest {

    @Autowired
    private AgendarCitaUseCase agendarCitaUseCase;

    @Test
    public void validarAntiColisionConcurrente() throws InterruptedException {
        int totalPeticiones = 20;
        ExecutorService executor = Executors.newFixedThreadPool(totalPeticiones);


        CountDownLatch barreraDeSalida = new CountDownLatch(1);
        CountDownLatch rastreadorDeHilos = new CountDownLatch(totalPeticiones);

        AtomicInteger citasExitosas = new AtomicInteger(0);
        AtomicInteger citasRechazadas = new AtomicInteger(0);

        for (int i = 0; i < totalPeticiones; i++) {
            executor.submit(() -> {
                try {
                    // Todos los hilos se pausan aquí esperando la señal
                    barreraDeSalida.await();

                    // Configuración de la cita (Asegúrate de que los IDs 1, 1 y 1 existan en tu DB)
                    Cita cita = new Cita();
                    cita.setIdPaciente(2);
                    cita.setIdTerapeuta(5);
                    cita.setIdSala(2);
                    cita.setNotas("Prueba de estrés concurrente");
                    cita.setEstado(EstadoCitaEnum.PENDIENTE);

                    BloqueHorario horario = new BloqueHorario();
                    horario.setFecha(LocalDate.of(2027, 5, 20)); // Una fecha futura
                    horario.setHoraInicio(LocalTime.of(10, 0));
                    horario.setHoraFin(LocalTime.of(11, 0));

                    // Intento de agendar
                    agendarCitaUseCase.ejecutar(cita, horario, "test@test.com", "9999999999");
                    citasExitosas.incrementAndGet();

                } catch (IllegalStateException e) {
                    // Si entra aquí, el algoritmo de colisión hizo su trabajo y bloqueó el intento
                    citasRechazadas.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    rastreadorDeHilos.countDown(); // Avisa que este hilo ya terminó
                }
            });
        }

        // ¡PUM! Disparamos todos los hilos al mismo exacto milisegundo
        barreraDeSalida.countDown();

        // El hilo principal espera a que los 20 terminen su proceso
        rastreadorDeHilos.await();
        executor.shutdown();

        System.out.println("✅ Citas creadas con éxito: " + citasExitosas.get());
        System.out.println("❌ Citas rechazadas por colisión: " + citasRechazadas.get());

        // Validaciones: Exactamente 1 debió ganar la carrera, 19 debieron rebotar
        assertEquals(1, citasExitosas.get(), "Solo 1 cita debió registrarse");
        assertEquals(19, citasRechazadas.get(), "19 citas debieron ser rechazadas por colisión");
    }
}