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

package org.protocoderrunner.apprunner.api;

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
import org.protocoderrunner.apidoc.annotation.APIRequires;
import org.protocoderrunner.apidoc.annotation.APIVersion;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.other.ApplicationInfo;
import org.protocoderrunner.apprunner.api.widgets.PWebEditor;
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

    public PProtocoder(android.content.Context a) {
        super(a);

        // get org.apprunner settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(a);
        id = sharedPrefs.getString("pref_id", "-1");
    }

    //TODO enable this after refactor
//
//	@APIMethod(description = "", example = "")
//	public String getId() {
//		return PrefsFragment.getId(mContext);
//
//	}

//
//	@APIMethod(description = "", example = "")
//	@APIParam(params = { "id" })
//	public void setId(String id) {
//		PrefsFragment.setId(mContext, id);
//	}

    //TODO this is mContext place holder
    //
    @ProtoMethod(description = "Returns an object to manipulate the device app webIDE", example = "")
    @ProtoMethodParam(params = {})
    public PWebEditor webEditor() {
        PWebEditor pWebEditor = new PWebEditor(getContext());

        return pWebEditor;
    }

//TODO reenable this
    //
//    @APIMethod(description = "Returns an object to manipulate the device app", example = "")
//    @APIParam(params = { })
//    public PDeviceEditor deviceEditor() {
//        contextUi.get().initLayout();
//
//        PDeviceEditor pEditor = new PDeviceEditor(mContext);
//
//        return pEditor;
//    }


//TODO reenable this
//
//	@APIMethod(description = "Launch another script given its name and type", example = "")
//	@APIParam(params = { "folder", "name" })
//	@APIVersion(minLevel = "2")
//	@APIRequires("android.permission.INTERNET")
//	public void launchScript(String folder, String name) {
//		Intent intent = new Intent(mContext, AppRunnerFragment.class);
//		intent.putExtra(Project.FOLDER, name);
//		intent.putExtra(Project.NAME, name);
//
//		// mContext.startActivity(intent);
//		// String code = StrUtils.generateRandomString();
//		contextUi.get().startActivityForResult(intent, 22);
//	}

    //TODO this is mContext place holder
    //
    @ProtoMethod(description = "", example = "")
    @APIVersion(minLevel = "2")
    @APIRequires("android.permission.INTERNET")
    public void returnValueToScript(String returnValue) {
        Intent output = new Intent();
        output.putExtra("return", returnValue);
        //contextUi.get().setResult(22, output);
        //contextUi.get().finish();
    }


    //TODO this doesnt work

    @ProtoMethod(description = "", example = "")
    public void returnResult(String data) {

        Bundle conData = new Bundle();
        conData.putString("param_result", data);
        Intent intent = new Intent();
        intent.putExtras(conData);
        // contextUi.get().setResult(contextUi.get().RESULT_OK, intent);
        // contextUi.get().finish();

    }

    // --------- addDebugger ---------//
    public interface AddDebuggerCB {
        void data(String debuggableScript);
    }

//reenable this
//
//
//	@APIMethod(description = "Add mContext debugger to the execution", example = "")
//	@APIVersion(minLevel = "2")
//	@APIRequires("android.permission.INTERNET")
//	public void addDebugger(final AddDebuggerCB cb) {
//
//        Debugger debugger = new Debugger() {
//            @Override
//            public void handleCompilationDone(Context context, DebuggableScript debuggableScript, String s) {
//                //cb.data("qq");
//                MLog.network(mContext, TAG, "" + debuggableScript.getFunctionName());
//            }
//
//            @Override
//            public DebugFrame getFrame(Context context, DebuggableScript debuggableScript) {
//                //cb.data("qq");
//                MLog.network(mContext, TAG, "" + debuggableScript.getFunctionName());
//
//                return new MyDebugFrame(debuggableScript);
//            }
//        };
//
//        contextUi.get().interp.addDebugger(debugger);
//
//
//    }

    class MyDebugFrame implements DebugFrame {
        private final DebuggableScript debuggableScript;

        public MyDebugFrame(DebuggableScript debuggableScript) {
            this.debuggableScript = debuggableScript;
        }

        public void onEnter(Context cx, Scriptable activation,
                            Scriptable thisObj, Object[] args) {
            MLog.network(getContext(), TAG, "" + "Frame entered");

        }

        public void onExceptionThrown(Context cx, Throwable ex) {
        }

        public void onExit(Context cx, boolean byThrow,
                           Object resultOrException) {
            MLog.network(getContext(), TAG, "" + "Frame exit, result=" + resultOrException);

        }

        @Override
        public void onDebuggerStatement(Context context) {

        }

        public void onLineChange(Context cx, int lineNumber) {
            if (isBreakpoint(lineNumber)) {
                MLog.network(getContext(), TAG, "" + "Breakpoint hit: " + debuggableScript.getSourceName() + ":" + lineNumber);
            }
        }

        private boolean isBreakpoint(int lineNumber) {
            return true;
        }
    }


    @ProtoMethod(description = "Get the current Protocoder version name", example = "")
    public String versionName() {
        PackageInfo pInfo = null;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }


    @ProtoMethod(description = "Get the current Protocoder version code", example = "")
    public int versionCode() {
        PackageInfo pInfo = null;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionCode;
    }


    @ProtoMethod(description = "Install a Proto app programatically", example = "")
    public void installProtoApp(String src, boolean b) {
        final String projectPath = ProjectManager.getInstance().getCurrentProject().getStoragePath();

        Intent intent = new Intent();
        intent.setComponent(new ComponentName("org.protocoder", "org.protocoder.ProtoAppInstallerActivity"));
        intent.setData(Uri.parse(projectPath + "/" + src));
        intent.putExtra("autoInstall", b);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        getContext().startActivity(intent);
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

        PackageManager manager = getContext().getPackageManager();

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