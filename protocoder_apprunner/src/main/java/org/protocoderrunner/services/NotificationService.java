package org.protocoderrunner.services;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.protocoderrunner.utils.AndroidUtils;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NotificationService extends NotificationListenerService {
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (AndroidUtils.isVersionKitKat()) {
            String pack = sbn.getPackageName();
            String ticker = getString(sbn.getNotification().tickerText);
            Bundle extras = sbn.getNotification().extras;
            String title = "";
            String text = "";
            if (extras != null) {
                title = extras.getString("android.title");
                text = getString(extras.getCharSequence("android.text")).toString();
            }

            Log.i("Package", pack);
            Log.i("Ticker", ticker);
            Log.i("Title", title);
            Log.i("Text", text);
            Intent msgrcv = new Intent("Msg");
            msgrcv.putExtra("package", pack);
            msgrcv.putExtra("ticker", ticker);
            msgrcv.putExtra("title", title);
            msgrcv.putExtra("text", text);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        }
    }

    private String getString(CharSequence c) {
       String ret;

        if (c == null) {
            ret = "";
        } else {
            ret = c.toString();
        }

        return ret;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }
}