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

package org.protocoder.projectlist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.Vector;

public class ProjectsPagerAdapter extends FragmentPagerAdapter {

	public Vector<ProjectListFragment> fragments;
    public Vector<String> fragmentNames;

    private String TAG = "ProjectsPagerAdapter";

    public ProjectsPagerAdapter(FragmentManager fm) {
		super(fm);

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