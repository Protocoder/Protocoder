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

package org.protocoderrunner;

import android.os.Build;
import android.os.Environment;

import java.io.File;

public class AppRunnerSettings {

    public final static String PROTOCODER_FOLDER = "protocodersandbox";
    public static final String USER_PROJECTS_FOLDER = "user_projects";
    public static final String EXAMPLES_FOLDER = "examples";
    public static final String LIBRARIES_FOLDER = "libraries";

    public static int MIN_SUPPORTED_VERSION = Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static int animGeneralSpeed = 500;
    public String id;

    public static String getBaseDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + PROTOCODER_FOLDER + File.separator;
    }

    public static String getFolderPath(String folder) {
        return getBaseDir() + folder + File.separator;
    }

    public static String getBaseLibrariesDir() {
        return getBaseDir() + LIBRARIES_FOLDER + File.separator;
    }

}
