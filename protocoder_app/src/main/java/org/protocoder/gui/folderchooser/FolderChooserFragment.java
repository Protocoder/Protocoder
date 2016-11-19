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

package org.protocoder.gui.folderchooser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.protocoder.R;
import org.protocoder.events.Events;
import org.protocoder.gui._components.ResizableRecyclerView;
import org.protocoder.gui.settings.ProtocoderSettings;
import org.protocoder.helpers.ProtoScriptHelper;
import org.protocoderrunner.base.BaseFragment;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Folder;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class FolderChooserFragment extends BaseFragment {

    private String TAG = FolderChooserFragment.class.getSimpleName();

    private ResizableRecyclerView mFolderRecyclerView;
    private LinearLayout toggleFolderChooser;
    private String currentParentFolder;
    private String currentFolder;
    private TextView mTxtChooseFolder;
    private TextView mTxtSeparator;
    private TextView mTxtParentFolder;
    private TextView mTxtFolder;
    private boolean isShown = true;
    private boolean isTablet;
    private boolean isLandscapeBig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isTablet = getResources().getBoolean(R.bool.isTablet);
        isLandscapeBig = getResources().getBoolean(R.bool.isLandscapeBig);

        final View v = inflater.inflate(R.layout.folderchooser_fragment, container, false);

        // project folder menu
        toggleFolderChooser = (LinearLayout) v.findViewById(R.id.selectFolderButton);
        mTxtChooseFolder = (TextView) v.findViewById(R.id.choosefolder);
        mTxtSeparator = (TextView) v.findViewById(R.id.separator);
        mTxtParentFolder = (TextView) v.findViewById(R.id.parentFolder);
        mTxtFolder = (TextView) v.findViewById(R.id.folder);

        //this goes to the adapter
        ArrayList<FolderAdapterData> foldersForAdapter = new ArrayList<FolderAdapterData>();

        //get the user folder
        ArrayList<Folder> folders = ProtoScriptHelper.listFolders(ProtocoderSettings.USER_PROJECTS_FOLDER, true);
        foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_TITLE, ProtocoderSettings.USER_PROJECTS_FOLDER, "User Projects"));
        for (Folder folder : folders) {
            foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_FOLDER_NAME, ProtocoderSettings.USER_PROJECTS_FOLDER, folder.getName()));
        }

        //get the examples folder
        ArrayList<Folder> examples = ProtoScriptHelper.listFolders(ProtocoderSettings.EXAMPLES_FOLDER, true);
        foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_TITLE, ProtocoderSettings.EXAMPLES_FOLDER, "Examples"));
        for (Folder folder : examples) {
            foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_FOLDER_NAME,  ProtocoderSettings.EXAMPLES_FOLDER, folder.getName()));
        }

        // Attach the adapter with the folders data
        mFolderRecyclerView = (ResizableRecyclerView) v.findViewById(R.id.folderList);
        mFolderRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFolderRecyclerView.setLayoutManager(linearLayoutManager);
        FolderChooserAdapter folderChooserAdapter = new FolderChooserAdapter(foldersForAdapter);
        mFolderRecyclerView.setAdapter(folderChooserAdapter);

        if (isTablet || isLandscapeBig) {
            toggleFolderChooser.setVisibility(View.GONE);
            mFolderRecyclerView.setVisibility(View.VISIBLE);
        }

        // default toggle text
        // toggleFolderChooser.setText("Choose folder");
        // toggleFolderChooser.setTextOn("Choose folder");
        // toggleFolderChooser.setTextOff("Choose folder");

        // folder list oculto cuando
        // isTablet => false
        // isLandscapeBig => false

        toggleFolderChooser.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               // show folderlist
               if (isShown) {
                   mFolderRecyclerView.setVisibility(View.VISIBLE);

                   mTxtChooseFolder.setVisibility(View.VISIBLE);
                   mTxtParentFolder.setVisibility(View.GONE);
                   mTxtSeparator.setVisibility(View.GONE);
                   mTxtFolder.setVisibility(View.GONE);

                   // hide folderlist
               } else {
                   if (!(isTablet || isLandscapeBig)) {
                       mFolderRecyclerView.setVisibility(View.GONE);

                       if (currentFolder == null) {

                       } else {
                           mTxtChooseFolder.setVisibility(View.GONE);
                           mTxtParentFolder.setVisibility(View.VISIBLE);
                           mTxtSeparator.setVisibility(View.VISIBLE);
                           mTxtFolder.setVisibility(View.VISIBLE);
                           // toggleFolderChooser.setTextOff(currentFolder);
                       }
                   }
               }
               isShown = !isShown;
           }
        });
        toggleFolderChooser.performClick();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public static FolderChooserFragment newInstance(String folderName, boolean orderByName) {
        FolderChooserFragment myFragment = new FolderChooserFragment();

        Bundle args = new Bundle();
        args.putString("folderName", folderName);
        args.putBoolean("orderByName", orderByName);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        if (e.getAction() == Events.CLOSE_APP) {
            if (!isShown) getActivity().finish();
            else toggleFolderChooser.performClick();
        }
    }

    // folder choose
    @Subscribe
    public void onEventMainThread(Events.FolderChosen e) {
        MLog.d(TAG, "< Event (folderChosen)");
        currentParentFolder = e.getParent();
        currentFolder = e.getName();
        mTxtParentFolder.setText(currentParentFolder);
        mTxtFolder.setText(currentFolder);
        toggleFolderChooser.performClick();
    }

}
