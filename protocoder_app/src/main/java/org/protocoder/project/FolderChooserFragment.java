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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.protocoder.ProtocoderAppHelper;
import org.protocoder.ProtocoderAppSettings;
import org.protocoder.R;
import org.protocoderrunner.apprunner.project.Folder;
import org.protocoderrunner.base.BaseFragment;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class FolderChooserFragment extends BaseFragment {

    private String TAG = FolderChooserFragment.class.getSimpleName();
    private Context mContext;

    ResizableRecyclerView mRecyclerView;
    boolean state = false;

    public FolderChooserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = (Context) getActivity();

        View v = inflater.inflate(R.layout.fragment_project_chooser, container, false);

        //this goes to the adapter
        ArrayList<FolderAdapterData> foldersForAdapter = new ArrayList<FolderAdapterData>();

        //get the user folder
        ArrayList<Folder> folders = ProtocoderAppHelper.listFolders(ProtocoderAppSettings.USER_PROJECTS_FOLDER, true);
        foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_TITLE, ProtocoderAppSettings.USER_PROJECTS_FOLDER, "User Projects"));
        for (Folder folder : folders) {
            foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_FOLDER_NAME, ProtocoderAppSettings.USER_PROJECTS_FOLDER, folder.getName()));
        }

        //get the examples folder
        ArrayList<Folder> examples = ProtocoderAppHelper.listFolders(ProtocoderAppSettings.EXAMPLES_FOLDER, true);
        foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_TITLE, ProtocoderAppSettings.EXAMPLES_FOLDER, "Examples"));
        for (Folder folder : examples) {
            foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_FOLDER_NAME,  ProtocoderAppSettings.EXAMPLES_FOLDER, folder.getName()));
        }

        // Attach the adapter with the folders data
        mRecyclerView = (ResizableRecyclerView) v.findViewById(R.id.folderList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        FolderChooserAdapter folderChooserAdapter = new FolderChooserAdapter(foldersForAdapter);
        mRecyclerView.setAdapter(folderChooserAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static FolderChooserFragment newInstance(String folderName, boolean orderByName) {
        FolderChooserFragment myFragment = new FolderChooserFragment();

        Bundle args = new Bundle();
        args.putString("folderName", folderName);
        args.putBoolean("orderByName", orderByName);
        myFragment.setArguments(args);

        return myFragment;
    }

}
