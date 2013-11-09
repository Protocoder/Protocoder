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

package org.protocoder.base;

import java.io.File;

import org.protocoder.utils.FileIO;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;

public class BaseMainApp extends Application {

    public static SharedPreferences app_preferences;
    public static String baseDir;
    public static String projectsDir;
    public static String examplesDir;
    public static Application instance;
    public static String typeExampleStr = "examples";
    public static String typeProjectStr = "projects";

    public BaseMainApp() {
	baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + AppSettings.appFolder
		+ File.separator;

	// baseDir = getFilesDir()+ File.separator +
	// AppSettings.appFolder + File.separator;

	projectsDir = baseDir + typeProjectStr;
	examplesDir = baseDir + typeExampleStr;

    }

    @Override
    public void onCreate() {
	super.onCreate();

	// Copy all example apps to the base directory
	FileIO.copyAssetFolder(getAssets(), "ExampleApps", baseDir);
    }
}