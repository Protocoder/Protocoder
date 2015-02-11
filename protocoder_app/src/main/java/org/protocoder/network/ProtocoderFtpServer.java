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

package org.protocoder.network;

import android.content.Context;

import org.protocoder.appApi.Protocoder;
import org.protocoder.appApi.Settings;
import org.protocoderrunner.apprunner.api.other.PFtpServer;
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
