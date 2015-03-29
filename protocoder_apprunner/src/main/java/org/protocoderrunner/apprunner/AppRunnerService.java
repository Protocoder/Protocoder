package org.protocoderrunner.apprunner;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.protocoderrunner.apprunner.api.PDevice;
import org.protocoderrunner.apprunner.api.PUtil;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

//stopService 
//stopSelf 

public class AppRunnerService extends Service {

    private AppRunnerInterpreter interp;
    private final String TAG = "AppRunnerService";
    private Project currentProject;
    private PDevice pDevice;
    private PUtil pUtil;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Can be called twice
        interp = new AppRunnerInterpreter(this);
        interp.createInterpreter(false);

        String projectName = intent.getStringExtra(Project.NAME);
        String projectFolder = intent.getStringExtra(Project.FOLDER);

        currentProject = ProjectManager.getInstance().get(projectFolder, projectName);
        ProjectManager.getInstance().setCurrentProject(currentProject);
        MLog.d(TAG, "launching " + projectName + " in " + projectFolder);

        AppRunnerSettings.get().project = currentProject;
        String script = ProjectManager.getInstance().getCode(currentProject);

        pDevice = new PDevice(this);
        pUtil = new PUtil(this);

        interp.interpreter.addObjectToInterface("device", pDevice);
        interp.interpreter.addObjectToInterface("util", pUtil);
        interp.evalFromService(script);

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
    }
}