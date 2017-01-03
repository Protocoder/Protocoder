/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.api;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.NotificationCompat;

import org.mozilla.javascript.NativeObject;
import org.protocoderrunner.AppRunnerActivity;
import org.protocoderrunner.R;
import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.other.PEvents;
import org.protocoderrunner.api.other.PLiveCodingFeedback;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apidoc.annotation.ProtoObject;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.utils.ExecuteCmd;
import org.protocoderrunner.base.utils.FileIO;
import org.protocoderrunner.base.utils.MLog;

import java.util.Map;

@ProtoObject
public class PApp extends ProtoBase {

    public final Notification notification;
    PEvents pevents;
    public String folder;
    public String name;

    public interface onAppStatus {
        public void onStart();
        public void onPause();
        public void onResume();
        public void onStop();
    }

    public PApp(AppRunner appRunner) {
        super(appRunner);
        notification = new Notification();
        pevents = new PEvents(appRunner);
    }

    //TODO reenable this
    //
    //@APIMethod(description = "get the script runner context", example = "")
    //public AppRunnerFragment getContext() {
    //	return (AppRunnerFragment) mContext;
    //}

    //TODO reenable this
    // @ProtoMethod(description = "", example = "")
    public void delayedAlarm(int delay, boolean alarmRepeat, boolean wakeUpScreen) {
        //Project p = ProjectManager.getInstance().getCurrentProject();
        //SchedulerManager.getInstance(getContext()).setAlarmDelayed(p, delay, alarmRepeat, wakeUpScreen);
    }

    //TODO reenable this
    // @ProtoMethod(description = "", example = "")
    public void delayedAlarm(int hour, int minute, int second, boolean wakeUpScreen) {
        //Project p = ProjectManager.getInstance().getCurrentProject();
        //SchedulerManager.getInstance(getContext()).setAlarm(p, hour, minute, second, wakeUpScreen);
    }

    //TODO reenable this
    // @ProtoMethod(description = "", example = "")
    public void exactAlarm(int hour, int minute, int second, boolean wakeUpScreen) {
        //Project p = ProjectManager.getInstance().getCurrentProject();
        //SchedulerManager.getInstance(getContext()).setAlarm(p, hour, minute, second, wakeUpScreen);
    }

    // @ProtoMethod(description = "", example = "")
    // @ProtoMethodParam(params = {"type", "data"})
    public void getSharedData(String type, String data) {

    }

    @ProtoMethod(description = "close the running script", example = "")
    public void close() {
        getActivity().finish();
    }

    @android.webkit.JavascriptInterface
    @ProtoMethod(description = "evaluate script", example = "")
    @ProtoMethodParam(params = {"code"})
    public void eval(final String code) {

        runOnUiThread(new CallbackRunUi() {
            @Override
            public void event() {
                getAppRunner().interp.eval(code);
            }
        });
    }

    @ProtoMethod(description = "loads and external file containing code", example = "")
    @ProtoMethodParam(params = {"fileName"})
    public void load(String filename) {
        String code = FileIO.loadCodeFromFile(getAppRunner().getProject().getFullPathForFile(filename));

        getAppRunner().interp.eval(code);
    }


