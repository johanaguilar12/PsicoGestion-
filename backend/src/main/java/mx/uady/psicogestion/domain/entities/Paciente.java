
package mx.uady.psicogestion.domain.entities;

import mx.uady.psicogestion.domain.validation.ValidacionesDatos;

import java.time.LocalDate;

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
        this.activo = true;
        this.fechaRegistro = LocalDate.now();
    }

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

    
    public boolean esMayorDeSieteAnios() {
        return ValidacionesDatos.edadMinima(
            this.fechaNacimiento, 15
        );
    }

    public boolean esCurpValida() {
        return ValidacionesDatos.curpValida(
            this.curp,
            this.nombreCompleto,
            this.fechaNacimiento,
            this.sexo
        );
    }

    public boolean esTelefonoValido() {
        return ValidacionesDatos.telefonoValido(this.telefono);
    }

    public void darDeBajaLogica() {
        this.activo = false;
    }
}
