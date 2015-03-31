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
            String pack = "";
            String ticker = "";
            Bundle extras = null;
            String title = "";
            String text = "";

            pack = sbn.getPackageName();
            try {
                ticker = sbn.getNotification().tickerText.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                extras = sbn.getNotification().extras;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                title = extras.getString("android.title");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                text = extras.getCharSequence("android.text").toString();
            } catch (Exception e) {
                e.printStackTrace();
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

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }
}