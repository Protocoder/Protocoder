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
import android.content.res.AssetManager;
import android.net.Uri;

public class Project {

    //public final static String TYPE = "projectType";
    public final static String FOLDER = "projectFolder";
    public final static String NAME = "projectName";
    public final static String URL = "projectUrl";
    public final static String LOAD_FROM = "projectLoadFrom";
    public static final String FORMAT = "projectFormat";
    public static final String COLOR = "projectColor";
    public static final String PREFIX = "prefix";
    public static final String CODE = "code";
    public static final String POSTFIX = "postfix";
    public static final String SETTINGS_SCREEN_ALWAYS_ON = "settings_screenOn";
    public static final String SETTINGS_SCREEN_WAKEUP = "settings_wakeUpScreen";

    public String name;
    public String folder;
    public String code;
    public String prefix;
    public String postfix;
	public boolean containsReadme = false;
	public boolean containsTutorial = false;
    public boolean selected = false;

    public Project(String folder, String projectName, boolean containsReadme, boolean containsTutorial) {
        this.folder = folder;
        this.name = projectName;
        this.containsReadme = containsReadme;
        this.containsTutorial = containsTutorial;
    }

    public Project(String folder, String projectName) {
        this.folder = folder;
        this.name = projectName;
    }

    public Project() {

    }

    public String getName() {
        return this.name;
    }

    public String getStoragePath() {
        return ProjectManager.getInstance().getProjectURL(this);
    }

    public String getServingURL() {
        String url = "http://" + ProjectManager.getInstance().getRemoteIP();
        url += "apps/" + this.getFolder() + "/" + this.getName() + "/";
        return url;
    }

    public String getFolder() {
        return this.folder;
    }

    //public String getTypeName() {
    //    return folder;
    //}

}
