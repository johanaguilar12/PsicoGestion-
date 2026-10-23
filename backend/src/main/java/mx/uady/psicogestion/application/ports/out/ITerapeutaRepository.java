package mx.uady.psicogestion.application.ports.out;

import mx.uady.psicogestion.domain.entities.Terapeuta;
import java.util.List;
import java.util.Optional;

public interface ITerapeutaRepository {
    Terapeuta guardar(Terapeuta terapeuta);
    List<Terapeuta> obtenerActivos();
    Optional<Terapeuta> buscarPorId(Integer idTerapeuta);
    Terapeuta actualizar(Terapeuta terapeuta);
    Optional<Terapeuta> buscarPorCedula(String cedula);
    Optional<Terapeuta> buscarPorCorreo(String correo);
    Optional<Terapeuta> buscarPorTelefono(String telefono);
    void marcarInactivo(Integer idTerapeuta);
    void eliminar(Integer idTerapeuta);
}