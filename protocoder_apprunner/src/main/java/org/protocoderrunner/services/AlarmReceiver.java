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