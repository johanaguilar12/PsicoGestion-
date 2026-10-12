package mx.uady.psicogestion.domain.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class BloqueHorario {
    private Integer idBloque;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public BloqueHorario() {}

    public Integer getIdBloque() { return idBloque; }
    public void setIdBloque(Integer idBloque) { this.idBloque = idBloque; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    // --- Reglas de Negocio Empresariales ---
    public boolean validarRangoOperativo() {
        if (this.horaInicio == null || this.horaFin == null) return false;
        return this.horaFin.isAfter(this.horaInicio);
    }
}