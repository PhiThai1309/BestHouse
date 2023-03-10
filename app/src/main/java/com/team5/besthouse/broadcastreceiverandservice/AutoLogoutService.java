package com.team5.besthouse.broadcastreceiverandservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team5.besthouse.activities.LoginActivity;
import com.team5.besthouse.constants.UnchangedValues;
import com.team5.besthouse.services.StoreService;

public class AutoLogoutService extends Service {
    private static final long LOGOUT_TIMEOUT = 1000 * 1000; // 10 seconds
    private FirebaseAuth firebaseAuth;
    private StoreService storeService;
    private long lastInteractionTime;
    private Handler handler;
    private Runnable logoutRunnable;
    private static AutoLogoutService instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        firebaseAuth = FirebaseAuth.getInstance();
        lastInteractionTime = System.currentTimeMillis();
        handler = new Handler();
        storeService = new StoreService(getApplicationContext());
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        logoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastInteractionTime > LOGOUT_TIMEOUT) {
                    if(currentUser != null) {
                        setSignOutAction();
                        stopSelf();
                    }
                }
                else {
                    handler.postDelayed(logoutRunnable, LOGOUT_TIMEOUT);
                }
            }
        };
        handler.post(logoutRunnable);
    }

    private void setSignOutAction() {
        if(storeService.clearTheStore()) {
            tempDisconnectGoogleAccount();        //sign out from google
            LoginManager.getInstance().logOut(); // signout from facebook
            FirebaseAuth.getInstance().signOut(); // sign out from firebase
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(UnchangedValues.LOGOUT_PERFORMED, "logout");
            startActivity(intent);
        }
    }

    private void tempDisconnectGoogleAccount() {
        GoogleSignInClient mGoogleSignInAccount = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN) ;
        if(mGoogleSignInAccount != null)
        {
            mGoogleSignInAccount.signOut();
        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("TAG", "Task removed, stopping service");
        stopSelf();
    }

    public static AutoLogoutService getInstance() {
        return instance;
    }

    public void updateInteractionTime() {
        lastInteractionTime = System.currentTimeMillis();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}