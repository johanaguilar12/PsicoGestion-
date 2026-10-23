package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.Paciente;
import mx.uady.psicogestion.application.ports.out.IPacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class GestionarPacientesUseCase {

    private final IPacienteRepository pacienteRepository;

    public GestionarPacientesUseCase(IPacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public Paciente registrar(Paciente paciente) {
        if (!paciente.esMayorDeSieteAnios()) {
            throw new IllegalArgumentException("El paciente debe tener 7 años o más al momento del registro.");
        }

        if (pacienteRepository.buscarPorCurp(paciente.getCurp()).isPresent()) {
            throw new IllegalStateException("La CURP ya está registrada en el sistema.");
        }
        if (pacienteRepository.buscarPorTelefono(paciente.getTelefono()).isPresent()) {
            throw new IllegalStateException("El teléfono ya está registrado.");
        }

        return pacienteRepository.guardar(paciente);
    }

    public List<Paciente> listarActivos() {
        return pacienteRepository.obtenerActivos();
    }

    public void darDeBaja(Integer idPaciente) {
        buscarPorId(idPaciente);
        pacienteRepository.marcarInactivo(idPaciente);
    }

    public Paciente buscarPorId(Integer idPaciente) {
        return pacienteRepository.buscarPorId(idPaciente)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + idPaciente));
    }

    public Paciente actualizar(Integer id, Paciente pacienteActualizado) {
        Paciente existente = buscarPorId(id);
        pacienteActualizado.setIdPaciente(id);

        if (!pacienteActualizado.esMayorDeSieteAnios()) {
            throw new IllegalArgumentException("El paciente debe tener 7 años o más.");
        }

        if (!existente.getCurp().equals(pacienteActualizado.getCurp()) &&
                pacienteRepository.buscarPorCurp(pacienteActualizado.getCurp()).isPresent()) {
            throw new IllegalStateException("La nueva CURP ya está registrada por otro paciente.");
        }
        if (!existente.getTelefono().equals(pacienteActualizado.getTelefono()) &&
                pacienteRepository.buscarPorTelefono(pacienteActualizado.getTelefono()).isPresent()) {
            throw new IllegalStateException("El nuevo teléfono ya está registrado por otro paciente.");
        }

        return pacienteRepository.actualizar(pacienteActualizado);
    }

    public void eliminarFisicamente(Integer idPaciente) {
        buscarPorId(idPaciente);
        pacienteRepository.eliminar(idPaciente);
    }
}