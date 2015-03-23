package com.myscript;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;


/**
 * Created by victormanueldiazbarrales on 15/09/14.
 */
public class MainActivity extends AppRunnerActivity {

    private Project mProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Solution 1: Using inheritance
        createProjectInstance();
        this.setProject(mProject);
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

        // Solution 2: Using an Intent
        //super.onCreate(savedInstanceState);
        //installMyScript();
        //runMyScript();
    }

    private void createProjectInstance() {
        mProject = new Project();
        mProject.name = "myproject";
        mProject.folder = "myscript";
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

    private void runMyScript() {
        Intent currentProjectApplicationIntent = new Intent(this, AppRunnerActivity.class);
        currentProjectApplicationIntent.putExtra(Project.NAME, "myproject");
        currentProjectApplicationIntent.putExtra(Project.FOLDER, "myscript");
        currentProjectApplicationIntent.putExtra(Project.LOAD_FROM, "assets");
        currentProjectApplicationIntent.putExtra(Project.FORMAT, "dir");
        //currentProjectApplicationIntent.putExtra(Project.FORMAT, "protoFile");

        startActivity(currentProjectApplicationIntent);
    }
}
