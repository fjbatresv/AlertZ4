package gt.edutec.z4.alertz4.clase;

import java.io.Serializable;

public class Persona implements Serializable {
    private String nombre;
    private int edad;
    private String apellido;
    private String direccion;

    public Persona() {
    }

    public Persona(String nombre, int edad, String apellido, String direccion) {
        this.nombre = nombre;
        this.edad = edad;
        this.apellido = apellido;
        this.direccion = direccion;
    }

    public Persona(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombreCompleto(){
        return this.nombre + " " + this.apellido;
    }

    public int getAnioNacimiento(){
        return 2018-this.edad;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj instanceof Persona){
            Persona tmp = (Persona) obj;
            if (tmp.getNombre().equalsIgnoreCase(this.nombre)
            && tmp.getApellido().equalsIgnoreCase(this.apellido)) {
                return true;
            }
        }
        return false;
    }
}
