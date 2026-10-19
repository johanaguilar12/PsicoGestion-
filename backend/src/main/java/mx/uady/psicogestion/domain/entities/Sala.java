package mx.uady.psicogestion.domain.entities;

public class Sala {
    private Integer idSala;
    private String nombreSala;

    public Sala() {}

    public Integer getIdSala() { return idSala; }
    public void setIdSala(Integer idSala) { this.idSala = idSala; }

    public String getNombreSala() { return nombreSala; }
    public void setNombreSala(String nombreSala) { this.nombreSala = nombreSala; }


    public boolean validarNombreCorto() {
        return this.nombreSala != null && !this.nombreSala.trim().isEmpty() && this.nombreSala.length() <= 10;
    }
}