package mx.uady.psicogestion.domain.entities;

import java.util.UUID;

public class Cita {
    private Integer idCita;
    private Integer idPaciente;
    private Integer idTerapeuta;
    private Integer idSala;
    private String folio;
    private EstadoCitaEnum estado;
    private String notas;

    public Cita() {
        this.estado = EstadoCitaEnum.PENDIENTE; // Estado inicial por defecto
    }

    public Integer getIdCita() { return idCita; }
    public void setIdCita(Integer idCita) { this.idCita = idCita; }

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer idPaciente) { this.idPaciente = idPaciente; }

    public Integer getIdTerapeuta() { return idTerapeuta; }
    public void setIdTerapeuta(Integer idTerapeuta) { this.idTerapeuta = idTerapeuta; }

    public Integer getIdSala() { return idSala; }
    public void setIdSala(Integer idSala) { this.idSala = idSala; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public EstadoCitaEnum getEstado() { return estado; }
    public void setEstado(EstadoCitaEnum estado) { this.estado = estado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }


    public void generarFolioUnico() {
        if (this.folio == null || this.folio.trim().isEmpty()) {
            this.folio = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public void cambiarEstado(EstadoCitaEnum nuevoEstado) {
        if (nuevoEstado != null) {
            this.estado = nuevoEstado;
        }
    }
}