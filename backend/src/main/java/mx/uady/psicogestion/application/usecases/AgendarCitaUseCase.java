package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.application.ports.out.ICitaRepository;
import mx.uady.psicogestion.application.ports.out.INotificacionService;
import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import org.springframework.stereotype.Service;

@Service
public class AgendarCitaUseCase {

    private final ICitaRepository citaRepository;
    private final INotificacionService notificacionService;

    public AgendarCitaUseCase(ICitaRepository citaRepository, INotificacionService notificacionService) {
        this.citaRepository = citaRepository;
        this.notificacionService = notificacionService;
    }

    public Cita ejecutar(Cita cita, BloqueHorario horario, String correoPaciente, String telefonoPaciente) {
        if (!horario.validarRangoOperativo()) {
            throw new IllegalArgumentException("El rango de horario operativo es inválido.");
        }

        boolean hayColision = citaRepository.buscarColisiones(
                cita.getIdTerapeuta(),
                cita.getIdSala(),
                horario.getFecha(),
                horario.getHoraInicio(),
                horario.getHoraFin()
        );

        if (hayColision) {
            throw new IllegalStateException("Horario ocupado. Existe una colisión para el terapeuta o la sala seleccionada.");
        }

        cita.generarFolioUnico();

        // Guardamos la cita junto con su horario para crear el registro en cita_bloque
        Cita citaGuardada = citaRepository.guardar(cita, horario);

        notificacionService.enviarConfirmacion(correoPaciente, telefonoPaciente, citaGuardada);

        return citaGuardada;
    }
}