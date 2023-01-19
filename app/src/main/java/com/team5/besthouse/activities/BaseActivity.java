package com.team5.besthouse.activities;

import static com.team5.besthouse.constants.UnchangedValues.FCM_API;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.team5.besthouse.R;
import com.team5.besthouse.broadcastreceiverandservice.AutoLogoutService;
import com.team5.besthouse.broadcastreceiverandservice.ConnectionReceiver;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.models.Contract;
import com.team5.besthouse.models.ContractStatus;
import com.team5.besthouse.models.User;
import com.team5.besthouse.services.FirebaseNotification;
import com.team5.besthouse.services.StoreService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public abstract class BaseActivity extends AppCompatActivity implements ConnectionReceiver.OnConnectivityChangedListener {
    private ConnectionReceiver connectionReceiver;
    public long lastInteractionTime;
    public static final String CHANNEL_1_ID = "channel1";
    private NotificationManager notificationManager;
    private Handler handler;
    private Runnable runnable;
    private FirebaseAuth firebaseAuth;
    private static final SimpleDateFormat toDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionReceiver = new ConnectionReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectionReceiver, filter);
        startService(new Intent(this, AutoLogoutService.class));
//        Intent intentBackgroundService = new Intent (getApplicationContext(), FirebaseNotification.class);
//        startService(intentBackgroundService);

//        Contract contract = new Contract();
//        Timestamp compareDate = Timestamp.now();
//        Timestamp contractTime = contract.getEndDate();
//        if (contractTime != null) {
//            if(compareDate.equals(contractTime)) {
//                Log.d("Tag", "equal");
//
//            }
//        }
//        else {
//            Log.d("Tag2", "null");
//        }
        //Contract contract = new Contract();
        //String tenantEmail = contract.getTenantEmail();
        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuth.fetchSignInMethodsForEmail(tenantEmail)
//                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
//                        if (task.isSuccessful()) {
//                            SignInMethodQueryResult result = task.getResult();
//                                FirebaseUser user = firebaseAuth.getCurrentUser();
//                                if (user != null) {
//                                    user.getIdToken(true)
//                                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
//                                                if (task.isSuccessful()) {
//                                                    String idToken = task.getResult().getToken();
//                                                    sendNotification(idToken);
//                                                }
//                                            }
//                                        });
//                                }
//                        }
//                    }
//                });

        //Notification
        notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannels();

        StoreService storeService = new StoreService(getApplicationContext());
        Gson json = new Gson();
        User currentUser = json.fromJson(storeService.getStringValue(UnchangedValues.LOGIN_USER), User.class );
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {   //https://stackoverflow.com/questions/37700995/how-to-send-notification-to-specific-users-with-fcm
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                database.collection(UnchangedValues.CONTRACTS_TABLE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Timestamp nowTime = Timestamp.now();
                            Date now = nowTime.toDate();
                            String currentTime = toDateFormat.format(now);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Contract contract = document.toObject(Contract.class);
                                try {
                                    if(currentUser.getEmail().equals(contract.getTenantEmail()) || currentUser.getEmail().equals(contract.getLandlordEmail())) {
                                        if(contract.getContractStatus().equals(ContractStatus.ACTIVE)) {
                                            //Log.d("tag", contract.getContractStatus().toString());
                                            Timestamp contractEndDate = contract.getEndDate();
                                            Date toEndDate = contractEndDate.toDate();
                                            String endDate = toDateFormat.format(toEndDate);
                                            if (endDate != null) {
                                                Log.d("tag", currentTime + " " + endDate);
                                                if (!endDate.equals(currentTime)) {
                                                    Log.d("tag", "Not equal");
                                                }
                                                else if (endDate.equals(currentTime)) {
                                                    Log.d("tag1", "show notification");
                                                    Toast.makeText(getApplicationContext(), "show noti", Toast.LENGTH_LONG).show();
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                        showNotification();
                                                    }
    //                                            firebaseAuth.fetchSignInMethodsForEmail(contract.getTenantEmail())
    //                                                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
    //                                                    @Override
    //                                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
    //                                                        if (task.isSuccessful()) {
    //                                                            //SignInMethodQueryResult result = task.getResult();
    //                                                            if (currentUser != null) {
    //                                                                Log.d("tag1", "not null");
    //                                                                currentUser.getIdToken(true)
    //                                                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
    //                                                                        @Override
    //                                                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
    //                                                                            if (task.isSuccessful()) {
    //                                                                                Log.d("tag1", "show noti now");
    //                                                                                String idToken = task.getResult().getToken();
    //                                                                                //sendNotification(idToken);
    //                                                                            }
    //                                                                        }
    //                                                                    });
    //                                                            }
    //                                                        }
    //                                                    }
    //                                                });
                                                    }
                                                }
                                            }

                                    }
                                } catch (Exception e) {Log.d("tag", "error showing noti");}

                            }
                        }
                    }
                });
                handler.postDelayed(this, 60 * 24 * 1000); //check every 1 day
            }
        };
        handler.post(runnable);
    }

//    private void sendNotification(String idToken) {
//        JSONObject notification = new JSONObject();
//        JSONObject notificationBody = new JSONObject();
//        try {
//            notificationBody.put("title", "Contract Expired");
//            notificationBody.put("message", "Your contract has expired, please renew it.");
//            notification.put("to", idToken);
//            notification.put("data", notificationBody);
//        } catch (JSONException e) {
//            Log.e("tag", "onCreate: " + e.getMessage());
//        }
//        sendNoti(notification);
//    }
//
//    private void sendNoti(JSONObject notification) {
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i("tag", "onResponse: " + response.toString());
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), "Request error", Toast.LENGTH_LONG).show();
//                        Log.i("TAG", "onErrorResponse: Didn't work");
//                    }
//                });
//    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void showNotification() {
        //Save notification
        String title = "BESTHOUSE NOTIFICATION";
        String message = "Your Contract Ends Today!";
        //Intent notificationIntent = new Intent(getApplicationContext(), ContractActivity.class);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //PendingIntent notificationPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(),
                CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                //.setContentIntent(notificationPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
        notificationManager.notify(1, notification);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel1.setDescription("This is Channel 1");
            notificationManager.createNotificationChannel(channel1);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        AutoLogoutService autoLogoutService = AutoLogoutService.getInstance();
        if (autoLogoutService != null) {
            autoLogoutService.updateInteractionTime();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, AutoLogoutService.class));
    }

    @Override
    public void onConnectivityChanged(boolean isConnected) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectionReceiver);
    }
}