
package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.Secretaria;
import mx.uady.psicogestion.application.ports.out.ISecretariaRepository;
import mx.uady.psicogestion.application.ports.out.ITerapeutaRepository;
import mx.uady.psicogestion.application.ports.out.IPacienteRepository;
import mx.uady.psicogestion.application.ports.out.IAutenticacionService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrarUsuarioUseCase {

    private final ISecretariaRepository secretariaRepository;
    private final ITerapeutaRepository terapeutaRepository;
    private final IPacienteRepository pacienteRepository;
    private final IAutenticacionService autenticacionService;

    public RegistrarUsuarioUseCase(
            ISecretariaRepository secretariaRepository,
            IAutenticacionService autenticacionService,
            ITerapeutaRepository terapeutaRepository,
            IPacienteRepository pacienteRepository) {

        this.secretariaRepository = secretariaRepository;
        this.autenticacionService = autenticacionService;
        this.terapeutaRepository = terapeutaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    private void validarDatos(Secretaria secretaria) {
        if (!secretaria.esNombreValido()) {
            throw new IllegalArgumentException(
                "El nombre debe tener un máximo de 50 caracteres."
            );
        }

        if (!secretaria.esCorreoInstitucionalValido()) {
            throw new IllegalArgumentException(
                "El correo debe tener el formato @correo.uady.mx o @alumnos.uady.mx"
            );
        }

        if (!secretaria.esTelefonoValido()) {
            throw new IllegalArgumentException(
                "El teléfono debe contener exactamente 10 dígitos numéricos."
            );
        }
    }

    private void validarDuplicados(
            Secretaria secretaria,
            Integer idActual) {

        String correo = secretaria.getCorreoElectronico().trim();
        String telefono = secretaria.getTelefono();

        secretariaRepository.buscarPorCorreo(correo)
                .ifPresent(encontrado -> {
                    if (idActual == null ||
                            !idActual.equals(encontrado.getIdSecretaria())) {
                        throw new IllegalStateException(
                            "El correo ya está registrado en el sistema."
                        );
                    }
                });

        secretariaRepository.buscarPorTelefono(telefono)
                .ifPresent(encontrado -> {
                    if (idActual == null ||
                            !idActual.equals(encontrado.getIdSecretaria())) {
                        throw new IllegalStateException(
                            "El teléfono ya está registrado en el sistema."
                        );
                    }
                });

        if (terapeutaRepository.buscarPorCorreo(correo).isPresent()) {
            throw new IllegalStateException(
                "El correo ya está registrado por un terapeuta."
            );
        }

        if (terapeutaRepository.buscarPorTelefono(telefono).isPresent()) {
            throw new IllegalStateException(
                "El teléfono ya está registrado por un terapeuta."
            );
        }

        if (pacienteRepository.buscarPorTelefono(telefono).isPresent()) {
            throw new IllegalStateException(
                "El teléfono ya está registrado por un paciente."
            );
        }
    }

    public Secretaria ejecutar(Secretaria nuevaSecretaria) {
        validarDatos(nuevaSecretaria);

        if (!nuevaSecretaria.esContrasenaSegura()) {
            throw new IllegalArgumentException(
                "La contraseña no cumple con los criterios de seguridad " +
                "(8-25 caracteres, 1 número, 1 mayúscula, 1 carácter especial)."
            );
        }

        validarDuplicados(nuevaSecretaria, null);

        nuevaSecretaria.setCorreoElectronico(
            nuevaSecretaria.getCorreoElectronico().trim()
        );

        String hash = autenticacionService.cifrarContrasena(
            nuevaSecretaria.getContrasena()
        );

        nuevaSecretaria.setContrasena(hash);
        nuevaSecretaria.setFechaRegistro(LocalDateTime.now());

        return secretariaRepository.guardar(nuevaSecretaria);
    }

    public Secretaria actualizar(
            Integer id,
            Secretaria datosActualizados,
            String nuevaContrasena) {

        Secretaria existente = buscarPorId(id);

        validarDatos(datosActualizados);
        validarDuplicados(datosActualizados, id);

        String hashNuevaContrasena = null;

        if (nuevaContrasena != null &&
                !nuevaContrasena.trim().isEmpty()) {

            Secretaria validacion = new Secretaria();
            validacion.setContrasena(nuevaContrasena);

            if (!validacion.esContrasenaSegura()) {
                throw new IllegalArgumentException(
                    "La contraseña no cumple con los criterios de seguridad " +
                    "(8-25 caracteres, 1 número, 1 mayúscula, 1 carácter especial)."
                );
            }

            hashNuevaContrasena =
                    autenticacionService.cifrarContrasena(nuevaContrasena);
        }

        existente.setNombreCompleto(
            datosActualizados.getNombreCompleto()
        );

        existente.setCorreoElectronico(
            datosActualizados.getCorreoElectronico().trim()
        );

        existente.setTelefono(
            datosActualizados.getTelefono()
        );

        existente.setRol(
            datosActualizados.getRol()
        );

        if (hashNuevaContrasena != null) {
            existente.setContrasena(hashNuevaContrasena);
        }

        if (datosActualizados.getFotoPerfil() != null) {
            existente.setFotoPerfil(
                datosActualizados.getFotoPerfil()
            );
        }

        return secretariaRepository.guardar(existente);
    }

    public List<Secretaria> listarTodas() {
        return secretariaRepository.obtenerTodas();
    }

    public Secretaria buscarPorId(Integer id) {
        return secretariaRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Usuario no encontrado."
                ));
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
