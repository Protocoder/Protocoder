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

public class Events {
    public static class ProjectEvent {
	private Project project;
	private String name;
	private String action;

	public ProjectEvent(Project aProject, String anAction) {
	    project = aProject;
	    action = anAction;
	}

	public ProjectEvent(Project aProject, String aName, String anAction) {
	    project = aProject;
	    action = anAction;
	    name = aName;
	}

	public String getAction() {
	    return action;
	}

	public void setAction(String newAction) {
	    action = newAction;
	}

	public String getFile() {
	    return project.getFolder();
	}

	public String getName() {
	    return (name == null) ? project.getName() : name;
	}

	public Project getProject() {
	    if (project == null) {
		project = ProjectManager.getInstance().get(getName(), ProjectManager.type);
	    }
	    return project;
	}
    }

    public static class ExecuteCodeEvent {
	private String code;

	public ExecuteCodeEvent(String code) {
	    this.code = code;
	}

	public String getCode() {
	    return code;
	}
    }

    public static class LogEvent {
	private String msg;
	private String tag;

	public LogEvent(final String aTag, final String aMsg) {
	    msg = aMsg;
	    tag = aTag;
	}

	public String getMessage() {
	    return msg;
	}

	public String getTag() {
	    return tag;
	}
    }
}