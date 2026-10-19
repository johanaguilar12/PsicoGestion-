package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.application.ports.out.IAutenticacionService;
import org.springframework.stereotype.Component;

@Component
public class AutenticacionAdapter implements IAutenticacionService {
    @Override
    public String cifrarContrasena(String contrasenaPlana) {
        return "{bcrypt}hash_simulado_" + contrasenaPlana;
    }
    @Override
    public boolean compararContrasena(String contrasenaPlana, String hash) {
        return hash.equals("{bcrypt}hash_simulado_" + contrasenaPlana);
    }
}