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

package org.protocoderrunner.api.media;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.AppRunner;
import org.protocoderrunner.api.widgets.PViewInterface;
import org.protocoderrunner.base.fragments.CameraNew;

public class PCamera extends CameraNew implements PViewInterface {


    private final PCamera cam;
    protected AppRunner mAppRunner;

    public PCamera(AppRunner appRunner, int camera, int color) {
        super(appRunner, camera, color);
        this.mAppRunner = appRunner;
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

        takePic(mAppRunner.mProjectManager.getProjectPath() + file);
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
        recordVideo(mAppRunner.mProjectManager.getProjectPath() + file);
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
