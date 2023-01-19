package com.team5.besthouse.activities;

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
import android.widget.Toast;

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
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNEL_3_ID = "channel3";
    public static final String CHANNEL_4_ID = "channel4";
    private NotificationManager notificationManager;
    private Handler handler;
    private Runnable runnable;
    private FirebaseAuth firebaseAuth;
    private boolean isNotiShow = false;
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

        firebaseAuth = FirebaseAuth.getInstance();

        //Notification
        notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannels1();
        notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannels2();
        notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannels3();
        notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannels4();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if((this.getClass().getSimpleName().equals("MainActivity") || this.getClass().getSimpleName().equals("LandlordActivity")) && !isNotiShow) {
            notifyFunction();
            isNotiShow = true;
        }
    }

    public void notifyFunction() {
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
                                            Timestamp contractEndDate = contract.getEndDate();
                                            Date toEndDate = contractEndDate.toDate();
                                            String endDate = toDateFormat.format(toEndDate);
                                            Timestamp contractStartDate = contract.getStartDate();
                                            Date toStartDate = contractStartDate.toDate();
                                            String startDate = toDateFormat.format(toStartDate);
                                            if (endDate.equals(currentTime)) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                    showContractEndNotification();
                                                }
                                            }
                                            else if(startDate.equals(currentTime)) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                    showContractAcceptedNotification();
                                                }
                                            }
                                        }
                                        else if(contract.getContractStatus().equals(ContractStatus.PENDING)) {
                                            Timestamp contractStartDate = contract.getStartDate();
                                            Date toStartDate = contractStartDate.toDate();
                                            String startDate = toDateFormat.format(toStartDate);
                                            if (startDate.equals(currentTime)) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                    showContractPendingNotification();
                                                }
                                            }
                                        }
                                        else if(contract.getContractStatus().equals(ContractStatus.REJECT)) {
                                            Timestamp contractStartDate = contract.getStartDate();
                                            Date toStartDate = contractStartDate.toDate();
                                            String startDate = toDateFormat.format(toStartDate);
                                            if (startDate.equals(currentTime)) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                    showContractRejectNotification();
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {Log.d("tag", "error showing notification");}
                            }
                        }
                    }
                });
                handler.postDelayed(this, 60 * 24 * 1000); //check every 1 day
            }
        };
        handler.post(runnable);
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    public void showContractEndNotification() {
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
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .build();
        notificationManager.notify(1, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void showContractAcceptedNotification() {
        //Save notification
        String title = "BESTHOUSE NOTIFICATION";
        String message = "Your Pending Contract Is Now Active!";
        //Intent notificationIntent = new Intent(getApplicationContext(), ContractActivity.class);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //PendingIntent notificationPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(),
                CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                //.setContentIntent(notificationPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .build();
        notificationManager.notify(2, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void showContractPendingNotification() {
        //Save notification
        String title = "BESTHOUSE NOTIFICATION";
        String message = "Your Have A Pending Contract";
        //Intent notificationIntent = new Intent(getApplicationContext(), ContractActivity.class);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //PendingIntent notificationPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(),
                CHANNEL_3_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                //.setContentIntent(notificationPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .build();
        notificationManager.notify(3, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void showContractRejectNotification() {
        //Save notification
        String title = "BESTHOUSE NOTIFICATION";
        String message = "The Contract Has Been Rejected";
        //Intent notificationIntent = new Intent(getApplicationContext(), ContractActivity.class);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //PendingIntent notificationPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(),
                CHANNEL_4_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                //.setContentIntent(notificationPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .build();
        notificationManager.notify(4, notification);
    }

    private void createNotificationChannels1() {
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

    private void createNotificationChannels2() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel2.setDescription("This is Channel 2");
            notificationManager.createNotificationChannel(channel2);
        }
    }

    private void createNotificationChannels3() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "Channel 3",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel3.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel3.setDescription("This is Channel 3");
            notificationManager.createNotificationChannel(channel3);
        }
    }

    private void createNotificationChannels4() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel4 = new NotificationChannel(
                    CHANNEL_4_ID,
                    "Channel 4",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel4.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel4.setDescription("This is Channel 4");
            notificationManager.createNotificationChannel(channel4);
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