package com.makewithmoto;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ProjectsPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment fragmentProjects;
    private Fragment fragmentExamples;

    public ProjectsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        Fragment f;

        if (i == 0) {
            f = fragmentProjects;
        } else {
            f = fragmentExamples;
        }
        return f;
        // return fragment;
    }

    @Override
    public int getCount() {
        // For this contrived example, we have a 100-object collection.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String r;

        if (position == 0) {
            r = "My Projects";
        } else {
            r = "Examples";
        }

        return r;
    }

    public void setProjectsFragment(Fragment f) {
        fragmentProjects = f;

    }

    public void setExamplesFragment(Fragment f) {
        fragmentExamples = f;
    }
}