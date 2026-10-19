package mx.uady.psicogestion.domain.entities;

import java.time.LocalDate;

public class Terapeuta {
    private Integer idTerapeuta;
    private String nombreCompleto;
    private String sexo;
    private String telefono;
    private String correoElectronico;
    private LocalDate fechaNacimiento;
    private String cedulaProfesional;
    private String fotoPerfil;
    private boolean activo; // Agregado para bajas lógicas

    public Terapeuta() {
        this.activo = true;
    }

    public Integer getIdTerapeuta() { return idTerapeuta; }
    public void setIdTerapeuta(Integer idTerapeuta) { this.idTerapeuta = idTerapeuta; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
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
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }


    public boolean esNombreValido() {
        return nombreCompleto != null && nombreCompleto.length() <= 50;
    }

    public boolean esFechaNacimientoValida() {
        return this.fechaNacimiento != null && this.fechaNacimiento.isBefore(LocalDate.now());
    }

    public boolean validarDimensionesFoto(int ancho, int alto) {
        return ancho >= 200 && alto >= 200;
    }
}