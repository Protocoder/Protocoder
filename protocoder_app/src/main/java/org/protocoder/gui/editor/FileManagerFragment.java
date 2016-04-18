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

package org.protocoder.gui.editor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.protocoder.R;
import org.protocoder.events.Events;
import org.protocoder.helpers.ProtoScriptHelper;
import org.protocoder.server.model.ProtoFile;
import org.protocoderrunner.base.BaseFragment;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Project;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("NewApi")
public class FileManagerFragment extends BaseFragment {

    private static final String TAG = FileManagerFragment.class.getSimpleName();

    private Menu mMenu;
    public ArrayList<ProtoFile> files;
    public HashMap<Integer, Boolean> filesModified;
    protected FileAdapter projectAdapter;
    protected ListView llFileView;
    private Project mProject;

    public FileManagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            mProject = new Project(bundle.getString(Project.FOLDER), bundle.getString(Project.NAME));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_file_manager, container, false);

        // Get ListView and set adapter
        llFileView = (ListView) v.findViewById(R.id.llFile);

        MLog.d(TAG, "Project " + mProject.getFullPath());

        files = ProtoScriptHelper.listFilesInFolder(mProject.getSandboxPath(), 0);
        filesModified = new HashMap<>();

        // get files
        projectAdapter = new FileAdapter(getActivity());
        llFileView.setEmptyView(v.findViewById(R.id.empty_list_view));

        // set the emptystate
        llFileView.setAdapter(projectAdapter);

        notifyAddedProject();
        registerForContextMenu(llFileView);

        llFileView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View v, final int position, long id) {

            }
        });

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
//		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (getUserVisibleHint()) {
            // Handle menu events and return true
        } else {
            return false; // Pass the event to the next fragment
        }

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        final int index = info.position;

        File file = new File(files.get(index).path);

        int itemId = item.getItemId();
        if (itemId == R.id.menu_project_list_run) {
            return true;
        } else if (itemId == R.id.menu_project_list_view) {
            viewFile(index);
            return true;
        } else if (itemId == R.id.menu_project_list_delete) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // Yes button clicked
                            deleteFile(index);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            return true;
        } else if (itemId == R.id.menu_project_list_share_with) {

            return true;

        } else {
            return super.onContextItemSelected(item);
        }
    }

    /**
     * Delete a file / folder
     */
    protected void deleteFile(int position) {

        File dir = new File(files.get(position).path);

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String element : children) {
                new File(dir, element).delete();
            }
        }
        dir.delete();

        files.remove(position);

        projectAdapter.notifyDataSetChanged();
        llFileView.invalidateViews();
    }

    public void clear() {
        llFileView.removeAllViews();
        projectAdapter.notifyDataSetChanged();
    }

    public void notifyAddedProject() {
        projectAdapter.notifyDataSetChanged();
        llFileView.invalidateViews();
    }

    @Override
    public View getView() {
        return super.getView();
    }

    private void viewFile(int index) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();

        Intent newIntent = new Intent(Intent.ACTION_VIEW);

        String fileExt = fileExt(files.get(index).name).substring(1);
        String mimeType = myMime.getMimeTypeFromExtension(fileExt);

        String path = ProtoScriptHelper.getAbsolutePathFromRelative(files.get(index).path);
        Uri uri = Uri.fromFile(new File(path));

        MLog.d(TAG, "Uri " + uri.toString() + " fileExtension " + fileExt + " mimeType " + mimeType);

        /*

        newIntent.setDataAndType(uri, mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            getActivity().startActivity(newIntent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
        */

        newIntent.setData(uri);
        Intent j = Intent.createChooser(newIntent, "Choose an application to open with:");
        startActivity(j);
    }

    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf("."));
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }

            return ext.toLowerCase();
        }
    }

    public class FileAdapter extends BaseAdapter {
        private final Context mContext;


        public FileAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return files.size();
        }

        @Override
        public Object getItem(int position) {
            return files.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private int getIcon(String type) {
            return type.equals("folder") ? R.drawable.protocoder_script_example: R.drawable.protocoder_script_project;
        }

        // create mContext new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final FileItem customView;

            final ProtoFile f = files.get(position);

            // if it's not recycled, initialize some
            if (convertView == null) {
                customView = new FileItem(mContext);
            } else {
                customView = (FileItem) convertView;
            }

            customView.setImage(getIcon(f.type));

            String prefix = "";
            if (filesModified.containsKey(position)) {
                if (filesModified.get(position)) {
                    prefix = "*";
                }
            }
            customView.setText(prefix + f.name);
            customView.setTag(f.name);
            customView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MLog.d(TAG, "" + f.name);
                    EventBus.getDefault().post(new Events.EditorEvent(Events.EDITOR_FILE_LOAD, mProject, f));
                }
            });

            return customView;
        }
    }


    public class FileItem extends LinearLayout {

        private WeakReference<View> v;
        // private Context c;
        private Context c;

        public FileItem(Context context) {
            super(context);
            this.c = context;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.v = new WeakReference<View>(inflater.inflate(R.layout.view_file_item, this, true));
        }

        public FileItem(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);

        }

        public void setImage(int resId) {
            ImageView imageView = (ImageView) v.get().findViewById(R.id.img_file);
            imageView.setImageResource(resId);
        }

        public void setText(String text) {
            TextView textView = (TextView) v.get().findViewById(R.id.txt_file_name);
            // TextUtils.changeFont(c.get(), textView, ProtocoderFonts.MENU_TITLE);
            textView.setText(text);
        }
    }

    // load file in editor
    @Subscribe
    public void onEventMainThread(Events.EditorEvent e) {
        if (e.getAction().equals(Events.EDITOR_FILE_CHANGED)) {
            ProtoFile f = e.getProtofile();
            for (int i = 0; i < files.size(); i++) {
                if (files.get(i).name == f.name) {
                    filesModified.put(i, true);
                }
            }

        }
    }

}
