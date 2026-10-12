package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.application.ports.out.IPacienteRepository;
import mx.uady.psicogestion.domain.entities.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GestionarPacientesUseCaseTest {

    @Mock
    private IPacienteRepository pacienteRepository;

    @InjectMocks
    private GestionarPacientesUseCase gestionarPacientesUseCase;

    private Paciente pacienteValido;

    @BeforeEach
    void setUp() {
        pacienteValido = new Paciente();
        pacienteValido.setNombreCompleto("Juan Perez");
        pacienteValido.setCurp("PEPJ100512HYCABR01");
        pacienteValido.setTelefono("9991234567");
        pacienteValido.setFechaNacimiento(LocalDate.now().minusYears(10)); // 10 años de edad
    }

    @Test
    void registrar_PacienteValido_GuardaExitosamente() {
        when(pacienteRepository.buscarPorCurp(anyString())).thenReturn(Optional.empty());
        when(pacienteRepository.buscarPorTelefono(anyString())).thenReturn(Optional.empty());
        when(pacienteRepository.guardar(any(Paciente.class))).thenReturn(pacienteValido);

        Paciente resultado = gestionarPacientesUseCase.registrar(pacienteValido);

        assertNotNull(resultado);
        verify(pacienteRepository, times(1)).guardar(pacienteValido);
    }

    @Test
    void registrar_PacienteMenorDe7Anios_LanzaExcepcion() {
        pacienteValido.setFechaNacimiento(LocalDate.now().minusYears(5)); // 5 años de edad

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gestionarPacientesUseCase.registrar(pacienteValido);
        });

        assertEquals("El paciente debe tener 7 años o más al momento del registro.", exception.getMessage());
        verify(pacienteRepository, never()).guardar(any(Paciente.class));
    }

    @Test
    void registrar_CurpDuplicada_LanzaExcepcion() {
        when(pacienteRepository.buscarPorCurp(anyString())).thenReturn(Optional.of(pacienteValido));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            gestionarPacientesUseCase.registrar(pacienteValido);
        });

        assertEquals("La CURP ya está registrada en el sistema.", exception.getMessage());
        verify(pacienteRepository, never()).guardar(any(Paciente.class));
    }
}