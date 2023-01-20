package com.team5.besthouse.broadcastreceiverandservice;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import com.team5.besthouse.R;


//Source: https://www.youtube.com/watch?v=BoiBuRwZ6RE
public class ConnectionReceiver extends BroadcastReceiver {
    private OnConnectivityChangedListener listener;
    public static AlertDialog dialog;
    AlertDialog.Builder builder;

        public ConnectionReceiver(OnConnectivityChangedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
            listener.onConnectivityChanged(isConnected);
            builder = new AlertDialog.Builder(context);
            if (!isConnected) {
                View layout_dialog = LayoutInflater.from(context).inflate(R.layout.wifi_connection_alert, null);
                builder.setView(layout_dialog);

                AppCompatButton btnRetry = layout_dialog.findViewById(R.id.btnRetry);

                //Show dialog
                if(dialog != null) {dialog.dismiss();}
                dialog = builder.create();
                dialog.show();
                dialog.setCancelable(false);
                dialog.getWindow().setGravity(Gravity.CENTER);

                btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        onReceive(context, intent);
                    }
                });
            }
        }

    public interface OnConnectivityChangedListener {
        void onConnectivityChanged(boolean isConnected);
    }
}