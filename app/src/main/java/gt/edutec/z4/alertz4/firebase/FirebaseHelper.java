package gt.edutec.z4.alertz4.firebase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import gt.edutec.z4.alertz4.R;
import gt.edutec.z4.alertz4.entities.Emergency;
import gt.edutec.z4.alertz4.entities.FCM;
import gt.edutec.z4.alertz4.entities.Suspect;

public class FirebaseHelper {

    private DatabaseReference ref;
    private FirebaseAuth auth;
    private StorageReference storage;
    private FirebaseRemoteConfig remoteConfig;
    private FirebaseAnalytics analytics;


    public FirebaseHelper(Context context) {
        this.ref = FirebaseDatabase.getInstance().getReference();
        this.auth = FirebaseAuth.getInstance();
        this.storage = FirebaseStorage.getInstance().getReference();
        this.remoteConfig = FirebaseRemoteConfig.getInstance();
        this.analytics = FirebaseAnalytics.getInstance(context);
        initConfig();
    }

    private void initConfig() {
        remoteConfig.setDefaults(R.xml.remote_config_default);
        remoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    remoteConfig.activateFetched();
                } else {
                    Log.e("helpe", "Remote COnfig Init failed");
                }
            }
        });
    }

    public void Emergency(final double latitud, final double longitud) {
        final long time = new Date().getTime();
        this.ref.child("emergency").push()
                .setValue(new Emergency(time, latitud, longitud, auth.getUid()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Bundle bundle = new Bundle();
                        bundle.putString("UID", auth.getUid());
                        bundle.putDouble("LATITUD", latitud);
                        bundle.putDouble("LONGITUD", longitud);
                        bundle.putLong("TIME", time);
                        analytics.setUserId(auth.getUid());
                        analytics.logEvent("EMERGENCY", bundle);
                    }
                });
    }

    public void saveFCM(String token, String id, boolean active,  final FirebaseResult result) {
        this.ref.child("fcm").child(id).setValue(new FCM(token, new Date().getTime(), active)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    result.ok(null);
                } else {
                    result.fail(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    public void getEmergency(String llave, final FirebaseResult result) {
        this.ref.child("emergency").child(llave).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result.ok(dataSnapshot.getValue(Emergency.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.fail(databaseError.toException().getLocalizedMessage());
            }
        });
    }

    public void newSuspect(final Uri uri, final double latitud, final double longitud, final String desc, final FirebaseResult result) {
        final StorageReference tmpref = storage.child("suspects").child(uri.getLastPathSegment());
        UploadTask uploadTask = tmpref.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    result.fail(task.getException().getLocalizedMessage());
                }
                return tmpref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.i("success", uri.toString());
                    final long time = new Date().getTime();
                    ref.child("suspect").push().setValue(new Suspect(downloadUri.toString(), time, latitud, longitud, desc, auth.getUid()))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("UID", auth.getUid());
                                        bundle.putDouble("LATITUD", latitud);
                                        bundle.putDouble("LONGITUD", longitud);
                                        bundle.putLong("TIME", time);
                                        analytics.setUserId(auth.getUid());
                                        analytics.logEvent("SUSPECT", bundle);
                                        result.ok(null);
                                    } else {
                                        result.fail(task.getException().getLocalizedMessage());
                                    }
                                }
                            });
                } else {
                    result.fail(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    public void getSuspect(String llave, final FirebaseResult result) {
        this.ref.child("suspect").child(llave).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result.ok(dataSnapshot.getValue(Suspect.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.fail(databaseError.toException().getLocalizedMessage());
            }
        });
    }

    public Integer getShakeThreshold() {
        Long tmp = remoteConfig.getLong("SHAKE_THRESHOLD");
        return tmp.intValue();
    }

    public Double getEmergencyDelay() {
        return remoteConfig.getDouble("EMERGENCY_DELAY");
    }

    public List<String> getEmergencyPhones() {
        String lista = remoteConfig.getString("EMERGENCY_PHONES");
        Log.i("emergency_phones", lista);
        return Arrays.asList(lista.split("\\\\s*,\\\\s*"));
    }

    public void enableFcm(String id) {
        ref.child("active_news").child(id).setValue(true);
    }

    public void getActiveFcm(String id, final FirebaseResult result) {
        ref.child("active_news").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result.ok(dataSnapshot.getValue(boolean.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                result.fail(databaseError.toException().getLocalizedMessage());
            }
        });
    }

    public String getAdminCode() {
        return remoteConfig.getString("ADMIN_CODE");
    }
}
