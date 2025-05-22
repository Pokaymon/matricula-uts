package uts.mi.matricula.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "grupos")
public class Grupo {
    @Id
    private String id;
    private String codigo;
    private String codMateria;
    private List<Horario> horarios;

    private String profesorId;

    public Grupo() {
    }

    public Grupo(String codigo, String codMateria, List<Horario> horarios, String profesorId) {
        this.codigo = codigo;
        this.codMateria = codMateria;
        this.horarios = horarios;
	this.profesorId = profesorId;
    }

    public String getId() { return id; }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getCodMateria() { return codMateria; }

    public void setCodMateria(String codMateria) { this.codMateria = codMateria; }

    public List<Horario> getHorarios() { return horarios; }

    public void setHorarios(List<Horario> horarios) { this.horarios = horarios; }

    public String getProfesorId() { return profesorId; }

    public void setProfesorId(String profesorId) { this.profesorId = profesorId; }
}

