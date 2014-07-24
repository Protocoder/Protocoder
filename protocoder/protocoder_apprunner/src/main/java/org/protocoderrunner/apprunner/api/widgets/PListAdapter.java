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

package org.protocoderrunner.apprunner.api.widgets;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.protocoderrunner.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PListAdapter extends BaseAdapter {
	private final WeakReference<Context> mContext;

	ArrayList<PListItem> items;

	public PListAdapter(Context c, ArrayList<PListItem> items2) {
		mContext = new WeakReference<Context>(c);
		this.items = items2;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final PListItem customView;

		if (convertView == null) { // if it's not recycled, initialize some
			// attributes
			customView = new PListItem(mContext.get());

			customView.setText(items.get(position).getName());
			customView.setImage(R.drawable.app_icon);

            //TODO activate this
			ImageView imageView = null; //(ImageView) customView.findViewById(R.id.card_menu_button);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					customView.showContextMenu();
				}
			});

		} else {
			customView = (PListItem) convertView;
			customView.setText(items.get(position).getName());
		}
		customView.setTag(items.get(position).getName());

		return customView;
	}
}
