package org.protocoderrunner.apprunner;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.protocoderrunner.R;
import org.protocoderrunner.apprunner.api.PApp;
import org.protocoderrunner.apprunner.api.PBoards;
import org.protocoderrunner.apprunner.api.PConsole;
import org.protocoderrunner.apprunner.api.PDashboard;
import org.protocoderrunner.apprunner.api.PDevice;
import org.protocoderrunner.apprunner.api.PFileIO;
import org.protocoderrunner.apprunner.api.PMedia;
import org.protocoderrunner.apprunner.api.PNetwork;
import org.protocoderrunner.apprunner.api.PProtocoder;
import org.protocoderrunner.apprunner.api.PSensors;
import org.protocoderrunner.apprunner.api.PUI;
import org.protocoderrunner.apprunner.api.PUtil;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

//stopService 
//stopSelf 

public class AppRunnerService extends Service {

    private static final String SERVICE_CLOSE = "service_close";
    private AppRunnerInterpreter interp;
    private final String TAG = "AppRunnerService";
    private Project currentProject;

    public PApp pApp;
    public PBoards pBoards;
    public PConsole pConsole;
    public PDashboard pDashboard;
    public PDevice pDevice;
    public PFileIO pFileIO;
    public PMedia pMedia;
    public PNetwork pNetwork;
    public PProtocoder pProtocoder;
    public PSensors pSensors;
    public PUI pUi;
    public PUtil pUtil;

    private WindowManager windowManager;
    private RelativeLayout parentScriptedLayout;
    private RelativeLayout mainLayout;
    private BroadcastReceiver mReceiver;
    private NotificationManager mNotifManager;
    private PendingIntent mRestartPendingIntent;
    private Toast mToast;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Can be called twice
        interp = new AppRunnerInterpreter();
        pApp = new PApp(this);
        //pApp.initForParentFragment(this);
        pBoards = new PBoards(this);
        pConsole = new PConsole(this);
        pDashboard = new PDashboard(this);
        pDevice = new PDevice(this);
        //pDevice.initForParentFragment(this);
        pFileIO = new PFileIO(this);
        pMedia = new PMedia(this);
        //pMedia.initForParentFragment(this);
        pNetwork = new PNetwork(this);
        //pNetwork.initForParentFragment(this);
        pProtocoder = new PProtocoder(this);
        pSensors = new PSensors(this);
        //pSensors.initForParentFragment(this);
        pUi = new PUI(this);
        pUi.initForParentService(this);
        //pUi.initForParentFragment(this);
        pUtil = new PUtil(this);

        interp.addJavaObjectToJs("app", pApp);
        interp.addJavaObjectToJs("boards", pBoards);
        interp.addJavaObjectToJs("console", pConsole);
        interp.addJavaObjectToJs("dashboard", pDashboard);
        interp.addJavaObjectToJs("device", pDevice);
        interp.addJavaObjectToJs("fileio", pFileIO);
        interp.addJavaObjectToJs("media", pMedia);
        interp.addJavaObjectToJs("network", pNetwork);
        interp.addJavaObjectToJs("protocoder", pProtocoder);
        interp.addJavaObjectToJs("sensors", pSensors);
        interp.addJavaObjectToJs("ui", pUi);
        interp.addJavaObjectToJs("util", pUtil);

        mainLayout = initLayout();

        String projectName = intent.getStringExtra(Project.NAME);
        String projectFolder = intent.getStringExtra(Project.FOLDER);

        currentProject = ProjectManager.getInstance().get(projectFolder, projectName);
        ProjectManager.getInstance().setCurrentProject(currentProject);
        MLog.d(TAG, "launching " + projectName + " in " + projectFolder);

        AppRunnerSettings.get().project = currentProject;
        String script = ProjectManager.getInstance().getCode(currentProject);

        interp.eval(script, currentProject.getName());

        //audio
        //AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        //int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        //this.setVolumeControlStream(AudioManager.STREAM_MUSIC);


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        boolean isTouchable = true;
        int touchParam;
        if (isTouchable) {
            touchParam = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            touchParam = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                touchParam,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

        windowManager.addView(mainLayout, params);

        //EventBus.getDefault().register(this);

        mNotifManager = (NotificationManager) AppRunnerService.this.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = (int) Math.ceil(100000 * Math.random());
        createNotification(notificationId, projectFolder, projectName);


        //just in case it crash
        Intent restartIntent = new Intent("org.protocoder.LauncherActivity"); //getApplicationContext(), AppRunnerActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        restartIntent.putExtra("wasCrash", true);

       // intent.setPackage("org.protocoder");
        //intent.setClassName("org.protocoder", "MainActivity");
        mRestartPendingIntent = PendingIntent.getActivity(AppRunnerService.this, 0, restartIntent, 0);
        mToast = Toast.makeText(AppRunnerService.this, "Crash :(", Toast.LENGTH_LONG);

        return Service.START_NOT_STICKY;
    }

    private void createNotification(final int notificationId, String scriptFolder, String scriptName) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICE_CLOSE);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SERVICE_CLOSE)) {
                    AppRunnerService.this.stopSelf();
                    mNotifManager.cancel(notificationId);

                }
            }
        };

        registerReceiver(mReceiver, filter);

        //RemoteViews remoteViews = new RemoteViews(getPackageName(),
        //        R.layout.widget);

        Intent stopIntent = new Intent(SERVICE_CLOSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.protocoder_icon)
                .setTicker("Running " + scriptName)
                .setContentTitle("Protocoder Service")
                .setContentText("Running service: " + scriptFolder + " > " + scriptName)
                .setOngoing(false)
                .addAction(R.drawable.ic_action_stop, "Stop service", pendingIntent)
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
        mNotificationManager.notify(notificationId, mBuilder.build());

        Thread.setDefaultUncaughtExceptionHandler(handler);

    }


    Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

            new Thread() {

                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(AppRunnerService.this, "lalll", Toast.LENGTH_LONG);
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



    public void addScriptedLayout(RelativeLayout scriptedUILayout) {
        parentScriptedLayout.addView(scriptedUILayout);
    }

    public RelativeLayout initLayout() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // set the parent
        parentScriptedLayout = new RelativeLayout(this);
        parentScriptedLayout.setLayoutParams(layoutParams);
        parentScriptedLayout.setGravity(Gravity.BOTTOM);
        parentScriptedLayout.setBackgroundColor(getResources().getColor(R.color.transparent));

        return parentScriptedLayout;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MLog.d(TAG, "onCreate");
       // interp.callJsFunction("onCreate");

        // its called only once
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.d(TAG, "onDestroy");
        interp.callJsFunction("onDestroy");

        windowManager.removeView(mainLayout);
        unregisterReceiver(mReceiver);
        WhatIsRunning.getInstance().stopAll();
        interp = null;
        //EventBus.getDefault().unregister(this);
    }


    // execute lines
    public void onEventMainThread(Events.ExecuteCodeEvent evt) {
        String code = evt.getCode(); // .trim();
        MLog.d(TAG, "event -> q " + code);

        interp.eval(code);
    }

}