package mx.uady.psicogestion.presentation.controllers;

import mx.uady.psicogestion.application.usecases.AgendarCitaUseCase;
import mx.uady.psicogestion.domain.entities.BloqueHorario;
import mx.uady.psicogestion.domain.entities.Cita;
import mx.uady.psicogestion.domain.entities.EstadoCitaEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/citas")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4321", "http://localhost:3000"})
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
            cita.setNotas(request.getNotas());

            BloqueHorario horario = new BloqueHorario();
            horario.setFecha(request.getFecha());
            horario.setHoraInicio(request.getHoraInicio());
            horario.setHoraFin(request.getHoraFin());

            Cita nueva = agendarCitaUseCase.ejecutar(cita, horario, request.getCorreoPaciente(), request.getTelefonoPaciente());
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detalles")
    public ResponseEntity<List<Map<String, Object>>> listarCitasConDetalles() {
        return new ResponseEntity<>(agendarCitaUseCase.listarTodasConDetalles(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarCita(@PathVariable Integer id, @RequestBody AgendarCitaRequest request) {
        try {
            Cita cita = new Cita();
            cita.setIdPaciente(request.getIdPaciente());
            cita.setIdTerapeuta(request.getIdTerapeuta());
            cita.setIdSala(request.getIdSala());
            cita.setNotas(request.getNotas());

            if (request.getEstado() != null && !request.getEstado().trim().isEmpty()) {
                cita.setEstado(EstadoCitaEnum.valueOf(request.getEstado().toUpperCase()));
            }

            BloqueHorario horario = new BloqueHorario();
            horario.setFecha(request.getFecha());
            horario.setHoraInicio(request.getHoraInicio());
            horario.setHoraFin(request.getHoraFin());

            Cita modificada = agendarCitaUseCase.modificar(id, cita, horario);
            return new ResponseEntity<>(modificada, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarCita(@PathVariable Integer id) {
        try {
            agendarCitaUseCase.cancelarCita(id);
            return new ResponseEntity<>("Cita cancelada correctamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static class AgendarCitaRequest {
        private Integer idPaciente;
        private Integer idTerapeuta;
        private Integer idSala;
        private LocalDate fecha;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private String estado;
        private String notas;
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
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getNotas() { return notas; }
        public void setNotas(String notas) { this.notas = notas; }
        public String getCorreoPaciente() { return correoPaciente; }
        public void setCorreoPaciente(String correoPaciente) { this.correoPaciente = correoPaciente; }
        public String getTelefonoPaciente() { return telefonoPaciente; }
        public void setTelefonoPaciente(String telefonoPaciente) { this.telefonoPaciente = telefonoPaciente; }
    }
}