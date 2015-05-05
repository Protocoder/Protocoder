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

package org.protocoder.server;

import android.content.Context;

import org.protocoder.ProtocoderAppSettings;
import org.protocoder.appinterpreter.AppRunnerCustom;
import org.protocoder.UserSettings;
import org.protocoderrunner.apprunner.api.network.PFtpServer;
import org.protocoderrunner.utils.MLog;

import java.lang.ref.WeakReference;

public class ProtocoderFtpServer extends PFtpServer {
    public static final String TAG = "ProtocoderFtpServer";
    private final WeakReference<Context> ctx;

    private static ProtocoderFtpServer instance = null;
    private static boolean started = false;

    public ProtocoderFtpServer(AppRunnerCustom appRunner, int port) {
        super(port, null);
        MLog.d(TAG, "" + port);

        ctx = new WeakReference<Context>(appRunner.getAppContext());
        UserSettings userSettings = appRunner.protocoderApp.userSettings;

        MLog.d(TAG, "" + userSettings.getFtpUserName() + " " + userSettings.getFtpUserPassword());
        addUser(userSettings.getFtpUserName(), userSettings.getFtpUserPassword(), ProtocoderAppSettings.getBaseDir(), true);
    }

    public void stopServer() {
        instance = null;

        if (instance != null) {
            stop();
            started = false;
        }
    }

    public void startServer() {
        if (!started) {
            start();
            started = true;
        }
    }

    public boolean isStarted() {
        return started;
    }
}
