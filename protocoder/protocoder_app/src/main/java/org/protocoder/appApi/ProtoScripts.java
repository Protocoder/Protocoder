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

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import org.protocoder.R;
import org.protocoder.fragments.NewProjectDialogFragment;
import org.protocoder.projectlist.ProjectItem;
import org.protocoder.projectlist.ProjectListFragment;
import org.protocoder.projectlist.ProjectsPagerAdapter;
import org.protocoder.projectlist.ZoomOutPageTransformer;
import org.protocoder.views.ProjectSelectorStrip;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.api.PUtil;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.MLog;

import java.io.File;

public class ProtoScripts {

    private static final String TAG = "ProtoScripts";
    private Protocoder mProtocoder;

    private int mProjectRequestCode = 1;
    private Intent currentProjectApplicationIntent;


    ProjectsPagerAdapter mProjectPagerAdapter;

    // fragments that hold the projects
    private ViewPager mViewPager;

    ProtoScripts(Protocoder protocoder) {
        this.mProtocoder = protocoder;
        init();
    }

    public void init() {
        //init views
        mProjectPagerAdapter = new ProjectsPagerAdapter(mProtocoder.a.getSupportFragmentManager());

        final ProjectSelectorStrip strip = (ProjectSelectorStrip) mProtocoder.a.findViewById(R.id.pager_title_strip);

        mViewPager = (ViewPager) mProtocoder.a.findViewById(R.id.pager);
        mViewPager.setAdapter(mProjectPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());


        //TODO remove at some point
        //colors
        final int c0 = mProtocoder.a.getResources().getColor(R.color.project_user_color);
        final int c1 = mProtocoder.a.getResources().getColor(R.color.project_example_color);


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                int c = AndroidUtils.calculateColor(arg0 + arg1, c0, c1);
                strip.setBackgroundColor(c);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public java.util.ArrayList<Project> getProjectsInFolder(String folder) {
        return ProjectManager.getInstance().list(folder, true);
    }

    public void goTo(String folder) {
        int num = mProjectPagerAdapter.getFragmentNumByName(folder);
        mViewPager.setCurrentItem(num, true);
    }

    public void goTo(String folder, String appName) {
        goTo(folder);
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        final int id = plf.findAppPosByName(appName);

        PUtil util = new PUtil(mProtocoder.a);

        util.delay(200, new PUtil.delayCB() {
            @Override
            public void event() {
                plf.goTo(id);
            }
        });
    }

    public void highlight(String folder, String appName) {
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        ProjectItem view = (ProjectItem) getViewByName(folder, appName);
        int pos = plf.findAppPosByName(appName);

        plf.projectAdapter.projects.get(pos).selected = true;
        view.setHighlighted(true);
    }


    public void resetHighlighting(String folder) {
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        plf.resetHighlighting();
    }

    public View getViewByName(String folder, String appName) {
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        ProjectItem view = (ProjectItem) plf.getView(appName);

        int pos = plf.findAppPosByName(appName);
        //->
        plf.projectAdapter.projects.get(pos).selected = true;
        view.setHighlighted(true);


        return view;
    }


    public void goNext() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    public void goPrevious() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }



    public void addScriptList(int icon, String name, int color, boolean orderByName) {
        ProjectListFragment listFragmentBase = ProjectListFragment.newInstance(icon, name, color, orderByName);
        listFragmentBase.icon = icon;
        listFragmentBase.projectFolder = name;
        listFragmentBase.color = color;
        listFragmentBase.orderByName = orderByName;

        mProjectPagerAdapter.addFragment(name, listFragmentBase);
        mProjectPagerAdapter.notifyDataSetChanged();
    }

    public void refresh(String folder, String appName) {
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        plf.projectRefresh(appName);
    }

    public void rename(String folder, String appName) {

    }

    public void run(String folder, String appName) {
        if (currentProjectApplicationIntent != null) {
            mProtocoder.a.finishActivity(mProjectRequestCode);
            currentProjectApplicationIntent = null;
        }

        try {
            currentProjectApplicationIntent = new Intent(mProtocoder.a, AppRunnerActivity.class);

            if (AndroidUtils.isVersionL()) {
                //TODO enable version Android-L
                //currentProjectApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            } else {
                currentProjectApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            currentProjectApplicationIntent.putExtra(Project.FOLDER, folder);
            currentProjectApplicationIntent.putExtra(Project.NAME, appName);

            mProtocoder.a.overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);
            mProtocoder.a.startActivityForResult(currentProjectApplicationIntent, mProjectRequestCode);
        } catch (Exception e) {
            MLog.d(TAG, "Error launching script");
        }
    }

