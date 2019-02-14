package gt.edutec.z4.alertz4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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

import gt.edutec.z4.alertz4.entities.Emergency;
import gt.edutec.z4.alertz4.entities.Suspect;
import retrofit2.http.OPTIONS;

public class MapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public GoogleMap googleMap;
    private GoogleApiClient apiClient;
    public HashMap<Marker, Object> markers = new HashMap<>();
    private boolean resolvingError = false;
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int REQUEST_RESOLVE_ERROR = 0;
    public RequestManager requestManager;
    public final static String NOMBRE_ORIGEN = "alert_z4";
    private Emergency emergency;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,

                }, PERMISSIONS_REQUEST_LOCATION);
            }
            return;
        }
        setLastLocation();
    }

    public void mark(double lat, double longi, Object obj) {
        LatLng location = new LatLng(lat, longi);
        Marker marker = this.googleMap.addMarker(new MarkerOptions().position(location));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20));
        markers.put(marker, obj);
    }

    public void setUpFragment() {
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void setUpGoogleApiClient() {
        if (apiClient==null){
            apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }
    }

    @SuppressLint("MissingPermission")
    private void setLastLocation() {
        if (LocationServices.getFusedLocationProviderClient(this).getLocationAvailability().isSuccessful()){
            this.googleMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(this, "Ubicacion no disponible", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        apiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (resolvingError) {
            return;
        } else if (connectionResult.hasResolution()) {
            resolvingError = true;
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException ex) {
            }
        } else {
            resolvingError = true;
            GoogleApiAvailability.getInstance()
                    .getErrorDialog(this, connectionResult.getErrorCode(), REQUEST_RESOLVE_ERROR)
                    .show();
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Object obj = markers.get(marker);
        if (obj instanceof Suspect){
            View view = getLayoutInflater().inflate(R.layout.suspect_map, null);
            Suspect suspect = (Suspect) obj;
            Log.e("SUSPECT", suspect.getPhoto());
            ImageView image = (ImageView) view.findViewById(R.id.suspect_image);
            RequestOptions options = new RequestOptions();
            options.error(R.drawable.baseline_warning_white_48);
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
            options.placeholder(R.drawable.suspect);
            options.override(700, 700);
            requestManager.load(Uri.parse(suspect.getPhoto()))
                    .apply(options)
                    .into(image);
            return view;
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setInfoWindowAdapter(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSIONS_REQUEST_LOCATION);
            }
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
    }
}
