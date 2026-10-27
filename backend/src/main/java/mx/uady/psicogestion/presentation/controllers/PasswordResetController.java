package mx.uady.psicogestion.presentation.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import mx.uady.psicogestion.infrastructure.adapters.PasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/password-reset")
public class PasswordResetController {

    private static final String REQUEST_MESSAGE =
            "Si existe una cuenta activa con ese correo, recibirás instrucciones para recuperar tu contraseña.";

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/request")
    public RequestResponse request(@Valid @RequestBody RequestReset request) {
        passwordResetService.requestReset(request.email());
        return new RequestResponse(REQUEST_MESSAGE);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@Valid @RequestBody ConfirmReset request) {
        try {
            passwordResetService.resetPassword(request.token(), request.newPassword());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(exception.getMessage()));
        }
    }

    public record RequestReset(@NotBlank @Email String email) {
    }

    public record ConfirmReset(@NotBlank String token, @NotBlank String newPassword) {
    }

    public record RequestResponse(String message) {
    }

    public record ErrorResponse(String message) {
    }
}
