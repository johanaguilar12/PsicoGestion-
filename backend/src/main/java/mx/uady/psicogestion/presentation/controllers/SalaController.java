package mx.uady.psicogestion.presentation.controllers;

import mx.uady.psicogestion.application.usecases.GestionarSalasUseCase;
import mx.uady.psicogestion.domain.entities.Sala;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salas")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4321", "http://localhost:3000"})
public class SalaController {

    private final GestionarSalasUseCase gestionarSalasUseCase;

    public SalaController(GestionarSalasUseCase gestionarSalasUseCase) {
        this.gestionarSalasUseCase = gestionarSalasUseCase;
    }

    @PostMapping
    public ResponseEntity<?> registrarSala(@RequestBody Sala sala) {
        try {
            Sala nuevaSala = gestionarSalasUseCase.registrar(sala);
            return new ResponseEntity<>(nuevaSala, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Sala>> listarSalas() {
        return new ResponseEntity<>(gestionarSalasUseCase.listarTodas(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarSalaPorId(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(gestionarSalasUseCase.buscarPorId(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSala(@PathVariable Integer id, @RequestBody Sala sala) {
        try {
            Sala actualizada = gestionarSalasUseCase.actualizar(id, sala);
            return new ResponseEntity<>(actualizada, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSala(@PathVariable Integer id) {
        try {
            gestionarSalasUseCase.eliminarSala(id);
            return new ResponseEntity<>("Sala eliminada", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}