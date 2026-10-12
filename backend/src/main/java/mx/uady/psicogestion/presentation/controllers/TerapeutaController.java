package mx.uady.psicogestion.presentation.controllers;

import mx.uady.psicogestion.application.usecases.GestionarTerapeutasUseCase;
import mx.uady.psicogestion.domain.entities.Terapeuta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/terapeutas")
public class TerapeutaController {

    private final GestionarTerapeutasUseCase gestionarTerapeutasUseCase;

    public TerapeutaController(GestionarTerapeutasUseCase gestionarTerapeutasUseCase) {
        this.gestionarTerapeutasUseCase = gestionarTerapeutasUseCase;
    }

    @PostMapping
    public ResponseEntity<?> registrarTerapeuta(@RequestBody Terapeuta terapeuta) {
        try {
            Terapeuta nuevoTerapeuta = gestionarTerapeutasUseCase.registrar(terapeuta);
            return new ResponseEntity<>(nuevoTerapeuta, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Terapeuta>> listarTerapeutas() {
        return new ResponseEntity<>(gestionarTerapeutasUseCase.listarTodos(), HttpStatus.OK);
    }
}