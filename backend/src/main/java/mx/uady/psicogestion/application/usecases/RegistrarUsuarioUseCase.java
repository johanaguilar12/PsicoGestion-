package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.Secretaria;
import mx.uady.psicogestion.application.ports.out.ISecretariaRepository;
import mx.uady.psicogestion.application.ports.out.IAutenticacionService;
import org.springframework.stereotype.Service;

@Service
public class RegistrarUsuarioUseCase {

    private final ISecretariaRepository secretariaRepository;
    private final IAutenticacionService autenticacionService;

    public RegistrarUsuarioUseCase(ISecretariaRepository secretariaRepository, IAutenticacionService autenticacionService) {
        this.secretariaRepository = secretariaRepository;
        this.autenticacionService = autenticacionService;
    }

    public Secretaria ejecutar(Secretaria nuevaSecretaria) {
        if (!nuevaSecretaria.esCorreoInstitucionalValido()) {
            throw new IllegalArgumentException("El correo debe tener el formato @correo.uady.mx");
        }
        if (!nuevaSecretaria.esContrasenaSegura()) {
            throw new IllegalArgumentException("La contraseña no cumple con los criterios de seguridad del SEAP");
        }

        if (secretariaRepository.buscarPorCorreo(nuevaSecretaria.getCorreoElectronico()).isPresent()) {
            throw new IllegalStateException("El correo ya está registrado en el sistema");
        }
        if (secretariaRepository.buscarPorTelefono(nuevaSecretaria.getTelefono()).isPresent()) {
            throw new IllegalStateException("El teléfono ya está registrado en el sistema");
        }

        String hash = autenticacionService.cifrarContrasena(nuevaSecretaria.getContrasena());
        nuevaSecretaria.setContrasena(hash);

        return secretariaRepository.guardar(nuevaSecretaria);
    }
}