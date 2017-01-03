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

import org.protocoder.appinterpreter.AppRunnerCustom;
import org.protocoder.gui.settings.NewUserPreferences;
import org.protocoder.gui.settings.ProtocoderSettings;
import org.protocoderrunner.api.network.PFtpServer;
import org.protocoderrunner.base.utils.MLog;

public class ProtocoderFtpServer extends PFtpServer {
    public static final String TAG = ProtocoderFtpServer.class.getSimpleName();

    private static ProtocoderFtpServer instance = null;
    private static boolean started = false;
    private Context c;

    public ProtocoderFtpServer(AppRunnerCustom appRunner, int port) {
        super(port, null);
        MLog.d(TAG, "" + port);

        c = appRunner.getAppContext();
        NewUserPreferences newUserPreferences = NewUserPreferences.getInstance();

        String user = (String) newUserPreferences.get("ftp_user");
        String password = (String) newUserPreferences.get("ftp_password");

        MLog.d(TAG, "" + user + " " + password);
        addUser(user, password, ProtocoderSettings.getBaseDir(), true);
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
