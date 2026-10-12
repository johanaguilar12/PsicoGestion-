package mx.uady.psicogestion.presentation.controllers;

import mx.uady.psicogestion.application.usecases.GestionarSalasUseCase;
import mx.uady.psicogestion.domain.entities.Sala;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salas")
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
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Sala>> listarSalas() {
        return new ResponseEntity<>(gestionarSalasUseCase.listarTodas(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSala(@PathVariable Integer id) {
        gestionarSalasUseCase.eliminarSala(id);
        return new ResponseEntity<>("Sala eliminada", HttpStatus.OK);
    }
}