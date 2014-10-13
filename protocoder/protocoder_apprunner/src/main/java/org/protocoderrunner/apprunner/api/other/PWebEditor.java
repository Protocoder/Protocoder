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

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;

public class PWebEditor extends PInterface {

	public PWebEditor(Context a) {
		super(a);

	}

    //TODO this is a place holder
	@ProtocoderScript
	@APIMethod(description = "Loads a Html file in the webIde sidebar", example = "")
	@APIParam(params = { "boolean" })
	public void loadHTMLonSideBar(boolean visible) {

	}

    //TODO this is a place holder
	@ProtocoderScript
	@APIMethod(description = "Shows/Hides the webIde sidebar", example = "")
	@APIParam(params = { "boolean" })
	public void showSideBar(boolean visible) {

	}

}