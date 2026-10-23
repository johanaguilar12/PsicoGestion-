package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.application.ports.out.ISecretariaRepository;
import mx.uady.psicogestion.domain.entities.Secretaria;
import mx.uady.psicogestion.infrastructure.persistence.JpaSecretariaRepository;
import mx.uady.psicogestion.infrastructure.persistence.SecretariaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SecretariaRepositoryAdapter implements ISecretariaRepository {

    private final JpaSecretariaRepository jpaRepository;

    public SecretariaRepositoryAdapter(JpaSecretariaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Secretaria guardar(Secretaria secretaria) {
        SecretariaEntity entity = new SecretariaEntity();
        entity.setIdSecretaria(secretaria.getIdSecretaria());
        entity.setNombreCompleto(secretaria.getNombreCompleto());
        entity.setTelefono(secretaria.getTelefono());
        entity.setCorreoElectronico(secretaria.getCorreoElectronico());
        entity.setContrasena(secretaria.getContrasena());
        entity.setActivo(secretaria.isActivo());
        entity.setFotoPerfil(secretaria.getFotoPerfil());
        entity.setRol(secretaria.getRol());
        entity.setFechaRegistro(secretaria.getFechaRegistro());

        SecretariaEntity guardada = jpaRepository.saveAndFlush(entity);
        secretaria.setIdSecretaria(guardada.getIdSecretaria());
        return secretaria;
    }

    @Override
    public List<Secretaria> obtenerTodas() {
        return jpaRepository.findAll().stream().map(this::mapearADominio).collect(Collectors.toList());
    }

    @Override
    public Optional<Secretaria> buscarPorId(Integer idSecretaria) {
        return jpaRepository.findById(idSecretaria).map(this::mapearADominio);
    }

    @Override
    public Optional<Secretaria> buscarPorCorreo(String correo) {
        return jpaRepository.findByCorreoElectronico(correo).map(this::mapearADominio);
    }

    @Override
    public Optional<Secretaria> buscarPorTelefono(String telefono) {
        return jpaRepository.findByTelefono(telefono).map(this::mapearADominio);
    }

    @Override
    public void marcarInactivo(Integer idSecretaria) {
        jpaRepository.alternarEstatus(idSecretaria);
    }

    @Override
    public void eliminar(Integer idSecretaria) {
        jpaRepository.deleteById(idSecretaria);
    }

    private Secretaria mapearADominio(SecretariaEntity entity) {
        Secretaria sec = new Secretaria();
        sec.setIdSecretaria(entity.getIdSecretaria());
        sec.setNombreCompleto(entity.getNombreCompleto());
        sec.setTelefono(entity.getTelefono());
        sec.setCorreoElectronico(entity.getCorreoElectronico());
        sec.setContrasena(entity.getContrasena());
        sec.setActivo(entity.isActivo());
        sec.setFotoPerfil(entity.getFotoPerfil());
        sec.setRol(entity.getRol());
        sec.setFechaRegistro(entity.getFechaRegistro());
        return sec;
    }
}