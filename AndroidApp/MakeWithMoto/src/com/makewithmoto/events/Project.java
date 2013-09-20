package com.makewithmoto.events;

import java.io.File;

import com.makewithmoto.base.BaseMainApp;

public class Project {

	String name;
	String url;
	int type;
	
	
	public Project(String projectName, String projectURL, int type) {
		this.name = projectName;
		this.url = projectURL;
		this.type = type;
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
	
	
	public String getName() {
		return this.name;
	}

	public String getUrl() {
		return this.url;
	}

	public int getType() {
		return this.type;
	}
}
