package uts.mi.matricula.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Document(collection = "horarios")
public class Horario {
    @Id
    private String id;
    private String codigo;
    private DayOfWeek dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String codGrupo;

    public Horario() {
    }

    public Horario(String codigo, DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin, String codGrupo) {
        this.codigo = codigo;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.codGrupo = codGrupo;
    }

    // Getters y setters
    public String getId() { return id; }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) { this.codigo = codigo; }

    public DayOfWeek getDia() { return dia; }

    public void setDia(DayOfWeek dia) { this.dia = dia; }

    public LocalTime getHoraInicio() { return horaInicio; }

    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }

    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getCodGrupo() { return codGrupo; }

    public void setCodGrupo(String codGrupo) { this.codGrupo = codGrupo; }
}
