package mx.uady.psicogestion.application.ports.out;

import mx.uady.psicogestion.domain.entities.Secretaria;
import java.util.List;
import java.util.Optional;

public interface ISecretariaRepository {
    Secretaria guardar(Secretaria secretaria);
    List<Secretaria> obtenerTodas();
    Optional<Secretaria> buscarPorId(Integer idSecretaria);
    Optional<Secretaria> buscarPorCorreo(String correo);
    Optional<Secretaria> buscarPorTelefono(String telefono);
    void marcarInactivo(Integer idSecretaria);
    void eliminar(Integer idSecretaria);
}