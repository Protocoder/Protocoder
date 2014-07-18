/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
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

package org.protocoder.apprunner.api;

import org.protocoder.MainActivity;
import org.protocoder.PrefsFragment;
import org.protocoder.R;
import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.apprunner.AppRunnerSettings;
import org.protocoder.apprunner.PInterface;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.events.Project;
import org.protocoder.events.ProjectManager;
import org.protocoder.events.SchedulerManager;
import org.protocoder.utils.FileIO;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.NotificationCompat;

public class PApp extends PInterface {

	public interface onAppStatus {

		public void onStart();

		public void onPause();

		public void onResume();

		public void onStop();
	}

	public PApp(Activity a) {
		super(a);

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setDelayedAlarm(int delay, boolean alarmRepeat, boolean wakeUpScreen) {
		Project p = ProjectManager.getInstance().getCurrentProject();
		SchedulerManager.getInstance(a.get()).setAlarmDelayed(p, delay, alarmRepeat, wakeUpScreen);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setDelayedAlarm(int hour, int minute, int second, boolean wakeUpScreen) {
		Project p = ProjectManager.getInstance().getCurrentProject();
		SchedulerManager.getInstance(a.get()).setAlarm(p, hour, minute, second, wakeUpScreen);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setExactAlarm(int hour, int minute, int second, boolean wakeUpScreen) {
		Project p = ProjectManager.getInstance().getCurrentProject();
		SchedulerManager.getInstance(a.get()).setAlarm(p, hour, minute, second, wakeUpScreen);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void close() {
		a.get().finish();

	}

	@android.webkit.JavascriptInterface
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "code" })
	public void eval(String code) {
		a.get().interp.eval(code);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "code" })
	public void load(String filename) {
		String code = FileIO.loadFile(filename);

		a.get().interp.eval(code);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "id" })
	public void setId(String id) {
		PrefsFragment.setId(a.get(), id);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "id" })
	public void setNotification(int id, String title, String description) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(a.get())
				.setSmallIcon(R.drawable.protocoder_icon).setContentTitle(title).setContentText(description);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(a.get(), AppRunnerActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(a.get());
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) a.get().getSystemService(
				a.get().NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(id, mBuilder.build());
	}

	// TOFIX not working yet
	// @ProtocoderScript
	// @APIMethod(description = "", example = "")
	// @APIParam(params = { "id" })
	public void shareImage(String imagePath) {

		ContentValues values = new ContentValues();
		values.put(MediaColumns.MIME_TYPE, "image/png");
		values.put(MediaColumns.DATA, AppRunnerSettings.get().project.getStoragePath() + "/" + imagePath);
		Uri uri = a.get().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/png");

		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		a.get().startActivity(shareIntent);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "id" })
	public void shareText(String text) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/*");
		shareIntent.putExtra(Intent.EXTRA_TEXT, text);
		a.get().startActivity(shareIntent);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public String getProjectUrl() {
		String url = ProjectManager.getInstance().getCurrentProject().getServingURL();
		return url;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public String getProjectPath() {
		String url = ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/";
		return url;
	}

    // --------- doNotExecute ---------//
    public interface DoNothingCB {
        void event();
    }

	@ProtocoderScript
	@APIMethod(description = "this function doesnt do any thing", example = "")
	public void doNothing(DoNothingCB callbackfn) {

	}
}