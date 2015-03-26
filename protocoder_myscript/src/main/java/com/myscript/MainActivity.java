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
 * Recreated by @josejuansanchez on 26/03/15 :)
 */
public class MainActivity extends AppRunnerActivity {

    private static final String PROJECT_FOLDER = "myscript";
    private static final String PROJECT_NAME = "myproject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Enable the standalone mode
        AppSettings.STANDALONE = true;

        // Create a new project and inject it to the base class
        Project project = new Project(PROJECT_FOLDER, PROJECT_NAME);
        setProject(project);

        super.onCreate(savedInstanceState);
    }
}
