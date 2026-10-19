package mx.uady.psicogestion.application.usecases;

import mx.uady.psicogestion.application.ports.out.ISalaRepository;
import mx.uady.psicogestion.domain.entities.Sala;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GestionarSalasUseCase {

    private final ISalaRepository salaRepository;

    public GestionarSalasUseCase(ISalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    public Sala registrar(Sala sala) {
        if (!sala.validarNombreCorto()) {
            throw new IllegalArgumentException("El nombre de la sala es inválido o excede los 10 caracteres.");
        }

        if (salaRepository.buscarPorNombre(sala.getNombreSala()).isPresent()) {
            throw new IllegalStateException("El nombre de la sala ya está registrado.");
        }

        return salaRepository.guardar(sala);
    }

    public List<Sala> listarTodas() {
        return salaRepository.obtenerTodas();
    }

    public void eliminarSala(Integer idSala) {
        salaRepository.eliminar(idSala);
    }
}