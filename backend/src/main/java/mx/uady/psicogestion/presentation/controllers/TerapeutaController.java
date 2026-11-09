
package mx.uady.psicogestion.presentation.controllers;

import mx.uady.psicogestion.application.usecases.GestionarTerapeutasUseCase;
import mx.uady.psicogestion.domain.entities.Terapeuta;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/terapeutas")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:4321",
    "http://localhost:3000"
})
public class TerapeutaController {

    private final GestionarTerapeutasUseCase gestionarTerapeutasUseCase;

    public TerapeutaController(
            GestionarTerapeutasUseCase gestionarTerapeutasUseCase) {
        this.gestionarTerapeutasUseCase = gestionarTerapeutasUseCase;
    }

    private String guardarFotografia(
            MultipartFile foto,
            Terapeuta terapeuta) throws Exception {

        if (foto == null || foto.isEmpty()) {
            return null;
        }

        if (foto.getSize() > 1024 * 1024) {
            throw new IllegalArgumentException(
                "La fotografía no debe superar 1 MB."
            );
        }

        String tipo = foto.getContentType();

        if (!"image/jpeg".equals(tipo) &&
            !"image/png".equals(tipo)) {
            throw new IllegalArgumentException(
                "La fotografía debe estar en formato JPG, JPEG o PNG."
            );
        }

        BufferedImage imagen = ImageIO.read(foto.getInputStream());

        if (imagen == null) {
            throw new IllegalArgumentException(
                "El archivo no es una imagen válida."
            );
        }

        if (!terapeuta.validarDimensionesFoto(
                imagen.getWidth(),
                imagen.getHeight())) {
            throw new IllegalArgumentException(
                "La fotografía debe ser mínimo de 200 x 200 píxeles."
            );
        }

        String extension = "image/png".equals(tipo)
                ? ".png"
                : ".jpg";

        String nombreArchivo =
                UUID.randomUUID().toString() + extension;

        Path carpetaUploads = Paths.get("uploads");
        Files.createDirectories(carpetaUploads);

        Path rutaArchivo = carpetaUploads.resolve(nombreArchivo);

        Files.copy(foto.getInputStream(), rutaArchivo);

        return "http://localhost:8081/uploads/" + nombreArchivo;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> registrarTerapeuta(
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("correoElectronico") String correoElectronico,
            @RequestParam("telefono") String telefono,
            @RequestParam("fechaNacimiento") LocalDate fechaNacimiento,
            @RequestParam("sexo") String sexo,
            @RequestParam("cedulaProfesional") String cedulaProfesional,
            @RequestParam(value = "foto", required = false)
            MultipartFile foto) {

        try {
            Terapeuta terapeuta = new Terapeuta();

            terapeuta.setNombreCompleto(nombreCompleto);
            terapeuta.setCorreoElectronico(correoElectronico);
            terapeuta.setTelefono(telefono);
            terapeuta.setFechaNacimiento(fechaNacimiento);
            terapeuta.setSexo(sexo);
            terapeuta.setCedulaProfesional(cedulaProfesional);

            if (foto != null && !foto.isEmpty()) {
                String ruta = guardarFotografia(foto, terapeuta);
                terapeuta.setFotoPerfil(ruta);
            } else {
                terapeuta.setFotoPerfil(
                    "/images/terapeuta-ejemplo.jpg"
                );
            }

            Terapeuta nuevo =
                    gestionarTerapeutasUseCase.registrar(terapeuta);

            return new ResponseEntity<>(
                nuevo,
                HttpStatus.CREATED
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                "Error interno: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<Terapeuta>> listarTerapeutas() {
        return new ResponseEntity<>(
            gestionarTerapeutasUseCase.listarTodos(),
            HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarTerapeutaPorId(
            @PathVariable Integer id) {

        try {
            return new ResponseEntity<>(
                gestionarTerapeutasUseCase.buscarPorId(id),
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.NOT_FOUND
            );
        }
    }

    @PutMapping(
        value = "/{id}",
        consumes = "multipart/form-data"
    )
    public ResponseEntity<?> actualizarTerapeuta(
            @PathVariable Integer id,
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("correoElectronico") String correoElectronico,
            @RequestParam("telefono") String telefono,
            @RequestParam("fechaNacimiento") LocalDate fechaNacimiento,
            @RequestParam("sexo") String sexo,
            @RequestParam("cedulaProfesional") String cedulaProfesional,
            @RequestParam(value = "foto", required = false)
            MultipartFile foto) {

        try {
            Terapeuta existente =
                    gestionarTerapeutasUseCase.buscarPorId(id);

            Terapeuta datosActualizados = new Terapeuta();

            datosActualizados.setNombreCompleto(nombreCompleto);
            datosActualizados.setCorreoElectronico(correoElectronico);
            datosActualizados.setTelefono(telefono);
            datosActualizados.setFechaNacimiento(fechaNacimiento);
            datosActualizados.setSexo(sexo);
            datosActualizados.setCedulaProfesional(cedulaProfesional);

            // Conservar la fotografía actual.
            datosActualizados.setFotoPerfil(
                existente.getFotoPerfil()
            );

            // Reemplazarla únicamente si se seleccionó otra.
            if (foto != null && !foto.isEmpty()) {
                String nuevaRuta = guardarFotografia(
                    foto,
                    datosActualizados
                );

                datosActualizados.setFotoPerfil(nuevaRuta);
            }

            Terapeuta actualizado =
                    gestionarTerapeutasUseCase.actualizar(
                        id,
                        datosActualizados
                    );

            return new ResponseEntity<>(
                actualizado,
                HttpStatus.OK
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                "Error interno: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<?> darDeBaja(
            @PathVariable Integer id) {

        try {
            gestionarTerapeutasUseCase.darDeBaja(id);

            return new ResponseEntity<>(
                "Terapeuta dado de baja exitosamente",
                HttpStatus.OK
            );

        } catch (Exception e) {
            return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTerapeuta(
            @PathVariable Integer id) {

        try {
            gestionarTerapeutasUseCase.eliminarFisicamente(id);

            return new ResponseEntity<>(
                "Terapeuta eliminado",
                HttpStatus.OK
            );

        } catch (Exception e) {
            return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
