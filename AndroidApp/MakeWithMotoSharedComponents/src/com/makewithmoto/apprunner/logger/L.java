package com.makewithmoto.apprunner.logger;

import android.util.Log;

public class L {

	public static boolean enabled = true;
	private static OverlayLogger overlayLogger = null;
	private static String filter = null;

	//TODO clean this up 
	public static void d(String TAG, String text) {

		if (enabled) {
			if (filter == null) {
				Log.d(TAG, text);

			//Log.d(TAG, "" + overlayLogger);
				if (overlayLogger != null) {
					overlayLogger.addItem(text);
				}
			} else if (TAG.equals(filter)) {
				
				Log.d(TAG, text);

				//Log.d(TAG, "" + overlayLogger);
				if (overlayLogger != null) {
					overlayLogger.addItem(text);
				}
			}
			
			
		}
	}

	public static void filterByTag(String tag) {
		filter = tag;
	}

	public static void addLoggerWindow(OverlayLogger ol) {
		overlayLogger = ol;

	}

}
