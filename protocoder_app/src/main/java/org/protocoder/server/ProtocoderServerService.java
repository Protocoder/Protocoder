package org.protocoder.server;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.protocoder.appinterpreter.AppRunnerCustom;
import org.protocoder.events.Events;
import org.protocoder.events.EventsProxy;
import org.protocoder.helpers.ProtoAppHelper;
import org.protocoder.gui.settings.ProtocoderSettings;
import org.protocoderrunner.api.PDevice;
import org.protocoderrunner.base.network.NetworkUtils;
import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.base.utils.MLog;

import java.net.UnknownHostException;
import java.util.HashMap;

public class ProtocoderServerService extends Service {

    private final String TAG = ProtocoderServerService.class.getSimpleName();

    private final int NOTIFICATION_ID = 58592;
    private static final String SERVICE_CLOSE = "service_close";

    private NotificationManager mNotifManager;
    private PendingIntent mRestartPendingIntent;
    private Toast mToast;
    private EventsProxy mEventsProxy;

    /*
     * Servers
     */
    private ProtocoderHttpServer protocoderHttpServer;
    private ProtocoderFtpServer protocoderFtpServer;
    private ProtocoderWebsocketServer protocoderWebsockets;

    private Gson gson = new Gson();
    private int counter = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MLog.d(TAG, "onStartCommand");

        if (intent != null) {
            AndroidUtils.debugIntent(TAG, intent);
            if (intent.getAction() == SERVICE_CLOSE) stopSelf();
        }

