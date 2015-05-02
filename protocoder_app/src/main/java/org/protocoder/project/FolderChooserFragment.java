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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.protocoder.AppSettings;
import org.protocoder.ProjectManager;
import org.protocoder.ProtocoderAppHelper;
import org.protocoder.R;
import org.protocoderrunner.apprunner.project.Project;
import org.protocoderrunner.base.BaseFragment;
import org.protocoderrunner.utils.MLog;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class FolderChooserFragment extends BaseFragment {

    private String TAG = FolderChooserFragment.class.getSimpleName();
    private Context mContext;

    RecyclerView mRecyclerView;

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

        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
        final String[] arraySpinner = new String[]{
                "projects", "examples",
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_dropdown_item_1line, arraySpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MLog.d(TAG, "clicked on " + arraySpinner[position]);

                //send event

                //ProjectEvent evt = new ProjectEvent(p, "run");
                //EventBus.getDefault().post(evt);

                //mListFragmentBase.loadFolder(arraySpinner[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button btn = (Button) v.findViewById(R.id.selectFolderButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRecyclerView.getVisibility() == View.VISIBLE) {
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });


        ArrayList<FolderData> folders = new ArrayList<FolderData>();

        ArrayList<Project> projects = ProtocoderAppHelper.list(AppSettings.USER_PROJECTS_FOLDER, true);
        folders.add(new FolderData(FolderData.TYPE_TITLE, "Projects"));
        for (Project project : projects) {
            folders.add(new FolderData(FolderData.TYPE_FOLDER_NAME, project.getName()));
        }

        ArrayList<Project> examples = ProtocoderAppHelper.list(AppSettings.EXAMPLES_FOLDER, true);
        folders.add(new FolderData(FolderData.TYPE_TITLE, "Examples"));
        for (Project example : examples) {
            folders.add(new FolderData(FolderData.TYPE_FOLDER_NAME, example.getName()));
        }

        // Attach the adapter
        mRecyclerView = (RecyclerView) v.findViewById(R.id.folderList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        FolderChooserAdapter folderChooserAdapter = new FolderChooserAdapter(folders);
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
