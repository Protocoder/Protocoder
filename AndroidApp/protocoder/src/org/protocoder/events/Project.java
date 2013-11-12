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

package org.protocoder.events;

import java.io.File;

import org.protocoder.base.BaseMainApp;

public class Project {

    public final static String TYPE = "projectType";
    public final static String NAME = "projectName";
    public final static String URL = "projectUrl";
    
    
    public String name;
    public String url;
    public int type;
    public boolean containsReadme;
    public boolean containsTutorial;

    public Project(String projectName, String projectURL, int type, boolean containsReadme, boolean containsTutorial) {
	this.name = projectName;
	this.url = projectURL;
	this.type = type;

	this.containsReadme = containsReadme;
	this.containsTutorial = containsTutorial;
    }

    public Project(String projectName, int type) {
	this.name = projectName;

	if (type == ProjectManager.PROJECT_USER_MADE) {
	    this.url = BaseMainApp.projectsDir + File.separator + projectName;
	} else {
	    this.url = BaseMainApp.examplesDir + File.separator + projectName;
	}
	this.type = type;

    }

    public Project(String name, String projecURL, int projectType) {
	this(name, projecURL, projectType, false, false);
    }

    public String getName() {
	return this.name;
    }

    public String getFolder() {
	return this.url;
    }

    public int getType() {
	return this.type;
    }

    public String getTypeString() {
	String rtn;
	if (type == ProjectManager.PROJECT_USER_MADE) {
	    rtn = BaseMainApp.typeProjectStr;
	} else {
	    rtn = BaseMainApp.typeExampleStr;
	}
	return rtn;
    }

}
