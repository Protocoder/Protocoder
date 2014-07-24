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

package org.protocoderrunner.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.network.CustomWebsocketServer;

import android.content.Context;
import android.util.Log;

import java.net.UnknownHostException;

public class MLog {
	private static String TAG = "MLog";

	private static final int LOG_D = 0;
	private static final int LOG_E = 1;
	private static final int LOG_I = 2;
	private static final int LOG_W = 3;

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