    //TODO
    public void createProject(String folder, String appName) {
        //create file
        Project newProject = ProjectManager.getInstance().addNewProject(mProtocoder.a, appName, folder, appName);

        //notify ui
        final ProjectListFragment plf = mProjectPagerAdapter.getFragmentByName(folder);
        plf.mProjects.add(newProject);
        plf.notifyAddedProject();
    }

    public void delete(String folder, String appName) {

    }

    public void createFileName(String folder, String appName, String fileName) {

    }
    public void deleteFileName(String folder, String appName, String fileName) {

    }
    public String exportProto(String folder, String fileName) {
        Project p = ProjectManager.getInstance().get(folder, fileName);
        String zipFilePath = ProjectManager.getInstance().createBackup(p);

        return zipFilePath;
    }

    public void createProjectDialog() {
        FragmentManager fm = mProtocoder.a.getSupportFragmentManager();
        NewProjectDialogFragment newProjectDialog = new NewProjectDialogFragment();
        newProjectDialog.show(fm, "fragment_edit_name");
        // implements NewProjectDialogFragment.NewProjectDialogListener
        newProjectDialog.setListener(new NewProjectDialogFragment.NewProjectDialogListener() {
            @Override
            public void onFinishEditDialog(String inputText) {
                Toast.makeText(mProtocoder.a, "Creating " + inputText, Toast.LENGTH_SHORT).show();
                createProject(ProjectManager.FOLDER_USER_PROJECTS, inputText);
            }
        });
    }


    public void listRefresh() {
        for (ProjectListFragment fragment : mProjectPagerAdapter.fragments) {
            fragment.refreshProjects();
        }
    }

    public void shareProtoFileDialog(String folder, String name) {
        final ProgressDialog progress = new ProgressDialog(mProtocoder.a);
        progress.setTitle("Exporting .proto");
        progress.setMessage("Your project will be ready soon!");
        progress.setCancelable(true);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        String zipFilePath = exportProto(folder, name);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(zipFilePath)));
        shareIntent.setType("application/zip");

        progress.dismiss();

        mProtocoder.a.startActivity(Intent.createChooser(shareIntent, mProtocoder.a.getResources().getText(R.string.share_proto_file)));
    }

    public void shareMainJsDialog(String folder, String name) {
        Project p = ProjectManager.getInstance().get(folder, name);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, ProjectManager.getInstance().getCode(p));
        sendIntent.setType("text/plain");
        mProtocoder.a.startActivity(Intent.createChooser(sendIntent, mProtocoder.a.getResources().getText(R.string.send_to)));
    }

    public void addShortcut(String folder, String name) {
        Project p = ProjectManager.getInstance().get(folder, name);

        Intent.ShortcutIconResource icon;
        //TODO remove this way of selecting icons
        if (folder.equals("examples")) {
            icon = Intent.ShortcutIconResource.fromContext(mProtocoder.a, R.drawable.protocoder_script_example);
        } else {
            icon = Intent.ShortcutIconResource.fromContext(mProtocoder.a, R.drawable.protocoder_script_project);
        }

        try {
            Intent shortcutIntent = new Intent(mProtocoder.a, AppRunnerActivity.class);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            String script = ProjectManager.getInstance().getCode(p);
            shortcutIntent.putExtra(Project.NAME, p.getName());
            shortcutIntent.putExtra(Project.FOLDER, p.getFolder());

            final Intent putShortCutIntent = new Intent();

            putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, p.getName());
            putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            putShortCutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            mProtocoder.a.sendBroadcast(putShortCutIntent);
        } catch (Exception e) {
            // TODO
        }
        // Show toast
        Toast.makeText(mProtocoder.a, "Adding shortcut for " + p.getName(), Toast.LENGTH_SHORT).show();
    }

    public void reinitScriptList() {
        mProjectPagerAdapter.addFragment("", (ProjectListFragment) getFragment(0));
        mProjectPagerAdapter.addFragment("", (ProjectListFragment) getFragment(1));
        mProjectPagerAdapter.notifyDataSetChanged();
    }



    private Fragment getFragment(int position){
        return mProtocoder.a.getSupportFragmentManager().findFragmentByTag(getFragmentTag(position));
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.pager + ":" + position;
    }

    // public void reinitScriptList() {
      //  ProjectListFragment f0 = (ProjectListFragment) mProjectPagerAdapter.getItem(0);
      //  mProjectPagerAdapter.addFragment("", );

   // }
}
