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
import org.protocoderrunner.apprunner.project.Project;
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
    private AppRunner mAppRunner;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mainLayout = initLayout();

        mAppRunner = new AppRunner(this);
        mAppRunner.hasUserInterface = false;

        mAppRunner.mProjectName = intent.getStringExtra(Project.NAME);
        mAppRunner.mProjectFolder = intent.getStringExtra(Project.FOLDER);
        //  mAppRunner.mIntentPrefixScript = intent.getString(Project.PREFIX, "");
        //  mAppRunner.mIntentCode = intent.getString(Project.CODE, "");
        //  mAppRunner.mIntentPostfixScript = intent.getString(Project.POSTFIX, "");
        mAppRunner.initInterpreter().loadProject().initProject();

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
        createNotification(notificationId, mAppRunner.mProjectFolder, mAppRunner.mProjectName);


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
                .setContentTitle(scriptName).setContentText("Running service: " + scriptFolder + " > " + scriptName)
                .setOngoing(false)
                .addAction(R.drawable.protocoder_icon, "stop", pendingIntent)
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
        mAppRunner.whatIsRunning.stopAll();
        interp = null;
        //EventBus.getDefault().unregister(this);
    }


    // execute lines
    public void onEventMainThread(AppRunnerEvents.ExecuteCodeEvent evt) {
        String code = evt.getCode(); // .trim();
        MLog.d(TAG, "event -> " + code);

        mAppRunner.evaluate(code, "");
    }

}