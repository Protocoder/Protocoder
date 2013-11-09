/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoder.base;

import org.protocoder.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

//TODO http://developer.android.com/training/notify-user/expanded.html 
public class BaseNotification {

    public static int NOTIFICATION_APP_RUNNING = 1;

    Context c;
    NotificationManager mNotificationManager;
    boolean mIsShowing = false;

    private Builder mBuilder;

    public BaseNotification(Context context) {
	c = context;

	mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public void show(Class<?> cls, int icon, String text, String title) {
	show(cls, icon, text, title, R.drawable.ic_launcher);
    }

    public void show(Class<?> cls, int icon, String text, String title, int actionIcon) {
	CharSequence tickerText = "MWM";
	mIsShowing = true;

	long when = System.currentTimeMillis();

	Intent notificationIntent = new Intent(c, cls);
	PendingIntent contentIntent = PendingIntent.getActivity(c, 0, notificationIntent, 0);

	Intent stopServerIntent = new Intent();
	stopServerIntent.setAction("com.makewithmoto.intent.action.STOP_SERVER");
	PendingIntent stopServerPendingIntent = PendingIntent.getBroadcast(c, 0, stopServerIntent,
		PendingIntent.FLAG_UPDATE_CURRENT);

	mBuilder = new NotificationCompat.Builder(c);
	mBuilder.setContentTitle(title).setContentText(text).setSmallIcon(icon).setOngoing(true)
		.setProgress(0, 0, true).setContentIntent(contentIntent)
		// .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
		.addAction(actionIcon, "Stop server", stopServerPendingIntent);

	// notification.defaults |= Notification.DEFAULT_LIGHTS;
	// notification.ledARGB = Color.RED;
	// notification.ledOffMS = 300;
	// notification.ledOnMS = 300;

	// notification.defaults |= Notification.DEFAULT_SOUND;

	mNotificationManager.notify(NOTIFICATION_APP_RUNNING, mBuilder.build());

    }

    public void hide() {
	if (mIsShowing)
	    mNotificationManager.cancel(null, NOTIFICATION_APP_RUNNING);
	mIsShowing = false;
    }

    public static void killAll(Context ctx) {
	NotificationManager notifManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
	notifManager.cancelAll();
    }

}
