package mx.uady.psicogestion.application.ports.out;

import mx.uady.psicogestion.domain.entities.Paciente;
import java.util.List;
import java.util.Optional;

public interface IPacienteRepository {
    Paciente guardar(Paciente paciente);
    Optional<Paciente> buscarPorCurp(String curp); // La CURP debe ser única[cite: 4]
    Optional<Paciente> buscarPorTelefono(String telefono); // El teléfono debe ser único[cite: 4]
    List<Paciente> obtenerActivos();
    void marcarInactivo(Integer idPaciente); // Para la baja lógica[cite: 4]
}