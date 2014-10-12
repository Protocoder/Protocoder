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

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import org.protocoder.R;
import org.protocoder.appApi.Protocoder;
import org.protocoder.fragments.SettingsFragment;
import org.protocoderrunner.base.BaseFragment;
import org.protocoderrunner.events.Events.ProjectEvent;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class ProjectListFragment extends BaseFragment {

    private String TAG = "ProjectListFragment";

    public ArrayList<Project> mProjects;
	protected ProjectItemAdapter projectAdapter;
	protected GridView gridView;
	public String projectFolder;
	boolean listMode;
    public int color;
    public boolean orderByName;
    public int num = 0;
    public static int totalNum = 0;
    //public Intent.ShortcutIconResource icon;

    public ProjectListFragment() {
        num = totalNum++;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        MLog.d(TAG, "onCreate " + getArguments().getString("folderName"));
        this.projectFolder = getArguments().getString("folderName");
        this.color = getArguments().getInt("color");
        this.orderByName = getArguments().getBoolean("orderByName");

        mProjects = ProjectManager.getInstance().list(this.projectFolder, this.orderByName);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //this.icon = getArguments().getString("icon");

        MLog.d(TAG, "onCreateView " + getArguments().getString("folderName"));

        View v = inflater.inflate(R.layout.fragment_project, container, false);

        // Get GridView and set adapter
		gridView = (GridView) v.findViewById(R.id.gridview);
		listMode = SettingsFragment.getListPreference(getActivity());

		MLog.d(TAG, "fragment for " + projectFolder);
		if (listMode) {
			gridView.setNumColumns(1);
		}
		// set the empty state
		gridView.setEmptyView(v.findViewById(R.id.empty_grid_view));

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
				Project project = mProjects.get(position);
				ProjectEvent evt = new ProjectEvent(project, "run");
				EventBus.getDefault().post(evt);
				getActivity().overridePendingTransition(R.anim.splash_slide_in_anim_set,
						R.anim.splash_slide_out_anim_set);

			}
		});


        return v;
	}


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MLog.d(TAG, "onActivityCreated");
        //setRetainInstance(true);

        //icon = Intent.ShortcutIconResource.fromContext(getActivity(), R.drawable.ic_script_example);
        projectAdapter = new ProjectItemAdapter(getActivity(), this.projectFolder, this.mProjects, this.listMode);
        gridView.setAdapter(projectAdapter);

        notifyAddedProject();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MLog.d(TAG, "onAttach");

    }

    public static ProjectListFragment newInstance(int icon, String folderName, int color, boolean orderByName) {
        ProjectListFragment myFragment = new ProjectListFragment();

        Bundle args = new Bundle();
        args.putInt("icon", icon);
        args.putString("folderName", folderName);
        args.putInt("color", color);
        args.putBoolean("orderByName", orderByName);
        myFragment.setArguments(args);

        return myFragment;
    }

	public void refreshProjects() {
        mProjects = ProjectManager.getInstance().list(this.projectFolder, this.orderByName);
    	notifyAddedProject();
    }

	protected void deleteProject(int position) {
		Project p = mProjects.get(position);
		ProjectManager.getInstance().deleteProject(p);

		mProjects.remove(position);

		projectAdapter.notifyDataSetChanged();
		gridView.invalidateViews();
	}


    public int findAppIdByName(String appName) {
        int id = -1;

        for (int i = 0; i < mProjects.size(); i++) {
            String name = mProjects.get(i).getName();
            if (name.equals(appName)) {
                id = i;
                break;
            }
        }

        return id;
    }
    public int findAppPosByName(String appName) {
        int pos = -1;
      
        MLog.d(TAG, "findAppPosByName " + appName + " " + mProjects);

        for (int i = 0; i < mProjects.size(); i++) {
            String name = mProjects.get(i).getName();
            if (name.equals(appName)) {
                pos = (int) projectAdapter.getItemId(i);
                break;
            }
        }

        return pos;
    }

    public void goTo(int pos) {
        if (pos != -1) gridView.smoothScrollToPosition(pos);
    }

    public void highlight(String projectName, boolean b) {

        View v = gridView.findViewWithTag(projectName);
        v.setSelected(b);
        projectAdapter.projects.get(findAppIdByName(projectName)).selected = true;
        //v.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

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

		Project project = mProjects.get(index);

		int itemId = item.getItemId();
		if (itemId == R.id.menu_project_list_run) {
            Protocoder.getInstance(getActivity()).protoScripts.run(project.getFolder(), project.getName());
			return true;
		} else if (itemId == R.id.menu_project_list_edit) {
            Protocoder.getInstance(getActivity()).app.editor.show(true, project.getFolder(), project.getName());
			return true;
		} else if (itemId == R.id.menu_project_list_delete) {
            Protocoder.getInstance(getActivity()).protoScripts.delete(project.getFolder(), project.getName());

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						deleteProject(index);
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						break;
					}
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();
			return true;
		} else if (itemId == R.id.menu_project_list_add_shortcut) {
		    Protocoder.getInstance(getActivity()).protoScripts.addShortcut(project.getFolder(), project.getName());
			return true;
		} else if (itemId == R.id.menu_project_list_share_with) {
            Protocoder.getInstance(getActivity()).protoScripts.shareMainJsDialog(project.getFolder(), project.getName());
            return true;
        } else if (itemId == R.id.menu_project_list_share_proto_file) {
            Protocoder.getInstance(getActivity()).protoScripts.shareProtoFileDialog(project.getFolder(), project.getName());
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}


	@Override
	public void onPause() {
		super.onPause();

        MLog.d(TAG, "onPause");

    }

	@Override
	public void onResume() {
		super.onResume();
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);

        MLog.d(TAG, "onResume");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        MLog.d(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MLog.d(TAG, "onDestroyView");
    }

    public void projectRefresh(String projectName) {
		View v = gridView.findViewWithTag(projectName);
        v.animate().alpha(0).setDuration(500).setInterpolator(new CycleInterpolator(1));
	}

	public void onEventMainThread(ProjectEvent evt) {
		if (evt.getAction() == "run") {
			projectRefresh(evt.getProject().getName());
		}

	}

}
