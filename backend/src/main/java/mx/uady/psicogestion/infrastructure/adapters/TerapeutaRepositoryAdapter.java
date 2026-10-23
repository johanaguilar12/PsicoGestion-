package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.application.ports.out.ITerapeutaRepository;
import mx.uady.psicogestion.domain.entities.Terapeuta;
import mx.uady.psicogestion.infrastructure.persistence.JpaTerapeutaRepository;
import mx.uady.psicogestion.infrastructure.persistence.TerapeutaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TerapeutaRepositoryAdapter implements ITerapeutaRepository {

    private final JpaTerapeutaRepository jpaRepository;

    public TerapeutaRepositoryAdapter(JpaTerapeutaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Terapeuta guardar(Terapeuta terapeuta) {
        TerapeutaEntity entity = mapearAEntity(terapeuta);
        TerapeutaEntity guardado = jpaRepository.saveAndFlush(entity);
        terapeuta.setIdTerapeuta(guardado.getIdTerapeuta());
        return terapeuta;
    }

    @Override
    public List<Terapeuta> obtenerActivos() {

        return jpaRepository.findAll().stream()
                .map(this::mapearADominio)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Terapeuta> buscarPorId(Integer idTerapeuta) {
        return jpaRepository.findById(idTerapeuta).map(this::mapearADominio);
    }

    @Override
    public Terapeuta actualizar(Terapeuta terapeuta) {
        TerapeutaEntity entity = mapearAEntity(terapeuta);
        jpaRepository.saveAndFlush(entity);
        return terapeuta;
    }

    @Override
    public Optional<Terapeuta> buscarPorCedula(String cedula) {
        return jpaRepository.findByCedulaProfesional(cedula).map(this::mapearADominio);
    }

    @Override
    public Optional<Terapeuta> buscarPorCorreo(String correo) {
        return jpaRepository.findByCorreoElectronico(correo).map(this::mapearADominio);
    }

    @Override
    public Optional<Terapeuta> buscarPorTelefono(String telefono) {
        return jpaRepository.findByTelefono(telefono).map(this::mapearADominio);
    }

    @Override
    public void marcarInactivo(Integer idTerapeuta) {
        jpaRepository.desactivarTerapeuta(idTerapeuta);
    }

    @Override
    public void eliminar(Integer idTerapeuta) {
        jpaRepository.eliminarTerapeuta(idTerapeuta);
    }

    private Terapeuta mapearADominio(TerapeutaEntity entity) {
        Terapeuta ter = new Terapeuta();
        ter.setIdTerapeuta(entity.getIdTerapeuta());
        ter.setNombreCompleto(entity.getNombreCompleto());
        ter.setSexo(entity.getSexo());
        ter.setTelefono(entity.getTelefono());
        ter.setCorreoElectronico(entity.getCorreoElectronico());
        ter.setFechaNacimiento(entity.getFechaNacimiento());
        ter.setCedulaProfesional(entity.getCedulaProfesional());
        ter.setFotoPerfil(entity.getFotoPerfil());
        ter.setActivo(entity.isActivo());
        return ter;
    }

    private TerapeutaEntity mapearAEntity(Terapeuta terapeuta) {
        TerapeutaEntity entity = new TerapeutaEntity();
        entity.setIdTerapeuta(terapeuta.getIdTerapeuta());
        entity.setNombreCompleto(terapeuta.getNombreCompleto());
        entity.setSexo(terapeuta.getSexo());
        entity.setTelefono(terapeuta.getTelefono());
        entity.setCorreoElectronico(terapeuta.getCorreoElectronico());
        entity.setFechaNacimiento(terapeuta.getFechaNacimiento());
        entity.setCedulaProfesional(terapeuta.getCedulaProfesional());
        entity.setFotoPerfil(terapeuta.getFotoPerfil());
        entity.setActivo(terapeuta.isActivo());
        return entity;
    }
}