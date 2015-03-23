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

package org.protocoder.projectlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.protocoderrunner.project.Project;

import java.util.ArrayList;

public class ProjectItemAdapter extends RecyclerView.Adapter<ProjectItemAdapter.ViewHolder> {
    private static final String TAG = "ProjectItemAdapter";
    private final Context mContext;
    private final int mIcon;
    private final ProjectListFragment mPlf;

    public ArrayList<Project> mProjects;
    private final String mProjectFolder;
    private final boolean mListMode;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ProjectItem mView;
        private final int position;

        public ViewHolder(ProjectItem v) {
            super(v);
            mView = v;
            position = getPosition();
        }
    }

    public ProjectItemAdapter(Context c, ProjectListFragment plf) {
        mContext = c;
        this.mPlf = plf;
        this.mProjects = plf.mProjects;
        this.mProjectFolder = plf.mProjectFolder;
        this.mListMode = plf.mListMode;
        this.mIcon = plf.icon;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {

        //MLog.d(TAG, "view created");
        // create mContext new view
        //ProjectItem v = (ProjectItem) LayoutInflater.from(parent.getContext())
        //        .inflate(R.layout.view_project_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ProjectItem projectItem = new ProjectItem(mContext, mPlf, mListMode);


        ViewHolder vh = new ProjectItemAdapter.ViewHolder(projectItem);

        return vh;
    }

    // Replace the contents of mContext view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Project p = mProjects.get(position);
        holder.mView.setProject(p);

//
//        if (getCount() - 1 == position) {
//            customView.setPadding(0, 0, 0, 100);
//        } else {
//            customView.setPadding(0, 0, 0, 0);
//        }

    }


    @Override
    public int getItemCount() {
        return mProjects.size();
    }

}
