package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.application.ports.out.IAutenticacionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AutenticacionAdapter implements IAutenticacionService {
    private final PasswordEncoder passwordEncoder;

    public AutenticacionAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String cifrarContrasena(String contrasenaPlana) {
        return passwordEncoder.encode(contrasenaPlana);
    }

    @Override
    public boolean compararContrasena(String contrasenaPlana, String hash) {
        return passwordEncoder.matches(contrasenaPlana, hash);
    }
}