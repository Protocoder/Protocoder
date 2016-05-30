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


package org.protocoder.gui.filemanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.protocoder.R;
import org.protocoder.events.Events;
import org.protocoder.helpers.ProtoScriptHelper;
import org.protocoder.server.model.ProtoFile;
import org.protocoderrunner.base.BaseFragment;
import org.protocoderrunner.base.utils.MLog;

import java.io.File;
import java.util.ArrayList;

@SuppressLint("NewApi")
public class FileManagerFragment extends BaseFragment {

    private static final String TAG = FileManagerFragment.class.getSimpleName();

    public static final String ROOT_FOLDER = "root_folder";
    public static final String CURRENT_FOLDER = "current_folder";
    public static final String PATH_HIDE_PATH_FROM = "hide_path_from";

    private Menu mMenu;
    // public HashMap<Integer, Boolean> filesModified;

    protected FileManagerAdapter mProjectAdapter;
    protected RecyclerView mRecyclerFileList;

    // private Project mProject;
    // private int mCurrentSelected = -1;

    public ArrayList<ProtoFile> mCurrentFileList;
    private String mCurrentFolder = "";
    private String mRootFolder = "";
    private String mPathHideFrom;

    private TextView mTxtPath;

    public static FileManagerFragment newInstance() {
        FileManagerFragment myFragment = new FileManagerFragment();

        Bundle bundle = new Bundle();
        myFragment.setArguments(bundle);

        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mRootFolder = bundle.getString(ROOT_FOLDER, "");
        mCurrentFolder = bundle.getString(CURRENT_FOLDER, mRootFolder);
        mPathHideFrom = bundle.getString(PATH_HIDE_PATH_FROM);

        MLog.d(TAG, "created with root: " + mCurrentFolder + " and current " + mCurrentFolder);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.filemanager_fragment, container, false);

        bindUI(v);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerFileList.setLayoutManager(linearLayoutManager);

        // if a folder is given we retrieve the files
        if (!mCurrentFolder.isEmpty()) {
            getFileList();
            // mProjectAdapter.notifyDataSetChanged();
        }

        mProjectAdapter = new FileManagerAdapter(this, mCurrentFileList);
        mRecyclerFileList.setAdapter(mProjectAdapter);

        // filesModified = new HashMap<>();

        // registerForContextMenu(mRecyclerFileList);z


        return v;
    }

    private void bindUI(View v) {
        mRecyclerFileList = (RecyclerView) v.findViewById(R.id.llFile);

        // bind up button
        Button btnDirUp = (Button) v.findViewById(R.id.dir_up);
        btnDirUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(mCurrentFolder);
                mCurrentFolder = f.getParent();
                getFileList();

                MLog.d(TAG, mCurrentFolder);
                mProjectAdapter.setData(mCurrentFileList);
                mProjectAdapter.notifyDataSetChanged();
            }
        });

        // bind path
        mTxtPath = (TextView) v.findViewById(R.id.path);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.file_list, menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(1, 21, 0, "Add").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public void onDestroyOptionsMenu() {
        mMenu.removeItem(21);
        super.onDestroyOptionsMenu();
    }

    /*
     * On long press on item
     */
    public void showMenuForItem(View fromView, final int index) {
        File file = new File(mCurrentFileList.get(index).path);

        PopupMenu myPopup = new PopupMenu(getContext(), fromView);
        myPopup.inflate(R.menu.filemanager_actions);
        myPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.filemanager_action_open:

                        return true;

                    case R.id.filemanager_action_open_with:
                        viewFile(index);

                        return true;

                    case R.id.filemanager_action_copy:

                        return true;

                    case R.id.filemanager_action_cut:

                        return true;

                    case R.id.filemanager_action_delete:
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        // deleteFile(index);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                        return true;

                    default:
                        return false;

                   }
            }
        });
        myPopup.show();
    }

    private void getFileList() {
        mCurrentFileList = ProtoScriptHelper.listFilesForFileManager(mCurrentFolder);

        String showPath = "";
        if (mPathHideFrom != null) {
            MLog.d(TAG, mPathHideFrom + " " + mCurrentFolder);
            showPath = mCurrentFolder.replace(mPathHideFrom, "");
        } else {
            showPath = mCurrentFolder;
        }
        mTxtPath.setText(showPath);
    }

    protected void deleteFile(int position) {
        File dir = new File(mCurrentFileList.get(position).path);

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String element : children) {
                new File(dir, element).delete();
            }
        }
        dir.delete();

        mCurrentFileList.remove(position);
    }

    private void viewFile(int index) {
        Intent newIntent = new Intent(Intent.ACTION_VIEW);

        String fileExt = ProtoScriptHelper.fileExt(mCurrentFileList.get(index).name).substring(1);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt);

        String path = ProtoScriptHelper.getAbsolutePathFromRelative(mCurrentFileList.get(index).path);
        Uri uri = Uri.fromFile(new File(path));

        MLog.d(TAG, "Uri " + uri.toString() + " fileExtension " + fileExt + " mimeType " + mimeType);

        newIntent.setDataAndType(uri, mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent j = Intent.createChooser(newIntent, "Choose an application to open with:");
        startActivity(j);
    }

    // load file in editor
    @Subscribe
    public void onEventMainThread(Events.EditorEvent e) {
        if (e.getAction().equals(Events.EDITOR_FILE_CHANGED)) {
            ProtoFile f = e.getProtofile();

            for (int i = 0; i < mCurrentFileList.size(); i++) {
                if (mCurrentFileList.get(i).name == f.name) {
                    MLog.d(TAG, "is modified: " + f.name);
                    // filesModified.put(i, true);
                }
            }
        }
    }

    public void setCurrentFolder(String path) {
        mCurrentFolder = path;
        MLog.d("qq", " " + path);
        getFileList();
        mProjectAdapter.setData(mCurrentFileList);
        mProjectAdapter.notifyDataSetChanged();
    }
}
