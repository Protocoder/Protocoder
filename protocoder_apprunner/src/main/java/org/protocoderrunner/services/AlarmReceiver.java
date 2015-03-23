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

package org.protocoderrunner.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.protocoderrunner.apprunner.AppRunnerFragment;
import org.protocoderrunner.utils.MLog;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    private void startComponent(Context c, int message, Intent intent) {

        Intent newIntent = new Intent(c, AppRunnerFragment.class);
        newIntent.putExtra("alarm_message", message);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.putExtras(intent);
        c.startActivity(newIntent);

    }

    @Override
    public void onReceive(Context c, Intent intent) {
        MLog.d(TAG, "alarm started");

        try {
            Bundle bundle = intent.getExtras();
            int message = bundle.getInt("alarm_message");
            MLog.d(TAG, "alarma para: " + message);
            startComponent(c, message, intent);

        } catch (Exception e) {
            MLog.d(TAG, "error on the alarm");
            Toast.makeText(c, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();

        }
    }
}