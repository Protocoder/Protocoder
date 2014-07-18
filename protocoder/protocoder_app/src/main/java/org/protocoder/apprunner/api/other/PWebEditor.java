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

package org.protocoder.apprunner.api.other;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.apprunner.PInterface;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.events.Project;
import org.protocoder.fragments.EditorFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class PWebEditor extends PInterface {

	public PWebEditor(Activity a) {
		super(a);

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "boolean" })
	public void loadHTMLonSideBar(boolean visible) {

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "boolean" })
	public void showSideBar(boolean visible) {

	}

}