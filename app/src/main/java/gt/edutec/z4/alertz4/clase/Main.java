package gt.edutec.z4.alertz4.clase;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class Main {

    public static void main(String[] args){
        clase();
    }

    public static void clase(){
        Persona persona = new Persona("Javier", "Batres");
        String serializado = "";
        try {
            serializado = ObjectSerializer.serialize(persona);
            System.out.println(serializado);
            System.out.println(((Persona)ObjectSerializer.deserialize(serializado))
                    .getNombreCompleto());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
