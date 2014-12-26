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
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.protocoder.R;
import org.protocoder.activities.MyAdapter;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.utils.MLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class ProjectItemAdapter extends RecyclerView.Adapter<ProjectItemAdapter.ViewHolder>  {
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
