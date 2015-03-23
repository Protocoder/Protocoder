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

package org.protocoder.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import org.protocoder.R;
import org.protocoder.fragments.SettingsFragment;

public class SetPreferenceActivity extends AppBaseActivity {

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        setToolbar();
        setToolbarBack();

        getFragmentManager().beginTransaction().replace(R.id.pref_container, new SettingsFragment()).commit();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case android.R.id.home:
//                // Up button pressed
//                Intent intentHome = new Intent(this, MainActivity.class);
//                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intentHome);
//                overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set, R.anim.splash_slide_out_anim_reverse_set);
//                finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }

}