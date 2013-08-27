package com.makewithmoto.projectlist;

import android.content.Intent;
import android.os.Bundle;

import com.makewithmoto.R;
import com.makewithmoto.events.ProjectManager;

public class ListFragmentExamples extends ListFragmentUserProjects {

	public ListFragmentExamples() {
		super();
		projectType = ProjectManager.PROJECT_EXAMPLE;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		icon = Intent.ShortcutIconResource.fromContext(getActivity(), R.drawable.ic_script_example);
	}
}
