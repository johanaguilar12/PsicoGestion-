package mx.uady.psicogestion.domain.entities;

import java.time.LocalDate;

public class Terapeuta {
    private Integer idTerapeuta;
    private String nombreCompleto;
    private String telefono;
    private String correoElectronico;
    private LocalDate fechaNacimiento;
    private String cedulaProfesional;
    private String fotoPerfil; // Almacenará la URL o ruta del archivo

    public Terapeuta() {
    }

    // Getters y Setters
    public Integer getIdTerapeuta() { return idTerapeuta; }
    public void setIdTerapeuta(Integer idTerapeuta) { this.idTerapeuta = idTerapeuta; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getCedulaProfesional() { return cedulaProfesional; }
    public void setCedulaProfesional(String cedulaProfesional) { this.cedulaProfesional = cedulaProfesional; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    // --- Reglas de Negocio Empresariales ---

    // Validar que la fecha de nacimiento sea estrictamente anterior a la fecha de registro actual[cite: 4]
    public boolean esFechaNacimientoValida() {
        return this.fechaNacimiento != null && this.fechaNacimiento.isBefore(LocalDate.now());
    }

    // Validar que la foto de perfil tenga dimensiones mínimas de 200x200 píxeles[cite: 4]
    public boolean validarDimensionesFoto(int ancho, int alto) {
        return ancho >= 200 && alto >= 200;
    }
}