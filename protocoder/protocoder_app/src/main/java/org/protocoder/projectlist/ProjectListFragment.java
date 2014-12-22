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
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.protocoder.MainActivity;
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
	public ProjectItemAdapter mProjectAdapter;
	protected RecyclerView mGrid;
	public String mProjectFolder;
	boolean mListMode;
    public int color;
    public int icon;
    public boolean orderByName;
    public int num = 0;
    public static int totalNum = 0;
    private GridLayoutManager mLayoutManager;
    private Context mContext;
    //public Intent.ShortcutIconResource icon;

    public ProjectListFragment() {
        num = totalNum++;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        this.mProjectFolder = getArguments().getString("folderName");
        this.color = getArguments().getInt("color");
        this.icon = getArguments().getInt("icon");
        this.orderByName = getArguments().getBoolean("orderByName");

        mProjects = ProjectManager.getInstance().list(this.mProjectFolder, this.orderByName);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //this.icon = getArguments().getString("icon");

        mContext = (Context) getActivity();
        View v = inflater.inflate(R.layout.fragment_project, container, false);

        // Get GridView and set adapter
		mGrid = (RecyclerView) v.findViewById(R.id.gridprojects);
		mListMode = SettingsFragment.getListPreference(getActivity());

        mGrid.setItemAnimator(new DefaultItemAnimator());

        int numColumns = 3;
        if (mListMode) {
            numColumns = 1;
		}

        mLayoutManager = new GridLayoutManager(mContext, numColumns);
        mGrid.setLayoutManager(mLayoutManager);

        // set the empty state
		//mGrid.setEmptyView(v.findViewById(R.id.empty_grid_view));

		registerForContextMenu(mGrid);


        return v;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setRetainInstance(true);

        //icon = Intent.ShortcutIconResource.fromContext(getActivity(), R.drawable.ic_script_example);
        mProjectAdapter = new ProjectItemAdapter(getActivity(), this);
        mGrid.setAdapter(mProjectAdapter);

        notifyAddedProject();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

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
        mProjects = ProjectManager.getInstance().list(this.mProjectFolder, this.orderByName);
    	notifyAddedProject();
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

       // MLog.d(TAG, "size " + mProjects.size());
        for (int i = 0; i < mProjects.size(); i++) {
            String name = mProjects.get(i).getName();
           // MLog.d(TAG, "name " + name);

            if (name.equals(appName)) {
                pos = i; //(int) mProjectAdapter.getItemId(i);

                break;
            }
        }
        //MLog.d(TAG, "pos " + pos);

        return pos;
    }

    public void removeItem(Project p) {
        ProjectManager.getInstance().deleteProject(p);

        int id = findAppPosByName(p.getName());
        mProjects.remove(id);
        notifyAddedProject();

    }

   // public View getViewByName(String appName) {
     //   int pos = findAppPosByName(appName);
        //View view = projectAdapter.getView(pos, null, null);

       // return null;
    //}

    public void goTo(int pos) {
        if (pos != -1) mGrid.smoothScrollToPosition(pos);
    }

    public View highlight(String projectName, boolean b) {

        View v = mGrid.findViewWithTag(projectName);
        v.setSelected(b);
        mProjectAdapter.mProjects.get(findAppIdByName(projectName)).selected = true;
        v.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

        return v;
    }

	public void clear() {
		mGrid.removeAllViews();
		mProjectAdapter.notifyDataSetChanged();
	}

	public void notifyAddedProject() {
		mProjectAdapter.notifyDataSetChanged();
		//mGrid.invalidateViews();
	}

	@Override
	public View getView() {
		return super.getView();
	}

	@Override
	public void onPause() {
		super.onPause();
    }

	@Override
	public void onResume() {
		super.onResume();

        if(!((MainActivity)getActivity()).isWear()) {
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void projectRefresh(String projectName) {
        getView(projectName).animate().alpha(0).setDuration(500).setInterpolator(new CycleInterpolator(1));
	}

    public View getView(String projectName) {
        return mGrid.findViewWithTag(projectName);
    }


    public void resetHighlighting() {
        for (int i = 0; i < mProjects.size(); i++) {
            mProjects.get(i).selected = false;
        }

        for (int i = 0; i < mGrid.getChildCount(); i++) {
            ProjectItem v = (ProjectItem) mGrid.getChildAt(i);
            if (v.isHighlighted()) {
                v.setHighlighted(false);
            }
        }
    }

	public void onEventMainThread(ProjectEvent evt) {
		if (evt.getAction() == "run") {
			projectRefresh(evt.getProject().getName());
		}
	}

}
