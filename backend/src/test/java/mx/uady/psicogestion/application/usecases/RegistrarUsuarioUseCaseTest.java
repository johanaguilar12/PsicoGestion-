package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.application.ports.out.IAutenticacionService;
import mx.uady.psicogestion.application.ports.out.ISecretariaRepository;
import mx.uady.psicogestion.domain.entities.Secretaria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RegistrarUsuarioUseCaseTest {


    @Mock
    private ISecretariaRepository secretariaRepository;

    @Mock
    private IAutenticacionService autenticacionService;

    @InjectMocks
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    private Secretaria secretariaValida;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los Mocks
        secretariaValida = new Secretaria();
        secretariaValida.setNombreCompleto("Mariana Gonzalez");
        secretariaValida.setTelefono("9998887766");
        secretariaValida.setCorreoElectronico("mariana@correo.uady.mx");
        secretariaValida.setContrasena("Secre.2026*");
    }

    // --- ESCENARIOS DE PRUEBA ---

    @Test
    void debeRegistrarUsuarioExitosamente() {
        // Arrange: Le decimos a los mocks cómo comportarse (simulamos que no hay duplicados)
        when(secretariaRepository.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(secretariaRepository.buscarPorTelefono(anyString())).thenReturn(Optional.empty());
        when(autenticacionService.cifrarContrasena(anyString())).thenReturn("{bcrypt}hash_generado");
        when(secretariaRepository.guardar(any(Secretaria.class))).thenReturn(secretariaValida);

        Secretaria resultado = registrarUsuarioUseCase.ejecutar(secretariaValida);


        assertNotNull(resultado);
        assertEquals("{bcrypt}hash_generado", secretariaValida.getContrasena(), "La contraseña debió ser cifrada");
        verify(secretariaRepository, times(1)).guardar(secretariaValida); // Se debió llamar a guardar() 1 vez
    }

    @Test
    void debeLanzarExcepcionSiCorreoNoEsInstitucional() {
        // Modificamos el correo a uno inválido
        secretariaValida.setCorreoElectronico("mariana@gmail.com");

        // Verificamos que lance la excepción exacta
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            registrarUsuarioUseCase.ejecutar(secretariaValida);
        });

        assertTrue(exception.getMessage().contains("formato @correo.uady.mx"));
        // Verificamos que NUNCA haya intentado guardar en BD
        verify(secretariaRepository, never()).guardar(any());
    }

    @Test
    void debeLanzarExcepcionSiCorreoYaExiste() {
        when(secretariaRepository.buscarPorCorreo(anyString())).thenReturn(Optional.of(new Secretaria()));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            registrarUsuarioUseCase.ejecutar(secretariaValida);
        });

        assertTrue(exception.getMessage().contains("ya está registrado"));
        verify(secretariaRepository, never()).guardar(any());
    }
}