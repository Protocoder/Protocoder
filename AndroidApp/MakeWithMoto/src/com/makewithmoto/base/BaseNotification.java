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

	Context c;
	NotificationManager mNotificationManager;
	boolean mIsShowing = false;

	private Builder mBuilder;

	public BaseNotification(Context context) {
		c = context;

		mNotificationManager = (NotificationManager) c
				.getSystemService(Context.NOTIFICATION_SERVICE);

	}

	public void show(Class<?> cls, int icon, String text, String title){
	    show(cls, icon, text, title, R.drawable.ic_launcher);
	}
	
	public void show(Class<?> cls, int icon, String text, String title, int actionIcon) {
		CharSequence tickerText = "MWM";
		mIsShowing = true;

		long when = System.currentTimeMillis();

		Intent notificationIntent = new Intent(c, cls);
		PendingIntent contentIntent = PendingIntent.getActivity(c, 0,
				notificationIntent, 0);
		
		Intent stopServerIntent = new Intent();  
		stopServerIntent.setAction("com.makewithmoto.intent.action.STOP_SERVER");
		PendingIntent stopServerPendingIntent = PendingIntent.getBroadcast(c, 0, stopServerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder = new NotificationCompat.Builder(c);
		mBuilder.setContentTitle(title)
				.setContentText(text)
				.setSmallIcon(icon)
				.setOngoing(true)
				.setProgress(0, 0, true)
				.setContentIntent(contentIntent)
				//.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.addAction(actionIcon, "Stop server", stopServerPendingIntent);
			
		// notification.defaults |= Notification.DEFAULT_LIGHTS;
		// notification.ledARGB = Color.RED;
		// notification.ledOffMS = 300;
		// notification.ledOnMS = 300;

		// notification.defaults |= Notification.DEFAULT_SOUND;

		mNotificationManager.notify(NOTIFICATION_APP_RUNNING, mBuilder.build());

	}

	public void hide() {
		if (mIsShowing) mNotificationManager.cancel(null, NOTIFICATION_APP_RUNNING);
		mIsShowing = false;
	}

	public static void killAll(Context ctx){
	    NotificationManager notifManager  = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
	    notifManager.cancelAll();
	}

}
