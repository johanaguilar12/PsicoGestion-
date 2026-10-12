package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.Paciente;
import mx.uady.psicogestion.application.ports.out.IPacienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GestionarPacientesUseCase {

    private final IPacienteRepository pacienteRepository;

    public GestionarPacientesUseCase(IPacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public Paciente registrar(Paciente paciente) {
        // 1. Reglas de Dominio
        if (!paciente.esMayorDeSieteAnios()) {
            throw new IllegalArgumentException("El paciente debe tener 7 años o más al momento del registro.");
        }

        // 2. Reglas de Aplicación (Unicidad en BD)
        if (pacienteRepository.buscarPorCurp(paciente.getCurp()).isPresent()) {
            throw new IllegalStateException("La CURP ya está registrada en el sistema.");
        }
        if (pacienteRepository.buscarPorTelefono(paciente.getTelefono()).isPresent()) {
            throw new IllegalStateException("El teléfono ya está registrado.");
        }

        // 3. Persistencia
        return pacienteRepository.guardar(paciente);
    }

    public List<Paciente> listarActivos() {
        return pacienteRepository.obtenerActivos();
    }

    public void darDeBaja(Integer idPaciente) {
        pacienteRepository.marcarInactivo(idPaciente);
    }
}