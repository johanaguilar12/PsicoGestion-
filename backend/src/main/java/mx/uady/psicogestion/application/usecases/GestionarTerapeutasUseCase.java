
package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.Terapeuta;
import mx.uady.psicogestion.application.ports.out.ITerapeutaRepository;
import mx.uady.psicogestion.application.ports.out.IPacienteRepository;
import mx.uady.psicogestion.application.ports.out.ISecretariaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GestionarTerapeutasUseCase {

    private final ITerapeutaRepository terapeutaRepository;
    private final IPacienteRepository pacienteRepository;
    private final ISecretariaRepository secretariaRepository;

    public GestionarTerapeutasUseCase(
            ITerapeutaRepository terapeutaRepository,
            IPacienteRepository pacienteRepository,
            ISecretariaRepository secretariaRepository) {
        this.terapeutaRepository = terapeutaRepository;
        this.pacienteRepository = pacienteRepository;
        this.secretariaRepository = secretariaRepository;
    }

    private void validarDatos(Terapeuta terapeuta) {
        if (!terapeuta.esNombreValido()) {
            throw new IllegalArgumentException(
                "El nombre debe tener un máximo de 50 caracteres."
            );
        }

        if (!terapeuta.esFechaNacimientoValida()) {
            throw new IllegalArgumentException(
                "El terapeuta debe tener al menos 22 años cumplidos."
            );
        }

        if (!terapeuta.esCorreoValido()) {
            throw new IllegalArgumentException(
                "Ingresa un correo electrónico con un formato válido."
            );
        }

        if (!terapeuta.esTelefonoValido()) {
            throw new IllegalArgumentException(
                "El teléfono debe contener exactamente 10 dígitos numéricos."
            );
        }

        if (!terapeuta.esCedulaValida()) {
            throw new IllegalArgumentException(
                "La cédula profesional debe contener entre 7 y 9 dígitos numéricos."
            );
        }
    }

    private void validarDuplicados(
            Terapeuta terapeuta,
            Integer idActual) {

        String cedula = terapeuta.getCedulaProfesional();
        String correo = terapeuta.getCorreoElectronico().trim();
        String telefono = terapeuta.getTelefono();

        terapeutaRepository.buscarPorCedula(cedula)
                .ifPresent(encontrado -> {
                    if (idActual == null ||
                            !idActual.equals(encontrado.getIdTerapeuta())) {
                        throw new IllegalStateException(
                            "La cédula profesional ya está registrada."
                        );
                    }
                });

        terapeutaRepository.buscarPorCorreo(correo)
                .ifPresent(encontrado -> {
                    if (idActual == null ||
                            !idActual.equals(encontrado.getIdTerapeuta())) {
                        throw new IllegalStateException(
                            "El correo electrónico ya está registrado."
                        );
                    }
                });

        terapeutaRepository.buscarPorTelefono(telefono)
                .ifPresent(encontrado -> {
                    if (idActual == null ||
                            !idActual.equals(encontrado.getIdTerapeuta())) {
                        throw new IllegalStateException(
                            "El teléfono ya está registrado."
                        );
                    }
                });

        if (secretariaRepository.buscarPorCorreo(correo).isPresent()) {
            throw new IllegalStateException(
                "El correo electrónico ya está registrado por un usuario."
            );
        }

        if (secretariaRepository.buscarPorTelefono(telefono).isPresent()) {
            throw new IllegalStateException(
                "El teléfono ya está registrado por un usuario."
            );
        }

        if (pacienteRepository.buscarPorTelefono(telefono).isPresent()) {
            throw new IllegalStateException(
                "El teléfono ya está registrado por un paciente."
            );
        }
    }

    public Terapeuta registrar(Terapeuta terapeuta) {
        validarDatos(terapeuta);
        validarDuplicados(terapeuta, null);

        terapeuta.setCorreoElectronico(
            terapeuta.getCorreoElectronico().trim()
        );

        return terapeutaRepository.guardar(terapeuta);
    }

    public List<Terapeuta> listarTodos() {
        return terapeutaRepository.obtenerActivos();
    }

    public Terapeuta buscarPorId(Integer idTerapeuta) {
        return terapeutaRepository.buscarPorId(idTerapeuta)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Terapeuta no encontrado."
                ));
    }

    public Terapeuta actualizar(Integer id, Terapeuta terapeuta) {
        buscarPorId(id);
        terapeuta.setIdTerapeuta(id);

        validarDatos(terapeuta);
        validarDuplicados(terapeuta, id);

        terapeuta.setCorreoElectronico(
            terapeuta.getCorreoElectronico().trim()
        );

        return terapeutaRepository.actualizar(terapeuta);
    }

    public void darDeBaja(Integer idTerapeuta) {
        buscarPorId(idTerapeuta);
        terapeutaRepository.marcarInactivo(idTerapeuta);
    }

    public void eliminarFisicamente(Integer idTerapeuta) {
        buscarPorId(idTerapeuta);
        terapeutaRepository.eliminar(idTerapeuta);
    }
}
