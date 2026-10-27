package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.infrastructure.persistence.JpaSecretariaRepository;
import mx.uady.psicogestion.infrastructure.persistence.SecretariaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaUserDetailsServiceTest {

    @Mock
    private JpaSecretariaRepository secretariaRepository;

    @InjectMocks
    private CuentaUserDetailsService cuentaUserDetailsService;

    @Test
    void encuentraCuentaPorIdentificadorYMapeaSuRolYEstado() {
        SecretariaEntity secretaria = new SecretariaEntity();
        secretaria.setIdSecretaria(7);
        secretaria.setNombreCompleto("Mariana Gonzalez");
        secretaria.setTelefono("9998887766");
        secretaria.setCorreoElectronico("mariana@correo.uady.mx");
        secretaria.setContrasena("{bcrypt}hash");
        secretaria.setActivo(true);
        secretaria.setRol("Administrador");
        when(secretariaRepository.findByIdentificador("9998887766"))
                .thenReturn(Optional.of(secretaria));

        CuentaUserDetailsService.CuentaUserDetails usuario =
                (CuentaUserDetailsService.CuentaUserDetails)
                        cuentaUserDetailsService.loadUserByUsername("9998887766");

        assertEquals(7, usuario.id());
        assertEquals("mariana@correo.uady.mx", usuario.getUsername());
        assertEquals("{bcrypt}hash", usuario.getPassword());
        assertTrue(usuario.isEnabled());
        assertEquals(
                new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"),
                usuario.getAuthorities().get(0)
        );
        verify(secretariaRepository).findByIdentificador("9998887766");
    }

    @Test
    void rechazaIdentificadorInexistente() {
        when(secretariaRepository.findByIdentificador("no-existe"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> cuentaUserDetailsService.loadUserByUsername("no-existe")
        );
    }
}
