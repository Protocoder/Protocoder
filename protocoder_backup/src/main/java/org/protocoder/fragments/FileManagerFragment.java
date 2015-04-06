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

package org.protocoder.fragments;

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
import android.widget.Toast;

import org.protocoder.R;
import org.protocoderrunner.base.BaseFragment;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

@SuppressLint("NewApi")
public class FileManagerFragment extends BaseFragment {

    public ArrayList<File> files;
    protected FileAdapter projectAdapter;
    protected ListView llFileView;
    private String name;
    private String folder;

    public FileManagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            this.name = bundle.getString(Project.NAME);
            this.folder = bundle.getString(Project.FOLDER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_file_manager, container, false);

        // Get ListView and set adapter
        llFileView = (ListView) v.findViewById(R.id.llFile);
        Project p = ProjectManager.getInstance().get(folder, name);
        files = ProjectManager.getInstance().listFilesInProject(p);

        // get files
        projectAdapter = new FileAdapter(getActivity(), folder, files);
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

    protected void deleteFile(int position) {

        File dir = new File(files.get(position).getAbsolutePath());

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.file_list, menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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

        File file = files.get(index);

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

    private void viewFile(int index) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();

        Intent newIntent = new Intent(android.content.Intent.ACTION_VIEW);

        // Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(fileExt(files.get(index).getName()).substring(1));
        newIntent.setDataAndType(Uri.fromFile(files.get(index)), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            getActivity().startActivity(newIntent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }

        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.setData(Uri.fromFile(files.get(index)));
        Intent j = Intent.createChooser(myIntent, "Choose an application to open with:");
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
//		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class FileAdapter extends BaseAdapter {
        private final Context mContext;

        ArrayList<File> files;
        private final String projectFolder;

        public FileAdapter(Context c, String projectFolder, ArrayList<File> files) {
            mContext = c;
            this.files = files;
            this.projectFolder = projectFolder;
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

        // create mContext new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final FileItem customView;

            if (convertView == null) { // if it's not recycled, initialize some
                // attributes
                customView = new FileItem(mContext);
                customView.setImage(R.drawable.protocoder_script_project);

                customView.setText(files.get(position).getName());

            } else {
                customView = (FileItem) convertView;
                customView.setText(files.get(position).getName());
            }
            customView.setTag(files.get(position).getName());

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
            // TextUtils.changeFont(c.get(), textView, Fonts.MENU_TITLE);
            textView.setText(text);
        }
    }

}
