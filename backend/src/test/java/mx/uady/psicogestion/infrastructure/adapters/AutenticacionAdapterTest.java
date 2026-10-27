package mx.uady.psicogestion.infrastructure.adapters;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AutenticacionAdapterTest {

    @Test
    void cifraYVerificaContrasenasConBcrypt() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        AutenticacionAdapter autenticacionAdapter = new AutenticacionAdapter(passwordEncoder);
        String contrasena = "Secre.2026*";

        String hash = autenticacionAdapter.cifrarContrasena(contrasena);

        assertNotEquals(contrasena, hash);
        assertTrue(hash.startsWith("{bcrypt}"));
        assertTrue(autenticacionAdapter.compararContrasena(contrasena, hash));
        assertFalse(autenticacionAdapter.compararContrasena("incorrecta", hash));
    }
}
