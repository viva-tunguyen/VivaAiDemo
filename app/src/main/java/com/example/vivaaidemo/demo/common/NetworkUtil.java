package com.example.vivaaidemo.demo.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil extends BroadcastReceiver {
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;
    private static String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private Callback callback;

    public NetworkUtil(Callback callback) {
        this.callback = callback;
    }
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (CONNECTIVITY_CHANGE.equals(intent.getAction())) {
            callback.getNetworkInfo();
        }
    }

    public interface Callback {
        void getNetworkInfo();
    }
}