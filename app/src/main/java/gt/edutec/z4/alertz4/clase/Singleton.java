package gt.edutec.z4.alertz4.clase;

import io.reactivex.Single;

public class Singleton {

    private String nombre;
    private int base;
    private static Singleton stn = null;

    public static Singleton getInstance(){
        if (stn == null) {
            stn = new Singleton();
        }
        return stn;
    }

    public String getNombre() {
        return nombre.toUpperCase();
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void addToBase(int nuevo){
        this.base += nuevo;
    }

    public String getDbConf(){
        return DBConf.getInstance().getConnection();
    }

    public int getBase() {
        return this.base;
    }
}
