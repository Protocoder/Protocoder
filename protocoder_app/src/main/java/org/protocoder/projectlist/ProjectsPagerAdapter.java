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

package org.protocoder.projectlist;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.Vector;

public class ProjectsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    public Vector<ProjectListFragment> fragments;
    public Vector<String> fragmentNames;

    private String TAG = "ProjectsPagerAdapter";

    public ProjectsPagerAdapter(Context c, FragmentManager fm) {
        super(fm);

        mContext = c;

        fragments = new Vector<>();
        fragmentNames = new Vector<>();

    }

    @Override
    public Fragment getItem(int i) {
        ProjectListFragment f = fragments.get(i);

        return f;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String name = fragments.get(position).mProjectFolder;
        return name;
    }

    public void addFragment(String name, ProjectListFragment f) {
        fragments.add(f);
        fragmentNames.add(name);
    }

    public int getFragmentNumByName(String fragmentName) {
        int num = -1;

        for (int i = 0; i < fragments.size(); i++) {
            String name = fragments.get(i).mProjectFolder;
            if (name.equals(fragmentName)) {
                num = i;
                break;
            }
        }

        return num;
    }

    public ProjectListFragment getFragmentByName(String folder) {
        int loc = getFragmentNumByName(folder);
        return fragments.get(loc);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }


}