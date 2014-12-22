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

package org.protocoderrunner.apprunner.api;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.NotificationCompat;

import org.mozilla.javascript.NativeObject;
import org.protocoderrunner.R;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.other.PEvents;
import org.protocoderrunner.apprunner.api.other.PLiveCodingFeedback;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.project.SchedulerManager;
import org.protocoderrunner.utils.ExecuteCmd;
import org.protocoderrunner.utils.FileIO;

public class PApp extends PInterface {

	public interface onAppStatus {

		public void onStart();

		public void onPause();

		public void onResume();

		public void onStop();
	}

    PEvents pevents;

	public PApp(Context a) {
		super(a);
        pevents = new PEvents(a);
	}


	@ProtocoderScript
	@APIMethod(description = "get the script runner context", example = "")
	public AppRunnerActivity getContext() {
		return (AppRunnerActivity) a.get();
	}

    //TODO
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setDelayedAlarm(int delay, boolean alarmRepeat, boolean wakeUpScreen) {
		Project p = ProjectManager.getInstance().getCurrentProject();
		SchedulerManager.getInstance(a.get()).setAlarmDelayed(p, delay, alarmRepeat, wakeUpScreen);
	}

    //TODO
    @ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setDelayedAlarm(int hour, int minute, int second, boolean wakeUpScreen) {
		Project p = ProjectManager.getInstance().getCurrentProject();
		SchedulerManager.getInstance(a.get()).setAlarm(p, hour, minute, second, wakeUpScreen);
	}

    //TODO
    @ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setExactAlarm(int hour, int minute, int second, boolean wakeUpScreen) {
		Project p = ProjectManager.getInstance().getCurrentProject();
		SchedulerManager.getInstance(a.get()).setAlarm(p, hour, minute, second, wakeUpScreen);
	}

    @ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { "type", "data" })
    public void getSharedData(String type, String data) {

    }

	@ProtocoderScript
	@APIMethod(description = "close the running script", example = "")
	public void close() {
        ((AppRunnerActivity) a.get()).finish();
	}

	@android.webkit.JavascriptInterface
	@ProtocoderScript
	@APIMethod(description = "evaluate a script", example = "")
	@APIParam(params = { "code" })
	public void eval(String code) {
        ((AppRunnerActivity) a.get()).interp.eval(code);
	}

	@ProtocoderScript
	@APIMethod(description = "loads and external file containing code", example = "")
	@APIParam(params = { "fileName" })
	public void load(String filename) {
		String code = FileIO.loadFile(filename);
        ((AppRunnerActivity) a.get()).interp.eval(code);
	}

    @ProtocoderScript
	@APIMethod(description = "loads a library stored in the <i>libraries</i>' folder", example = "")
	@APIParam(params = { "libraryName" })
	public void loadLibrary(String name) {
		String code = FileIO.loadFile("../../libraries/" + name + "/main.js");
        ((AppRunnerActivity) a.get()).interp.eval(code);
	}

    //TODO way to cancel notification and come back to the script
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "id", "title", "description" })
	public void setNotification(int id, String title, String description) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(a.get())
				.setSmallIcon(R.drawable.app_icon).setContentTitle(title).setContentText(description);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(a.get(), AppRunnerActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(a.get());
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(AppRunnerActivity.class);
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
		//values.put(MediaColumns.DATA, AppRunnerSettings.get().project.getStoragePath() + "/" + imagePath);
		Uri uri = a.get().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/png");

		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		a.get().startActivity(shareIntent);
	}

	@ProtocoderScript
	@APIMethod(description = "launch the share intent with the included text", example = "")
	@APIParam(params = { "text" })
	public void shareText(String text) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/*");
		shareIntent.putExtra(Intent.EXTRA_TEXT, text);
		a.get().startActivity(shareIntent);
	}

	@ProtocoderScript
	@APIMethod(description = "get the current project HTTP URL", example = "")
	public String getProjectUrl() {
		String url = ProjectManager.getInstance().getCurrentProject().getServingURL();
		return url;
	}

	@ProtocoderScript
	@APIMethod(description = "get the current project path", example = "")
	public String getProjectPath() {
		String url = ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/";
		return url;
	}

    // --------- doNotExecute ---------//
    public interface DoNothingCB {
        void event();
    }

	@ProtocoderScript
	@APIMethod(description = "this dummy function doesn't execute the callback", example = "")
    @APIParam(params = { "function()" })
    public void doNotExecute(DoNothingCB callbackfn) {

	}


	@ProtocoderScript
	@APIMethod(description = "execute a shell command", example = "")
    @APIParam(params = { "cmd", "function(data)" })
    public ExecuteCmd executeCommand(final String cmd, final ExecuteCmd.ExecuteCommandCB callbackfn) {

        return new ExecuteCmd(cmd, callbackfn);
	}


    @ProtocoderScript
    @APIMethod(description = "shows a feedback overlay with the live-executed code", example = "")
    @APIParam(params = { })
    public PLiveCodingFeedback liveCodingFeedback() {
        appRunnerActivity.get().initLayout();

        PLiveCodingFeedback l = appRunnerActivity.get().liveCoding;
        l.enable = true;

        return l;
    }

    @ProtocoderScript
    @APIMethod(description = "sends a name event with a json object", example = "")
    @APIParam(params = { "name", "jsonObject"})
    public void sendEvent(String name, Object obj) {
        pevents.sendEvent(name, (NativeObject) obj);
    }

    @ProtocoderScript
    @APIMethod(description = "receives a named event with a json object", example = "")
    @APIParam(params = { "name", "function(name, jsonObject)"})
    public String listenEvent(String name, PEvents.EventCB callback) {
        return pevents.add(name, callback);
    }

    @ProtocoderScript
    @APIMethod(description = "receives a named event with a json object", example = "")
    @APIParam(params = { "name", "function(name, jsonObject)"})
    public void removeEvent(String id) {
        pevents.remove(id);
    }

//
//    @ProtocoderScript
//    @APIMethod(description = "", example = "")
//    @APIParam(params = { "fileName" })
//    public void openWithApp(final String src) {
//        final String projectPath = ProjectManager.getInstance().getCurrentProject().getStoragePath();
//
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(projectPath + "/" + src));
//
//        a.get().startActivity(intent);
//    }

    @ProtocoderScript
    @APIMethod(description = "opens a file with a given app provided as package name ", example = "")
    @APIParam(params = { "fileName", "packageName" })
    public void openWithApp(final String src, String packageName) {
        final String projectPath = ProjectManager.getInstance().getCurrentProject().getStoragePath();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + projectPath + "/" + src), packageName);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        a.get().startActivity(intent);
    }

}