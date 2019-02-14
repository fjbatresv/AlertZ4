package gt.edutec.z4.alertz4.clase;

public class Serpiente extends Animal {

    public Serpiente() {
        this.raza = "Serpiente";
        this.dientes= 2;
        this.ojos=2;
    }

    @Override
    public String mover() {
        return "Arrastra";
    }

}
