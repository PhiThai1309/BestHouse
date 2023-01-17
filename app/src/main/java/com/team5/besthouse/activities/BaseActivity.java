package com.team5.besthouse.activities;

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
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.team5.besthouse.R;
import com.team5.besthouse.broadcastreceiverandservice.AutoLogoutService;
import com.team5.besthouse.broadcastreceiverandservice.ConnectionReceiver;

public abstract class BaseActivity extends AppCompatActivity implements ConnectionReceiver.OnConnectivityChangedListener {
    private ConnectionReceiver connectionReceiver;
    public long lastInteractionTime;
    public static final String CHANNEL_1_ID = "channel1";
    private NotificationManager notificationManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionReceiver = new ConnectionReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectionReceiver, filter);

        startService(new Intent(this, AutoLogoutService.class));

        //Notification
        notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannels();

    }

    public void showNotification() {
        //Save notification
        String title = "APPOINTMENT SAVED";
        String message = "Your appointment has been booked. Please come to the hospital on time. Thank you!";
        Intent notificationIntent = new Intent(this, ContractActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this,
                CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(notificationPendingIntent)
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