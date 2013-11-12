/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
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

package org.protocoder;

import android.os.Build;

public class AppSettings {

    // == APP SETTINGS ==========
    public static boolean debug = true;
    public static boolean fullscreen = false;
    public static boolean portrait = false;
    public static boolean stayAwake = false;
    public static boolean overrideHomeButtons = false;
    public static boolean overrideVolumeButtons = false;
    public static boolean hideHomeBar = false;
    public static boolean screenAlwaysOn = false;
    public static boolean closeWithBack = true;
    public static boolean standAlone = false;

    public static int CURRENT_VERSION = Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    public static String appFolder = "protocoder";
    public static String SERVER_ADDRESS = "";
    public static int websocketPort = 8587;
    public static long animSpeed = 500;
    public static int httpPort = 8585;

    public String id;

    private static AppSettings instance;

    public static AppSettings get() {
	if (instance == null)
	    instance = new AppSettings();
	return instance;
    }

}
