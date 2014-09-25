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

package org.protocoderrunner;

import android.os.Build;

public class AppSettings {

	// == APP SETTINGS ==========
	public final static boolean DEBUG = true;
	public final static boolean FULLSCREEN = false;
	public final static boolean PORTRAIT = false;
	public final static boolean STAY_AWAKE = false;
	public final static boolean OVERRIDE_HOME_BUTTONS = false;
	public final static boolean OVERRIDE_VOLUME_BUTTONS = false;
	public final static boolean HIDE_HOME_BAR = false;
	public final static boolean SCREEN_ALWAYS_ON = false;
	public final static boolean CLOSE_WITH_BACK = true;
	public final static boolean STANDALONE = false;

	public static int MIN_SUPPORTED_VERSION = Build.VERSION_CODES.ICE_CREAM_SANDWICH;

	public final static String APP_FOLDER = "protocoder";
	public final static int WEBSOCKET_PORT = 8587;
	public final static int HTTP_PORT = 8585;

    public static String serverAddress = "";
    public static int animGeneralSpeed = 500;
    public String id;

	private static AppSettings instance;

	public static AppSettings get() {
		if (instance == null)
			instance = new AppSettings();
		return instance;
	}



}
