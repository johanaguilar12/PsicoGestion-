package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.infrastructure.persistence.JpaPasswordResetTokenRepository;
import mx.uady.psicogestion.infrastructure.persistence.JpaSecretariaRepository;
import mx.uady.psicogestion.infrastructure.persistence.PasswordResetTokenEntity;
import mx.uady.psicogestion.infrastructure.persistence.SecretariaEntity;
import mx.uady.psicogestion.presentation.events.PasswordResetEmailEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private JpaSecretariaRepository secretariaRepository;

    @Mock
    private JpaPasswordResetTokenRepository tokenRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetService(
                secretariaRepository,
                tokenRepository,
                eventPublisher,
                passwordEncoder,
                "http://localhost:5173/"
        );
    }

    @Test
    void requestResetPublishesEmailEventAndPersistsOnlyTokenHash() {
        SecretariaEntity user = activeUser();
        when(secretariaRepository.findByCorreoForUpdate("mariano@correo.uady.mx"))
                .thenReturn(Optional.of(user));
        when(tokenRepository.findFirstByIdSecretariaOrderByCreatedAtDesc(1))
                .thenReturn(Optional.empty());
        when(tokenRepository.saveAndFlush(any(PasswordResetTokenEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        passwordResetService.requestReset("  mariano@correo.uady.mx ");

        ArgumentCaptor<PasswordResetTokenEntity> tokenCaptor =
                ArgumentCaptor.forClass(PasswordResetTokenEntity.class);
        verify(tokenRepository).saveAndFlush(tokenCaptor.capture());
        PasswordResetTokenEntity savedToken = tokenCaptor.getValue();
        assertEquals(64, savedToken.getTokenHash().length());
        assertTrue(savedToken.getExpiresAt().isAfter(savedToken.getCreatedAt()));

        ArgumentCaptor<PasswordResetEmailEvent> eventCaptor =
                ArgumentCaptor.forClass(PasswordResetEmailEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        PasswordResetEmailEvent event = eventCaptor.getValue();
        assertEquals("mariano@correo.uady.mx", event.recipient());
        assertTrue(event.resetUrl().startsWith("http://localhost:5173/restablecer-contrasena?token="));
        assertNotEquals(event.resetUrl().substring(event.resetUrl().lastIndexOf('=') + 1), savedToken.getTokenHash());
    }

    @Test
    void requestResetDoesNotRevealOrSendForUnknownAccount() {
        when(secretariaRepository.findByCorreoForUpdate("unknown@correo.uady.mx"))
                .thenReturn(Optional.empty());

        passwordResetService.requestReset("unknown@correo.uady.mx");

        verifyNoInteractions(tokenRepository, eventPublisher);
    }

    @Test
    void resetPasswordUpdatesHashAndConsumesToken() throws Exception {
        String rawToken = "one-time-reset-token";
        PasswordResetTokenEntity token = new PasswordResetTokenEntity();
        token.setIdSecretaria(1);
        token.setTokenHash(hashToken(rawToken));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        SecretariaEntity user = activeUser();
        when(tokenRepository.findByTokenHashForUpdate(token.getTokenHash()))
                .thenReturn(Optional.of(token));
        when(secretariaRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("Nueva.2026*")).thenReturn("{bcrypt}encoded-password");

        passwordResetService.resetPassword(rawToken, "Nueva.2026*");

        assertEquals("{bcrypt}encoded-password", user.getContrasena());
        verify(secretariaRepository).save(user);
        verify(tokenRepository).delete(token);
    }

    @Test
    void resetPasswordRejectsExpiredToken() throws Exception {
        String rawToken = "expired-reset-token";
        PasswordResetTokenEntity token = new PasswordResetTokenEntity();
        token.setTokenHash(hashToken(rawToken));
        token.setExpiresAt(LocalDateTime.now().minusSeconds(1));
        when(tokenRepository.findByTokenHashForUpdate(token.getTokenHash()))
                .thenReturn(Optional.of(token));

        assertThrows(
                IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(rawToken, "Nueva.2026*")
        );
        verifyNoInteractions(secretariaRepository, passwordEncoder);
        verify(tokenRepository, never()).delete(token);
    }

    private SecretariaEntity activeUser() {
        SecretariaEntity user = new SecretariaEntity();
        user.setIdSecretaria(1);
        user.setNombreCompleto("Mariano Calderon");
        user.setCorreoElectronico("mariano@correo.uady.mx");
        user.setActivo(true);
        return user;
    }

    private String hashToken(String token) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }
}
