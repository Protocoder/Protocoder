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

package org.protocoderrunner.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.network.CustomWebsocketServer;

import java.net.UnknownHostException;

public class MLog {
    private static String TAG = "MLog";

    private static final int LOG_D = 0;
    private static final int LOG_E = 1;
    private static final int LOG_I = 2;
    private static final int LOG_W = 3;
    private static final int LOG_V = 4;

    public static boolean network = true;
    public static boolean device = true;
    public static boolean verbose = false;

    public static void d(final String tag, final String msg) {
        generic(LOG_D, tag, msg);
    }

    public static void e(String tag, String msg) {
        generic(LOG_E, tag, msg);

    }

    public static void i(String tag, String msg) {
        generic(LOG_I, tag, msg);

    }

    public static void w(String tag, String msg) {
        generic(LOG_W, tag, msg);
    }

    public static void v(String tag, String msg) {
        generic(LOG_V, tag, msg);
    }

    public static void generic(int type, final String tag, final String msg) {
        String callerClassName = "";
        String callerMethodName = "";

        if (verbose) {
            final Throwable th = new Throwable();
            final StackTraceElement[] elements = th.getStackTrace();

            callerClassName = elements[1].getClassName();
            callerMethodName = elements[1].getMethodName();
        }

        if (device) {
            switch (type) {
                case LOG_D:
                    Log.d(tag, "[" + callerMethodName + "] " + msg);

                    break;

                case LOG_E:
                    Log.e(tag, "[" + callerMethodName + "] " + msg);

                    break;

                case LOG_I:
                    Log.i(tag, "[" + callerMethodName + "] " + msg);

                    break;

                case LOG_W:
                    Log.w(tag, "[" + callerMethodName + "] " + msg);

                    break;

                default:
                    break;
            }
        }

        if (network) {
            //LogEvent evt = new LogEvent("DEBUG", msg);
            //EventBus.getDefault().post(evt);


        }
    }

    public static void network(Context c, String TAG, String msg) {

        JSONObject jsonMsg = new JSONObject();
        try {
            jsonMsg.put("type", "console");
            jsonMsg.put("action", "log");
            JSONObject values = new JSONObject();
            values.put("val", TAG + " " + msg);
            jsonMsg.put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        CustomWebsocketServer ws = null;
        try {
            ws = CustomWebsocketServer.getInstance(c);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ws.send(jsonMsg);
    }

}