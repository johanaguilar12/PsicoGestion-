
package mx.uady.psicogestion.presentation.controllers;

import mx.uady.psicogestion.application.usecases.RegistrarUsuarioUseCase;
import mx.uady.psicogestion.domain.entities.Secretaria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cuentas")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4321", "http://localhost:3000"})
public class SecretariaController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    public SecretariaController(RegistrarUsuarioUseCase registrarUsuarioUseCase) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarSecretaria(
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("correoElectronico") String correoElectronico,
            @RequestParam("telefono") String telefono,
            @RequestParam("contrasena") String contrasena,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {
        try {
            Secretaria secretaria = new Secretaria();
            secretaria.setNombreCompleto(nombreCompleto);
            secretaria.setCorreoElectronico(correoElectronico);
            secretaria.setTelefono(telefono);
            secretaria.setContrasena(contrasena);

            if (foto != null && !foto.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + foto.getOriginalFilename().replaceAll("\\s+", "_");
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                Files.copy(foto.getInputStream(), uploadPath.resolve(fileName));
                secretaria.setFotoPerfil("http://localhost:8081/uploads/" + fileName);
            } else {
                secretaria.setFotoPerfil("/images/usuario-ejemplo.jpg");
            }

            Secretaria nuevaSecretaria = registrarUsuarioUseCase.ejecutar(secretaria);
            return new ResponseEntity<>(nuevaSecretaria, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al registrar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Secretaria>> listarUsuarios() {
        return new ResponseEntity<>(registrarUsuarioUseCase.listarTodas(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuarioPorId(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(registrarUsuarioUseCase.buscarPorId(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<?> darDeBaja(@PathVariable Integer id) {
        try {
            registrarUsuarioUseCase.darDeBaja(id);
            return new ResponseEntity<>("Estatus cambiado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        try {
            registrarUsuarioUseCase.eliminarFisicamente(id);
            return new ResponseEntity<>("Usuario eliminado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSecretaria(
            @PathVariable Integer id,
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("correoElectronico") String correoElectronico,
            @RequestParam("telefono") String telefono,
            @RequestParam("rol") String rol,
            @RequestParam(value = "contrasena", required = false) String contrasena,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {

        try {
            Secretaria datosActualizados = new Secretaria();
            datosActualizados.setNombreCompleto(nombreCompleto);
            datosActualizados.setCorreoElectronico(correoElectronico);
            datosActualizados.setTelefono(telefono);
            datosActualizados.setRol(rol);

            if (foto != null && !foto.isEmpty()) {
                if (foto.getSize() > 2 * 1024 * 1024) {
                    return new ResponseEntity<>("La fotografía no debe superar los 2 MB.", HttpStatus.BAD_REQUEST);
                }

                String nombreOriginal = foto.getOriginalFilename();

                if (nombreOriginal == null || nombreOriginal.isBlank()) {
                    return new ResponseEntity<>("Nombre de archivo inválido.", HttpStatus.BAD_REQUEST);
                }

                String nombreSeguro = Paths.get(nombreOriginal.replace("\\", "/"))
                        .getFileName().toString();

                String extension = nombreSeguro.toLowerCase();

                if (!extension.endsWith(".jpg") &&
                    !extension.endsWith(".jpeg") &&
                    !extension.endsWith(".png")) {
                    return new ResponseEntity<>("Solo se permiten imágenes JPG, JPEG o PNG.", HttpStatus.BAD_REQUEST);
                }

                String fileName = UUID.randomUUID() + "_" + nombreSeguro;
                Path uploadPath = Paths.get("uploads");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Files.copy(foto.getInputStream(), uploadPath.resolve(fileName));

                datosActualizados.setFotoPerfil("http://localhost:8081/uploads/" + fileName);
            }

            Secretaria actualizada = registrarUsuarioUseCase.actualizar(
                    id,
                    datosActualizados,
                    contrasena
            );

            return new ResponseEntity<>(actualizada, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}