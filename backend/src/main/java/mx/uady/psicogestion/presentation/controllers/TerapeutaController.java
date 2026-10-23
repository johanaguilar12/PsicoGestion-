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

@RestController
@RequestMapping("/api/v1/terapeutas")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4321", "http://localhost:3000"})
public class TerapeutaController {

    private final GestionarTerapeutasUseCase gestionarTerapeutasUseCase;

    public TerapeutaController(GestionarTerapeutasUseCase gestionarTerapeutasUseCase) {
        this.gestionarTerapeutasUseCase = gestionarTerapeutasUseCase;
    }

    // 🚨 Modificado para recibir MultipartFile y validar 200x200
    @PostMapping
    public ResponseEntity<?> registrarTerapeuta(
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("correoElectronico") String correoElectronico,
            @RequestParam("telefono") String telefono,
            @RequestParam("fechaNacimiento") LocalDate fechaNacimiento,
            @RequestParam("sexo") String sexo,
            @RequestParam("cedulaProfesional") String cedulaProfesional,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {

        try {
            Terapeuta terapeuta = new Terapeuta();
            terapeuta.setNombreCompleto(nombreCompleto);
            terapeuta.setCorreoElectronico(correoElectronico);
            terapeuta.setTelefono(telefono);
            terapeuta.setFechaNacimiento(fechaNacimiento);
            terapeuta.setSexo(sexo);
            terapeuta.setCedulaProfesional(cedulaProfesional);

            // Procesamiento de la imagen
            if (foto != null && !foto.isEmpty()) {
                BufferedImage bi = ImageIO.read(foto.getInputStream());
                if (bi != null) {
                    if (!terapeuta.validarDimensionesFoto(bi.getWidth(), bi.getHeight())) {
                        return new ResponseEntity<>("La fotografía debe ser mínimo de 200 x 200 píxeles.", HttpStatus.BAD_REQUEST);
                    }

                    String fileName = System.currentTimeMillis() + "_" + foto.getOriginalFilename().replaceAll("\\s+", "_");
                    Path uploadPath = Paths.get("uploads");
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(foto.getInputStream(), filePath);

                    terapeuta.setFotoPerfil("http://localhost:8081/uploads/" + fileName);
                } else {
                    return new ResponseEntity<>("El archivo no es una imagen válida.", HttpStatus.BAD_REQUEST);
                }
            } else {
                terapeuta.setFotoPerfil("/images/terapeuta-ejemplo.jpg");
            }

            Terapeuta nuevoTerapeuta = gestionarTerapeutasUseCase.registrar(terapeuta);
            return new ResponseEntity<>(nuevoTerapeuta, HttpStatus.CREATED);

        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Terapeuta>> listarTerapeutas() {
        return new ResponseEntity<>(gestionarTerapeutasUseCase.listarTodos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarTerapeutaPorId(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(gestionarTerapeutasUseCase.buscarPorId(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTerapeuta(@PathVariable Integer id, @RequestBody Terapeuta terapeuta) {
        try {
            Terapeuta actualizado = gestionarTerapeutasUseCase.actualizar(id, terapeuta);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<?> darDeBaja(@PathVariable Integer id) {
        try {
            gestionarTerapeutasUseCase.darDeBaja(id);
            return new ResponseEntity<>("Terapeuta dado de baja exitosamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTerapeuta(@PathVariable Integer id) {
        try {
            gestionarTerapeutasUseCase.eliminarFisicamente(id);
            return new ResponseEntity<>("Terapeuta eliminado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}