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

package org.protocoder.apprunner.api;

import java.io.File;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.AppRunnerSettings;
import org.protocoder.apprunner.JInterface;
import org.protocoder.apprunner.JavascriptInterface;
import org.protocoder.apprunner.api.widgets.JViewInterface;
import org.protocoder.base.AppSettings;
import org.protocoder.fragments.CameraFragment;
import org.protocoder.fragments.CameraFragment.CameraListener;

import android.app.Activity;

public class JCamera extends JInterface implements JViewInterface {

    private CameraFragment cameraFragment;

    public JCamera(Activity a, CameraFragment cameraFragment) {
	super(a);

	this.cameraFragment = cameraFragment;
    }

    @JavascriptInterface
    @APIParam(params = { "file", "function()" })
    @APIMethod(description = "", example = "camera.takePicture();")
    // @APIRequires()
    public void takePicture(String file, final String callbackfn) {
	String[] q = new String[] { "blah", "hey", "yo" };
	// String[] qq = new Str
	cameraFragment.takePic(AppRunnerSettings.get().project.getFolder() + File.separator + file);
	cameraFragment.addListener(new CameraListener() {

	    @Override
	    public void onVideoRecorded() {

	    }

	    @Override
	    public void onPicTaken() {
		callback(callbackfn);
		cameraFragment.removeListener(this);
	    }
	});
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public void recordVideo(String file) {
	cameraFragment.recordVideo(AppRunnerSettings.get().project.getFolder() + File.separator + file);
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public void stopRecordingVideo(final String callbackfn) {
	// cameraFragment.recordVideo(((AppRunnerActivity)
	// a.get()).getCurrentDir() + File.separator + file);
	// cameraFragment.addListener(new CameraListener() {
	//
	// @Override
	// public void onVideoRecorded() {
	// callback(callbackfn);
	// cameraFragment.removeListener(this);
	//
	// }
	//
	// @Override
	// public void onPicTaken() {
	// }
	// });
    }

    @Override
    public void move(float x, float y) {
	cameraFragment.getView().animate().x(x).setDuration(AppSettings.animSpeed);
	cameraFragment.getView().animate().y(y).setDuration(AppSettings.animSpeed);

    }

    @Override
    public void rotate(float deg) {
	cameraFragment.getView().animate().rotation(deg).setDuration(AppSettings.animSpeed);
    }

}
