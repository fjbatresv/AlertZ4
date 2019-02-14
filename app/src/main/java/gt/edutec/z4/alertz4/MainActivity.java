package gt.edutec.z4.alertz4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import gt.edutec.z4.alertz4.firebase.FirebaseHelper;

public class MainActivity extends BaseActivity implements SensorListener {

    private static final int PERMISSIONS_REQUEST_LOCATION = 100;
    @BindView(R.id.suspect)
    ImageView suspect;
    @BindView(R.id.emergency)
    ImageView emergency;
    @BindString(R.string.emergencySent)
    String emergencySent;

    private SensorManager sensorMgr;
    private FusedLocationProviderClient locationProviderClient;
    private int SHAKE_THRESHOLD = 800;
    private long lastUpdate;
    private float x, y, z;
    private float last_x, last_y, last_z;
    private FirebaseHelper helper;
    private Date lastEmergency;
    private Double emergencyDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.helper = new FirebaseHelper(this);
        this.SHAKE_THRESHOLD = helper.getShakeThreshold();
        this.emergencyDelay = helper.getEmergencyDelay();
        permisos();
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMgr = null;
    }

    private void sensor() {
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorMgr.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);

    }

    private void permisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.SEND_SMS
            }, PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @OnLongClick(R.id.emergency)
    public boolean emergency(){
        if (this.SHAKE_THRESHOLD == 0) {
            sendEmergency();
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void sendEmergency(){
        Date date = new Date();
        if (lastEmergency == null || (date.getTime() - lastEmergency.getTime()) > emergencyDelay*60*1000) {//Milliseconds
            lastEmergency = date;
            locationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location!=null){
                        helper.Emergency(location.getLatitude(), location.getLongitude());
                        Toast.makeText(MainActivity.this, emergencySent, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @OnClick(R.id.suspect)
    public void suspect(){
        startActivity(new Intent(this, SuspectActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case PERMISSIONS_REQUEST_LOCATION:{
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }else{
                    permisos();
                }
                return;
            }
        }
    }


    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_Y];
                z = values[SensorManager.DATA_Z];

                float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    sendEmergency();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }
}
