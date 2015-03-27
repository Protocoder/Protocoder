/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.protocoder.R;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

import java.io.File;

public class ProtoAppInstallerActivity extends AppBaseActivity {

    private static final String TAG = "ProtoAppInstallerActivity";
    Intent intent = null;
    Uri urlData;
    ProgressBar progress;
    private Button btnFinish;
    private LinearLayout ll;
    private String projectName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_proto_installer);
        setToolbar();
        setToolbarBack();

        TextView txtProto = (TextView) findViewById(R.id.text_proto_install_info);
        TextView txtOrigin = (TextView) findViewById(R.id.text_proto_install_origin);
        TextView txtDestiny = (TextView) findViewById(R.id.text_proto_install_destiny);
        TextView txtWarning = (TextView) findViewById(R.id.text_proto_install_warning);

        ll = (LinearLayout) findViewById(R.id.proto_install_group);
        Button btnInstall = (Button) findViewById(R.id.button_proto_install_ok);
        Button btnCancel = (Button) findViewById(R.id.button_proto_install_cancel);
        btnFinish = (Button) findViewById(R.id.button_proto_install_finish);
        progress = (ProgressBar) findViewById(R.id.progressBar_installing);

        intent = getIntent();
        if (intent == null) {
            finish();
        }

        urlData = intent.getData();
        txtProto.setText("A project is going to be installed");
        txtOrigin.setText("from: " + urlData.getPath());
        txtDestiny.setText("to: " + urlData.getPath());

        //check if project is already installed
        File f = new File(urlData.getPath());
        projectName = f.getName().replaceFirst("[.][^.]+$", "");
        MLog.d(TAG, projectName);

        final String folder = ProjectManager.FOLDER_USER_PROJECTS;

        if (ProjectManager.getInstance().isProjectExisting(folder, projectName)) {
            txtWarning.setVisibility(View.VISIBLE);
        }

        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                install(folder);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //check if is autoinstall and proceed
        // Read in the script given in the intent.
        Intent intent = getIntent();
        if (null != intent) {
            boolean autoInstall = intent.getBooleanExtra("autoInstall", false);

            if (autoInstall) {
                btnInstall.performClick();
                btnFinish.performClick();
            }
        }


    }

    private void install(String folder) {
        progress.setVisibility(View.VISIBLE);

        if (urlData != null) {
            //check if project already exist

            // install project
            boolean ok = ProjectManager.getInstance().installProject(folder, urlData.getPath());
            MLog.d(TAG, "installed " + ok);
            progress.setVisibility(View.GONE);

            ll.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Proto app " + projectName + " installed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
