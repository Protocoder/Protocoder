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

import java.io.File;

import org.protocoderrunner.base.BaseMainApp;

public class Project {

	public final static String TYPE = "projectType";
	public final static String NAME = "projectName";
	public final static String URL = "projectUrl";

	public String name;
	public String storagePath;
	public int type;
	public boolean containsReadme;
	public boolean containsTutorial;

	public Project(String projectName, String storagePath, int type, boolean containsReadme, boolean containsTutorial) {
		this.name = projectName;
		this.storagePath = storagePath;
		this.type = type;

		this.containsReadme = containsReadme;
		this.containsTutorial = containsTutorial;
	}

	public Project(String projectName, int type) {
		this.name = projectName;

		if (type == ProjectManager.PROJECT_USER_MADE) {
			this.storagePath = BaseMainApp.projectsDir + File.separator + projectName;
		} else {
			this.storagePath = BaseMainApp.examplesDir + File.separator + projectName;
		}
		this.type = type;

	}

	public Project(String name, String projecURL, int projectType) {
		this(name, projecURL, projectType, false, false);
	}

	public String getName() {
		return this.name;
	}

	public String getStoragePath() {
		return this.storagePath;
	}

	public String getServingURL() {
		String url = "http://" + ProjectManager.getInstance().getRemoteIP();
		url += "apps/" + this.getTypeName() + "/" + this.getName() + "/";
		return url;
	}

	public int getType() {
		return this.type;
	}


    public String getTypeName() {
        String pname = "";
        if (this.type == ProjectManager.PROJECT_USER_MADE) {
            pname = BaseMainApp.TYPE_PROJECT_STRING;
        } else if (this.type == ProjectManager.PROJECT_EXAMPLE) {
            pname = BaseMainApp.TYPE_EXAMPLE_STRING;
        }
        return pname;
    }
}
