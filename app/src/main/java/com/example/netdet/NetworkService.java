package com.example.netdet;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.Manifest;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NetworkService extends Service {

    private static final String CHANNEL_ID = "network_channel";
    private int lastNetworkType = -1;
    private TelephonyManager tm;
    private TelephonyCallback callback;

    @RequiresApi(api = Build.VERSION_CODES.S)
    private class NetworkCallback extends TelephonyCallback
            implements TelephonyCallback.DataConnectionStateListener {
        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            if (lastNetworkType == TelephonyManager.NETWORK_TYPE_NR && networkType == TelephonyManager.NETWORK_TYPE_LTE) {
                sendAlertNotification();
            }
            lastNetworkType = networkType;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, getForegroundNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
        }
        else
            startForeground(1, getForegroundNotification());
        monitorNetwork();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void monitorNetwork() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            callback = new NetworkCallback();
            tm.registerTelephonyCallback(getMainExecutor(), callback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && callback != null && tm != null) {
            tm.unregisterTelephonyCallback(callback);
        }
    }

    private Notification getForegroundNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Network Monitor Running")
                .setContentText("Watching for 5G → 4G changes")
                .setSmallIcon(android.R.drawable.ic_menu_preferences)
                .build();
    }

    private void sendAlertNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Network Changed")
                .setContentText("Switched from 5G to 4G")
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(2, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Network Monitor",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}