package com.makewithmoto.base;

import java.io.File;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;

public class MainApp extends Application {

	public static SharedPreferences app_preferences;
	public static String baseDir;

	public MainApp() {
		baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + 
				AppSettings.appFolder + File.separator;

	}
}