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

package org.protocoderrunner.apprunner.api.other;

import android.content.Context;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;

public class PDeviceEditor extends PInterface {

	public PDeviceEditor(Context a) {
		super(a);

	}

    //TODO reenable this 

	@ProtoMethod(description = "Shows the console in the device app", example = "")
	@ProtoMethodParam(params = { "boolean" })
	public void showConsole(boolean visible) {
		//((AppRunnerFragment) mContext).showConsole(visible);

	}


	@ProtoMethod(description = "Shows the editor in the device app", example = "")
	@ProtoMethodParam(params = { "boolean" })
	public void show(boolean visible) {
       //((AppRunnerActivity) mContext).showEditor(visible);


        //this.addFragment(editorFragment, EDITOR_ID, "editorFragment", true);

    }


}