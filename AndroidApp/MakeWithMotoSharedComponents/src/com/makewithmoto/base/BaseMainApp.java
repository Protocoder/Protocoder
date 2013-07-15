package com.makewithmoto.base;

import java.io.File;

import com.makewithmoto.utils.FileIO;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;

public class BaseMainApp extends Application {

	public static SharedPreferences app_preferences;
	public static String baseDir;
	public static Application instance;

	public BaseMainApp() {
		baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + 
				AppSettings.appFolder + File.separator;
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// Copy all example apps to the base directory
		FileIO.copyAssetFolder(getAssets(), "ExampleApps", baseDir);
	}
}