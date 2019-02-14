package gt.edutec.z4.alertz4;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.internal.location.zzas;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gt.edutec.z4.alertz4.firebase.FirebaseResult;

public class SuspectActivity extends BaseActivity {

    private static final int REQUEST_CAPTURE_IMAGE = 150;
    private static final int REQUEST_PICTURE_CAPTURE = 130;
    private String pictureFilePath;
    @BindView(R.id.texto)
    EditText texto;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.loader)
    ProgressBar loader;
    @BindView(R.id.new_suspect_image)
    ImageView image;
    @BindView(R.id.add_image)
    LinearLayout add;

    @BindString(R.string.sospechosoDone)
    String sospechosoDone;

    private FusedLocationProviderClient locationProviderClient;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspect);
        ButterKnife.bind(this);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @OnClick(R.id.foto)
    public void foto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Elige una foto"), REQUEST_CAPTURE_IMAGE);
    }

    //@OnClick(R.id.camera)
    public void cameraAdd(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (Exception ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "gt.edutec.z4.alertz4.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        }
    }

    public File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "ALERTZ4_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
            image = File.createTempFile(pictureFile,  ".jpg", storageDir);
            pictureFilePath = image.getAbsolutePath();
            return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE){
            if (resultCode == RESULT_OK){
                if(data != null && data.getExtras() != null){
                    this.uri = data.getData();
                    updateImage();
                }
            }
        } else if (requestCode == REQUEST_PICTURE_CAPTURE) {
            if (resultCode == RESULT_OK){
                Log.e("FOTO", data.getData().toString());
                File imgFile = new  File(pictureFilePath);
                if(imgFile.exists())            {
                    this.uri = Uri.fromFile(imgFile);
                    updateImage();
                }
            }else{
                Log.e("FOTO", String.valueOf(resultCode));
                Log.e("FOTO", data.toString());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateImage() {
        if (uri != null){
            image.setImageURI(uri);
            add.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
        }else{
            image.setVisibility(View.GONE);
            add.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.send)
    @SuppressLint("MissingPermission")
    public void send(){
        loading(true);
        locationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    helper.newSuspect(uri, location.getLatitude(), location.getLongitude(), texto.getText().toString()
                            , new FirebaseResult() {
                        @Override
                        public void ok(Object obj) {
                            loading(false);
                            Toast.makeText(SuspectActivity.this, sospechosoDone, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SuspectActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP
                            |Intent.FLAG_ACTIVITY_NEW_TASK));
                        }

                        @Override
                        public void fail(String error) {
                            loading(false);
                            Toast.makeText(SuspectActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void loading(boolean b) {
        loader.setVisibility(b ? View.VISIBLE : View.GONE);
        send.setEnabled(!b);
    }
}
