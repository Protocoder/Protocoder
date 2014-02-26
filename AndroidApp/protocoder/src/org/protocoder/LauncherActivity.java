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
import org.protocoder.utils.StrUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LauncherActivity extends BaseActivity {

	Intent intent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Prepare intent to exit the activity and move to the main one
		boolean firstLaunch; // If this is the first time the
		SharedPreferences userDetails = getSharedPreferences("org.protocoder", MODE_PRIVATE);
		firstLaunch = userDetails.getBoolean(getResources().getString(R.string.pref_is_first_launch), true);

		if (firstLaunch) {
			intent = new Intent(this, WelcomeActivity.class);
			userDetails.edit().putString("device_id", StrUtils.generateRandomString());
		} else {
			intent = new Intent(this, MainActivity.class);
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
		finish();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
