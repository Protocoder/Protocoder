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

package org.protocoder.project;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.protocoder.Events;
import org.protocoder.R;
import org.protocoderrunner.utils.MLog;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class FolderChooserAdapter extends RecyclerView.Adapter<FolderChooserAdapter.ViewHolder> {

    private static final String TAG = FolderChooserAdapter.class.getSimpleName();
    private final ArrayList<FolderAdapterData> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(int viewType, LinearLayout v) {
            super(v);

            switch (viewType) {
                case FolderAdapterData.TYPE_TITLE:
                    textView = (TextView) v.findViewById(R.id.textType);

                    break;
                case (FolderAdapterData.TYPE_FOLDER_NAME):
                    textView = (TextView) v.findViewById(R.id.textFolder);

                    break;
            }
        }
    }

    public FolderChooserAdapter(ArrayList<FolderAdapterData> folders) {
        mDataSet = folders;
    }

    @Override
    public int getItemViewType(int position) {

        return mDataSet.get(position).item_type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout t = null;
        if (viewType == FolderAdapterData.TYPE_TITLE) {
            t = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_folderchooser_title, parent, false);
        }  else if (viewType == FolderAdapterData.TYPE_FOLDER_NAME) {
            t = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_folderchooser_folder, parent, false);
        }
        return new ViewHolder(viewType, t);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int type = mDataSet.get(position).item_type;
        final String name = mDataSet.get(position).name;
        final String folder = mDataSet.get(position).parentFolder;

        switch (type) {
            case FolderAdapterData.TYPE_TITLE:
                holder.textView.setText(name);
            case FolderAdapterData.TYPE_FOLDER_NAME:
                holder.textView.setText(name);
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MLog.d(TAG, "> Event (folderChosen) " + folder + "/" + name);

                        Events.FolderChosen ev = new Events.FolderChosen(folder, name);
                        EventBus.getDefault().post(ev);
                    }
                });
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
