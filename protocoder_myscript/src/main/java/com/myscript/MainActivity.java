package com.myscript;

import android.app.Activity;
import android.content.Intent;

import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.project.Project;

/**
 * Created by victormanueldiazbarrales on 15/09/14.
 */
public class MainActivity extends Activity {

    public MainActivity() {
        Intent currentProjectApplicationIntent = new Intent(this, AppRunnerActivity.class);
        currentProjectApplicationIntent.putExtra(Project.NAME, "myproject");
        currentProjectApplicationIntent.putExtra(Project.FOLDER, "project");
        currentProjectApplicationIntent.putExtra(Project.LOAD_FROM, "assets");
        currentProjectApplicationIntent.putExtra(Project.FORMAT, "dir");
        //currentProjectApplicationIntent.putExtra(Project.FORMAT, "protoFile");


        startActivity(currentProjectApplicationIntent);
    }


}
