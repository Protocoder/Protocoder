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

package org.protocoder.apprunner.api;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.protocoder.PrefsFragment;
import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apidoc.annotation.APIRequires;
import org.protocoder.apidoc.annotation.APIVersion;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.apprunner.PInterface;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.apprunner.api.other.ApplicationInfo;
import org.protocoder.apprunner.api.other.PDeviceEditor;
import org.protocoder.apprunner.api.other.PWebEditor;
import org.protocoder.apprunner.api.other.PProtocoderFeedback;
import org.protocoder.events.ProjectManager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class PProtocoder extends PInterface {

	public String id;

	public PProtocoder(Activity a) {
		super(a);

		// get apprunner settings
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(a);
		id = sharedPrefs.getString("pref_id", "-1");
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public String getId() {
		return PrefsFragment.getId(a.get());

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "id" })
	public void setId(String id) {
		PrefsFragment.setId(a.get(), id);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { })
	public PProtocoderFeedback liveCoding() {
		a.get().initLayout();

		PProtocoderFeedback l = a.get().liveCoding;
		l.enable = true;

		return l;
	}


    @ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { })
    public PWebEditor webEditor() {
        a.get().initLayout();

        PWebEditor pWebEditor = new PWebEditor(a.get());

        return pWebEditor;
    }


    @ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { })
    public PDeviceEditor deviceEditor() {
        a.get().initLayout();

        PDeviceEditor pEditor = new PDeviceEditor(a.get());

        return pEditor;
    }



    @ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "name", "type" })
	@APIVersion(minLevel = "2")
	@APIRequires("android.permission.INTERNET")
	public void launchScript(String name, String typeString) {
		Intent intent = new Intent(a.get(), AppRunnerActivity.class);
		intent.putExtra("projectName", name);

		int type = -1;
		if (typeString.equals("examples")) {
			type = ProjectManager.PROJECT_EXAMPLE;
		} else if (typeString.equals("projects")) {
			type = ProjectManager.PROJECT_USER_MADE;
		}
		intent.putExtra("projectType", type);
		// a.get().startActivity(intent);
		// String code = StrUtils.generateRandomString();
		a.get().startActivityForResult(intent, 22);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "code" })
	public void eval(String code) {
		a.get().interp.eval(code);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIVersion(minLevel = "2")
	@APIRequires("android.permission.INTERNET")
	public void returnValueToScript(String returnValue) {
		Intent output = new Intent();
		output.putExtra("return", returnValue);
		a.get().setResult(22, output);
		a.get().finish();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void returnResult(String data) {

		Bundle conData = new Bundle();
		conData.putString("param_result", data);
		Intent intent = new Intent();
		intent.putExtras(conData);
		a.get().setResult(a.get().RESULT_OK, intent);
		a.get().finish();

	}

    @ProtocoderScript
    @APIMethod(description = "", example = "")
	public String getVersionName() {
		PackageInfo pInfo = null;
		try {
			pInfo = a.get().getPackageManager().getPackageInfo(a.get().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return pInfo.versionName;
	}

    @ProtocoderScript
    @APIMethod(description = "", example = "")
	public int getVersionCode() {
		PackageInfo pInfo = null;
		try {
			pInfo = a.get().getPackageManager().getPackageInfo(a.get().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return pInfo.versionCode;
	}



	public static ArrayList<ApplicationInfo> mApplications;

	/**
	 * Loads the list of installed applications in mApplications.
	 */
	private void loadApplications(boolean isLaunching) {
		if (isLaunching && mApplications != null) {
			return;
		}

		PackageManager manager = a.get().getPackageManager();

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

		if (apps != null) {
			final int count = apps.size();

			if (mApplications == null) {
				mApplications = new ArrayList<ApplicationInfo>(count);
			}
			mApplications.clear();

			for (int i = 0; i < count; i++) {
				ApplicationInfo application = new ApplicationInfo();
				ResolveInfo info = apps.get(i);

				application.title = info.loadLabel(manager);
				application.packageName = info.activityInfo.packageName;
				Log.d("qq", "qq " + application.packageName);
				application.setActivity(new ComponentName(info.activityInfo.applicationInfo.packageName,
						info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				application.icon = info.activityInfo.loadIcon(manager);

				Bitmap bitmap = ((BitmapDrawable) application.icon).getBitmap();

				// Bitmap icon =
				// BitmapFactory.decodeResource(this.getResources(),
				// application.icon);

				String path = Environment.getExternalStorageDirectory().toString();
				application.iconURL = path + "/" + application.packageName + ".png";

				try {
					FileOutputStream out = new FileOutputStream(application.iconURL);
					bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				} catch (Exception e) {
					e.printStackTrace();
				}

				mApplications.add(application);
			}
		}
	}

}