package uts.mi.matricula.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "carreras")
public class Carrera {

    @Id
    private String id;

    @Indexed(unique = true)
    private String cod;

    @Indexed(unique = true)
    private String nombre;

    @DBRef
    private Pensum pensum;

    public Carrera() {
    }

    public Carrera(String cod, String nombre, Pensum pensum) {
        this.cod = cod;
        this.nombre = nombre;
        this.pensum = pensum;
    }

    public String getId() {
        return id;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Pensum getPensum() {
        return pensum;
    }

    public void setPensum(Pensum pensum) {
        this.pensum = pensum;
    }
}

