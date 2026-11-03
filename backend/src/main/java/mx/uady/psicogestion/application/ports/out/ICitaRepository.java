package mx.uady.psicogestion.application.ports.out;

import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import mx.uady.psicogestion.domain.entities.EstadoCitaEnum;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ICitaRepository {
    Cita guardar(Cita cita, BloqueHorario horario);
    boolean buscarColisiones(Integer idTerapeuta, Integer idSala, Integer idPaciente, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Integer idCitaExcluida);
    Optional<Cita> buscarPorFolio(String folio);
    Optional<Cita> buscarPorId(Integer idCita);
    Cita actualizar(Cita cita);
    List<Cita> obtenerTodas();
    List<Map<String, Object>>  obtenerTodasConDetalles();
    void cambiarEstado(Integer idCita, EstadoCitaEnum estado);
}
