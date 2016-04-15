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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.protocoder.R;
import org.protocoder.gui._components.BaseWebviewFragment;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Project;

public class EditorActivity extends BaseActivity {

    private final String TAG = EditorActivity.class.getSimpleName();

    // TODO change this dirty hack
    private Menu mMenu;
    private static final int MENU_RUN = 8;
    private static final int MENU_SAVE = 9;
    private static final int MENU_BACK = 10;
    private static final int MENU_FILES = 11;
    private static final int MENU_API = 12;

    private EditorFragment editorFragment;
    private FileManagerFragment fileFragment;
    private BaseWebviewFragment webviewFragment;

    // drawers
    private boolean showFilesDrawer = true;

    private boolean showAPIDrawer = true;
    private Project mCurrentProject;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        setupActivity();

        //setToolbarBack();

        // Get the bundle and pass it to the fragment.
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        if (null != intent) {
            String folder = intent.getStringExtra(Project.FOLDER);
            String name = intent.getStringExtra(Project.NAME);
            bundle.putString(Project.FOLDER, folder);
            bundle.putString(Project.NAME, name);
            mCurrentProject = new Project(folder, name);
        }

        // add editor fragment
        editorFragment = EditorFragment.newInstance(bundle);
        FrameLayout fl = (FrameLayout) findViewById(R.id.pref_container);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fl.getId(), editorFragment, String.valueOf(fl.getId()));
        ft.commit();
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        menu.clear();
        menu.add(1, MENU_RUN, 0, "Run").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(1, MENU_SAVE, 0, "Save").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(1, MENU_FILES, 0, "Files").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(1, MENU_API, 0, "API").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case MENU_RUN:
                editorFragment.saveProject();
                editorFragment.run();

                return true;

            case MENU_SAVE:
                editorFragment.saveProject();

                return true;

            case MENU_FILES:
                MenuItem menuFiles = mMenu.findItem(MENU_FILES).setChecked(showFilesDrawer);

                showFileManagerDrawer(showFilesDrawer);
                showFilesDrawer = !showFilesDrawer;

                return true;

            case MENU_API:
                MenuItem menuApi = mMenu.findItem(MENU_API).setChecked(showFilesDrawer);
                showAPIDrawer(showAPIDrawer);
                showAPIDrawer = !showAPIDrawer;

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *  Toggle API drawer
     */
    public void showAPIDrawer(boolean b) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_out_left,
                R.anim.slide_out_left);

        if (b) {
            webviewFragment = new BaseWebviewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("url", "http://localhost:8585/reference.html");
            webviewFragment.setArguments(bundle);
            ft.add(R.id.fragmentWebview, webviewFragment).addToBackStack(null);

            editorFragment.getView().animate().translationX(-50).setDuration(500).start();
        } else {
            editorFragment.getView().animate().translationX(0).setDuration(500).start();
            ft.remove(webviewFragment);
        }

        ft.commit();
    }

    public void showFileManagerDrawer(boolean b) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left);

        if (b) {
            fileFragment = new FileManagerFragment();
            Bundle bundle = new Bundle();

            bundle.putString(Project.NAME, mCurrentProject.name);
            bundle.putString(Project.FOLDER, mCurrentProject.folder);

            fileFragment.setArguments(bundle);
            ft.add(R.id.fragmentFileManager, fileFragment).addToBackStack(null);
            editorFragment.getView().animate().translationX(-50).setDuration(500).start();

        } else {
            editorFragment.getView().animate().translationX(0).setDuration(500).start();
            ft.remove(fileFragment);
        }

        ft.commit();
    }

    public void addAFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // startActivityForResult(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        MLog.d(TAG, "");
    }
}