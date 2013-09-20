package com.makewithmoto.network;

import android.util.Log;

import com.makewithmoto.events.Events.LogEvent;

import de.greenrobot.event.EventBus;

public class ALog {
	private static String TAG = "ALog";
	private enum LogState{
		DEBUG, INFO, ERROR, ALL
	}
	
	public static final LogState CURRENT_STATE = LogState.ALL;
	public static void i(final String tag, final String msg) {i(msg);}
	public static void i(final String msg) {
		switch(CURRENT_STATE){
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
	public static void d(final String tag, final String msg) { d(msg); }
	public static void d(final String msg) {
		final Throwable th = new Throwable();
		final StackTraceElement[] elements = th.getStackTrace();
		
        final String callerClassName = elements[1].getClassName();
        final String callerMethodName = elements[1].getMethodName();
        
		LogEvent evt = new LogEvent("debug", msg);
		EventBus.getDefault().post(evt);
//		WebSocketService.getWebSocketServer().sendToConnections(msg);
		
        Log.i(callerClassName, "[" + callerMethodName + "] " + msg);
	}
	
	public static void e(final String tag, final String msg) { d(msg); }
	public static void e(final String msg) {
		final Throwable th = new Throwable();
		final StackTraceElement[] elements = th.getStackTrace();
		
        final String callerClassName = elements[1].getClassName();
        final String callerMethodName = elements[1].getMethodName();
        
		LogEvent evt = new LogEvent("debug", msg);
		EventBus.getDefault().post(evt);
//		WebSocketService.getWebSocketServer().sendToConnections(msg);
		
        Log.i(callerClassName, "[" + callerMethodName + "] " + msg);
	}
}