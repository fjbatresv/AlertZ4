package gt.edutec.z4.alertz4;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import gt.edutec.z4.alertz4.firebase.FirebaseHelper;
import gt.edutec.z4.alertz4.firebase.FirebaseResult;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth auth;
    private FirebaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        helper = new FirebaseHelper(this);

        if (auth.getCurrentUser() == null) {
            login();
        }else {
            fcm();
        }
    }

    private void login() {
        AuthUI.SignInIntentBuilder intentBuilder = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(getProviders())
                .setLogo(R.drawable.baseline_warning_white_48)
                .setIsSmartLockEnabled(!BuildConfig.DEBUG);
        Intent intent = intentBuilder.build();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private List<AuthUI.IdpConfig> getProviders() {
        List<AuthUI.IdpConfig> providers = new ArrayList<AuthUI.IdpConfig>();
        providers.add(new AuthUI.IdpConfig.PhoneBuilder().build());
        providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());
        return providers;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                fcm();
            }else{
                Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fcm() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Fail FCM", Toast.LENGTH_LONG).show();
                    auth.signOut();
                    return;
                }
                final String token = task.getResult().getToken();
                final String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                helper.getActiveFcm(id, new FirebaseResult() {
                    @Override
                    public void ok(Object obj) {
                        if (obj == null) {
                            obj = false;
                        }
                        helper.saveFCM(token, id, (boolean) obj, new FirebaseResult() {
                            @Override
                            public void ok(Object obj) {
                                welcome();
                            }

                            @Override
                            public void fail(String error) {
                                Toast.makeText(LoginActivity.this, "Fail FCM", Toast.LENGTH_LONG).show();
                                auth.signOut();
                            }
                        });
                    }

                    @Override
                    public void fail(String error) {

                    }
                });
            }
        });
    }

    private void welcome() {
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
                        |Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
