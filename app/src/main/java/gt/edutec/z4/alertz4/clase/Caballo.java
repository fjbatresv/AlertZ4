package gt.edutec.z4.alertz4.clase;

public class Caballo extends Animal {

    public Caballo(){
        this.raza = "Caballo";
        this.ojos=2;
        this.dientes=35;
    }

    @Override
    public String mover() {
        return "Galopea";
    }
}
