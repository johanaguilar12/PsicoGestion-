package mx.uady.psicogestion.presentation.controllers;

import mx.uady.psicogestion.application.usecases.RegistrarUsuarioUseCase;
import mx.uady.psicogestion.domain.entities.Secretaria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cuentas")
public class SecretariaController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    public SecretariaController(RegistrarUsuarioUseCase registrarUsuarioUseCase) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarSecretaria(@RequestBody Secretaria secretaria) {
        try {
            Secretaria nuevaSecretaria = registrarUsuarioUseCase.ejecutar(secretaria);
            return new ResponseEntity<>(nuevaSecretaria, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Retorna un 400 Bad Request con el mensaje de error de la regla de negocio
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Captura errores inesperados (como duplicidad de teléfono en base de datos)
            return new ResponseEntity<>("Error al registrar la secretaria.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}