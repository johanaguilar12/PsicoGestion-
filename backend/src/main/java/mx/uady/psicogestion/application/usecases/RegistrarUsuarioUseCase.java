package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.Secretaria;
import mx.uady.psicogestion.application.ports.out.ISecretariaRepository;
import mx.uady.psicogestion.application.ports.out.IAutenticacionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrarUsuarioUseCase {

    private final ISecretariaRepository secretariaRepository;
    private final IAutenticacionService autenticacionService;

    public RegistrarUsuarioUseCase(ISecretariaRepository secretariaRepository, IAutenticacionService autenticacionService) {
        this.secretariaRepository = secretariaRepository;
        this.autenticacionService = autenticacionService;
    }

    public Secretaria ejecutar(Secretaria nuevaSecretaria) {
        if (!nuevaSecretaria.esNombreValido()) {
            throw new IllegalArgumentException("El nombre debe tener un máximo de 50 caracteres.");
        }
        if (!nuevaSecretaria.esCorreoInstitucionalValido()) {
            throw new IllegalArgumentException("El correo debe tener el formato @correo.uady.mx");
        }
        if (!nuevaSecretaria.esContrasenaSegura()) {
            throw new IllegalArgumentException("La contraseña no cumple con los criterios de seguridad (8-25 caracteres, 1 número, 1 mayúscula, 1 carácter especial).");
        }
        if (secretariaRepository.buscarPorCorreo(nuevaSecretaria.getCorreoElectronico()).isPresent()) {
            throw new IllegalStateException("El correo ya está registrado en el sistema.");
        }
        if (secretariaRepository.buscarPorTelefono(nuevaSecretaria.getTelefono()).isPresent()) {
            throw new IllegalStateException("El teléfono ya está registrado en el sistema.");
        }

        String hash = autenticacionService.cifrarContrasena(nuevaSecretaria.getContrasena());
        nuevaSecretaria.setContrasena(hash);
        nuevaSecretaria.setFechaRegistro(LocalDateTime.now());

        return secretariaRepository.guardar(nuevaSecretaria);
    }

    public List<Secretaria> listarTodas() {
        return secretariaRepository.obtenerTodas();
    }

    public Secretaria buscarPorId(Integer id) {
        return secretariaRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
    }

    public void darDeBaja(Integer id) {
        buscarPorId(id);
        secretariaRepository.marcarInactivo(id);
    }

    public void eliminarFisicamente(Integer id) {
        buscarPorId(id);
        secretariaRepository.eliminar(id);
    }
}