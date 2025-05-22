package uts.mi.matricula.dto;

public class GrupoDTO {
    private String codigo;
    private String nombreMateria;

    public GrupoDTO(String codigo, String nombreMateria) {
        this.codigo = codigo;
        this.nombreMateria = nombreMateria;
    }

    // Getters
    public String getCodigo() {
        return codigo;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    // Setters
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }
}
