package uts.mi.matricula.model;

import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "pensums")
public class Pensum {

    @Id
    private String id;

    private String carrera;

    private String codigo;

    private LocalDate fechaInicio;

    private boolean activo;

    private List<String> materias;

    // Constructor por defecto
    public Pensum() {}

    // Constructor con parámetros
    public Pensum(String id, String carrera, String codigo, LocalDate fechaInicio, boolean activo, List<String> materias) {
        this.id = id;
        this.carrera = carrera;
        this.codigo = codigo;
        this.fechaInicio = fechaInicio;
        this.activo = activo;
        this.materias = materias;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<String> getMaterias() {
        return materias;
    }

    public void setMaterias(List<String> materias) {
        this.materias = materias;
    }
}

