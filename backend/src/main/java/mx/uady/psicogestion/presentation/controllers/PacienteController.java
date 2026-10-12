package mx.uady.psicogestion.presentation.controllers;

import mx.uady.psicogestion.application.usecases.GestionarPacientesUseCase;
import mx.uady.psicogestion.domain.entities.Paciente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    private final GestionarPacientesUseCase gestionarPacientesUseCase;

    public PacienteController(GestionarPacientesUseCase gestionarPacientesUseCase) {
        this.gestionarPacientesUseCase = gestionarPacientesUseCase;
    }

    @PostMapping
    public ResponseEntity<?> registrarPaciente(@RequestBody Paciente paciente) {
        try {
            Paciente nuevoPaciente = gestionarPacientesUseCase.registrar(paciente);
            return new ResponseEntity<>(nuevoPaciente, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Retorna un Error 400 con el mensaje exacto de la regla de negocio violada
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Paciente>> listarPacientesActivos() {
        return new ResponseEntity<>(gestionarPacientesUseCase.listarActivos(), HttpStatus.OK);
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<?> darDeBaja(@PathVariable Integer id) {
        try {
            gestionarPacientesUseCase.darDeBaja(id);
            return new ResponseEntity<>("Paciente dado de baja exitosamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}