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

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.protocoder.MainActivity;
import org.protocoder.R;
import org.protocoder.appApi.Protocoder;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.project.ProjectManager.InstallListener;
import org.protocoderrunner.utils.MLog;
import org.protocoderrunner.utils.StrUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressLint("NewApi")
public class WelcomeActivity extends AppBaseActivity {

    private static final String TAG = "WelcomeActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        setToolbar();
        //setToolbarBack();

        // Set copyright
        TextView copyright = (TextView) findViewById(R.id.copyright);
        copyright.setText(readFile(R.raw.copyright_notice));

        // first time id
        Protocoder.getInstance(this).settings.setId(StrUtils.generateRandomString());
    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        MLog.d(TAG, "onResume");
    }

    /**
     * onPause
     */
    @Override
    protected void onPause() {
        super.onPause();
        // do something here
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onAcceptClick(View v) {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Installing examples");
        progress.setMessage("You can start creating with Protocoder in just a second");
        progress.show();
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

        // install examples
        ProjectManager.getInstance().install(this, ProjectManager.getInstance().FOLDER_EXAMPLES, new InstallListener() {

            @Override
            public void onReady() {
                progress.dismiss();
                // Write mContext shared pref to never come back here
                SharedPreferences userDetails = getSharedPreferences("org.protocoder", MODE_PRIVATE);
                userDetails.edit().putBoolean(getResources().getString(R.string.pref_is_first_launch), false).commit();
                // Start the activity
                Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

    }

    //TODO remove and use fileIO methods

    /**
     * Returns mContext string from mContext txt file resource
     *
     * @return
     */
    private String readFile(int resource) {
        InputStream inputStream = getResources().openRawResource(resource);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

}
