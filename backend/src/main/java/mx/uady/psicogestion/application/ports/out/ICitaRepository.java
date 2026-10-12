package mx.uady.psicogestion.application.ports.out;

import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface ICitaRepository {
    Cita guardar(Cita cita, BloqueHorario horario);
    boolean buscarColisiones(Integer idTerapeuta, Integer idSala, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
    Optional<Cita> buscarPorFolio(String folio);
    Cita actualizar(Cita cita);
}