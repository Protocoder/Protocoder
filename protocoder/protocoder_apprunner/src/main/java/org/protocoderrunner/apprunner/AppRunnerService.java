package org.protocoderrunner.apprunner;

import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//stopService 
//stopSelf 

public class AppRunnerService extends Service {

	private AppRunnerInterpreter interp;
	private final String TAG = "AppRunnerService";
	private Project currentProject;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Can be called twice
		interp = new AppRunnerInterpreter(this);
		interp.createInterpreter(false);

		String projectName = intent.getStringExtra(Project.NAME);
		int projectType = intent.getIntExtra(Project.TYPE, -1);

		currentProject = ProjectManager.getInstance().get(projectName, projectType);
		ProjectManager.getInstance().setCurrentProject(currentProject);
		MLog.d(TAG, "launching " + projectName + " " + projectType);

		AppRunnerSettings.get().project = currentProject;
		String script = ProjectManager.getInstance().getCode(currentProject);

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
}