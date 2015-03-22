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
