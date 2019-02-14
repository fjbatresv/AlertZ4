package gt.edutec.z4.alertz4.clase;

public abstract class Animal {
    public int ojos, dientes;
    public String raza;
    private String nombre;

    public abstract String mover();

    public String getNombre(){
        return this.nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        String move = mover();
        return "Animal: " + this.raza + " | Nombre: " + this.nombre
                + " | Ojos: " + this.ojos + " | Dientes: " + this.dientes
                + " | Se mueve: " + move;
    }
}
