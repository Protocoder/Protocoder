package com.makewithmoto.projectlist;

import android.content.Intent;
import android.os.Bundle;

import com.makewithmoto.R;
import com.makewithmoto.events.ProjectManager;


public class ListFragmentUserProjects extends ListFragmentBase {

	public ListFragmentUserProjects() {
		super();
		projectType = ProjectManager.PROJECT_USER_MADE;

	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		icon = Intent.ShortcutIconResource.fromContext(getActivity(), R.drawable.ic_script);

	}
}
