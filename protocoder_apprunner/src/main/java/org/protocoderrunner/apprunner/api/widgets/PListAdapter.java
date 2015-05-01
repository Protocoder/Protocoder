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

package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.protocoderrunner.R;

import java.util.ArrayList;

public class PListAdapter extends BaseAdapter {
    private final Context mContext;

    ArrayList<PListItem> items;

    public PListAdapter(Context c, ArrayList<PListItem> items2) {
        mContext = c;
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

    // create mContext new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PListItem customView;

        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            customView = new PListItem(mContext);

            customView.setText(items.get(position).getName());
            customView.setImage(R.drawable.protocoder_icon);

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
