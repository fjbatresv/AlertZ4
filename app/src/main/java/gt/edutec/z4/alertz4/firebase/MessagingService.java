package gt.edutec.z4.alertz4.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import gt.edutec.z4.alertz4.EmergencyActivity;
import gt.edutec.z4.alertz4.MainActivity;
import gt.edutec.z4.alertz4.R;
import gt.edutec.z4.alertz4.SuspectDetailActivity;
import gt.edutec.z4.alertz4.entities.Emergency;

public class MessagingService extends FirebaseMessagingService {

    String TAG = "Service";
    private List<String> numeros = new ArrayList<String>(){};
    private FirebaseHelper helper;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        helper = new FirebaseHelper(this);
        this.numeros.addAll(helper.getEmergencyPhones());
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Log.e("service", "New Message : " + remoteMessage.getMessageId());
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(), R.mipmap.ic_launcher
                )).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"));
        if(remoteMessage.getData().containsKey("emergencia_uid")){
            Intent intent = new Intent(getApplicationContext(), EmergencyActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP
                            |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("emergency", remoteMessage.getData().get("emergencia_uid"));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);
        }
        if(remoteMessage.getData().containsKey("suspect_uid")){
            Intent intent = new Intent(getApplicationContext(), SuspectDetailActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP
                            |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("suspect", remoteMessage.getData().get("suspect_uid"));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 120, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);
        }
        if (remoteMessage.getData().containsKey("emergencia_uid") 
        || remoteMessage.getData().containsKey("suspect_uid")) {
            sendSms(remoteMessage.getData().get("desc")
            , remoteMessage.getData().get("url"));
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = this.getString(R.string.default_notification_channel_id);
            NotificationChannel channel = new NotificationChannel(channelId,   remoteMessage.getData().get("title"), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(remoteMessage.getData().get("body"));
            manager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
        }
        manager.notify(1000, notificationBuilder.build());
        super.onMessageReceived(remoteMessage);
    }

    private void sendSms (String desc, String url) {
        SmsManager manager = SmsManager.getDefault();
        String mensaje = "Nueva %tipo%: ";
        if (desc != null && !desc.isEmpty()) {
            mensaje = mensaje.replace("%tipo%", "SOSPECHA");
            mensaje = mensaje + " " + desc;
        }else{
            mensaje = mensaje.replace("%tipo%", "EMERGENCIA");
        }
        for (String num : numeros) {
            manager.sendTextMessage(num, null, mensaje + url, null, null);
        }
    }
}
