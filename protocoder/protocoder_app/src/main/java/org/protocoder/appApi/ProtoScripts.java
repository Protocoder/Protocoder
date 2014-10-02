/*
 * Protocoder
 * A prototyping platform for Android devices
 *
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package org.protocoder.appApi;

import android.content.Intent;
import android.support.v4.view.ViewPager;

import org.protocoder.MainActivity;
import org.protocoder.R;
import org.protocoder.projectlist.ListFragmentBase;
import org.protocoder.projectlist.ProjectsPagerAdapter;
import org.protocoder.views.ProjectSelectorStrip;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.MLog;

import java.util.HashMap;
import java.util.Vector;

public class ProtoScripts {

    private static final String TAG = "ProtoScripts";
    private final Protocoder protocoder;

    private final int mProjectRequestCode = 1;
    private Intent currentProjectApplicationIntent;


    ProjectsPagerAdapter mProjectPagerAdapter;

    // fragments that hold the projects
    private HashMap<String, ListFragmentBase> mFragmentList;

    ProtoScripts(Protocoder protocoder) {
        this.protocoder = protocoder;
        mFragmentList = new HashMap<>();

        //init views
        ViewPager mViewPager;

        MainActivity ac = (MainActivity) protocoder.a;
        mProjectPagerAdapter = new ProjectsPagerAdapter(ac.getSupportFragmentManager());
        final ProjectSelectorStrip strip = (ProjectSelectorStrip) protocoder.a.findViewById(R.id.pager_title_strip);

        mViewPager = (ViewPager) protocoder.a.findViewById(R.id.pager);
        mViewPager.setAdapter(mProjectPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                //int c = AndroidUtils.calculateColor(arg0 + arg1, c0, c1);
                //strip.setBackgroundColor(c);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void goTo(String folder) {

    }
    public void highlight(String folder, String appName) {

    }

    public void addScriptList(Intent.ShortcutIconResource icon, String name, int color, boolean orderByName) {
        ListFragmentBase listFragmentBase = new ListFragmentBase();
        listFragmentBase.icon = icon;
        listFragmentBase.projectFolder = name;
        listFragmentBase.color = color;
        listFragmentBase.orderByName = orderByName;

        mFragmentList.put(name, listFragmentBase);
        mProjectPagerAdapter.addFragment(listFragmentBase);
        mProjectPagerAdapter.notifyDataSetChanged();
    }

    public void refresh(String folder, String appName) {
        MLog.d(TAG, "refresh project " + appName);
        mFragmentList.get(folder).projectRefresh(appName);
    }

    public void rename(String folder, String appName) {

    }
    public void goTo(String folder, String appName) {

    }
    public void run(String folder, String appName) {
        if (currentProjectApplicationIntent != null) {
            protocoder.a.finishActivity(mProjectRequestCode);
            currentProjectApplicationIntent = null;
        }

        try {
            currentProjectApplicationIntent = new Intent(protocoder.a, AppRunnerActivity.class);

            if (AndroidUtils.isVersionL()) {
                //TODO enable version Android-L
                //currentProjectApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            } else {
                currentProjectApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            currentProjectApplicationIntent.putExtra(Project.FOLDER, folder);
            currentProjectApplicationIntent.putExtra(Project.NAME, appName);

            protocoder.a.startActivityForResult(currentProjectApplicationIntent, mProjectRequestCode);
        } catch (Exception e) {
            MLog.d(TAG, "Error launching script");
        }
    }


    public void create(String folder, String appName) {
        Project newProject = ProjectManager.getInstance().addNewProject(protocoder.a, appName, folder, appName);

        ListFragmentBase f = mFragmentList.get(folder);
        f.projects.add(newProject);
        f.notifyAddedProject();
    }

    public void delete(String folder, String appName) {

    }
    public void add(String folder, String packageName) {

    }
    public void createFileName(String folder, String appName, String fileName) {

    }
    public void deleteFileName(String folder, String appName, String fileName) {

    }
    public void exportProto(String folder, String fileName) {

    }


    public void listRefresh() {
        MLog.d(TAG, "update list");
        for (ListFragmentBase l : mFragmentList.values()) {
            l.refreshProjects();
        }
    }
}
