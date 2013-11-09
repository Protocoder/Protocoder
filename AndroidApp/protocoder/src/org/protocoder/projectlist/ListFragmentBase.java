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

package org.protocoder.projectlist;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.protocoder.MainActivity;
import org.protocoder.R;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.base.BaseFragment;
import org.protocoder.beam.BeamActivity;
import org.protocoder.events.Events.ProjectEvent;
import org.protocoder.events.Project;
import org.protocoder.events.ProjectManager;
import org.protocoder.fragments.EditorFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class ListFragmentBase extends BaseFragment {

    // icon for shortcuts
    ShortcutIconResource icon;

    public ArrayList<Project> projects;
    protected ProjectAdapter projectAdapter;
    protected GridView gridView;
    int projectType;

    public ListFragmentBase() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	View v = inflater.inflate(R.layout.view_gridlayout, container, false);

	// Get GridView and set adapter
	gridView = (GridView) v.findViewById(R.id.gridview);
	projects = ProjectManager.getInstance().list(projectType);
	projectAdapter = new ProjectAdapter(getActivity(), projects, projectType);
	gridView.setEmptyView(v.findViewById(R.id.empty_grid_view));// set the
								    // empty
								    // state
	gridView.setAdapter(projectAdapter);

	for (Iterator<Project> iterator = projects.iterator(); iterator.hasNext();) {
	    Project project = iterator.next();

	    String projectURL = project.getFolder();
	    String projectName = project.getName();

	    // addProject(projectName, projectURL);

	    notifyAddedProject();
	}

	registerForContextMenu(gridView);

	gridView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, final View v, int position, long id) {

		ProjectAnimations.projectLaunch(v);

		Project project = projects.get(position);
		Log.d("BB", "onItemClickListener" + " " + position + " " + project.getName());
		ProjectEvent evt = new ProjectEvent(project, "run");
		EventBus.getDefault().post(evt);
		getActivity().overridePendingTransition(R.anim.splash_slide_in_anim_set,
			R.anim.splash_slide_out_anim_set);

		// Code this in if you want to show the context menu on single
		// click
		// gridView.showContextMenuForChild(v);

	    }
	});

	return v;
    }

    protected void deleteProject(int position) {

	File dir = new File(projects.get(position).getFolder());

	if (dir.isDirectory()) {
	    String[] children = dir.list();
	    for (int i = 0; i < children.length; i++) {
		new File(dir, children[i]).delete();
	    }
	}
	dir.delete();

	projects.remove(position);

	projectAdapter.notifyDataSetChanged();
	gridView.invalidateViews();
    }

    public void clear() {
	gridView.removeAllViews();
	projectAdapter.notifyDataSetChanged();
    }

    public void notifyAddedProject() {

	projectAdapter.notifyDataSetChanged();
	gridView.invalidateViews();
    }

    @Override
    public View getView() {
	return super.getView();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	super.onCreateContextMenu(menu, v, menuInfo);
	MenuInflater inflater = getActivity().getMenuInflater();
	inflater.inflate(R.menu.project_list, menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

	if (getUserVisibleHint()) {
	    // Handle menu events and return true
	} else
	    return false; // Pass the event to the next fragment

	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	final int index = info.position;

	Project project = projects.get(index);

	switch (item.getItemId()) {

	case R.id.menu_project_list_run:
	    ProjectEvent evt = new ProjectEvent(project, "run");
	    EventBus.getDefault().post(evt);
	    getActivity().overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);
	    return true;
	case R.id.menu_project_list_edit:
	    EditorFragment editorFragment = new EditorFragment(); // .newInstance(project);
	    Bundle bundle = new Bundle();

	    bundle.putString("project_name", project.getName());
	    bundle.putString("project_url", project.getFolder());
	    bundle.putInt("project_type", projectType);
	    Log.d("UU", "" + project.getFolder() + " " + projectType + " " + project.getName());

	    editorFragment.setArguments(bundle);
	    ((MainActivity) getActivity()).addFragment(editorFragment, R.id.fragmentEditor, "editorFragment", true);

	    return true;
	case R.id.menu_project_list_delete:

	    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
		    switch (which) {
		    case DialogInterface.BUTTON_POSITIVE:
			// Yes button clicked
			deleteProject(index);
			break;

		    case DialogInterface.BUTTON_NEGATIVE:
			// No button clicked
			break;
		    }
		}
	    };

	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No",
		    dialogClickListener).show();

	    return true;

	case R.id.menu_project_list_add_shortcut:

	    try {

		Intent shortcutIntent = new Intent(getActivity(), AppRunnerActivity.class);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		String script = ProjectManager.getInstance().getCode(project);
		shortcutIntent.putExtra("projectName", project.getName());
		shortcutIntent.putExtra("projectType", project.getType());

		final Intent putShortCutIntent = new Intent();

		putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, project.getName());
		putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon); // can
										       // also
										       // be
										       // ignored
										       // too
		putShortCutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		getActivity().sendBroadcast(putShortCutIntent);
	    } catch (Exception e) {
		// TODO
	    }

	    // Show toast
	    Toast.makeText(getActivity(), "Adding shortcut for " + project.getName(), Toast.LENGTH_SHORT).show();

	    return true;

	case R.id.menu_project_list_share_with:

	    Intent sendIntent = new Intent();
	    sendIntent.setAction(Intent.ACTION_SEND);
	    sendIntent.putExtra(Intent.EXTRA_TEXT, ProjectManager.getInstance().getCode(project));
	    sendIntent.setType("text/plain");
	    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

	    return true;
	case R.id.menu_project_list_beam:

	    Intent beamIntent = new Intent(getActivity(), BeamActivity.class);
	    // beamIntent.setAction(Intent.ACTION_SEND);
	    beamIntent.putExtra(Intent.EXTRA_TEXT, ProjectManager.getInstance().getCode(project));
	    beamIntent.setType("text/plain");
	    beamIntent.setAction(Intent.ACTION_SEND);
	    startActivity(beamIntent);
	    getActivity().overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);

	    return true;
	default:
	    return super.onContextItemSelected(item);
	}
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
	super.onPause();

    }

    @Override
    public void onResume() {
	super.onResume();
	getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
    }

    public void projectRefresh(String projectName) {
	View v = gridView.findViewWithTag(projectName);
	ProjectAnimations.projectRefresh(v);
    }

    public void projectLaunch(String projectName) {
	View v = gridView.findViewWithTag(projectName);
	ProjectAnimations.projectLaunch(v);

    }

    public void onEventMainThread(ProjectEvent evt) {
	if (evt.getAction() == "run") {
	    projectRefresh(evt.getProject().getName());
	}

    }

}
