package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.team5.besthouse.broadcastreceiverandservice.AutoLogoutService;
import com.team5.besthouse.broadcastreceiverandservice.ConnectionReceiver;

public abstract class BaseActivity extends AppCompatActivity implements ConnectionReceiver.OnConnectivityChangedListener {
    private ConnectionReceiver connectionReceiver;
    public long lastInteractionTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionReceiver = new ConnectionReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectionReceiver, filter);

        startService(new Intent(this, AutoLogoutService.class));

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        AutoLogoutService autoLogoutService = AutoLogoutService.getInstance();
        if (autoLogoutService != null) {
            autoLogoutService.updateInteractionTime();
        }
        Toast.makeText((Context)BaseActivity.this, "updating", Toast.LENGTH_SHORT).show();
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