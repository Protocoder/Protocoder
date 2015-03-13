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

package org.protocoderrunner.apprunner;

import android.content.Context;

import org.mozilla.javascript.Scriptable;
import org.protocoderrunner.apprunner.api.other.ProtocoderNativeArray;
import org.protocoderrunner.project.Project;

import java.io.File;

public class AppRunnerSettings {

	private static AppRunnerSettings instance;
    public AppRunnerInterpreter interp;
    public Project project;
    public boolean hasUi = false;
    public boolean hasCustomJSInterpreter = true;


    public static AppRunnerSettings get() {
		if (instance == null)
			instance = new AppRunnerSettings();

		return instance;
	}

    public Scriptable newArray() {
        return interp.interpreter.mainScriptContext.newArray(interp.interpreter.scope, 0);
    }


    public Scriptable newArray(File[] files) {
        return interp.interpreter.mainScriptContext.newArray(interp.interpreter.scope, files);
    }

}
