package com.makewithmoto.events;

public class Project {

	String name;
	String url;
	
	
	public Project(String projectName, String projectURL) {
		this.name = projectName;
		this.url = projectURL;
	}
	
	
	public String getName() {
		return this.name;
	}

	public String getUrl() {
		return this.url;
	}
}
