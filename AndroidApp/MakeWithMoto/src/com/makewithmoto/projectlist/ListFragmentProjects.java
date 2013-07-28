package com.makewithmoto.projectlist;

import com.makewithmoto.events.ProjectManager;


public class ListFragmentProjects extends ListFragmentBase {

	public ListFragmentProjects() {
		super();
		projectType = ProjectManager.PROJECT_USER_MADE;
	}
}
