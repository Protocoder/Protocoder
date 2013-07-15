package com.makewithmoto.projectlist;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.makewithmoto.R;
import com.makewithmoto.events.Project;

public class ProjectAdapter extends BaseAdapter {
	private WeakReference<Context> mContext;

	ArrayList<Project> projects;

	public ProjectAdapter(Context c, ArrayList<Project> projects) {
		mContext = new WeakReference<Context>(c);
		this.projects = projects;
	}

	public int getCount() {
		return projects.size();
	}

	public Object getItem(int position) {
		return projects.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ProjectItem customView;
		
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			customView = new ProjectItem(mContext.get()); 
			customView.setImage(R.drawable.ic_script); 
			Log.d("qq", "" + projects.get(position).getName());
			customView.setText(projects.get(position).getName()); 

			
		} else {
			customView = (ProjectItem) convertView;
		}
		customView.setTag(projects.get(position).getName());


		return customView;
	}
	

	
}
