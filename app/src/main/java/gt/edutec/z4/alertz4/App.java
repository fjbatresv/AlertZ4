package gt.edutec.z4.alertz4;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class App extends Application {

    static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseApp.initializeApp(this);
    }

    public static synchronized App getInstance(){
        return instance;
    }
}
