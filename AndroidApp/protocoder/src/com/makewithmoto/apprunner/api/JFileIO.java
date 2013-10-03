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
