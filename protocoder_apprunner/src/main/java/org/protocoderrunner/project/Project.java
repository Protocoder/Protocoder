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

package org.protocoderrunner.project;

public class Project {

	//public final static String TYPE = "projectType";
	public final static String FOLDER = "projectFolder";
	public final static String NAME = "projectName";
	public final static String URL = "projectUrl";
	public final static String LOAD_FROM = "projectLoadFrom";
    public static final String FORMAT = "projectFormat";
    public static final String COLOR = "projectColor";
    public static final String PREFIX = "prefix";
    public static final String SETTINGS_SCREEN_ALWAYS_ON = "settings_screenOn";
    public static final String SETTINGS_SCREEN_WAKEUP = "settings_wakeUpScreen";

    public String name;
    public String folder;
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
