package org.protocoder.server;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.protocoder.ProtocoderAppHelper;
import org.protocoder.ProtocoderAppSettings;
import org.protocoderrunner.R;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.utils.MLog;

import java.io.IOException;

import de.greenrobot.event.EventBus;

public class ProtocoderServerService extends Service {

    private final String TAG = ProtocoderServerService.class.getSimpleName();

    private final int NOTIFICATION_SERVER_ID = 58592;
    private static final String SERVICE_CLOSE = "service_close";

    private NotificationManager mNotifManager;
    private PendingIntent mRestartPendingIntent;
    private Toast mToast;
    private ProtocoderHttpServer protocoderHttpServer;
    private ProtocoderFtpServer protocoderFtpServer;
    private ProtocoderWebsocketServer protocoderWebsockets;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    private void createNotification(String scriptFolder, String scriptName) {
        //create pending intent that will be triggered if the notification is clicked
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICE_CLOSE);
        registerReceiver(mReceiver, filter);

        Intent stopIntent = new Intent(SERVICE_CLOSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.protocoder_icon)
                .setContentTitle(scriptName).setContentText("Running service: " + scriptFolder + " > " + scriptName)
                .setOngoing(false)
                .addAction(R.drawable.ic_action_stop, "stop", pendingIntent)
                .setDeleteIntent(pendingIntent);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, AppRunnerActivity.class);

        // The stack builder object will contain an artificial back stack for
        // navigating backward from the Activity leads out your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AppRunnerActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_SERVER_ID, mBuilder.build());

        Thread.setDefaultUncaughtExceptionHandler(handler);
    }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SERVICE_CLOSE)) {
                ProtocoderServerService.this.stopSelf();
                mNotifManager.cancel(NOTIFICATION_SERVER_ID);
            }
        }
    };


    Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(ProtocoderServerService.this, "lalll", Toast.LENGTH_LONG);
                Looper.loop();
            }
        }.start();
        //          handlerToast.post(runnable);

        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mRestartPendingIntent);
        mNotifManager.cancelAll();

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);

        throw new RuntimeException(ex);
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        // TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MLog.d(TAG, "created");

        ProtocoderAppSettings protocoderAppSettings = ProtocoderAppSettings.get();

        try {
            protocoderHttpServer = new ProtocoderHttpServer(this, protocoderAppSettings.HTTP_PORT);
        } catch (IOException e) {
            MLog.e(TAG, "http server not initialized");
            e.printStackTrace();
        }
        //protocoderFtpServer = new ProtocoderFtpServer(this);
        //protocoderWebsockets = new ProtocoderWebsocketServer(this);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.d(TAG, "destroyed");

        unregisterReceiver(mReceiver);

        EventBus.getDefault().unregister(this);
    }


    /*
    * Events
    *
    * - Start app
    * - Stop service
    *
    */


    // TODO call intent and kill it in an appropiate way
    public void onEventMainThread(Events.ProjectEvent evt) {
        // Using transaction so the view blocks
        MLog.d(TAG, "event -> " + evt.getAction());

        String action = evt.getAction();
        if (action.equals(Events.PROJECT_RUN)) {
            Project p = evt.getProject();
            ProtocoderAppHelper.launchScript(getApplicationContext(), p.getFolder(), p.getName());
        } else if (action.equals(Events.PROJECT_SAVE)) {
            //Project p = evt.getProject();
            //mProtocoder.protoScripts.refresh(p.getFolder(), p.getName());
        } else if (action.equals(Events.PROJECT_NEW)) {
            //MLog.d(TAG, "creating new project " + evt.getProject().getName());
            //mProtocoder.protoScripts.createProject("projects", evt.getProject().getName());
        } else if (action.equals(Events.PROJECT_UPDATE)) {
            //mProtocoder.protoScripts.listRefresh();
        }
    }

    //stop service
    public void onEventMainThread(Events.SelectedProjectEvent evt) {
       // stopSelf();
    }


}