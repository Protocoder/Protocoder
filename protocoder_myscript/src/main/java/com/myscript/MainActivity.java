package com.myscript;

import android.content.Intent;
import android.os.Bundle;

import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;


/**
 * Created by victormanueldiazbarrales on 15/09/14.
 */
public class MainActivity extends AppRunnerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Solution 1: Using inheritance
        Project project = new Project("myscript", "myproject");
        project.load_from_assets = true;
        setProject(project);

        super.onCreate(savedInstanceState);

    }

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
