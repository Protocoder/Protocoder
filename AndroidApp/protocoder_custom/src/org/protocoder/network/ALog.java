/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
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

import org.protocoder.events.Events.LogEvent;

import android.util.Log;
import de.greenrobot.event.EventBus;

public class ALog {
    private static String TAG = "ALog";

    private enum LogState {
	DEBUG, INFO, ERROR, ALL
    }

    public static final LogState CURRENT_STATE = LogState.ALL;

    public static void i(final String tag, final String msg) {
	i(msg);
    }

    public static void i(final String msg) {
	switch (CURRENT_STATE) {
	case ALL:
	case ERROR:
	    final Throwable th = new Throwable();
	    final StackTraceElement[] elements = th.getStackTrace();

	    final String callerClassName = elements[1].getClassName();
	    final String callerMethodName = elements[1].getMethodName();

	    LogEvent evt = new LogEvent("info", msg);
	    EventBus.getDefault().post(evt);

	    Log.i(callerClassName, "[" + callerMethodName + "] " + msg);
	default:
	    break;
	}
    }

    public static void d(final String tag, final String msg) {
	d(msg);
    }

    public static void d(final String msg) {
	final Throwable th = new Throwable();
	final StackTraceElement[] elements = th.getStackTrace();

	final String callerClassName = elements[1].getClassName();
	final String callerMethodName = elements[1].getMethodName();

	LogEvent evt = new LogEvent("debug", msg);
	EventBus.getDefault().post(evt);
	// WebSocketService.getWebSocketServer().sendToConnections(msg);

	Log.i(callerClassName, "[" + callerMethodName + "] " + msg);
    }

    public static void e(final String tag, final String msg) {
	d(msg);
    }

    public static void e(final String msg) {
	final Throwable th = new Throwable();
	final StackTraceElement[] elements = th.getStackTrace();

	final String callerClassName = elements[1].getClassName();
	final String callerMethodName = elements[1].getMethodName();

	LogEvent evt = new LogEvent("debug", msg);
	EventBus.getDefault().post(evt);
	// WebSocketService.getWebSocketServer().sendToConnections(msg);

	Log.i(callerClassName, "[" + callerMethodName + "] " + msg);
    }
}