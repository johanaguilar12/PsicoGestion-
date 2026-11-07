package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.application.ports.out.ICitaRepository;
import mx.uady.psicogestion.application.ports.out.INotificacionService;
import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import mx.uady.psicogestion.domain.entities.EstadoCitaEnum;
import mx.uady.psicogestion.infrastructure.adapters.BloqueoReservaService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AgendarCitaUseCase {

    private final ICitaRepository citaRepository;
    private final INotificacionService notificacionService;
    private final BloqueoReservaService bloqueoReservaService;

    public AgendarCitaUseCase(
            ICitaRepository citaRepository,
            INotificacionService notificacionService,
            BloqueoReservaService bloqueoReservaService
    ) {
        this.citaRepository = citaRepository;
        this.notificacionService = notificacionService;
        this.bloqueoReservaService = bloqueoReservaService;
    }

    private BloqueoReservaService.Solicitud solicitud(
            Cita cita,
            BloqueHorario horario
    ) {
        return new BloqueoReservaService.Solicitud(
                cita.getIdPaciente(),
                cita.getIdTerapeuta(),
                cita.getIdSala(),
                horario
        );
    }

    private void validarColision(
            Cita cita,
            BloqueHorario horario,
            Integer excluida
    ) {
        boolean hayColision = citaRepository.buscarColisiones(
                cita.getIdTerapeuta(),
                cita.getIdSala(),
                cita.getIdPaciente(),
                horario.getFecha(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                excluida
        );

        if (hayColision) {
            throw new IllegalStateException(
                    "Horario ocupado. Existe una colisión " +
                    "para el paciente, terapeuta o la sala."
            );
        }
    }

    private Cita obtenerCita(Integer idCita) {
        return citaRepository.buscarPorId(idCita)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Cita no encontrada: " + idCita
                        )
                );
    }

    private BloqueHorario obtenerHorario(Integer idCita) {
        return citaRepository.buscarHorarioPorCita(idCita)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "La cita no tiene horario asignado."
                        )
                );
    }

    private boolean mismoHorario(
            BloqueHorario a,
            BloqueHorario b
    ) {
        return Objects.equals(a.getFecha(), b.getFecha())
                && Objects.equals(
                        a.getHoraInicio(), b.getHoraInicio()
                )
                && Objects.equals(
                        a.getHoraFin(), b.getHoraFin()
                );
    }

    private boolean mismosRecursos(Cita a, Cita b) {
        return Objects.equals(
                    a.getIdPaciente(), b.getIdPaciente()
                )
                && Objects.equals(
                    a.getIdTerapeuta(), b.getIdTerapeuta()
                )
                && Objects.equals(
                    a.getIdSala(), b.getIdSala()
                );
    }

    // =====================================================
    // CREAR
    // =====================================================

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Cita ejecutar(
            Cita cita,
            BloqueHorario horario,
            String correoPaciente,
            String telefonoPaciente
    ) {
        bloqueoReservaService.validarHorario(horario);

        // Bloquear las dos medias horas correspondientes.
        bloqueoReservaService.bloquear(
                solicitud(cita, horario)
        );

        // Comprobar disponibilidad con los bloqueos adquiridos.
        validarColision(cita, horario, -1);

        cita.generarFolioUnico();

        Cita guardada = citaRepository.guardar(
                cita, horario
        );

        // Por ahora conservamos la notificación existente.
        notificacionService.enviarConfirmacion(
                correoPaciente,
                telefonoPaciente,
                guardada
        );

        return guardada;
    }

    // =====================================================
    // MODIFICAR
    // =====================================================

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Cita modificar(
            Integer idCita,
            Cita datosNuevos,
            BloqueHorario nuevoHorario
    ) {
        bloqueoReservaService.validarHorario(nuevoHorario);

        // Lectura inicial para conocer qué recursos bloquear.
        Cita anterior = obtenerCita(idCita);
        BloqueHorario horarioAnterior = obtenerHorario(idCita);

        if (anterior.getEstado() == EstadoCitaEnum.CANCELADA) {
            throw new IllegalStateException(
                    "No se puede modificar una cita cancelada."
            );
        }

        // Bloquear recursos anteriores y nuevos juntos,
        // siempre en orden estable.
        bloqueoReservaService.bloquear(
                solicitud(anterior, horarioAnterior),
                solicitud(datosNuevos, nuevoHorario)
        );

        // Bloquear la cita después de bloquear las franjas.
        Cita existente = citaRepository.bloquearCita(idCita)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Cita no encontrada."
                        )
                );

        // Verificar que no se modificó durante la espera.
        BloqueHorario horarioActual = obtenerHorario(idCita);

        if (!mismosRecursos(anterior, existente)
                || !mismoHorario(
                        horarioAnterior, horarioActual
                )) {
            throw new IllegalStateException(
                    "La cita cambió durante la operación. " +
                    "Vuelve a intentar modificarla."
            );
        }

        if (existente.getEstado() == EstadoCitaEnum.CANCELADA) {
            throw new IllegalStateException(
                    "No se puede modificar una cita cancelada."
            );
        }

        // Verificar colisiones excluyendo la cita original.
        validarColision(
                datosNuevos,
                nuevoHorario,
                idCita
        );

        // Conservar ID y folio originales.
        existente.setIdPaciente(
                datosNuevos.getIdPaciente()
        );
        existente.setIdTerapeuta(
                datosNuevos.getIdTerapeuta()
        );
        existente.setIdSala(
                datosNuevos.getIdSala()
        );
        existente.setNotas(
                datosNuevos.getNotas()
        );

        if (datosNuevos.getEstado() != null) {
            existente.setEstado(
                    datosNuevos.getEstado()
            );
        }

        return citaRepository.guardar(
                existente,
                nuevoHorario
        );
    }

    // =====================================================
    // CANCELAR
    // =====================================================

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void cancelarCita(Integer idCita) {

        Cita anterior = obtenerCita(idCita);

        if (anterior.getEstado() == EstadoCitaEnum.CANCELADA) {
            return;
        }

        BloqueHorario horarioAnterior =
                obtenerHorario(idCita);

        // Adquirir los mismos bloqueos que usa la creación.
        bloqueoReservaService.bloquear(
                solicitud(anterior, horarioAnterior)
        );

        Cita existente = citaRepository.bloquearCita(idCita)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Cita no encontrada."
                        )
                );

        BloqueHorario horarioActual =
                obtenerHorario(idCita);

        if (!mismosRecursos(anterior, existente)
                || !mismoHorario(
                        horarioAnterior, horarioActual
                )) {
            throw new IllegalStateException(
                    "La cita cambió durante la cancelación. " +
                    "Vuelve a intentar la operación."
            );
        }

        if (existente.getEstado() == EstadoCitaEnum.CANCELADA) {
            return;
        }

        citaRepository.cambiarEstado(
                idCita,
                EstadoCitaEnum.CANCELADA
        );
    }

    // =====================================================
    // CONSULTAR AGENDA
    // =====================================================

    public List<Map<String, Object>> listarTodasConDetalles() {
        return citaRepository.obtenerTodasConDetalles();
    }
}
