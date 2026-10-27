package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.domain.entities.Secretaria;
import mx.uady.psicogestion.infrastructure.persistence.JpaPasswordResetTokenRepository;
import mx.uady.psicogestion.infrastructure.persistence.JpaSecretariaRepository;
import mx.uady.psicogestion.infrastructure.persistence.PasswordResetTokenEntity;
import mx.uady.psicogestion.infrastructure.persistence.SecretariaEntity;
import mx.uady.psicogestion.presentation.events.PasswordResetEmailEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(30);
    private static final Duration REQUEST_COOLDOWN = Duration.ofMinutes(1);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final JpaSecretariaRepository secretariaRepository;
    private final JpaPasswordResetTokenRepository tokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final String frontendBaseUrl;

    public PasswordResetService(
            JpaSecretariaRepository secretariaRepository,
            JpaPasswordResetTokenRepository tokenRepository,
            ApplicationEventPublisher eventPublisher,
            PasswordEncoder passwordEncoder,
            @Value("${app.frontend.base-url}") String frontendBaseUrl) {
        this.secretariaRepository = secretariaRepository;
        this.tokenRepository = tokenRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Transactional
    public void requestReset(String email) {
        if (!StringUtils.hasText(email)) {
            return;
        }

        Optional<SecretariaEntity> account = secretariaRepository.findByCorreoForUpdate(email.trim());
        if (account.isEmpty() || !account.get().isActivo()) {
            return;
        }

        SecretariaEntity user = account.get();
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteExpired(now);
        Optional<PasswordResetTokenEntity> latestToken =
                tokenRepository.findFirstByIdSecretariaOrderByCreatedAtDesc(user.getIdSecretaria());
        if (latestToken.isPresent()
                && latestToken.get().getCreatedAt().isAfter(now.minus(REQUEST_COOLDOWN))) {
            return;
        }

        byte[] tokenBytes = new byte[32];
        SECURE_RANDOM.nextBytes(tokenBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        String tokenHash = hashToken(rawToken);

        tokenRepository.deleteByIdSecretaria(user.getIdSecretaria());
        PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();
        resetToken.setIdSecretaria(user.getIdSecretaria());
        resetToken.setTokenHash(tokenHash);
        resetToken.setCreatedAt(now);
        resetToken.setExpiresAt(now.plus(TOKEN_LIFETIME));
        tokenRepository.saveAndFlush(resetToken);

        String resetUrl = frontendBaseUrl.replaceAll("/+$", "")
                + "/restablecer-contrasena?token=" + rawToken;
        eventPublisher.publishEvent(new PasswordResetEmailEvent(
                user.getIdSecretaria(),
                user.getCorreoElectronico(),
                user.getNombreCompleto(),
                resetUrl
        ));
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        if (!StringUtils.hasText(rawToken) || !StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("El enlace o la nueva contraseña no son válidos.");
        }

        PasswordResetTokenEntity resetToken = tokenRepository.findByTokenHashForUpdate(hashToken(rawToken))
                .orElseThrow(() -> new IllegalArgumentException("El enlace de recuperación no es válido o expiró."));
        LocalDateTime now = LocalDateTime.now();
        if (!resetToken.getExpiresAt().isAfter(now)) {
            throw new IllegalArgumentException("El enlace de recuperación no es válido o expiró.");
        }

        SecretariaEntity account = secretariaRepository.findById(resetToken.getIdSecretaria())
                .filter(SecretariaEntity::isActivo)
                .orElseThrow(() -> new IllegalArgumentException("El enlace de recuperación no es válido o expiró."));

        Secretaria candidate = new Secretaria();
        candidate.setContrasena(newPassword);
        if (!candidate.esContrasenaSegura()) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener entre 8 y 25 caracteres, un número, una mayúscula y un carácter especial."
            );
        }

        account.setContrasena(passwordEncoder.encode(newPassword));
        secretariaRepository.save(account);
        tokenRepository.delete(resetToken);
    }

    private String hashToken(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("No está disponible SHA-256 para validar el token.", exception);
        }
    }
}
