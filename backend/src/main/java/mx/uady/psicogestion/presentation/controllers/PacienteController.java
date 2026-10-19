package mx.uady.psicogestion.presentation.controllers;

import mx.uady.psicogestion.application.usecases.GestionarPacientesUseCase;
import mx.uady.psicogestion.domain.entities.Paciente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pacientes")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4321", "http://localhost:3000"})
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
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientesActivos() {
        return new ResponseEntity<>(gestionarPacientesUseCase.listarActivos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPacientePorId(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(gestionarPacientesUseCase.buscarPorId(id), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<?> darDeBaja(@PathVariable Integer id) {
        System.out.println(">>> RECIBIDA PETICIÓN PARA DESACTIVAR PACIENTE ID: " + id);
        try {
            gestionarPacientesUseCase.darDeBaja(id);
            System.out.println(">>> PACIENTE DESACTIVADO EN BASE DE DATOS");
            return new ResponseEntity<>("Paciente dado de baja exitosamente", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(">>> ERROR AL DESACTIVAR: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPaciente(@PathVariable Integer id, @RequestBody Paciente paciente) {
        try {
            Paciente actualizado = gestionarPacientesUseCase.actualizar(id, paciente);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPaciente(@PathVariable Integer id) {
        System.out.println(">>> RECIBIDA PETICIÓN PARA ELIMINAR PACIENTE ID: " + id);
        try {
            gestionarPacientesUseCase.eliminarFisicamente(id);
            System.out.println(">>>  PACIENTE BORRADO DE LA BASE DE DATOS");
            return new ResponseEntity<>("Paciente eliminado correctamente", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.out.println(">>> ERROR AL ELIMINAR (NO ENCONTRADO): " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(">>>  ERROR AL ELIMINAR: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}