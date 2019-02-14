package gt.edutec.z4.alertz4;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import gt.edutec.z4.alertz4.firebase.FirebaseHelper;
import gt.edutec.z4.alertz4.firebase.FirebaseResult;

public class BaseActivity extends AppCompatActivity {

    public FirebaseHelper helper;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        helper = new FirebaseHelper(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case R.id.fcm:
                saveFcm();
                break;
            /*case R.id.suspects:
                break;
            case R.id.emergencies:
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveFcm() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_active_news, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.codigo);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(R.string.okbtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().equals(helper.getAdminCode())) {
                    String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    helper.enableFcm(id);
                    news("Bienvenido administrador");
                } else {
                    news("Solo los administradores reciben notificaciones");
                }
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void news(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
