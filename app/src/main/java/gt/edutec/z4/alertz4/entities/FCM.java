package gt.edutec.z4.alertz4.entities;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
@IgnoreExtraProperties
public class FCM implements Serializable {

    private String token;
    private long time;
    private boolean active;

    public FCM() {
    }

    public FCM(String token, long time, boolean active) {
        this.token = token;
        this.time = time;
        this.active = active;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
