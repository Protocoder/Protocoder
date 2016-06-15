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

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.other.WhatIsRunning;
import org.protocoderrunner.api.widgets.PViewInterface;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.gui.CameraNew;
import org.protocoderrunner.base.gui.CameraNew2;

public class PCamera2 extends CameraNew2 implements PCameraInterface, PViewInterface {

    private final PCamera2 cam;
    protected AppRunner mAppRunner;

    public PCamera2(AppRunner appRunner, int camera, int color) {
        super(appRunner, camera, color);
        this.mAppRunner = appRunner;
        cam = this;

        mAppRunner.whatIsRunning.add(this);
    }

    @Override
    public void takePicture(String file, ReturnInterface callbackfn) {

    }

    @Override
    public void recordVideo(String file) {

    }

    @Override
    public void stopRecordingVideo() {

    }

    @Override
    public void focus(CameraNew.FocusCB callback) {

    }

    @Override
    public void setPreviewSize(int w, int h) {

    }

    @Override
    public void setPictureResolution(int w, int h) {

    }

    @Override
    public boolean isFlashAvailable() {
        return false;
    }

    @Override
    public void turnOnFlash(boolean b) {

    }

    @Override
    public void setColorEffect(String effect) {

    }

    @Override
    public void onNewBitmap(CameraNew.CallbackBmp callbackfn) {

    }

    @Override
    public void onNewStreamFrame(CameraNew.CallbackStream callbackfn) {
    }

    public void __close() {
    //    cam.closeCamera();
    }



}
