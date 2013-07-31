package com.makewithmoto.events;

public class Project {

	String name;
	String url;
	int type;
	
	
	public Project(String projectName, String projectURL, int type) {
		this.name = projectName;
		this.url = projectURL;
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
