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
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.widgets.PViewInterface;
import org.protocoderrunner.fragments.CameraNew;

import java.io.File;

public class PCamera extends CameraNew implements PViewInterface {


    private final PCamera cam;
    protected AppRunnerActivity a;

	public PCamera(AppRunnerActivity a, int camera, int color) {
		super(a, camera, color);
        this.a = a;
        cam = this;
    }

    // --------- takePicture ---------//
    public interface TakePictureCB {
        void event();
    }


    @ProtocoderScript
    @APIParam(params = { "fileName", "function()" })
    @APIMethod(description = "Takes a picture and saves it to fileName", example = "camera.takePicture();")
	// @APIRequires()
	public void takePicture(String file, final TakePictureCB callbackfn) {

		takePic(AppRunnerSettings.get().project.getStoragePath() + File.separator + file);
		addListener(new CameraListener() {

            @Override
            public void onVideoRecorded() {

            }

            @Override
            public void onPicTaken() {
                callbackfn.event();
                cam.removeListener(this);
            }
        });
	}

    @ProtocoderScript
    @APIParam(params = { "function(bitmap)" })
    @APIMethod(description = "Gets bitmap frames ready to use", example = "camera.takePicture();")
	// @APIRequires()
	public void onNewBitmap(final CameraNew.CallbackBmp callbackfn) {
        cam.addCallbackBmp(callbackfn);
	}

    @ProtocoderScript
    @APIParam(params = { "function(base64Image)" })
    @APIMethod(description = "Get the frames ready to stream", example = "camera.takePicture();")
    public void onNewStreamFrame(CameraNew.CallbackStream callbackfn) {
        cam.addCallbackStream(callbackfn);
    }

    @ProtocoderScript
    @APIParam(params = { "width", "height" })
    @APIMethod(description = "Set the camera preview resolution", example = "camera.takePicture();")
    public void setPreviewSize(int w, int h) {
        super.setPreviewSize(w, h);
    }

    @ProtocoderScript
    @APIParam(params = { "width", "height" })
    @APIMethod(description = "Set the camera picture resolution", example = "camera.takePicture();")
    public void setPictureResolution(int w, int h) {
        super.setPictureSize(w, h);
    }

    @ProtocoderScript
    @APIParam(params = { "{'none', 'mono', 'sepia', 'negative', 'solarize', 'posterize', 'whiteboard', 'blackboard'}" })
    @APIMethod(description = "Set the camera picture effect if supported", example = "camera.takePicture();")
    public void setColorEffect(String effect) {
        super.setColorEffect(effect);
    }

    @ProtocoderScript
    @APIMethod(description = "Records a video in fileName", example = "")
    @APIParam(params = { "fileName" })
	public void recordVideo(String file) {
		recordVideo(AppRunnerSettings.get().project.getStoragePath() + File.separator + file);
	}

    @ProtocoderScript
    @APIMethod(description = "Stops recording the video", example = "")
    @APIParam(params = { "" })
	public void stopRecordingVideo() {
		stopRecordingVideo();
	}

    @ProtocoderScript
    @APIMethod(description = "Checks if flash is available", example = "")
    @APIParam(params = { "" })
    public boolean isFlashAvailable() {
        return super.isFlashAvailable();
    }

    @ProtocoderScript
    @APIMethod(description = "Turns on/off the flash", example = "")
    @APIParam(params = { "" })
    public void turnOnFlash(boolean b) {
        super.turnOnFlash(b);
    }

    @ProtocoderScript
    @APIMethod(description = "Turn the autofocus on/off", example = "")
    @APIParam(params = { "" })
    public void focus() {
        super.focus(null);
    }

    @ProtocoderScript
    @APIMethod(description = "Turn the autofocus on/off", example = "")
    @APIParam(params = { "" })
    public void focus(FocusCB callback) {
        super.focus(callback);
    }
}
