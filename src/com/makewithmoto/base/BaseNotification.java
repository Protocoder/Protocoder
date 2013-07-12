package com.makewithmoto.base;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.makewithmoto.R;


//TODO http://developer.android.com/training/notify-user/expanded.html 
public class BaseNotification {

	public static int NOTIFICATION_APP_RUNNING = 1;
    public static String ACTION_STOP = "STOP";
	
	Context c;
	NotificationManager mNotificationManager;

	private Builder mBuilder;

	public BaseNotification(Context context) {
		c = context;

		mNotificationManager = (NotificationManager) c
				.getSystemService(Context.NOTIFICATION_SERVICE);

	}

	public void show(Class<?> cls, int icon, String text, String title) {


		Intent notificationIntent = new Intent(c, cls);
		notificationIntent.setAction(ACTION_STOP);
		PendingIntent contentIntent = PendingIntent.getActivity(c, 0,
				notificationIntent, 0);
		long[] vibrate = { 0, 100, 200, 300 };

		mBuilder = new NotificationCompat.Builder(c);
		mBuilder.setContentTitle(title)
				.setContentText(text)
				.setSmallIcon(icon)
				.setOngoing(true)
				.setProgress(0, 0, true)
				.setVibrate(vibrate)
				.setContentIntent(contentIntent)
				.addAction(R.drawable.ic_launcher, "Stop Server", contentIntent);
			

		mNotificationManager.notify(NOTIFICATION_APP_RUNNING, mBuilder.build());

	}

	public void hide() {
		mNotificationManager.cancel(null, NOTIFICATION_APP_RUNNING);

	}

}
