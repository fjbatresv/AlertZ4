package gt.edutec.z4.alertz4.entities;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Emergency implements Serializable {

    private long time;
    private double latitud;
    private double longitud;
    private String key;
    private String uid;

    public Emergency() {
    }

    public Emergency(long time, double latitud, double longitud, String uid) {
        this.time = time;
        this.latitud = latitud;
        this.longitud = longitud;
        this.uid = uid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
