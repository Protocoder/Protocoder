/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoder.qq;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.LinearLayout;

import org.protocoder.R;
import org.protocoderrunner.base.BaseFragment;
import org.protocoderrunner.events.Events.ProjectEvent;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class ProjectListFragment extends BaseFragment {

    private String TAG = "ProjectListFragment";
    private Context mContext;

    protected FitRecyclerView mGrid;
    private GridLayoutManager mLayoutManager;
    private LinearLayout mEmptyGrid;

    public ArrayList<Project> mListProjects = null;
    public ProjectItemAdapter mProjectAdapter;

    public String mProjectFolder;
    boolean mListMode;
    public boolean mOrderByName;
    public int num = 0;
    public static int totalNum = 0;

    public ProjectListFragment() {
        num = totalNum++;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProjectFolder = getArguments().getString("folderName");
        //mProjectFolder = "projects";
        MLog.d(TAG, "qq " + mProjectFolder);
        mOrderByName = getArguments().getBoolean("orderByName");
        mProjectAdapter = new ProjectItemAdapter(getActivity()); //, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = (Context) getActivity();
        //TODO make it work again
        //mListMode = Protocoder.getInstance((BaseActivity) getActivity()).settings.getListPreference();
        mListMode = false;

        View v;
        if (mListMode) {
            v = inflater.inflate(R.layout.fragment_project_single, container, false);
        } else {
            v = inflater.inflate(R.layout.fragment_project, container, false);
        }
        // Get GridView and set adapter
        mGrid = (FitRecyclerView) v.findViewById(R.id.gridprojects);
        // mGrid.setHasFixedSize(true);
        mGrid.setItemAnimator(new DefaultItemAnimator());

        // set the empty state
        mEmptyGrid = (LinearLayout) v.findViewById(R.id.empty_grid_view);
        checkEmptyState();
        registerForContextMenu(mGrid);

        loadFolder(mProjectFolder);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setRetainInstance(true);

    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO reenable
        //if (!AndroidUtils.isWear(getActivity())) {
        //    ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //}
    }

    public static ProjectListFragment newInstance(String folderName, boolean orderByName) {
        ProjectListFragment myFragment = new ProjectListFragment();

        Bundle args = new Bundle();
        args.putString("folderName", folderName);
        args.putBoolean("orderByName", orderByName);
        myFragment.setArguments(args);

        return myFragment;
    }

    private void checkEmptyState() {
        //check if mProjects has been loaded
        if (mListProjects == null) {
            showProjectList(false);

            return;
        }

        //if empty we show, hey! there is no projects!
        if (mListProjects.isEmpty()) {
            showProjectList(false);
        } else {
            showProjectList(true);
        }

    }

    private void showProjectList(boolean b) {
        if (b) {
            mGrid.setVisibility(View.VISIBLE);
            mEmptyGrid.setVisibility(View.GONE);
        } else {
            mGrid.setVisibility(View.GONE);
            mEmptyGrid.setVisibility(View.VISIBLE);
        }
    }

    // public View getViewByName(String appName) {
    //   int pos = findAppPosByName(appName);
    //View view = projectAdapter.getView(pos, null, null);

    // return null;
    //}

    public void goTo(int pos) {
        if (pos != -1) mGrid.smoothScrollToPosition(pos);
    }

    public void clear() {
        if (mListProjects != null) mListProjects.clear();
       // mGrid.removeAllViews();
        mProjectAdapter.notifyDataSetChanged();
    }

    public void notifyAddedProject() {
        checkEmptyState();
        //mProjectAdapter.notifyDataSetChanged();
    }

    public void loadFolder(String folder) {
        clear();

        mProjectFolder = folder;

        mListProjects = ProjectManager.getInstance().list(mProjectFolder, mOrderByName);
        mProjectAdapter.setArray(mListProjects);
        mGrid.setAdapter(mProjectAdapter);
        notifyAddedProject();

        MLog.d(TAG, "loading " + mProjectFolder);
    }

    public View getItemView(String projectName) {
        return mGrid.findViewWithTag(projectName);
    }


    /*
     * UI fancyness
     */
    public void projectRefresh(String projectName) {
        getItemView(projectName).animate().alpha(0).setDuration(500).setInterpolator(new CycleInterpolator(1));
    }

    public void resetHighlighting() {
        for (int i = 0; i < mListProjects.size(); i++) {
            mListProjects.get(i).selected = false;
        }

        for (int i = 0; i < mGrid.getChildCount(); i++) {
            ProjectItem v = (ProjectItem) mGrid.getChildAt(i);
            if (v.isHighlighted()) {
                v.setHighlighted(false);
            }
        }
    }

    public View highlight(String projectName, boolean b) {
        View v = mGrid.findViewWithTag(projectName);
        v.setSelected(b);
        mProjectAdapter.mProjectList.get(mProjectAdapter.findAppIdByName(projectName)).selected = true;
        v.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

        return v;
    }

    /*
     * Events
     */
    public void onEventMainThread(ProjectEvent evt) {
        if (evt.getAction() == "run") {
            projectRefresh(evt.getProject().getName());
        }
    }

}
