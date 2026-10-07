package mx.uady.psicogestion.application.ports.out;

import mx.uady.psicogestion.domain.entities.Secretaria;
import java.util.Optional;

public interface ISecretariaRepository {
    Secretaria guardar(Secretaria secretaria);
    Optional<Secretaria> buscarPorCorreo(String correo);
    Optional<Secretaria> buscarPorTelefono(String telefono);
}