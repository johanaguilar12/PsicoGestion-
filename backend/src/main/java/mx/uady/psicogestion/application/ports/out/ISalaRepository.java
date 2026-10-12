package mx.uady.psicogestion.application.ports.out;

import mx.uady.psicogestion.domain.entities.Sala;
import java.util.List;

public interface ISalaRepository {
    Sala guardar(Sala sala);
    List<Sala> obtenerTodas();
    void eliminar(Integer idSala);
}