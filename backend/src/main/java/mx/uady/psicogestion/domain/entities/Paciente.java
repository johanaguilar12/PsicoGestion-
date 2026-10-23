package mx.uady.psicogestion.domain.entities;

import java.time.LocalDate;
import java.time.Period;

public class Paciente {
    private Integer idPaciente;
    private String nombreCompleto;
    private String curp;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String telefono;
    private LocalDate fechaRegistro;
    private boolean activo;

    public Paciente() {
        this.activo = true; // Por defecto, un paciente nuevo está activo
        this.fechaRegistro = LocalDate.now();
    }

    // Getters y Setters
    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer idPaciente) { this.idPaciente = idPaciente; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCurp() { return curp; }
    public void setCurp(String curp) { this.curp = curp; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    // --- Reglas de Negocio Empresariales ---

    // Validar que la fecha de nacimiento corresponda a una edad igual o mayor a 7 años[cite: 4]
    public boolean esMayorDeSieteAnios() {
        if (this.fechaNacimiento == null) return false;
        int edad = Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
        return edad >= 7;
    }

    // El atributo activo gestionará la baja lógica[cite: 4]
    public void darDeBajaLogica() {
        this.activo = false;
    }
}