package org.protocoderrunner.apprunner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

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
import org.protocoderrunner.events.Events;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

import de.greenrobot.event.EventBus;

//stopService 
//stopSelf 

public class AppRunnerService extends Service {

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
    private ImageView img;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Can be called twice
        interp = new AppRunnerInterpreter(this);
        interp.createInterpreter(false);

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
        //pUi.initForParentFragment(this);
        pUtil = new PUtil(this);

        interp.interpreter.addObjectToInterface("app", pApp);
        interp.interpreter.addObjectToInterface("boards", pBoards);
        interp.interpreter.addObjectToInterface("console", pConsole);
        interp.interpreter.addObjectToInterface("dashboard", pDashboard);
        interp.interpreter.addObjectToInterface("device", pDevice);
        interp.interpreter.addObjectToInterface("fileio", pFileIO);
        interp.interpreter.addObjectToInterface("media", pMedia);
        interp.interpreter.addObjectToInterface("network", pNetwork);
        interp.interpreter.addObjectToInterface("protocoder", pProtocoder);
        interp.interpreter.addObjectToInterface("sensors", pSensors);
        interp.interpreter.addObjectToInterface("ui", pUi);
        interp.interpreter.addObjectToInterface("util", pUtil);

        String projectName = intent.getStringExtra(Project.NAME);
        String projectFolder = intent.getStringExtra(Project.FOLDER);

        currentProject = ProjectManager.getInstance().get(projectFolder, projectName);
        ProjectManager.getInstance().setCurrentProject(currentProject);
        MLog.d(TAG, "launching " + projectName + " in " + projectFolder);

        AppRunnerSettings.get().project = currentProject;
        String script = ProjectManager.getInstance().getCode(currentProject);




        interp.interpreter.addObjectToInterface("device", pDevice);
        interp.interpreter.addObjectToInterface("util", pUtil);
        interp.evalFromService(script);

        //audio
        //AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        //int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        //this.setVolumeControlStream(AudioManager.STREAM_MUSIC);


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        img = new ImageView(this);
        img.setImageResource(R.drawable.protocoder_icon);
        img.setAlpha(0.5f);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                //WindowManager.LayoutParams.WRAP_CONTENT,
                //WindowManager.LayoutParams.WRAP_CONTENT,
                50,
                50,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(img, params);

        EventBus.getDefault().register(this);

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // its called only once
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        windowManager.removeView(img);
        EventBus.getDefault().unregister(this);
    }


    // execute lines
    public void onEventMainThread(Events.ExecuteCodeEvent evt) {
        String code = evt.getCode(); // .trim();
        MLog.d(TAG, "event -> q " + code);

        interp.evalFromService(code);
    }

}