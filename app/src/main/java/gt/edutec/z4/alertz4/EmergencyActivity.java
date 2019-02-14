package gt.edutec.z4.alertz4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import butterknife.ButterKnife;
import gt.edutec.z4.alertz4.entities.Emergency;
import gt.edutec.z4.alertz4.firebase.FirebaseHelper;
import gt.edutec.z4.alertz4.firebase.FirebaseResult;

public class EmergencyActivity extends MapActivity {
    private Emergency emergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        ButterKnife.bind(this);
        helper = new FirebaseHelper(this);
        setUpGoogleApiClient();
        setUpFragment();
        if (getIntent().hasExtra("emergency")){
            helper.getEmergency(getIntent().getStringExtra("emergency"), new FirebaseResult() {
                @Override
                public void ok(Object obj) {
                    emergency = (Emergency) obj;
                    mark(emergency.getLatitud(), emergency.getLongitud(), emergency);
                }

                @Override
                public void fail(String error) {
                    Toast.makeText(EmergencyActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
