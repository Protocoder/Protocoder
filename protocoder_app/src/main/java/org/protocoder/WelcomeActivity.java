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

package org.protocoder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import org.protocoder.helpers.ProtoSettingsHelper;
import org.protocoder.gui.settings.ProtocoderSettings;
import org.protocoder.gui.settings.UserSettings;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.base.utils.StrUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class WelcomeActivity extends BaseActivity {

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 11;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_activity);
        setupActivity();

        // request permissions
        checkPermissions();

        // first time id
        UserSettings userSettings = new UserSettings(this);
        userSettings.setId(StrUtils.generateRandomString());
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void checkPermissions() {
        List<String> requiredPermissions = new ArrayList<String>();

        requiredPermissions.add(Manifest.permission.INTERNET);
        requiredPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        requiredPermissions.add(Manifest.permission.CHANGE_WIFI_STATE);
        requiredPermissions.add(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE);
        requiredPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        requiredPermissions.add(Manifest.permission.VIBRATE);
        requiredPermissions.add(Manifest.permission.WAKE_LOCK);
        requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requiredPermissions.add(Manifest.permission.READ_PHONE_STATE);
        requiredPermissions.add(Manifest.permission.BLUETOOTH);
        requiredPermissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        requiredPermissions.add(Manifest.permission.WRITE_SETTINGS);
        requiredPermissions.add(Manifest.permission.NFC);
        requiredPermissions.add(Manifest.permission.RECEIVE_SMS);
        requiredPermissions.add(Manifest.permission.INSTALL_SHORTCUT);
        requiredPermissions.add(Manifest.permission.CAMERA);

        // requiredPermissions.add(Manifest.permission.FLASHLIGHT);

        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        requiredPermissions.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
        requiredPermissions.add(Manifest.permission.RECORD_AUDIO);
        requiredPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);

        // check if permission is already granted
        for (int i = 0; i < requiredPermissions.size(); i++) {
            String permissionName = requiredPermissions.get(i);
            int isGranted1 = checkSelfPermission(permissionName);
            int isGranted2 = isGranted1 & PackageManager.PERMISSION_GRANTED;

            MLog.d(TAG, permissionName + " " + isGranted1 + " " + isGranted2);

            // if is already granted we remove it from the list
            // requiredPermissions.remove(i);
        }


        // request the permissions
        if (!requiredPermissions.isEmpty()) {
            requestPermissions(requiredPermissions.toArray(new String[requiredPermissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        MLog.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        MLog.d("Permissions", "Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void onAcceptClick(View v) {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Installing examples");
        progress.setMessage("You can start creating with Protocoder in just a second");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        //create folder structure
        new File(ProtocoderSettings.getFolderPath(ProtocoderSettings.USER_PROJECTS_FOLDER)).mkdirs();
        new File(ProtocoderSettings.getFolderPath(ProtocoderSettings.EXAMPLES_FOLDER)).mkdirs();
        new File(ProtocoderSettings.getBaseWebEditorsDir()).mkdirs();
        new File(ProtocoderSettings.getBaseLibrariesDir()).mkdirs();

        // install examples
        ProtoSettingsHelper.installExamples(getApplicationContext(), ProtocoderSettings.EXAMPLES_FOLDER, new ProtoSettingsHelper.InstallListener() {
            @Override
            public void onReady() {
                progress.dismiss();
                // Write a shared pref to never come back here
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
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
