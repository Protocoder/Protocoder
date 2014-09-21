/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

import java.io.File;

public class ProtoAppInstallerActivity extends BaseActivity {

    private static final String TAG = "ProtoAppInstallerActivity";
    Intent intent = null;
    Uri urlData;
    ProgressBar progress;
    private Button btnFinish;
    private LinearLayout ll;
    private String projectName;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_proto_installer);
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

        if (ProjectManager.getInstance().isProjectExisting(projectName)) {
            txtWarning.setVisibility(View.VISIBLE);
        }

        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                install();
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

    private void install() {
        progress.setVisibility(View.VISIBLE);

        if(urlData != null) {
            //check if project already exist

            // install project
            boolean ok = ProjectManager.getInstance().installProject(urlData.getPath());
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
