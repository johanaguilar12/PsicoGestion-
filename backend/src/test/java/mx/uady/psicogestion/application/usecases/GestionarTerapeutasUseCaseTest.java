package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.application.ports.out.ITerapeutaRepository;
import mx.uady.psicogestion.domain.entities.Terapeuta;
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
public class GestionarTerapeutasUseCaseTest {

    @Mock
    private ITerapeutaRepository terapeutaRepository;

    @InjectMocks
    private GestionarTerapeutasUseCase gestionarTerapeutasUseCase;

    private Terapeuta terapeutaValido;

    @BeforeEach
    void setUp() {
        terapeutaValido = new Terapeuta();
        terapeutaValido.setNombreCompleto("Dra. Ana Gomez");
        terapeutaValido.setCedulaProfesional("CED12345678");
        terapeutaValido.setCorreoElectronico("ana.gomez@uady.mx");
        terapeutaValido.setTelefono("9998887766");
        terapeutaValido.setFechaNacimiento(LocalDate.now().minusYears(30)); // 30 años
    }

    @Test
    void registrar_TerapeutaValido_GuardaExitosamente() {
        when(terapeutaRepository.buscarPorCedula(anyString())).thenReturn(Optional.empty());
        when(terapeutaRepository.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(terapeutaRepository.buscarPorTelefono(anyString())).thenReturn(Optional.empty());
        when(terapeutaRepository.guardar(any(Terapeuta.class))).thenReturn(terapeutaValido);

        Terapeuta resultado = gestionarTerapeutasUseCase.registrar(terapeutaValido);

        assertNotNull(resultado);
        verify(terapeutaRepository, times(1)).guardar(terapeutaValido);
    }

    @Test
    void registrar_FechaNacimientoFutura_LanzaExcepcion() {
        terapeutaValido.setFechaNacimiento(LocalDate.now().plusDays(1)); // Fecha en el futuro

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gestionarTerapeutasUseCase.registrar(terapeutaValido);
        });

        assertEquals("La fecha de nacimiento debe ser estrictamente anterior a la fecha actual.", exception.getMessage());
        verify(terapeutaRepository, never()).guardar(any(Terapeuta.class));
    }

    @Test
    void registrar_CedulaDuplicada_LanzaExcepcion() {
        when(terapeutaRepository.buscarPorCedula(anyString())).thenReturn(Optional.of(terapeutaValido));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            gestionarTerapeutasUseCase.registrar(terapeutaValido);
        });

        assertEquals("La cédula profesional ya está registrada.", exception.getMessage());
        verify(terapeutaRepository, never()).guardar(any(Terapeuta.class));
    }
}