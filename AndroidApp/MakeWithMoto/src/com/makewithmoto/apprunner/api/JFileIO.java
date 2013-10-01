package com.makewithmoto.apprunner.api;

import java.io.File;

import android.app.Activity;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;
import com.makewithmoto.apprunner.AppRunnerSettings;
import com.makewithmoto.utils.FileIO;

public class JFileIO extends JInterface {

	String TAG = "JFileIO";

	public JFileIO(Activity a) {
		super(a);
	}

	@JavascriptInterface
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void createDir(String name) {

		File file = new File(AppRunnerSettings.get().project.getUrl() + File.separator + name);
		file.mkdirs();
	}

	@JavascriptInterface
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void remove(String name) {
		FileIO.deleteDir(name);
	}

	@JavascriptInterface
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void saveStrings(String fileName, String[] lines) {
		FileIO.saveStrings(fileName, lines);
	}
	
	
	@JavascriptInterface
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public String[] loadStrings(String fileName) {
		return FileIO.loadStrings(fileName);
	}
	

	
	
}
