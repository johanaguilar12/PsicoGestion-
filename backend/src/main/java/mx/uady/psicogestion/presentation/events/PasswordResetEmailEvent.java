package mx.uady.psicogestion.presentation.events;

public record PasswordResetEmailEvent(
        Integer accountId,
        String recipient,
        String recipientName,
        String resetUrl
) {
}
