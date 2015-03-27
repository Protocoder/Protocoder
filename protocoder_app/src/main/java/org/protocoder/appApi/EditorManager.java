/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoder.appApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.protocoderrunner.AppSettings;
import org.protocoderrunner.utils.FileIO;
import org.protocoderrunner.utils.MLog;

import java.io.File;
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


    public String getCustomJSInterpreterIfExist(Context c) {
        File file = new File(getUrlEditor(c) + "protocoder_js" + File.separator + "custom.js");

        MLog.d("TAG", "trying to load custom js interpreter from " + file.getAbsolutePath());
        String code = "";

        if (file.exists()) {
            code = FileIO.loadFile(file.getAbsolutePath());
            MLog.d("TAG", "loaded custom js interpreter in " + file.getAbsolutePath() + " " + code);
        } else {
            MLog.d("TAG", "cannot load custom js interpreter ");
        }

        return code;
    }


}