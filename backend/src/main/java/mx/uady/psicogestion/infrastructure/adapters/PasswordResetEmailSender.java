package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.presentation.events.PasswordResetEmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.StringUtils;

@Component
public class PasswordResetEmailSender {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetEmailSender.class);

    private final JavaMailSender mailSender;
    private final String from;

    public PasswordResetEmailSender(
            JavaMailSender mailSender,
            @Value("${app.mail.from:}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendResetLink(PasswordResetEmailEvent event) {
        try {
            if (!StringUtils.hasText(from)) {
                throw new IllegalStateException("Falta configurar MAIL_FROM o MAIL_USERNAME para enviar correos.");
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(event.recipient());
            message.setSubject("Recuperación de contraseña - PsicoGestión");
            message.setText("""
                    Hola %s:

                    Recibimos una solicitud para restablecer la contraseña de tu cuenta.
                    Usa el siguiente enlace dentro de los próximos 30 minutos:

                    %s

                    Si no solicitaste este cambio, puedes ignorar este correo.
                    """.formatted(event.recipientName(), event.resetUrl()));
            mailSender.send(message);
        } catch (MailException | IllegalStateException exception) {
            log.error("No se pudo enviar el correo de recuperación para la cuenta id={}", event.accountId(), exception);
        }
    }
}
