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

import java.io.File;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.widgets.PViewInterface;
import org.protocoderrunner.fragments.CameraFragment;

import android.app.Activity;

public class PCamera extends PInterface implements PViewInterface {

	private final CameraFragment cameraFragment;
	protected Activity a;

	public PCamera(Activity a, CameraFragment cameraFragment) {
		super(a);
		this.a = a;

		this.cameraFragment = cameraFragment;
	}

    // --------- takePicture ---------//
    public interface TakePictureCB {
        void event();
    }

	@ProtocoderScript
	@APIParam(params = { "file", "function()" })
	@APIMethod(description = "", example = "camera.takePicture();")
	// @APIRequires()
	public void takePicture(String file, final TakePictureCB callbackfn) {

		cameraFragment.takePic(AppRunnerSettings.get().project.getStoragePath() + File.separator + file);
		cameraFragment.addListener(new CameraFragment.CameraListener() {

			@Override
			public void onVideoRecorded() {

			}

			@Override
			public void onPicTaken() {
                callbackfn.event();
				cameraFragment.removeListener(this);
			}
		});
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void recordVideo(String file) {
		cameraFragment.recordVideo(AppRunnerSettings.get().project.getStoragePath() + File.separator + file);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void stopRecordingVideo() {
		cameraFragment.stopRecordingVideo();

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

	public void turnOnFlash(boolean b) {
		cameraFragment.turnOnFlash(b);
	}

}
