package com.team5.besthouse.broadcastreceiverandservice;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

public class AutoLogoutService extends Service {
    private AutoLogoutService. listener;
    public static CountDownTimer timer;
    @Override
    public void onCreate(){
        // TODO Auto-generated method stub
        super.onCreate();
        timer = new CountDownTimer(1 * 60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                //Some code
                Log.v("LogoutService", "Service Started");
            }

            public void onFinish() {
                Log.v("LogoutService", "Call Logout by Service");
                // Code for Logout
                stopSelf();
            }
        };
    }
    public AutoLogoutService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}