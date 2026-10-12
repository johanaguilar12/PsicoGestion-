package mx.uady.psicogestion.application.ports.out;

import mx.uady.psicogestion.domain.entities.Terapeuta;
import java.util.List;
import java.util.Optional;

public interface ITerapeutaRepository {
    Terapeuta guardar(Terapeuta terapeuta);
    List<Terapeuta> obtenerTodos();
    Optional<Terapeuta> buscarPorId(Integer idTerapeuta);
    Terapeuta actualizar(Terapeuta terapeuta);
    Optional<Terapeuta> buscarPorCedula(String cedula); // Cédula única[cite: 4]
    Optional<Terapeuta> buscarPorCorreo(String correo); // Correo único[cite: 4]
    Optional<Terapeuta> buscarPorTelefono(String telefono); // Teléfono único[cite: 4]
}