/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.protocoderrunner.apprunner.api.other;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;

/**
 * Represents mContext launchable application. An application is made of mContext name (or
 * title), an intent and an icon.
 */
public class ApplicationInfo {
	/**
	 * The application name.
	 */
	@Expose
	public CharSequence title;

	/**
	 * The intent used to start the application.
	 */
	Intent intent;

	/**
	 * The application icon.
	 */
	public Drawable icon;

	/**
	 * When set to true, indicates that the icon has been resized.
	 */
	boolean filtered;

	@Expose
	public String iconURL;

	@Expose
	public String packageName;

	public final void setActivity(ComponentName className, int launchFlags) {
		intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(className);
		intent.setFlags(launchFlags);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ApplicationInfo)) {
			return false;
		}

		ApplicationInfo that = (ApplicationInfo) o;
		return title.equals(that.title)
				&& intent.getComponent().getClassName().equals(that.intent.getComponent().getClassName());
	}

	@Override
	public int hashCode() {
		int result;
		result = (title != null ? title.hashCode() : 0);
		final String name = intent.getComponent().getClassName();
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}
}
