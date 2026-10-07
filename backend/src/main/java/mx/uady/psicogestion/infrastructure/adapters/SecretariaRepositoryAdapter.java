package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.application.ports.out.ISecretariaRepository;
import mx.uady.psicogestion.domain.entities.Secretaria;
import mx.uady.psicogestion.infrastructure.persistence.JpaSecretariaRepository;
import mx.uady.psicogestion.infrastructure.persistence.SecretariaEntity;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class SecretariaRepositoryAdapter implements ISecretariaRepository {

    private final JpaSecretariaRepository jpaRepository;

    public SecretariaRepositoryAdapter(JpaSecretariaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Secretaria guardar(Secretaria secretaria) {
        SecretariaEntity entity = new SecretariaEntity();
        entity.setNombreCompleto(secretaria.getNombreCompleto());
        entity.setTelefono(secretaria.getTelefono());
        entity.setCorreoElectronico(secretaria.getCorreoElectronico());
        entity.setContrasena(secretaria.getContrasena());

        SecretariaEntity guardada = jpaRepository.save(entity);
        secretaria.setIdSecretaria(guardada.getIdSecretaria());
        return secretaria;
    }

    @Override
    public Optional<Secretaria> buscarPorCorreo(String correo) {
        return jpaRepository.findByCorreoElectronico(correo).map(this::mapearADominio);
    }

    @Override
    public Optional<Secretaria> buscarPorTelefono(String telefono) {
        return jpaRepository.findByTelefono(telefono).map(this::mapearADominio);
    }


    private Secretaria mapearADominio(SecretariaEntity entity) {
        Secretaria sec = new Secretaria();
        sec.setIdSecretaria(entity.getIdSecretaria());
        sec.setNombreCompleto(entity.getNombreCompleto());
        sec.setTelefono(entity.getTelefono());
        sec.setCorreoElectronico(entity.getCorreoElectronico());
        sec.setContrasena(entity.getContrasena());
        return sec;
    }
}