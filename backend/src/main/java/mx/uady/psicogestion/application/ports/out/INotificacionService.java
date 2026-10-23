package mx.uady.psicogestion.application.ports.out;

import mx.uady.psicogestion.domain.entities.Cita;

public interface INotificacionService {
    void enviarConfirmacion(String correo, String telefono, Cita detalles);
}