package uts.mi.matricula.dto;

public class GrupoDTO {
    private String id;
    private String codigo;
    private String nombreMateria;

    public GrupoDTO(String id, String codigo, String nombreMateria) {
        this.id = id;
	this.codigo = codigo;
        this.nombreMateria = nombreMateria;
    }

    // Getters
    public String getId() {
	return id;
    }

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
