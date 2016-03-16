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
import android.widget.FrameLayout;

import org.protocoder.R;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.models.Project;

public class EditorActivity extends BaseActivity {

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        //setToolbar();
        //setToolbarBack();

         /*
         * Get the bundle and pass it to the fragment.
          */
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        if (null != intent) {
            bundle.putString(Project.NAME, intent.getStringExtra(Project.NAME));
            bundle.putString(Project.FOLDER, intent.getStringExtra(Project.FOLDER));
        }

        EditorFragment editorFragment = EditorFragment.newInstance(bundle);
        FrameLayout fl = (FrameLayout) findViewById(R.id.pref_container);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fl.getId(), editorFragment, String.valueOf(fl.getId()));
        ft.commit();

    }

}