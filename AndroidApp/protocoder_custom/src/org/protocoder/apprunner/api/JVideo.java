/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
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
import org.protocoder.fragments.VideoTextureFragment;

import android.app.Activity;

public class JVideo extends JInterface implements JViewInterface {

    private VideoTextureFragment videoFragment;

    public JVideo(Activity a, VideoTextureFragment fragment) {
	super(a);

	this.videoFragment = fragment;
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public void play() {
	videoFragment.play();
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    @APIParam(params = { "milliseconds" })
    public void seekTo(int ms) {
	videoFragment.seekTo(ms);
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public void pause() {
	videoFragment.pause();
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public void stop() {
	videoFragment.stop();
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    @APIParam(params = { "fileName" })
    public void load(String videoFile) {
	videoFragment.loadExternalVideo(AppRunnerSettings.get().project.getFolder() + File.separator + videoFile);
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "camera.takePicture();")
    @APIParam(params = { "function(milliseconds, totalDuration)" })
    public void onUpdate(final String callbackfn) {
	videoFragment.addListener(new VideoTextureFragment.VideoListener() {

	    @Override
	    public void onTimeUpdate(int ms, int totalDuration) {
		callback(callbackfn, ms, totalDuration);
	    }

	    @Override
	    public void onReady(boolean ready) {

	    }

	    @Override
	    public void onFinish(boolean finished) {

	    }
	});
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "camera.takePicture();")
    @APIParam(params = { "file", "function()" })
    public void setVolume(int vol) {
	videoFragment.setVolume(vol);
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "camera.takePicture();")
    @APIParam(params = { "file", "function()" })
    public void getWidth() {
	videoFragment.getWidth();
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "camera.takePicture();")
    @APIParam(params = { "file", "function()" })
    public void getHeight() {
	videoFragment.getHeight();
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "camera.takePicture();")
    @APIParam(params = { "file", "function()" })
    public void setLoop(boolean b) {
	videoFragment.setLoop(b);
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "camera.takePicture();")
    @APIParam(params = { "file", "function()" })
    public int getDuration() {
	return videoFragment.getDuration();
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "camera.takePicture();")
    @APIParam(params = { "file", "function()" })
    public int getCurrentPosition() {
	return videoFragment.getCurrentPosition();
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "camera.takePicture();")
    @APIParam(params = { "milliseconds", "function()" })
    public void fadeIn(int time) {
	videoFragment.fade(time, 1.0f);
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "camera.takePicture();")
    @APIParam(params = { "milliseconds", "function()" })
    public void fadeOut(int time) {
	videoFragment.fade(time, 0.0f);
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "camera.takePicture();")
    @APIParam(params = { "file", "function()" })
    @Override
    public void move(float x, float y) {
	videoFragment.getView().animate().x(x).setDuration(AppSettings.animSpeed);
	videoFragment.getView().animate().y(y).setDuration(AppSettings.animSpeed);

    }

    @JavascriptInterface
    @APIMethod(description = "", example = "camera.takePicture();")
    @APIParam(params = { "file", "function()" })
    @Override
    public void rotate(float deg) {
	videoFragment.getView().animate().rotation(deg).setDuration(AppSettings.animSpeed);
    }

}
