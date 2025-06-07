package uts.mi.matricula.dto;

import uts.mi.matricula.model.Materia;
import uts.mi.matricula.model.Pensum;

import java.time.LocalDate;
import java.util.List;

public class PensumDTO {

    private String id;
    private String codigo;
    private LocalDate fechaInicio;
    private boolean activo;
    private String idCarrera;
    private String nombreCarrera;
    private List<Materia> materias;

    public PensumDTO() {
    }

    public PensumDTO(String id, String codigo, LocalDate fechaInicio, boolean activo,
                     String idCarrera, String nombreCarrera, List<Materia> materias) {
        this.id = id;
        this.codigo = codigo;
        this.fechaInicio = fechaInicio;
        this.activo = activo;
        this.idCarrera = idCarrera;
        this.nombreCarrera = nombreCarrera;
        this.materias = materias;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getIdCarrera() {
        return idCarrera;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }

    public List<Materia> getMaterias() {
        return materias;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setIdCarrera(String idCarrera) {
        this.idCarrera = idCarrera;
    }

    public void setNombreCarrera(String nombreCarrera) {
        this.nombreCarrera = nombreCarrera;
    }

    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
    }
}

