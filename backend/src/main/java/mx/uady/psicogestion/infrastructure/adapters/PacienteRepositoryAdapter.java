package mx.uady.psicogestion.infrastructure.adapters;

import mx.uady.psicogestion.application.ports.out.IPacienteRepository;
import mx.uady.psicogestion.domain.entities.Paciente;
import mx.uady.psicogestion.infrastructure.persistence.JpaPacienteRepository;
import mx.uady.psicogestion.infrastructure.persistence.PacienteEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PacienteRepositoryAdapter implements IPacienteRepository {

    private final JpaPacienteRepository jpaRepository;

    public PacienteRepositoryAdapter(JpaPacienteRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Paciente guardar(Paciente paciente) {
        PacienteEntity entity = new PacienteEntity();
        entity.setIdPaciente(paciente.getIdPaciente());
        entity.setNombreCompleto(paciente.getNombreCompleto());
        entity.setCurp(paciente.getCurp());
        entity.setSexo(paciente.getSexo());
        entity.setFechaNacimiento(paciente.getFechaNacimiento());
        entity.setTelefono(paciente.getTelefono());

        if (paciente.getFechaRegistro() != null) {
            entity.setFechaRegistro(paciente.getFechaRegistro().atStartOfDay());
        }
        entity.setActivo(paciente.isActivo());

        PacienteEntity guardado = jpaRepository.saveAndFlush(entity);
        paciente.setIdPaciente(guardado.getIdPaciente());
        return paciente;
    }

    @Override
    public Optional<Paciente> buscarPorCurp(String curp) {
        return jpaRepository.findByCurp(curp).map(this::mapearADominio);
    }

    @Override
    public Optional<Paciente> buscarPorTelefono(String telefono) {
        return jpaRepository.findByTelefono(telefono).map(this::mapearADominio);
    }

    @Override
    public List<Paciente> obtenerActivos() {
        // 🚨 Modificado: Ahora devuelve TODOS (activos e inactivos) para que React pueda filtrarlos
        return jpaRepository.findAll().stream()
                .map(this::mapearADominio)
                .collect(Collectors.toList());
    }

    @Override
    public void marcarInactivo(Integer idPaciente) {
        jpaRepository.desactivarPacienteFuerzaBruta(idPaciente);
    }

    @Override
    public Optional<Paciente> buscarPorId(Integer idPaciente) {
        return jpaRepository.findById(idPaciente).map(this::mapearADominio);
    }

    @Override
    public Paciente actualizar(Paciente paciente) {
        return jpaRepository.findById(paciente.getIdPaciente()).map(entity -> {
            entity.setNombreCompleto(paciente.getNombreCompleto());
            entity.setCurp(paciente.getCurp());
            entity.setSexo(paciente.getSexo());
            entity.setFechaNacimiento(paciente.getFechaNacimiento());
            entity.setTelefono(paciente.getTelefono());

            PacienteEntity actualizado = jpaRepository.saveAndFlush(entity);
            return mapearADominio(actualizado);
        }).orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
    }

    @Override
    public void eliminar(Integer idPaciente) {
        jpaRepository.eliminarPacienteFuerzaBruta(idPaciente);
    }

    private Paciente mapearADominio(PacienteEntity entity) {
        Paciente pac = new Paciente();
        pac.setIdPaciente(entity.getIdPaciente());
        pac.setNombreCompleto(entity.getNombreCompleto());
        pac.setCurp(entity.getCurp());
        pac.setSexo(entity.getSexo());
        pac.setFechaNacimiento(entity.getFechaNacimiento());
        pac.setTelefono(entity.getTelefono());
        if (entity.getFechaRegistro() != null) {
            pac.setFechaRegistro(entity.getFechaRegistro().toLocalDate());
        }
        pac.setActivo(entity.isActivo());
        return pac;
    }
}