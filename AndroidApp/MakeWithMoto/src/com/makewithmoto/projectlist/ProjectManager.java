package com.makewithmoto.projectlist;

import android.content.Context;

import com.makewithmoto.MainActivity;
import com.makewithmoto.events.Project;
import com.makewithmoto.utils.FileIO;

public class ProjectManager {

	private static ProjectManager instance;

	protected ProjectManager() {

	}

	public static ProjectManager getInstance() {
		if (instance == null)
			instance = new ProjectManager();
		return instance;

	}


	public void addProject(String newProjectName, String fileName) {

	}

	public Project addNewProject(Context c, String newProjectName, String fileName) {
		String newTemplateCode = FileIO.readAssetFile(c, "assets/new.js");
		if (newTemplateCode == null) newTemplateCode = "";
		String file = FileIO.writeStringToFile(newProjectName, newTemplateCode);

		Project newProject = new Project(newProjectName, file);
		ProjectsListFragment projectsListFragment = ((MainActivity) c).getProjectListFragment();
		projectsListFragment.addProject(newProjectName, newProject.getUrl()); 
		
		return newProject;

	}


}