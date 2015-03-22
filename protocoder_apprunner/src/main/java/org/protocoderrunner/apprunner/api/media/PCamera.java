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

package org.protocoderrunner.apprunner.api.media;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.AppRunnerSettings;
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


    @ProtoMethodParam(params = {"fileName", "function()"})
    @ProtoMethod(description = "Takes a picture and saves it to fileName", example = "camera.takePicture();")
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


    @ProtoMethodParam(params = {"function(bitmap)"})
    @ProtoMethod(description = "Gets bitmap frames ready to use", example = "camera.takePicture();")
    // @APIRequires()
    public void onNewBitmap(final CameraNew.CallbackBmp callbackfn) {
        cam.addCallbackBmp(callbackfn);
    }


    @ProtoMethodParam(params = {"function(base64Image)"})
    @ProtoMethod(description = "Get the frames ready to stream", example = "camera.takePicture();")
    public void onNewStreamFrame(CameraNew.CallbackStream callbackfn) {
        cam.addCallbackStream(callbackfn);
    }


    @ProtoMethodParam(params = {"width", "height"})
    @ProtoMethod(description = "Set the camera preview resolution", example = "camera.takePicture();")
    public void setPreviewSize(int w, int h) {
        super.setPreviewSize(w, h);
    }


    @ProtoMethodParam(params = {"width", "height"})
    @ProtoMethod(description = "Set the camera picture resolution", example = "camera.takePicture();")
    public void setPictureResolution(int w, int h) {
        super.setPictureSize(w, h);
    }


    @ProtoMethodParam(params = {"{'none', 'mono', 'sepia', 'negative', 'solarize', 'posterize', 'whiteboard', 'blackboard'}"})
    @ProtoMethod(description = "Set the camera picture effect if supported", example = "camera.takePicture();")
    public void setColorEffect(String effect) {
        super.setColorEffect(effect);
    }


    @ProtoMethod(description = "Records a video in fileName", example = "")
    @ProtoMethodParam(params = {"fileName"})
    public void recordVideo(String file) {
        recordVideo(AppRunnerSettings.get().project.getStoragePath() + File.separator + file);
    }


    @ProtoMethod(description = "Stops recording the video", example = "")
    @ProtoMethodParam(params = {""})
    public void stopRecordingVideo() {
        stopRecordingVideo();
    }


    @ProtoMethod(description = "Checks if flash is available", example = "")
    @ProtoMethodParam(params = {""})
    public boolean isFlashAvailable() {
        return super.isFlashAvailable();
    }


    @ProtoMethod(description = "Turns on/off the flash", example = "")
    @ProtoMethodParam(params = {""})
    public void turnOnFlash(boolean b) {
        super.turnOnFlash(b);
    }


    @ProtoMethod(description = "Turn the autofocus on/off", example = "")
    @ProtoMethodParam(params = {""})
    public void focus() {
        super.focus(null);
    }


    @ProtoMethod(description = "Turn the autofocus on/off", example = "")
    @ProtoMethodParam(params = {""})
    public void focus(FocusCB callback) {
        super.focus(callback);
    }
}
