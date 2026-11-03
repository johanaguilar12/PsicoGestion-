package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.application.ports.out.ICitaRepository;
import mx.uady.psicogestion.application.ports.out.INotificacionService;
import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import mx.uady.psicogestion.domain.entities.EstadoCitaEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgendarCitaUseCaseTest {

    @Mock
    private ICitaRepository citaRepository;

    @Mock
    private INotificacionService notificacionService;

    @InjectMocks
    private AgendarCitaUseCase agendarCitaUseCase;

    private Cita citaPrueba;
    private BloqueHorario horarioPrueba;

    @BeforeEach
    void setUp() {
        citaPrueba = new Cita();
        citaPrueba.setIdPaciente(1);
        citaPrueba.setIdTerapeuta(1);
        citaPrueba.setIdSala(1);

        horarioPrueba = new BloqueHorario();
        horarioPrueba.setFecha(LocalDate.of(2026, 10, 15));
        horarioPrueba.setHoraInicio(LocalTime.of(10, 0));
        horarioPrueba.setHoraFin(LocalTime.of(11, 0));
    }

    @Test
    void ejecutar_SinColisiones_GuardaYNotificaExitosamente() {
        // Se actualizó a 7 parámetros (se agregaron dos anyInt() extra)
        when(citaRepository.buscarColisiones(anyInt(), anyInt(), anyInt(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class), anyInt()))
                .thenReturn(false); // No hay colisión
        when(citaRepository.guardar(any(Cita.class), any(BloqueHorario.class))).thenReturn(citaPrueba);

        Cita resultado = agendarCitaUseCase.ejecutar(citaPrueba, horarioPrueba, "paciente@correo.com", "9991234567");

        assertNotNull(resultado.getFolio());
        assertEquals(EstadoCitaEnum.PENDIENTE, resultado.getEstado());
        verify(citaRepository, times(1)).guardar(eq(citaPrueba), eq(horarioPrueba));
        verify(notificacionService, times(1)).enviarConfirmacion(anyString(), anyString(), any(Cita.class));
    }

    @Test
    void ejecutar_ConColisiones_LanzaExcepcion() {
        // Se actualizó a 7 parámetros
        when(citaRepository.buscarColisiones(anyInt(), anyInt(), anyInt(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class), anyInt()))
                .thenReturn(true); // Simulamos que el horario está ocupado

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            agendarCitaUseCase.ejecutar(citaPrueba, horarioPrueba, "paciente@correo.com", "9991234567");
        });

        // Se actualizó el mensaje esperado para que coincida con el nuevo que pusimos en el UseCase
        assertEquals("Horario ocupado. Existe una colisión para el paciente, terapeuta o la sala.", exception.getMessage());
        verify(citaRepository, never()).guardar(any(Cita.class), any(BloqueHorario.class));
        verify(notificacionService, never()).enviarConfirmacion(anyString(), anyString(), any(Cita.class));
    }

    @Test
    void ejecutar_HorarioInvalido_LanzaExcepcion() {
        horarioPrueba.setHoraFin(LocalTime.of(9, 0)); // Hora de fin anterior a hora de inicio

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            agendarCitaUseCase.ejecutar(citaPrueba, horarioPrueba, "paciente@correo.com", "9991234567");
        });

        assertEquals("El rango de horario operativo es inválido.", exception.getMessage());
        // Se actualizó el verify a 7 parámetros
        verify(citaRepository, never()).buscarColisiones(anyInt(), anyInt(), anyInt(), any(), any(), any(), anyInt());
    }
}