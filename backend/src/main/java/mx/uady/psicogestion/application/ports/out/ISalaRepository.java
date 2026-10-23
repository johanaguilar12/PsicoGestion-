package mx.uady.psicogestion.application.ports.out;

import mx.uady.psicogestion.domain.entities.Sala;
import java.util.List;
import java.util.Optional;

public interface ISalaRepository {
    Sala guardar(Sala sala);
    List<Sala> obtenerTodas();
    void eliminar(Integer idSala);
    Optional<Sala> buscarPorNombre(String nombre);
    Optional<Sala> buscarPorId(Integer idSala); // Agregado para soportar edición y vistas detalle
}