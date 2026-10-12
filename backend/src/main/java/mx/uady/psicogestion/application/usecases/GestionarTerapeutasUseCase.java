package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.domain.entities.Terapeuta;
import mx.uady.psicogestion.application.ports.out.ITerapeutaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GestionarTerapeutasUseCase {

    private final ITerapeutaRepository terapeutaRepository;

    public GestionarTerapeutasUseCase(ITerapeutaRepository terapeutaRepository) {
        this.terapeutaRepository = terapeutaRepository;
    }

    public Terapeuta registrar(Terapeuta terapeuta) {
        // 1. Reglas de Dominio
        if (!terapeuta.esFechaNacimientoValida()) {
            throw new IllegalArgumentException("La fecha de nacimiento debe ser estrictamente anterior a la fecha actual.");
        }
        if (!terapeuta.validarDimensionesFoto(200, 200)) {
            throw new IllegalArgumentException("La foto de perfil debe tener dimensiones mínimas de 200x200 píxeles.");
        }

        // 2. Reglas de Aplicación (Unicidad en BD)
        if (terapeutaRepository.buscarPorCedula(terapeuta.getCedulaProfesional()).isPresent()) {
            throw new IllegalStateException("La cédula profesional ya está registrada.");
        }
        if (terapeutaRepository.buscarPorCorreo(terapeuta.getCorreoElectronico()).isPresent()) {
            throw new IllegalStateException("El correo electrónico ya está registrado.");
        }
        if (terapeutaRepository.buscarPorTelefono(terapeuta.getTelefono()).isPresent()) {
            throw new IllegalStateException("El teléfono ya está registrado.");
        }

        // 3. Persistencia
        return terapeutaRepository.guardar(terapeuta);
    }

    public List<Terapeuta> listarTodos() {
        return terapeutaRepository.obtenerTodos();
    }
}