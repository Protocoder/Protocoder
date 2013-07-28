package com.makewithmoto.projectlist;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.makewithmoto.R;
import com.makewithmoto.events.Project;
import com.makewithmoto.events.ProjectManager;

public class ProjectAdapter extends BaseAdapter {
	private WeakReference<Context> mContext;

	ArrayList<Project> projects;
	private int projectType;

	public ProjectAdapter(Context c, ArrayList<Project> projects,
			int projectType) {
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
			ImageView imageView = (ImageView) customView
					.findViewById(R.id.card_menu_button);
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

		return customView;
	}

}
