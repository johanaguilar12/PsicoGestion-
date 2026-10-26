package mx.uady.psicogestion.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

public class Secretaria {
    private Integer idSecretaria;
    private String nombreCompleto;
    private String telefono;
    private String correoElectronico;
    private String contrasena;

    // Campos visuales para el frontend
    private boolean activo;
    private String fotoPerfil;
    private String rol;
    private LocalDateTime fechaRegistro;

    public Secretaria() {
        this.activo = true;
        this.rol = "Administrador";
    }

    public Integer getIdSecretaria() { return idSecretaria; }
    public void setIdSecretaria(Integer idSecretaria) { this.idSecretaria = idSecretaria; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    @JsonIgnore
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }


    public boolean esNombreValido() {
        return this.nombreCompleto != null && this.nombreCompleto.length() <= 50;
    }

    public boolean esCorreoInstitucionalValido() {
        return this.correoElectronico != null && this.correoElectronico.toLowerCase().endsWith("@correo.uady.mx");
    }

    public boolean esContrasenaSegura() {

        if (this.contrasena == null || this.contrasena.length() < 8 || this.contrasena.length() > 25) return false;

        boolean tieneNumero = this.contrasena.matches(".*\\d.*");

        boolean tieneMayuscula = !this.contrasena.equals(this.contrasena.toLowerCase());

        boolean tieneEspecial = this.contrasena.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        return tieneNumero && tieneMayuscula && tieneEspecial;
    }
}