        return Service.START_STICKY;
    }

    BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "lalall");
            MLog.d(TAG, "received action: " + intent.getAction());
            if (intent.getAction().equals(SERVICE_CLOSE)) {
                //ProtocoderServerService.this.stopSelf();
                //mNotifManager.cancel(NOTIFICATION_SERVER_ID);
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
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MLog.d(TAG, "network service created");

        /*
         * Init the event proxy
         */
        mEventsProxy = new EventsProxy();

        EventBus.getDefault().register(this);

        Intent notificationIntent = new Intent(this, ProtocoderServerService.class).setAction(SERVICE_CLOSE);
        PendingIntent pendingIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(org.protocoderrunner.R.drawable.protocoder_icon)
                .setContentTitle("Protocoder").setContentText("Running service ")
                .setOngoing(false)
                .addAction(org.protocoderrunner.R.drawable.ic_action_stop, "stop", pendingIntent)
                //.setDeleteIntent(pendingIntent)
                .setContentInfo("1 Connection");

        Notification notification = builder.build();

        startForeground(NOTIFICATION_ID, notification);

        protocoderHttpServer = new ProtocoderHttpServer(this, ProtocoderSettings.HTTP_PORT);

        try {
            protocoderWebsockets = new ProtocoderWebsocketServer(this, ProtocoderSettings.WEBSOCKET_PORT);
            protocoderWebsockets.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //protocoderFtpServer = new ProtocoderFtpServer(this);

        registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        fileObserver.startWatching();

        // register log broadcast
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("org.protocoder.intent.CONSOLE");
        registerReceiver(logBroadcastReceiver, filterSend);

        // register a broadcast to receive the notification commands
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICE_CLOSE);
        registerReceiver(mNotificationReceiver, filter);

        final AppRunnerCustom appRunner = new AppRunnerCustom(this).initDefaultObjects();

        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {

                HashMap info = new HashMap();
                info.put("action", "device");

                info.put("battery level", appRunner.pDevice.battery());
                info.put("memory", appRunner.pDevice.memory().summary());
                info.put("brightness", appRunner.pDevice.brightness());
                info.put("screenOn", appRunner.pDevice.isScreenOn());

                PDevice.DeviceInfo deviceInfo = appRunner.pDevice.info();
                info.put("type", appRunner.pDevice.type());
                info.put("modelName", deviceInfo.model);
                info.put("orientation", appRunner.pDevice.orientation());
                info.put("manufacturer", deviceInfo.manufacturer);
                info.put("screenResolution", deviceInfo.screenWidth + " x " + deviceInfo.screenHeight);
                info.put("screen dpi", deviceInfo.screenDpi);

                info.put("network vailable", appRunner.pNetwork.isNetworkAvailable());
                info.put("wifi enabled", appRunner.pNetwork.isWifiEnabled());
                info.put("network type", appRunner.pNetwork.getNetworkType());
                info.put("ip", appRunner.pNetwork.ipAddress());
                info.put("rssi", appRunner.pNetwork.wifiInfo().getRssi());
                info.put("ssid", appRunner.pNetwork.wifiInfo().getSSID());

                String jsonObject = gson.toJson(info);

                protocoderWebsockets.send(jsonObject);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(r, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.d(TAG, "service destroyed");
        mEventsProxy.stop();
        protocoderHttpServer.stop();
        protocoderWebsockets.stop();

        // unregisterReceiver(mNotificationReceiver);

        unregisterReceiver(connectivityChangeReceiver);
        unregisterReceiver(mNotificationReceiver);
        unregisterReceiver(logBroadcastReceiver);
        fileObserver.stopWatching();

        EventBus.getDefault().unregister(this);
    }

    /*
     * Network Connectivity listener
     */
    BroadcastReceiver connectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AndroidUtils.debugIntent("connectivityChangerReceiver", intent);

            // check if there is mContext WIFI connection or we can connect via USB
            if (NetworkUtils.getLocalIpAddress(ProtocoderServerService.this).equals("-1")) {
                MLog.d(TAG, "No WIFI, still you can hack via USB using the adb command");
                EventBus.getDefault().post(new Events.Connection("none", ""));

            } else {
                MLog.d(TAG, "Hack via your browser @ http://" + NetworkUtils.getLocalIpAddress(ProtocoderServerService.this) + ":" + ProtocoderSettings.HTTP_PORT);
                String ip = NetworkUtils.getLocalIpAddress(ProtocoderServerService.this) + ":" + ProtocoderSettings.HTTP_PORT;
                EventBus.getDefault().post(new Events.Connection("wifi", ip));
            }
        }
    };

    /*
     * FileObserver to notify when projects are added or removed
     */
    FileObserver fileObserver = new FileObserver(ProtocoderSettings.getBaseDir(), FileObserver.CREATE| FileObserver.DELETE) {

        @Override
        public void onEvent(int event, String file) {
            if ((FileObserver.CREATE & event) != 0) {
                MLog.d(TAG, "File created [" + ProtocoderSettings.getBaseDir() + "/" + file + "]");
            } else if ((FileObserver.DELETE & event) != 0) {
                MLog.d(TAG, "File deleted [" + ProtocoderSettings.getBaseDir() + "/" + file + "]");
            }
        }
    };

    /*
     * Notification that show if the server is ON
     */
    private void createNotification() {
        /*
        //create pending intent that will be triggered if the notification is clicked
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICE_CLOSE);
        // registerReceiver(mNotificationReceiver, filter);

        Intent stopIntent = new Intent(SERVICE_CLOSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(org.protocoderrunner.R.drawable.protocoder_icon)
                .setContentTitle("Protocoder").setContentText("Running service ")
                .setOngoing(false)
                .addAction(org.protocoderrunner.R.drawable.ic_action_stop, "stop", pendingIntent)
                .setDeleteIntent(pendingIntent);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

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
        */
    }

    /**
     * Events
     *
     * - Start app
     * - Stop service
     *
     */

    /**
     * send logs to WEBIDE
     */
    BroadcastReceiver logBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, intent.getAction());

            HashMap hashMap = new HashMap();
            hashMap.put("action", "console");
            hashMap.put("log", intent.getStringExtra("log"));
            String jsonObject = gson.toJson(hashMap);

            protocoderWebsockets.send(jsonObject);
        }
    };

    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        MLog.d(TAG, "event -> " + e.getAction());

        String action = e.getAction();
        if (action.equals(Events.PROJECT_RUN)) {
            ProtoAppHelper.launchScript(getApplicationContext(), e.getProject());
        } else if (action.equals(Events.PROJECT_STOP_ALL)) {
            MLog.d(TAG, "stop_all 1");
            Intent i = new Intent("org.protocoderrunner.intent.CLOSE");
            sendBroadcast(i);
        } else if (action.equals(Events.PROJECT_SAVE)) {
            //Project p = evt.getProject();
            //mProtocoder.protoScripts.refresh(p.getPath(), p.getName());
        } else if (action.equals(Events.PROJECT_NEW)) {
            //MLog.d(TAG, "creating new project " + evt.getProject().getName());
            //mProtocoder.protoScripts.createProject("projects", evt.getProject().getName());
        } else if (action.equals(Events.PROJECT_UPDATE)) {
            //mProtocoder.protoScripts.listRefresh();
        } else if (action.equals(Events.PROJECT_EDIT)) {
            MLog.d(TAG, "edit " + e.getProject().getName());

            ProtoAppHelper.launchEditor(getApplicationContext(), e.getProject());
        }
    }

    //stop service
    @Subscribe
    public void onEventMainThread(Events.SelectedProjectEvent e) {
        // stopSelf();
    }

}