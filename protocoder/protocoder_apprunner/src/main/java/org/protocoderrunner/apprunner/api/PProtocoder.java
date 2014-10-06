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

package org.protocoderrunner.apprunner.api;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apidoc.annotation.APIRequires;
import org.protocoderrunner.apidoc.annotation.APIVersion;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.other.ApplicationInfo;
import org.protocoderrunner.apprunner.api.other.PDeviceEditor;
import org.protocoderrunner.apprunner.api.other.PWebEditor;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * This class only contains methods used during app creation. These methods shouldnt be used
 * once the script is done and won't be able to be used once the app is exported.
 *
 */
public class PProtocoder extends PInterface {

	public String id;

	public PProtocoder(Activity a) {
		super(a);

		// get org.apprunner settings
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(a);
		id = sharedPrefs.getString("pref_id", "-1");
	}

    //TODO enable this after refactor
//	@ProtocoderScript
//	@APIMethod(description = "", example = "")
//	public String getId() {
//		return PrefsFragment.getId(a.get());
//
//	}

//	@ProtocoderScript
//	@APIMethod(description = "", example = "")
//	@APIParam(params = { "id" })
//	public void setId(String id) {
//		PrefsFragment.setId(a.get(), id);
//	}

    //TODO this is a place holder
   // @ProtocoderScript
    @APIMethod(description = "Returns an object to manipulate the device app webIDE", example = "")
    @APIParam(params = { })
    public PWebEditor webEditor() {
        PWebEditor pWebEditor = new PWebEditor(a.get());

        return pWebEditor;
    }


  //  @ProtocoderScript
    @APIMethod(description = "Returns an object to manipulate the device app", example = "")
    @APIParam(params = { })
    public PDeviceEditor deviceEditor() {
        appRunnerActivity.get().initLayout();

        PDeviceEditor pEditor = new PDeviceEditor(a.get());

        return pEditor;
    }



    @ProtocoderScript
	@APIMethod(description = "Launch another script given its name and type", example = "")
	@APIParam(params = { "folder", "name" })
	@APIVersion(minLevel = "2")
	@APIRequires("android.permission.INTERNET")
	public void launchScript(String folder, String name) {
		Intent intent = new Intent(a.get(), AppRunnerActivity.class);
		intent.putExtra(Project.FOLDER, name);
		intent.putExtra(Project.NAME, name);

		// a.get().startActivity(intent);
		// String code = StrUtils.generateRandomString();
		appRunnerActivity.get().startActivityForResult(intent, 22);
	}

    //TODO this is a place holder
	//@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIVersion(minLevel = "2")
	@APIRequires("android.permission.INTERNET")
	public void returnValueToScript(String returnValue) {
		Intent output = new Intent();
		output.putExtra("return", returnValue);
		appRunnerActivity.get().setResult(22, output);
		appRunnerActivity.get().finish();
	}


    @ProtocoderScript
    @APIMethod(description = "", example = "")
    public void returnResult(String data) {

        Bundle conData = new Bundle();
        conData.putString("param_result", data);
        Intent intent = new Intent();
        intent.putExtras(conData);
        appRunnerActivity.get().setResult(appRunnerActivity.get().RESULT_OK, intent);
        appRunnerActivity.get().finish();

    }

    // --------- addDebugger ---------//
    public interface AddDebuggerCB {
        void data(String debuggableScript);
    }

	@ProtocoderScript
	@APIMethod(description = "Add a debugger to the execution", example = "")
	@APIVersion(minLevel = "2")
	@APIRequires("android.permission.INTERNET")
	public void addDebugger(final AddDebuggerCB cb) {

        Debugger debugger = new Debugger() {
            @Override
            public void handleCompilationDone(Context context, DebuggableScript debuggableScript, String s) {
                //cb.data("qq");
                MLog.network(a.get(), TAG, "" + debuggableScript.getFunctionName());
            }

            @Override
            public DebugFrame getFrame(Context context, DebuggableScript debuggableScript) {
                //cb.data("qq");
                MLog.network(a.get(), TAG, "" + debuggableScript.getFunctionName());

                return new MyDebugFrame(debuggableScript);
            }
        };

        appRunnerActivity.get().interp.addDebugger(debugger);


    }

    class MyDebugFrame implements DebugFrame {
        private final DebuggableScript debuggableScript;

        public MyDebugFrame(DebuggableScript debuggableScript) {
            this.debuggableScript = debuggableScript;
        }

        public void onEnter(Context cx, Scriptable activation,
                            Scriptable thisObj, Object[] args) {
            MLog.network(a.get(), TAG, "" + "Frame entered");

        }

        public void onExceptionThrown(Context cx, Throwable ex) {
        }

        public void onExit(Context cx, boolean byThrow,
                           Object resultOrException) {
            MLog.network(a.get(), TAG, "" + "Frame exit, result="+resultOrException);

        }

        @Override
        public void onDebuggerStatement(Context context) {

        }

        public void onLineChange(Context cx, int lineNumber) {
            if (isBreakpoint(lineNumber)) {
                MLog.network(a.get(), TAG, "" + "Breakpoint hit: "+debuggableScript.getSourceName()+":"+lineNumber);
            }
        }

        private boolean isBreakpoint(int lineNumber) {
            return true;
        }
    }



    @ProtocoderScript
    @APIMethod(description = "Get the current Protocoder version name", example = "")
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
    @APIMethod(description = "Get the current Protocoder version code", example = "")
	public int getVersionCode() {
		PackageInfo pInfo = null;
		try {
			pInfo = a.get().getPackageManager().getPackageInfo(a.get().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return pInfo.versionCode;
	}


    @ProtocoderScript
    @APIMethod(description = "Install a Proto app programatically", example = "")
    public void installProtoApp(String src, boolean b) {
        final String projectPath = ProjectManager.getInstance().getCurrentProject().getStoragePath();

        Intent intent = new Intent();
        intent.setComponent(new ComponentName("org.protocoder", "org.protocoder.ProtoAppInstallerActivity"));
        intent.setData(Uri.parse(projectPath + "/" + src));
        intent.putExtra("autoInstall", b);
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        a.get().startActivity(intent);
    }




    //TODO this is not finished either
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