
package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.Paciente;
import mx.uady.psicogestion.application.ports.out.IPacienteRepository;
import mx.uady.psicogestion.application.ports.out.ITerapeutaRepository;
import mx.uady.psicogestion.application.ports.out.ISecretariaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class GestionarPacientesUseCase {

    private final IPacienteRepository pacienteRepository;
    private final ITerapeutaRepository terapeutaRepository;
    private final ISecretariaRepository secretariaRepository;

    public GestionarPacientesUseCase(
            IPacienteRepository pacienteRepository,
            ITerapeutaRepository terapeutaRepository,
            ISecretariaRepository secretariaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.terapeutaRepository = terapeutaRepository;
        this.secretariaRepository = secretariaRepository;
    }

    private void validarDatos(Paciente paciente) {
        if (!paciente.esMayorDeSieteAnios()) {
            throw new IllegalArgumentException(
                "El paciente debe tener al menos 15 años cumplidos."
            );
        }

        if (!paciente.esCurpValida()) {
            throw new IllegalArgumentException(
                "La CURP debe contener exactamente 18 caracteres."
            );
        }

        if (!paciente.esTelefonoValido()) {
            throw new IllegalArgumentException(
                "El teléfono debe contener exactamente 10 dígitos numéricos."
            );
        }
    }

    private void validarDuplicados(
            Paciente paciente,
            Integer idActual) {

        String curp = paciente.getCurp().trim()
                .toUpperCase(Locale.ROOT);
        String telefono = paciente.getTelefono();

        pacienteRepository.buscarPorCurp(curp)
                .ifPresent(encontrado -> {
                    if (idActual == null ||
                            !idActual.equals(encontrado.getIdPaciente())) {
                        throw new IllegalStateException(
                            "La CURP ya está registrada en el sistema."
                        );
                    }
                });

        pacienteRepository.buscarPorTelefono(telefono)
                .ifPresent(encontrado -> {
                    if (idActual == null ||
                            !idActual.equals(encontrado.getIdPaciente())) {
                        throw new IllegalStateException(
                            "El teléfono ya está registrado en el sistema."
                        );
                    }
                });

        if (terapeutaRepository.buscarPorTelefono(telefono).isPresent()) {
            throw new IllegalStateException(
                "El teléfono ya está registrado por un terapeuta."
            );
        }

        if (secretariaRepository.buscarPorTelefono(telefono).isPresent()) {
            throw new IllegalStateException(
                "El teléfono ya está registrado por un usuario."
            );
        }
    }

    public Paciente registrar(Paciente paciente) {
        validarDatos(paciente);
        validarDuplicados(paciente, null);

        paciente.setCurp(
            paciente.getCurp().trim().toUpperCase(Locale.ROOT)
        );

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
                .orElseThrow(() -> new IllegalArgumentException(
                    "Paciente no encontrado con ID: " + idPaciente
                ));
    }

    public Paciente actualizar(
            Integer id,
            Paciente pacienteActualizado) {

        buscarPorId(id);
        pacienteActualizado.setIdPaciente(id);

        validarDatos(pacienteActualizado);
        validarDuplicados(pacienteActualizado, id);

        pacienteActualizado.setCurp(
            pacienteActualizado.getCurp().trim()
                .toUpperCase(Locale.ROOT)
        );

        return pacienteRepository.actualizar(pacienteActualizado);
    }

    public void eliminarFisicamente(Integer idPaciente) {
        buscarPorId(idPaciente);
        pacienteRepository.eliminar(idPaciente);
    }
}