    @ProtoMethod(description = "loads mContext library stored in the <i>libraries</i>' folder", example = "")
    @ProtoMethodParam(params = {"libraryName"})
    public void loadLibrary(String name) {
        String code = FileIO.loadFile("../../libraries/" + name + "/main.js");
        getAppRunner().interp.eval(code);
    }


    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "notification cancelled");
        }

    }

    class Notification {
        private NotificationManager mNotificationManager;

        Notification () {
            mNotificationManager = (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);
        }

        public Notification show(Map map) {

            Bitmap iconBmp = null;
            String iconName = (String) map.get("icon");
            if (iconName != null) iconBmp = BitmapFactory.decodeFile(getAppRunner().getProject().getFullPathForFile(iconName));

            Intent intent = new Intent(getContext(), MyBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);

            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(getContext(), AppRunnerActivity.class);
            // The stack builder object will contain an artificial back stack for navigating backward from the Activity leads out your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
            stackBuilder.addParentStack(AppRunnerActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            int id = ((Number) map.get("id")).intValue();
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle((CharSequence) map.get("title"))
                    .setContentText((CharSequence) map.get("description"))
                    .setLights(Color.parseColor((String) map.get("color")), 1000, 1000)
                    .setLargeIcon(iconBmp)
                    .setAutoCancel((Boolean) map.get("autocancel"))
                    .setTicker((String)map.get("ticker"))
                    .setSubText((CharSequence) map.get("subtext"))
                    .setDeleteIntent(pendingIntent)
                    .setContentIntent(resultPendingIntent);

            mNotificationManager.notify(id, mBuilder.build());

            return this;
        }

        public Notification cancel(int id) {
            mNotificationManager.cancel(id);

            return this;
        }

        public Notification cancelAll() {
            mNotificationManager.cancelAll();

            return this;
        }

        public Notification onClick(ReturnInterface callback) {

            return this;
        }
    }

    // TOFIX not working yet
    //
    // @APIMethod(description = "", example = "")
    // @APIParam(params = { "id" })
    public void shareImage(String imagePath) {
        ContentValues values = new ContentValues();
        values.put(MediaColumns.MIME_TYPE, "image/png");
        //values.put(MediaColumns.DATA, AppRunnerSettings.get().project.getStoragePath() + "/" + imagePath);
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        getContext().startActivity(shareIntent);
    }


    @ProtoMethod(description = "launch the share intent with the included text", example = "")
    @ProtoMethodParam(params = {"text"})
    public void shareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        getContext().startActivity(shareIntent);
    }

    @ProtoMethod(description = "get the current project path", example = "")
    public String path() {
        String url = getAppRunner().getProject().getSandboxPath();
        return url;
    }

    @ProtoMethod(description = "get the current project path", example = "")
    public String realpath() {
        String url = getAppRunner().getProject().getFullPath();
        return url;
    }

    // --------- doNotExecute ---------//
    public interface DoNothingCB {
        void event();
    }


    @ProtoMethod(description = "this dummy function doesn't execute the callback", example = "")
    @ProtoMethodParam(params = {"function()"})
    public void doNotExecute(DoNothingCB callbackfn) {

    }

    public interface CallbackRunUi {
        void event();
    }

    @ProtoMethod(description = "This runs on the UI thread", example = "")
    @ProtoMethodParam(params = {"function(code)"})
    public void runOnUiThread(final CallbackRunUi callback) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.event();
            }
        });
    }

    @ProtoMethod(description = "execute a shell command", example = "")
    @ProtoMethodParam(params = {"cmd", "function(data)"})
    public ExecuteCmd executeCommand(final String cmd, final ExecuteCmd.ExecuteCommandCB callbackfn) {

        return new ExecuteCmd(cmd, callbackfn);
    }

    @ProtoMethod(description = "shows mContext feedback overlay with the live-executed code", example = "")
    @ProtoMethodParam(params = {})
    public PLiveCodingFeedback liveCodingFeedback() {
        PLiveCodingFeedback l = getFragment().liveCodingFeedback();
        l.enable = true;

        return l;
    }

    @ProtoMethod(description = "sends a name event with a json object", example = "")
    @ProtoMethodParam(params = {"name", "jsonObject"})
    public void sendEvent(String name, Object obj) {
        pevents.sendEvent(name, (NativeObject) obj);
    }

    @ProtoMethod(description = "receives a named event with a json object", example = "")
    @ProtoMethodParam(params = {"name", "function(name, jsonObject)"})
    public String listenEvent(String name, PEvents.EventCB callback) {
        return pevents.add(name, callback);
    }

    @ProtoMethod(description = "receives a named event with a json object", example = "")
    @ProtoMethodParam(params = {"name", "function(name, jsonObject)"})
    public void removeEvent(String id) {
        pevents.remove(id);
    }

//
//
//    @APIMethod(description = "", example = "")
//    @APIParam(params = { "fileName" })
//    public void openWithApp(final String src) {
//        final String projectPath = ProjectManager.getInstance().getCurrentProject().getStoragePath();
//
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(projectPath + "/" + src));
//
//        mContext.startActivity(intent);
//    }

    public void onCreate(ReturnInterface callback) {
      //  callback.event(null);
    }

    public void onPause(ReturnInterface callback) {
      //  callback.event(null);
    }

    public void onResume(ReturnInterface callback) {
      //  callback.event(null);
    }

    public void onDestroy(ReturnInterface callback) {
      //  callback.event(null);
    }

    @Override
    public void __stop() {

    }
}