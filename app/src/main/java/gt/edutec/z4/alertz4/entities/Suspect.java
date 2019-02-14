package gt.edutec.z4.alertz4.entities;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Suspect implements Serializable{

    private String photo;
    private long time;
    private double latitude;
    private double longitud;
    private String desc;
    private String uid;

    public Suspect() {
    }


    public Suspect(String photo, long time, double latitude, double longitud, String desc, String uid) {
        this.photo = photo;
        this.time = time;
        this.latitude = latitude;
        this.longitud = longitud;
        this.desc = desc;
        this.uid = uid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
