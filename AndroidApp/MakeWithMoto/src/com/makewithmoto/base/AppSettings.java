package com.makewithmoto.base;

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
	
	public static int CURRENT_VERSION = Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	
	public static String appFolder = "protocoder";
	public static String SERVER_ADDRESS = "";
	public static int websocketPort = 8587;
	public static final int httpPort = 8585;	
	
}
