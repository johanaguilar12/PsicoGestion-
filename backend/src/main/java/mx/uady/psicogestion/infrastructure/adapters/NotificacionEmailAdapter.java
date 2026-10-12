package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.application.ports.out.INotificacionService;
import mx.uady.psicogestion.domain.entities.Cita;
import org.springframework.stereotype.Component;

@Component
public class NotificacionEmailAdapter implements INotificacionService {
    @Override
    public void enviarConfirmacion(String correo, String telefono, Cita detalles) {
        // En un escenario real aquí se conecta a un SMTP o API de SMS
        System.out.println("--- SIMULACIÓN DE NOTIFICACIÓN ---");
        System.out.println("Enviando correo de confirmación a: " + correo);
        System.out.println("Folio de la Cita Generado: " + detalles.getFolio());
    }
}