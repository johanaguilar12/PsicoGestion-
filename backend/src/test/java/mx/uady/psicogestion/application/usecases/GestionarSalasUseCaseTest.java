package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.application.ports.out.ISalaRepository;
import mx.uady.psicogestion.domain.entities.Sala;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GestionarSalasUseCaseTest {

    @Mock
    private ISalaRepository salaRepository;

    @InjectMocks
    private GestionarSalasUseCase gestionarSalasUseCase;

    private Sala salaValida;

    @BeforeEach
    void setUp() {
        salaValida = new Sala();
        salaValida.setNombreSala("Sala A");
    }

    @Test
    void registrar_SalaValida_GuardaExitosamente() {
        when(salaRepository.guardar(any(Sala.class))).thenReturn(salaValida);

        Sala resultado = gestionarSalasUseCase.registrar(salaValida);

        assertNotNull(resultado);
        verify(salaRepository, times(1)).guardar(salaValida);
    }

    @Test
    void registrar_NombreSalaInvalido_LanzaExcepcion() {
        salaValida.setNombreSala("Sala Principal Muy Larga"); // Más de 10 caracteres

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gestionarSalasUseCase.registrar(salaValida);
        });

        assertEquals("El nombre de la sala es inválido o excede los 10 caracteres.", exception.getMessage());
        verify(salaRepository, never()).guardar(any(Sala.class));
    }
}