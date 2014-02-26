/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.protocoder.R;
import org.protocoder.events.Project;
import org.protocoder.events.ProjectManager;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ProjectAdapter extends BaseAdapter {
    private WeakReference<Context> mContext;

    ArrayList<Project> projects;
    private int projectType;

    public ProjectAdapter(Context c, ArrayList<Project> projects, int projectType) {
	mContext = new WeakReference<Context>(c);
	this.projects = projects;
	this.projectType = projectType;
    }

    @Override
    public int getCount() {
	return projects.size();
    }

    @Override
    public Object getItem(int position) {
	return projects.get(position);
    }

    @Override
    public long getItemId(int position) {
	return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	final ProjectItem customView;

	if (convertView == null) { // if it's not recycled, initialize some
				   // attributes
	    customView = new ProjectItem(mContext.get());

	    if (projectType == ProjectManager.PROJECT_USER_MADE) {
		customView.setImage(R.drawable.ic_script);
	    } else {
		customView.setImage(R.drawable.ic_script_example);
	    }
	    // Log.d("qq", "" + projects.get(position).getName());
	    customView.setText(projects.get(position).getName());
	    ImageView imageView = (ImageView) customView.findViewById(R.id.card_menu_button);
	    imageView.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    customView.showContextMenu();
		}
	    });
	    imageView.setOnLongClickListener(new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
		    customView.showContextMenu();
		    return true;
		}
	    });

	} else {
	    customView = (ProjectItem) convertView;
	    customView.setText(projects.get(position).getName());
	}
	customView.setTag(projects.get(position).getName());

	if (getCount() - 1 == position) {
	    customView.setPadding(0, 0, 0, 100);
	} else {
	    customView.setPadding(0, 0, 0, 0);
	}

	return customView;
    }

}
