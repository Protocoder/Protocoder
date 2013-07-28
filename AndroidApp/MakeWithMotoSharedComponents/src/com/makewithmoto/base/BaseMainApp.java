package com.makewithmoto.base;

import java.io.File;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;

import com.makewithmoto.utils.FileIO;

public class BaseMainApp extends Application {

	public static SharedPreferences app_preferences;
	public static String baseDir;
	public static String projectsDir;
	public static String examplesDir;
	public static Application instance;

	public BaseMainApp() {
		baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + 
				AppSettings.appFolder + File.separator;
		
		projectsDir = baseDir + File.separator + "projects";
		examplesDir = baseDir + File.separator + "examples";
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// Copy all example apps to the base directory
		FileIO.copyAssetFolder(getAssets(), "ExampleApps", baseDir);
	}
}