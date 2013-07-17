package com.makewithmoto.apprunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;

import android.app.Activity;


@SuppressWarnings("serial")
public class ScriptAssetProvider implements ModuleScriptProvider, Serializable 
{
	private Activity activity;
	
	
	ScriptAssetProvider(Activity a) 
	{
		activity = a;
	}
	
    public ModuleScript getModuleScript(Context context, String moduleId, Scriptable paths) throws Exception 
    {
		// activity.getAssets()
		try {
			InputStream stream = ScriptIO.create().openAssetFileInputStream(activity, moduleId + ".js");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			
	        return new ModuleScript( context.compileReader(reader, moduleId, 1, null), moduleId);	
		} 
		catch(Exception e) {
            throw e;
        }
	}
}