package mx.uady.psicogestion.application.ports.out;

import mx.uady.psicogestion.domain.entities.Paciente;
import java.util.List;
import java.util.Optional;

public interface IPacienteRepository {
    Paciente guardar(Paciente paciente);
    Optional<Paciente> buscarPorCurp(String curp);
    Optional<Paciente> buscarPorTelefono(String telefono);
    List<Paciente> obtenerActivos();
    Optional<Paciente> buscarPorId(Integer idPaciente);
    void marcarInactivo(Integer idPaciente);
    Paciente actualizar(Paciente paciente);
    void eliminar(Integer idPaciente);
}