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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import org.protocoder.MainActivity;
import org.protocoder.R;
import org.protocoder.appApi.Protocoder;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.utils.MLog;
import org.protocoderrunner.utils.StrUtils;

public class LauncherActivity extends BaseActivity {

    Intent intent = null;
    private String TAG = "LauncherActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prepare intent to exit the activity and move to the main one
        boolean firstLaunch; // If this is the first time the
        SharedPreferences userDetails = getSharedPreferences("org.protocoder", MODE_PRIVATE);
        firstLaunch = userDetails.getBoolean(getResources().getString(R.string.pref_is_first_launch), true);

        //uncomment to avoid first launch
        //userDetails.edit().putBoolean(getResources().getString(R.string.pref_is_first_launch), false).commit();

        Intent i = getIntent();
        boolean wasCrash = i.getBooleanExtra("wasCrash", false);
        if (wasCrash) {
            MLog.d(TAG, "lalall");
            Toast.makeText(this, "The script crashed :(", Toast.LENGTH_LONG).show();
        }

        if (firstLaunch) {
            intent = new Intent(this, WelcomeActivity.class);
            Protocoder.getInstance(this).settings.setId(StrUtils.generateRandomString());
            Protocoder.getInstance(this).settings.setConnectionAlert(true);
        } else {
            intent = new Intent(this, MainActivity.class);
           // intent.putExtras();
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();

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
