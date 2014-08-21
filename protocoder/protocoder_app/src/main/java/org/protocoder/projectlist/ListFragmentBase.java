/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
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

import org.protocoder.MainActivity;
import org.protocoder.PrefsFragment;
import org.protocoder.R;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.base.BaseFragment;
import org.protocoderrunner.events.Events.ProjectEvent;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoder.fragments.EditorFragment;
import org.protocoderrunner.utils.MLog;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.net.Uri;
import android.os.Bundle;
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
	boolean listMode;

	public ListFragmentBase() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_project, container, false);

		// Get GridView and set adapter
		gridView = (GridView) v.findViewById(R.id.gridview);
		listMode = PrefsFragment.getListPreference(getActivity());

		MLog.d("mode", "" + listMode);
		if (listMode) {
			gridView.setNumColumns(1);
		}
		// set the empty state
		gridView.setEmptyView(v.findViewById(R.id.empty_grid_view));
		refreshProjects();

		registerForContextMenu(gridView);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View v, final int position, long id) {

				// ProjectAnimations.projectLaunch(v);

				AnimatorSet animSpin;
				animSpin = (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(), R.animator.flip_up);
				animSpin.setTarget(v);
				animSpin.addListener(new AnimatorListener() {

					@Override
					public void onAnimationStart(Animator animation) {

					}

					@Override
					public void onAnimationRepeat(Animator animation) {

					}

					@Override
					public void onAnimationEnd(Animator animation) {

					}

					@Override
					public void onAnimationCancel(Animator animation) {

					}
				});
				animSpin.start();
				Project project = projects.get(position);
				ProjectEvent evt = new ProjectEvent(project, "run");
				EventBus.getDefault().post(evt);
				getActivity().overridePendingTransition(R.anim.splash_slide_in_anim_set,
						R.anim.splash_slide_out_anim_set);

			}
		});

		return v;
	}

	public void refreshProjects() {
		projects = ProjectManager.getInstance().list(projectType);
		projectAdapter = new ProjectAdapter(getActivity(), projects, projectType, listMode);
		gridView.setAdapter(projectAdapter);
		notifyAddedProject();
	}

	protected void deleteProject(int position) {
		Project p = projects.get(position);
		ProjectManager.getInstance().deleteProject(p);

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
		} else {
			return false; // Pass the event to the next fragment
		}

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final int index = info.position;

		Project project = projects.get(index);

		int itemId = item.getItemId();
		if (itemId == R.id.menu_project_list_run) {
			ProjectEvent evt = new ProjectEvent(project, "run");
			EventBus.getDefault().post(evt);
			getActivity().overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);
			return true;
		} else if (itemId == R.id.menu_project_list_edit) {
			EditorFragment editorFragment = new EditorFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Project.NAME, project.getName());
			bundle.putString(Project.URL, project.getStoragePath());
			bundle.putInt(Project.TYPE, projectType);

			editorFragment.setArguments(bundle);
			((MainActivity) getActivity()).addFragment(editorFragment, R.id.fragmentEditor, "editorFragment", true);
			return true;
		} else if (itemId == R.id.menu_project_list_delete) {
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
			builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();
			return true;
		} else if (itemId == R.id.menu_project_list_add_shortcut) {
			try {

				Intent shortcutIntent = new Intent(getActivity(), AppRunnerActivity.class);
				shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				String script = ProjectManager.getInstance().getCode(project);
				shortcutIntent.putExtra(Project.NAME, project.getName());
				shortcutIntent.putExtra(Project.TYPE, project.getType());

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
		} else if (itemId == R.id.menu_project_list_share_with) {
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, ProjectManager.getInstance().getCode(project));
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
			return true;
        } else if (itemId == R.id.menu_project_list_share_zip) {
            String zipFilePath = ProjectManager.getInstance().createBackup(project);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(zipFilePath)));
            shareIntent.setType("application/zip");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_zip)));
			return true;
		} else {
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

	public void onEventMainThread(ProjectEvent evt) {
		if (evt.getAction() == "run") {
			projectRefresh(evt.getProject().getName());
		}

	}

}
