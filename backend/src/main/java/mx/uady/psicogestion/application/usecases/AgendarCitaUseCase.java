package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.application.ports.out.ICitaRepository;
import mx.uady.psicogestion.application.ports.out.INotificacionService;
import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import mx.uady.psicogestion.domain.entities.EstadoCitaEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AgendarCitaUseCase {

    private final ICitaRepository citaRepository;
    private final INotificacionService notificacionService;

    public AgendarCitaUseCase(ICitaRepository citaRepository, INotificacionService notificacionService) {
        this.citaRepository = citaRepository;
        this.notificacionService = notificacionService;
    }

    public Cita ejecutar(Cita cita, BloqueHorario horario, String correoPaciente, String telefonoPaciente) {
        if (!horario.validarRangoOperativo()) throw new IllegalArgumentException("El rango de horario operativo es inválido.");

        boolean hayColision = citaRepository.buscarColisiones(cita.getIdTerapeuta(), cita.getIdSala(), horario.getFecha(), horario.getHoraInicio(), horario.getHoraFin());
        if (hayColision) throw new IllegalStateException("Horario ocupado. Existe una colisión para el terapeuta o la sala.");

        cita.generarFolioUnico();
        Cita citaGuardada = citaRepository.guardar(cita, horario);

        notificacionService.enviarConfirmacion(correoPaciente, telefonoPaciente, citaGuardada);
        return citaGuardada;
    }

    public Cita modificar(Integer idCita, Cita datosNuevos, BloqueHorario nuevoHorario) {
        if (!nuevoHorario.validarRangoOperativo()) throw new IllegalArgumentException("El rango de horario operativo es inválido.");

        Cita existente = citaRepository.buscarPorId(idCita).orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        existente.setIdPaciente(datosNuevos.getIdPaciente());
        existente.setIdTerapeuta(datosNuevos.getIdTerapeuta());
        existente.setIdSala(datosNuevos.getIdSala());
        existente.setNotas(datosNuevos.getNotas());

        if (datosNuevos.getEstado() != null) {
            existente.setEstado(datosNuevos.getEstado());
        }

        return citaRepository.guardar(existente, nuevoHorario);
    }

    public void cancelarCita(Integer idCita) {
        citaRepository.cambiarEstado(idCita, EstadoCitaEnum.CANCELADA);
    }

    public List<Map<String, Object>> listarTodasConDetalles() {
        return citaRepository.obtenerTodasConDetalles();
    }
}