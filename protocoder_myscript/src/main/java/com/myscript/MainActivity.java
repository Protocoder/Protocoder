package com.myscript;

import android.os.Bundle;

import org.protocoderrunner.apprunner.AppRunnerContext;
import org.protocoderrunner.AppSettings;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;


/**
 * Created by victormanueldiazbarrales on 15/09/14.
 * Recreated by @josejuansanchez on 26/03/15 :)
 */
public class MainActivity extends AppRunnerActivity {

    private static final String PROJECT_FOLDER = "myscript";
    private static final String PROJECT_NAME = "myproject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Get and store the application context
        AppRunnerContext.get().init(getApplicationContext());

        // Enable the standalone mode
        AppSettings.STANDALONE = true;

        // Create a new project and inject it to the base class
        Project project = new Project(PROJECT_FOLDER, PROJECT_NAME);
        setProject(project);

        // Copy the project files from assets to internal storage
        installMyScript();

        // TODO: Wait until the script has been installed
        // Possible solution: Could we use the callback "onReady"?
        // Temporary solution: Use Thread.sleep(x)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
    }

    // Copy the project files from assets to internal storage
    private void installMyScript() {
        ProjectManager.getInstance().install(this,
            ProjectManager.getInstance().FOLDER_MYSCRIPT,
            new ProjectManager.InstallListener() {
                @Override
                public void onReady() {
                }
            });
    }
}
