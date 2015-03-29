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

package org.protocoder.appApi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.protocoder.R;
import org.protocoder.fragments.NewProjectDialogFragment;
import org.protocoder.projectlist.ProjectItem;
import org.protocoder.projectlist.ProjectListFragment;
import org.protocoder.projectlist.ProjectsPagerAdapter;
import org.protocoder.projectlist.ZoomOutPageTransformer;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.AppRunnerService;
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
    private TextView tProjects;
    private TextView tExamples;

    ProtoScripts(Protocoder protocoder) {
        this.mProtocoder = protocoder;
        init();
    }

    private void selectProjects() {
        tProjects.setTextColor(Color.parseColor("#88000000"));
        tExamples.setTextColor(Color.parseColor("#55000000"));
    }

    private void selectExamples() {
        tProjects.setTextColor(Color.parseColor("#55000000"));
        tExamples.setTextColor(Color.parseColor("#88000000"));
    }


    public void init() {
        //init views
        mProjectPagerAdapter = new ProjectsPagerAdapter(mProtocoder.mActivityContext, mProtocoder.mActivityContext.getSupportFragmentManager());

        // final ProjectSelectorStrip strip = (ProjectSelectorStrip) mProtocoder.mActivityContext.findViewById(R.id.pager_title_strip);

        mViewPager = (ViewPager) mProtocoder.mActivityContext.findViewById(R.id.pager);

        mViewPager.setAdapter(mProjectPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        //final SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) mProtocoder.mActivityContext.findViewById(R.id.sliding_tabs);
        //mSlidingTabLayout.setDistributeEvenly(true);
        //mSlidingTabLayout.setViewPager(mViewPager);

        tProjects = (TextView) mProtocoder.mActivityContext.findViewById(R.id.textProjects);
        tExamples = (TextView) mProtocoder.mActivityContext.findViewById(R.id.textExamples);

        tProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProjects();
                goTo("projects");
            }
        });

        tExamples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectExamples();
                goTo("examples");
            }
        });

        //TODO remove at some point
        //colors
        //final int c0 = mProtocoder.mActivityContext.getResources().getColor(R.color.project_user_color);
        // final int c1 = mProtocoder.mActivityContext.getResources().getColor(R.color.project_example_color);


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int page) {
                switch (page) {
                    case 0:
                        selectProjects();
                        break;
                    case 1:
                        selectExamples();
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                //   int c = AndroidUtils.calculateColor(arg0 + arg1, c0, c1);
                //   groupedToolbar.setBackgroundColor(c);
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

        PUtil util = new PUtil(mProtocoder.mActivityContext);

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

        plf.mProjectAdapter.mProjects.get(pos).selected = true;
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
        plf.mProjectAdapter.mProjects.get(pos).selected = true;
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
        listFragmentBase.mProjectFolder = name;
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
        //close app if open
        if (currentProjectApplicationIntent != null) {
            mProtocoder.mActivityContext.finishActivity(mProjectRequestCode);
            currentProjectApplicationIntent = null;
        }

        if (appName.toLowerCase().endsWith("service")) {
            currentProjectApplicationIntent = new Intent(mProtocoder.mActivityContext, AppRunnerService.class);
            currentProjectApplicationIntent.putExtra(Project.FOLDER, folder);
            currentProjectApplicationIntent.putExtra(Project.NAME, appName);

            mProtocoder.mActivityContext.startService(currentProjectApplicationIntent);
        } else {
            //open new activity
            try {
                currentProjectApplicationIntent = new Intent(mProtocoder.mActivityContext, AppRunnerActivity.class);

                if (AndroidUtils.isVersionLollipop()) {
                    //TODO enable version Android-L
                    //currentProjectApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                } else {
                    currentProjectApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                //settings
                currentProjectApplicationIntent.putExtra(Project.SETTINGS_SCREEN_ALWAYS_ON, mProtocoder.settings.getScreenOn());

                //set the folder and app name to be loaded
                currentProjectApplicationIntent.putExtra(Project.FOLDER, folder);
                currentProjectApplicationIntent.putExtra(Project.NAME, appName);

                currentProjectApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                //currentProjectApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                currentProjectApplicationIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                //load the custom js interpreter if exists with the webide
                currentProjectApplicationIntent.putExtra(Project.PREFIX, EditorManager.getInstance().getCustomJSInterpreterIfExist(mProtocoder.mActivityContext));

                mProtocoder.mActivityContext.overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);
                mProtocoder.mActivityContext.startActivityForResult(currentProjectApplicationIntent, mProjectRequestCode);
            } catch (Exception e) {
                MLog.d(TAG, "Error launching script");
            }
        }
    }

    //TODO
    public void createProject(String folder, String appName) {
        //create file
        Project newProject = ProjectManager.getInstance().addNewProject(mProtocoder.mActivityContext, appName, folder, appName);

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
        FragmentManager fm = mProtocoder.mActivityContext.getSupportFragmentManager();
        NewProjectDialogFragment newProjectDialog = new NewProjectDialogFragment();
        newProjectDialog.show(fm, "fragment_edit_name");
        // implements NewProjectDialogFragment.NewProjectDialogListener
        newProjectDialog.setListener(new NewProjectDialogFragment.NewProjectDialogListener() {
            @Override
            public void onFinishEditDialog(String inputText) {
                Toast.makeText(mProtocoder.mActivityContext, "Creating " + inputText, Toast.LENGTH_SHORT).show();
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
        final ProgressDialog progress = new ProgressDialog(mProtocoder.mActivityContext);
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

        mProtocoder.mActivityContext.startActivity(Intent.createChooser(shareIntent, mProtocoder.mActivityContext.getResources().getText(R.string.share_proto_file)));
    }

    public void shareMainJsDialog(String folder, String name) {
        Project p = ProjectManager.getInstance().get(folder, name);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, ProjectManager.getInstance().getCode(p));
        sendIntent.setType("text/plain");
        mProtocoder.mActivityContext.startActivity(Intent.createChooser(sendIntent, mProtocoder.mActivityContext.getResources().getText(R.string.send_to)));
    }

    public void addShortcut(String folder, String name) {
        Project p = ProjectManager.getInstance().get(folder, name);

        Intent.ShortcutIconResource icon;
        //TODO remove this way of selecting icons
        if (folder.equals("examples")) {
            icon = Intent.ShortcutIconResource.fromContext(mProtocoder.mActivityContext, R.drawable.protocoder_script_example);
        } else {
            icon = Intent.ShortcutIconResource.fromContext(mProtocoder.mActivityContext, R.drawable.protocoder_script_project);
        }

        try {
            Intent shortcutIntent = new Intent(mProtocoder.mActivityContext, AppRunnerActivity.class);
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
            mProtocoder.mActivityContext.sendBroadcast(putShortCutIntent);
        } catch (Exception e) {
            // TODO
        }
        // Show toast
        Toast.makeText(mProtocoder.mActivityContext, "Adding shortcut for " + p.getName(), Toast.LENGTH_SHORT).show();
    }

    public void reinitScriptList() {
        mProjectPagerAdapter.addFragment("", (ProjectListFragment) getFragment(0));
        mProjectPagerAdapter.addFragment("", (ProjectListFragment) getFragment(1));
        mProjectPagerAdapter.notifyDataSetChanged();
    }


    private Fragment getFragment(int position) {
        return mProtocoder.mActivityContext.getSupportFragmentManager().findFragmentByTag(getFragmentTag(position));
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.pager + ":" + position;
    }

    // public void reinitScriptList() {
    //  ProjectListFragment f0 = (ProjectListFragment) mProjectPagerAdapter.getItem(0);
    //  mProjectPagerAdapter.addFragment("", );

    // }
}
