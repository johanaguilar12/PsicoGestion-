package mx.uady.psicogestion.domain.entities;

public class Secretaria {
    private Integer idSecretaria;
    private String nombreCompleto;
    private String telefono;
    private String correoElectronico;
    private String contrasena;

    public Secretaria() {
    }

    public Integer getIdSecretaria() { return idSecretaria; }
    public void setIdSecretaria(Integer idSecretaria) { this.idSecretaria = idSecretaria; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    // Reglas de Negocio
    public boolean esCorreoInstitucionalValido() {
        return this.correoElectronico != null &&
                this.correoElectronico.toLowerCase().endsWith("@correo.uady.mx");
    }

    public boolean esContrasenaSegura() {
        if (this.contrasena == null || this.contrasena.length() < 8) return false;
        boolean tieneNumero = this.contrasena.matches(".*\\d.*");
        boolean tieneMayuscula = !this.contrasena.equals(this.contrasena.toLowerCase());
        boolean tieneEspecial = this.contrasena.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        return tieneNumero && tieneMayuscula && tieneEspecial;
    }
}