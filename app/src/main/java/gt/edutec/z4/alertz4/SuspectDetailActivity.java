package gt.edutec.z4.alertz4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import gt.edutec.z4.alertz4.entities.Emergency;
import gt.edutec.z4.alertz4.entities.Suspect;
import gt.edutec.z4.alertz4.firebase.FirebaseHelper;
import gt.edutec.z4.alertz4.firebase.FirebaseResult;

public class SuspectDetailActivity extends MapActivity{

    @BindView(R.id.desc)
    TextView desc;

    private Suspect suspect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspect_detail);
        ButterKnife.bind(this);
        helper = new FirebaseHelper(this);
        requestManager = Glide.with(getApplicationContext());
        setUpGoogleApiClient();
        setUpFragment();
        if (getIntent().hasExtra("suspect")){
            helper.getSuspect(getIntent().getStringExtra("suspect"), new FirebaseResult() {
                @Override
                public void ok(Object obj) {
                    suspect = (Suspect) obj;
                    desc.setText(suspect.getDesc());
                    mark(suspect.getLatitude(), suspect.getLongitud(), suspect);
                }

                @Override
                public void fail(String error) {
                    Toast.makeText(SuspectDetailActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
