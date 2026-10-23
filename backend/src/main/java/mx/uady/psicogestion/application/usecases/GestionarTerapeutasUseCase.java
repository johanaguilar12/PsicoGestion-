package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.Terapeuta;
import mx.uady.psicogestion.application.ports.out.ITerapeutaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class GestionarTerapeutasUseCase {

    private final ITerapeutaRepository terapeutaRepository;

    public GestionarTerapeutasUseCase(ITerapeutaRepository terapeutaRepository) {
        this.terapeutaRepository = terapeutaRepository;
    }

    public Terapeuta registrar(Terapeuta terapeuta) {
        if (!terapeuta.esNombreValido()) {
            throw new IllegalArgumentException("El nombre debe tener un máximo de 50 caracteres.");
        }
        if (!terapeuta.esFechaNacimientoValida()) {
            throw new IllegalArgumentException("La fecha de nacimiento debe ser estrictamente anterior a la fecha actual.");
        }

        if (terapeutaRepository.buscarPorCedula(terapeuta.getCedulaProfesional()).isPresent()) {
            throw new IllegalStateException("La cédula profesional ya está registrada.");
        }
        if (terapeutaRepository.buscarPorCorreo(terapeuta.getCorreoElectronico()).isPresent()) {
            throw new IllegalStateException("El correo electrónico ya está registrado.");
        }
        if (terapeutaRepository.buscarPorTelefono(terapeuta.getTelefono()).isPresent()) {
            throw new IllegalStateException("El teléfono ya está registrado.");
        }

        return terapeutaRepository.guardar(terapeuta);
    }

    public List<Terapeuta> listarTodos() {
        return terapeutaRepository.obtenerActivos();
    }

    public Terapeuta buscarPorId(Integer idTerapeuta) {
        return terapeutaRepository.buscarPorId(idTerapeuta)
                .orElseThrow(() -> new IllegalArgumentException("Terapeuta no encontrado."));
    }

    public Terapeuta actualizar(Integer id, Terapeuta terapeuta) {
        Terapeuta existente = buscarPorId(id);
        terapeuta.setIdTerapeuta(id);

        if (!terapeuta.esNombreValido()) {
            throw new IllegalArgumentException("El nombre debe tener un máximo de 50 caracteres.");
        }
        if (!terapeuta.esFechaNacimientoValida()) {
            throw new IllegalArgumentException("La fecha de nacimiento debe ser estrictamente anterior a la fecha actual.");
        }

        if (!existente.getCedulaProfesional().equals(terapeuta.getCedulaProfesional()) &&
                terapeutaRepository.buscarPorCedula(terapeuta.getCedulaProfesional()).isPresent()) {
            throw new IllegalStateException("La cédula profesional ya está registrada por otro terapeuta.");
        }
        if (!existente.getCorreoElectronico().equals(terapeuta.getCorreoElectronico()) &&
                terapeutaRepository.buscarPorCorreo(terapeuta.getCorreoElectronico()).isPresent()) {
            throw new IllegalStateException("El correo ya está registrado por otro terapeuta.");
        }
        if (!existente.getTelefono().equals(terapeuta.getTelefono()) &&
                terapeutaRepository.buscarPorTelefono(terapeuta.getTelefono()).isPresent()) {
            throw new IllegalStateException("El teléfono ya está registrado por otro terapeuta.");
        }

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