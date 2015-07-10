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

package org.protocoderrunner.project;

import android.content.Context;

import org.protocoderrunner.AppRunnerSettings;
import org.protocoderrunner.base.utils.FileIO;
import org.protocoderrunner.base.utils.MLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AppRunnerProjectManager {

    protected final String TAG = AppRunnerProjectManager.class.getSimpleName();

    public static String MAIN_FILENAME = "main.js";
    private final Context mContext;
    private Project currentProject;

    public AppRunnerProjectManager(Context context, Project p) {
        mContext = context;
        currentProject = p;
    }

    public String getProjectPath() {
        return AppRunnerSettings.getFolderPath(currentProject.getFullFolder());
    }

    // Get code from sdcard
    public String getCode(Project p) {
        if (true) {
            String out = null;
            File f = new File(getProjectPath() + File.separator + MAIN_FILENAME);
            MLog.d(TAG, f.getAbsolutePath());

            try {
                InputStream in = new FileInputStream(f);
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int i;
                try {
                    i = in.read();
                    while (i != -1) {
                        buf.write(i);
                        i = in.read();
                    }
                    in.close();
                } catch (IOException ex) {
                }
                out = buf.toString();
            } catch (IOException e) {
                e.printStackTrace();
                // Log.e("Project", e.toString());
            }
            return out;

        // Get code from assets if standalone app
        } else {
            return FileIO.readAssetFile(mContext, currentProject.getFolder() + File.separator +
                    currentProject.getFolder() + File.separator + MAIN_FILENAME);
        }
    }


}