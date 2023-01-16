package com.team5.besthouse.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.team5.besthouse.broadcastreceiverandservice.ConnectionReceiver;

public abstract class BaseActivity extends AppCompatActivity implements ConnectionReceiver.OnConnectivityChangedListener {
    private ConnectionReceiver connectionReceiver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionReceiver = new ConnectionReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectionReceiver, filter);
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