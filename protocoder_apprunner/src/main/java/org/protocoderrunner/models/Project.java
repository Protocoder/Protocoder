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

package org.protocoderrunner.models;

import org.protocoderrunner.apprunner.AppRunnerSettings;

import java.io.File;

public class Project {

    // we need this to serialize the data using intent bundles
    //public final static String TYPE = "projectType";
    public static final String FOLDER = "projectFolder";
    public static final String NAME = "projectName";
    public static final String URL = "projectUrl";
    public static final String PREFIX = "prefix";
    public static final String INTENTCODE = "code";
    public static final String POSTFIX = "postfix";

    public static final String SETTINGS_SCREEN_ALWAYS_ON = "settings_screenOn";
    public static final String SETTINGS_SCREEN_WAKEUP = "settings_wakeUpScreen";
    public static final String SETTINGS_SCREEN_ORIENTATION = "settings_screenOrientation";
    public static final String SERVER_PORT = "settings_protocoder_port";

    /*
     * this will get serialized
    */
    public String name;
    public String folder;

    public Project() {

    }

    public Project(String folder, String projectName) {
        this.folder = folder;
        this.name = projectName;
    }

    // TODO this is a hack, separate it to folder and name
    public Project(String script) {
        this.folder = "";
        this.name = script;
    }

    public String getName() {
        return this.name;
    }

    public String getFolder() {
        return this.folder;
    }

    public String getPathPrev() {
        return AppRunnerSettings.getBaseDir() + this.folder + File.separator;
    }

    public String getSandboxPath() {
        return this.folder + File.separator + this.name + File.separator;
    }

    public String getSandboxPathParent() {
        return this.folder + File.separator;
    }

    public String getSandboxPathForFile(String filename) {
        return getSandboxPath() + filename;
    }

    public String getParentPath() {
        return AppRunnerSettings.getBaseDir() + this.folder + File.separator;
    }

    public String getFullPath() {
        return AppRunnerSettings.getBaseDir() + getSandboxPath();
    }

    public String getFullPathForFile(String fileName) {
        return getFullPath() + fileName;
    }

    public boolean exists() {
        File f = new File(getFullPath());
        return f.exists();
    }
}
