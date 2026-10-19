package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.application.ports.out.ISalaRepository;
import mx.uady.psicogestion.domain.entities.Sala;
import mx.uady.psicogestion.infrastructure.persistence.JpaSalaRepository;
import mx.uady.psicogestion.infrastructure.persistence.SalaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SalaRepositoryAdapter implements ISalaRepository {

    private final JpaSalaRepository jpaRepository;

    public SalaRepositoryAdapter(JpaSalaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Sala guardar(Sala sala) {
        SalaEntity entity = new SalaEntity();
        entity.setIdSala(sala.getIdSala());
        entity.setNombreSala(sala.getNombreSala());

        SalaEntity guardado = jpaRepository.save(entity);
        sala.setIdSala(guardado.getIdSala());
        return sala;
    }

    @Override
    public List<Sala> obtenerTodas() {
        return jpaRepository.findAll().stream().map(e -> {
            Sala s = new Sala();
            s.setIdSala(e.getIdSala());
            s.setNombreSala(e.getNombreSala());
            return s;
        }).collect(Collectors.toList());
    }

    @Override
    public void eliminar(Integer idSala) {
        jpaRepository.deleteById(idSala);
    }

    @Override
    public Optional<Sala> buscarPorNombre(String nombre) {
        return jpaRepository.findByNombreSala(nombre).map(e -> {
            Sala s = new Sala();
            s.setIdSala(e.getIdSala());
            s.setNombreSala(e.getNombreSala());
            return s;
        });
    }

    @Override
    public Optional<Sala> buscarPorId(Integer idSala) {
        return jpaRepository.findById(idSala).map(e -> {
            Sala s = new Sala();
            s.setIdSala(e.getIdSala());
            s.setNombreSala(e.getNombreSala());
            return s;
        });
    }
}