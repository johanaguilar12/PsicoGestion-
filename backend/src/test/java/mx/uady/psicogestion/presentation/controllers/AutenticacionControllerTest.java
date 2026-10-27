package mx.uady.psicogestion.presentation.controllers;

import mx.uady.psicogestion.infrastructure.adapters.CuentaUserDetailsService.CuentaUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacionControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @Mock
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;

    @InjectMocks
    private AutenticacionController autenticacionController;

    @AfterEach
    void limpiarContextoDeSeguridad() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void iniciaSesionYDevuelveSoloDatosPublicosDeLaCuenta() {
        CuentaUserDetails usuario = new CuentaUserDetails(
                7,
                "Mariana Gonzalez",
                "9998887766",
                "mariana@correo.uady.mx",
                "{bcrypt}hash",
                true,
                null,
                "Administrador",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
        );
        Authentication autenticacion = UsernamePasswordAuthenticationToken.authenticated(
                usuario,
                usuario.getPassword(),
                usuario.getAuthorities()
        );
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(autenticacion);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        var resultado = autenticacionController.login(
                new AutenticacionController.LoginRequest("9998887766", "Secre.2026*"),
                request,
                response
        );

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertInstanceOf(AutenticacionController.UsuarioResponse.class, resultado.getBody());
        AutenticacionController.UsuarioResponse usuarioRespuesta =
                (AutenticacionController.UsuarioResponse) resultado.getBody();
        assertEquals(usuario.correoElectronico(), usuarioRespuesta.correoElectronico());
        assertEquals("Mariana Gonzalez", usuarioRespuesta.nombreCompleto());
        assertNotNull(request.getSession(false));
        verify(sessionAuthenticationStrategy).onAuthentication(same(autenticacion), same(request), same(response));
        verify(securityContextRepository).saveContext(any(), same(request), same(response));
    }

    @Test
    void responde401ParaCredencialesInvalidas() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        var resultado = autenticacionController.login(
                new AutenticacionController.LoginRequest("mariana@correo.uady.mx", "incorrecta"),
                new MockHttpServletRequest(),
                new MockHttpServletResponse()
        );

        assertEquals(HttpStatus.UNAUTHORIZED, resultado.getStatusCode());
        verifyNoInteractions(sessionAuthenticationStrategy, securityContextRepository);
    }
}
