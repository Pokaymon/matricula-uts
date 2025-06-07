package uts.mi.matricula.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String cedula;

    @Indexed(unique = true)
    private String username;

    private String password;
    private String nombre;
    private String apellido;
    private String rol;

    @DBRef
    private Pensum pensum;

    public User() {}

    public User(String cedula, String username, String password, String nombre, String apellido, String rol, Pensum pensum) {
        this.cedula = cedula;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
	this.pensum = pensum;}

    public String getId() {
        return id;}

    public String getCedula() {
        return cedula;}
    public void setCedula(String cedula) {
        this.cedula = cedula;}

    public String getUsername() {
        return username;}
    public void setUsername(String username) {
        this.username = username;}

    public String getPassword() {
        return password; }
    public void setPassword(String password) {
        this.password = password;}

    public String getNombre() {
        return nombre;}
    public void setNombre(String nombre) {
        this.nombre = nombre;}

    public String getApellido() {
        return apellido;}
    public void setApellido(String apellido) {
        this.apellido = apellido;}

    public String getRol() {
        return rol;}
    public void setRol(String rol) {
        this.rol = rol;}

    public Pensum getPensum() {
        return pensum;}
    public void setPensum(Pensum pensum) {
        this.pensum = pensum;}
}
