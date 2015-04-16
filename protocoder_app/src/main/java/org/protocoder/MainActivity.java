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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import org.protocoder.appinterpreter.AppRunnerCustom;
import org.protocoder.appinterpreter.ProtocoderApp;
import org.protocoder.qq.ProjectListFragment;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected AppRunnerCustom appRunner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        final String[] arraySpinner = new String[]{
                "Projects", "Examples",
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MLog.d(TAG, "clicked on " + arraySpinner[position]);
            }

        });


        //add script list fragment
        FrameLayout fl = (FrameLayout) findViewById(R.id.fragmentScriptList);
        ProjectListFragment listFragmentBase = ProjectListFragment.newInstance(ProjectManager.FOLDER_EXAMPLES, true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fl.getId(), listFragmentBase, String.valueOf(fl.getId()));
        ft.commit();

        if (savedInstanceState == null) {
         //   addFragments();
        } else {
           // mProtocoder.protoScripts.reinitScriptList();
        }

        //init appRunner
        appRunner = new AppRunnerCustom(this);
        appRunner.initDefaultObjects().initInterpreter();
        ProtocoderApp protocoderApp = new ProtocoderApp(appRunner);
        protocoderApp.network.checkVersion();

        appRunner.interp.eval("device.vibrate(1000);");



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

        startServers();
    }

    private void startServers() {
        MLog.d(TAG, "starting servers");
        Intent serverIntent = new Intent(this, ProtocoderServerService.class);
        //serverIntent.putExtra(Project.FOLDER, folder);
        startService(serverIntent);
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

    // TODO call intent and kill it in an appropiate way
    public void onEventMainThread(Events.ProjectEvent evt) {
        // Using transaction so the view blocks
        MLog.d(TAG, "event -> " + evt.getAction());

        if (evt.getAction() == "run") {
            Project p = evt.getProject();
            //mProtocoder.protoScripts.run(p.getFolder(), p.getName());
        }
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
