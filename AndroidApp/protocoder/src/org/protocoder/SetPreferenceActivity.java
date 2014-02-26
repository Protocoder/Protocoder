/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
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

package org.protocoder;

import org.protocoder.base.BaseActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class SetPreferenceActivity extends BaseActivity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

		// If a preference needs to be default true, we need this hack to make
		// sure it is hard set
		// This is to work around the Android bug:
		// http://code.google.com/p/android/issues/detail?id=6641
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit()
				.putBoolean(getResources().getString(R.string.pref_curtain_notifications),
						prefs.getBoolean(getResources().getString(R.string.pref_curtain_notifications), true)).commit();

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			// Up button pressed
			Intent intentHome = new Intent(this, MainActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentHome);
			overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set, R.anim.splash_slide_out_anim_reverse_set);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

}
