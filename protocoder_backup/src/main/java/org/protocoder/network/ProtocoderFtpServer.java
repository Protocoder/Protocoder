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

package org.protocoder.network;

import android.content.Context;

import org.protocoder.appApi.Protocoder;
import org.protocoder.appApi.Settings;
import org.protocoderrunner.apprunner.api.network.PFtpServer;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

import java.lang.ref.WeakReference;

public class ProtocoderFtpServer extends PFtpServer {
    public static final String TAG = "ProtocoderFtpServer";
    private final WeakReference<Context> ctx;

    private static ProtocoderFtpServer instance = null;
    private static boolean started = false;


    public static ProtocoderFtpServer getInstance(Context aCtx, int port) {
        MLog.d(TAG, "launching ftp server... " + instance);
        if (instance == null) {
            instance = new ProtocoderFtpServer(aCtx, port);
        }

        return instance;
    }

    public ProtocoderFtpServer(Context c, int port) {
        super(port, null);
        MLog.d(TAG, "" + port);

        ctx = new WeakReference<Context>(c);
        Settings settings = Protocoder.getInstance(ctx.get()).settings;

        MLog.d(TAG, "" + settings.getFtpUserName() + " " + settings.getFtpUserPassword());
        addUser(settings.getFtpUserName(), settings.getFtpUserPassword(), ProjectManager.getInstance().getBaseDir(), true);
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
            MLog.d(TAG, "start 4");
            started = true;
        }
    }

    public boolean isStarted() {
        return started;
    }
}
