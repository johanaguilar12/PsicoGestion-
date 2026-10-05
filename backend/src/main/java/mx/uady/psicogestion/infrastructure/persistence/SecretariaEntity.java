package mx.uady.psicogestion.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "secretaria")
public class SecretariaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_secretaria")
    private Integer idSecretaria;

    @Column(name = "nombre_completo", nullable = false, length = 50)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 15)
    private String telefono;

    @Column(name = "correo_electronico", nullable = false, unique = true, length = 100)
    private String correoElectronico;

    @Column(nullable = false, length = 255)
    private String contrasena;

    // Getters y Setters
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
}