package com.myscript;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.protocoderrunner.AppSettings;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;


/**
 * Created by victormanueldiazbarrales on 15/09/14.
 */
public class MainActivity extends AppRunnerActivity {

    private static final String PROJECT_FOLDER = "myscript";
    private static final String PROJECT_NAME = "myproject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //AppSettings.STANDALONE = true;

        // Load project from sdcard
        if (AppSettings.STANDALONE == false) {

            // Copy the project files from assets to sdcard
            installMyScript();

            // TODO: Wait until the script has been installed
            // Possible solution: Could we use the callback "onReady"?
            // Temporary solution: Use Thread.sleep(x)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        // Create a new project and inject it to the base class
        Project project = new Project(PROJECT_FOLDER, PROJECT_NAME);
        setProject(project);

        super.onCreate(savedInstanceState);
    }

    // Copy the project files from assets to sdcard
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
