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

package org.protocoder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.protocoder.appinterpreter.AppRunnerCustom;
import org.protocoder.appinterpreter.ProtocoderApp;
import org.protocoder.qq.FolderChooserFragment;
import org.protocoder.qq.IntroductionFragment;
import org.protocoder.qq.ProjectListFragment;
import org.protocoder.server.ProtocoderServerService;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.MLog;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected AppRunnerCustom appRunner;

    //ui
    private Toolbar mToolbar;
    private ProjectListFragment mListFragmentBase;
    private FolderChooserFragment mFolderChooserFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Setup the ui
         */
        setupToolbar();
        addProjectFolderChooser(savedInstanceState);
        addProjectListFragment(savedInstanceState);
        showIntroduction(savedInstanceState);

        /*
         * init custom appRunner
         */
        appRunner = new AppRunnerCustom(this);
        appRunner.initDefaultObjects().initInterpreter();
        ProtocoderApp protocoderApp = new ProtocoderApp(appRunner);
        protocoderApp.network.checkVersion();
        appRunner.interp.eval("device.vibrate(1000);");

        /*
         * Servers
         */
        startBroadCastReceiver();
        startServers();
    }


    //This broadcast will receive JS commands if is in debug mode, useful to debug the app through adb
    private void startBroadCastReceiver() {
        if (ProtocoderAppSettings.DEBUG) {
            //execute commands from intents
            //ie: adb shell am broadcast -a org.protocoder.intent.EXECUTE --es cmd "device.vibrate(100)"
            BroadcastReceiver recv = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String cmd = intent.getStringExtra("cmd");
                    MLog.d(TAG, "executing >> " + cmd);
                    appRunner.interp.eval(cmd);
                }
            };

            IntentFilter filterSend = new IntentFilter();
            filterSend.addAction("org.protocoder.intent.EXECUTE");
            registerReceiver(recv, filterSend);
        }
    }

    private void setupToolbar() {
        // Create the action bar programmatically
        if (!AndroidUtils.isWear(this)) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        }
    }

    //Project folder chooser, ATM just a spinner
    private void addProjectFolderChooser(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            //add script list fragment
            FrameLayout fl = (FrameLayout) findViewById(R.id.fragmentFolderChooser);
            mFolderChooserFragment = FolderChooserFragment.newInstance(ProjectManager.FOLDER_EXAMPLES, true);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(fl.getId(), mFolderChooserFragment, String.valueOf(fl.getId()));
            ft.commit();
        } else {
            // mProtocoder.protoScripts.reinitScriptList();
        }
    }

    //add the project list fragment
    private void addProjectListFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            //add script list fragment
            FrameLayout fl = (FrameLayout) findViewById(R.id.fragmentScriptList);
            mListFragmentBase = ProjectListFragment.newInstance(ProjectManager.FOLDER_EXAMPLES, true);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(fl.getId(), mListFragmentBase, String.valueOf(fl.getId()));
            ft.commit();
        } else {
            // mProtocoder.protoScripts.reinitScriptList();
        }

    }

    private void showIntroduction(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            //add script list fragment
            FrameLayout fl = (FrameLayout) findViewById(R.id.fragmentIntroduction);

            IntroductionFragment introductionFragment = IntroductionFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(fl.getId(), introductionFragment, String.valueOf(fl.getId()));
            ft.commit();

        } else {
            // mProtocoder.protoScripts.reinitScriptList();
        }
    }

    private void startServers() {
        MLog.d(TAG, "starting servers");
        Intent serverIntent = new Intent(this, ProtocoderServerService.class);
        //serverIntent.putExtra(Project.FOLDER, folder);
        startService(serverIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterViewCompat.AdapterContextMenuInfo info = (AdapterViewCompat.AdapterContextMenuInfo) item.getMenuInfo();

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            return true;
        } else if (itemId == R.id.menu_new) {
            //createProjectDialog();
            return true;
        } else if (itemId == R.id.menu_help) {
            //this.showHelp(true);
            return true;
        } else if (itemId == R.id.menu_settings) {
            //Protocoder.getInstance(this).app.showSettings(true);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // execute lines
    public void onEventMainThread(Events.ExecuteCodeEvent evt) {
        String code = evt.getCode();
        MLog.d(TAG, "event -> " + code);

        //TODO apprunner
        // if (debugApp) {
        //     interp.eval(code);
        // }
    }

}
