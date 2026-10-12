package mx.uady.psicogestion.presentation.controllers;

import mx.uady.psicogestion.application.usecases.AgendarCitaUseCase;
import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/citas")
public class CitaController {

    private final AgendarCitaUseCase agendarCitaUseCase;

    public CitaController(AgendarCitaUseCase agendarCitaUseCase) {
        this.agendarCitaUseCase = agendarCitaUseCase;
    }

    @PostMapping
    public ResponseEntity<?> agendarCita(@RequestBody AgendarCitaRequest request) {
        try {
            Cita cita = new Cita();
            cita.setIdPaciente(request.getIdPaciente());
            cita.setIdTerapeuta(request.getIdTerapeuta());
            cita.setIdSala(request.getIdSala());

            BloqueHorario horario = new BloqueHorario();
            horario.setFecha(request.getFecha());
            horario.setHoraInicio(request.getHoraInicio());
            horario.setHoraFin(request.getHoraFin());

            Cita nuevaCita = agendarCitaUseCase.ejecutar(cita, horario, request.getCorreoPaciente(), request.getTelefonoPaciente());
            return new ResponseEntity<>(nuevaCita, HttpStatus.CREATED);

        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict para colisiones
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DTO interno para mapear el JSON entrante
    public static class AgendarCitaRequest {
        private Integer idPaciente;
        private Integer idTerapeuta;
        private Integer idSala;
        private LocalDate fecha;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private String correoPaciente;
        private String telefonoPaciente;

        public Integer getIdPaciente() { return idPaciente; }
        public void setIdPaciente(Integer idPaciente) { this.idPaciente = idPaciente; }
        public Integer getIdTerapeuta() { return idTerapeuta; }
        public void setIdTerapeuta(Integer idTerapeuta) { this.idTerapeuta = idTerapeuta; }
        public Integer getIdSala() { return idSala; }
        public void setIdSala(Integer idSala) { this.idSala = idSala; }
        public LocalDate getFecha() { return fecha; }
        public void setFecha(LocalDate fecha) { this.fecha = fecha; }
        public LocalTime getHoraInicio() { return horaInicio; }
        public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
        public LocalTime getHoraFin() { return horaFin; }
        public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
        public String getCorreoPaciente() { return correoPaciente; }
        public void setCorreoPaciente(String correoPaciente) { this.correoPaciente = correoPaciente; }
        public String getTelefonoPaciente() { return telefonoPaciente; }
        public void setTelefonoPaciente(String telefonoPaciente) { this.telefonoPaciente = telefonoPaciente; }
    }
}