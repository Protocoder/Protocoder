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

package org.protocoder.appApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.AppSettings;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.FileIO;
import org.protocoderrunner.utils.MLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class EditorManager {

    private static final String TAG = "EditorManager";
    public static final String DEFAULT = "default";

    private static EditorManager instance;

    public static EditorManager getInstance() {
        if (instance == null) {
            instance = new EditorManager();
        }

        return instance;
    }

    public String[] listEditors() {
        String folderUrl = getBaseDir();
        //MLog.d(TAG, folderUrl);
        ArrayList<String> editors = new ArrayList<String>();
        editors.add("default");
        File dir = null;

        dir = new File(folderUrl);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File[] all_projects = dir.listFiles();
        Arrays.sort(all_projects);

        for (File file : all_projects) {
            if (file.getName().equals(AppSettings.CUSTOM_WEBEDITOR) == false) {
                //MLog.d(TAG, file.getName());
                editors.add(file.getName());
            }
        }

        return editors.toArray(new String[editors.size()]);
    }

    public String getCurrentEditor(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getString("pref_change_editor", "default");
    }

    public String getUrlEditor(Context c) {
        return getBaseDir() + File.separator + getCurrentEditor(c) + File.separator;
    }

    public String getBaseDir() {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + AppSettings.APP_FOLDER + File.separator + AppSettings.CUSTOM_WEBEDITOR + File.separator;

        return baseDir;
    }

}