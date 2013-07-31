package com.makewithmoto.projectlist;

import com.makewithmoto.events.ProjectManager;


public class ListFragmentUserProjects extends ListFragmentBase {

	public ListFragmentUserProjects() {
		super();
		projectType = ProjectManager.PROJECT_USER_MADE;
	}
}
