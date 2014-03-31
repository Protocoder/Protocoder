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

package org.protocoder.apprunner.api.other;

import java.io.File;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.apprunner.AppRunnerSettings;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.apprunner.api.widgets.JViewInterface;
import org.protocoder.fragments.CustomVideoTextureView;

import android.app.Activity;

public class JVideo extends CustomVideoTextureView implements JViewInterface {

	protected Activity a;

	public JVideo(Activity a) {
		super(a);
		this.a = a;
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void play() {
		super.play();
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "milliseconds" })
	public void seekTo(int ms) {
		super.seekTo(ms);
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void pause() {
		super.pause();
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void stop() {
		super.stop();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName" })
	public void load(String videoFile) {
		super.loadExternalVideo(AppRunnerSettings.get().project.getStoragePath() + File.separator + videoFile);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "camera.takePicture();")
	@APIParam(params = { "function(milliseconds, totalDuration)" })
	public void onUpdate(final String callbackfn) {
		super.addListener(new CustomVideoTextureView.VideoListener() {

			@Override
			public void onTimeUpdate(int ms, int totalDuration) {
				((AppRunnerActivity) a).interp.callback(callbackfn, ms, totalDuration);

			}

			@Override
			public void onReady(boolean ready) {

			}

			@Override
			public void onFinish(boolean finished) {

			}
		});
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "camera.takePicture();")
	@APIParam(params = { "int" })
	public void setVolume(int vol) {
		super.setVolume(vol);
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "camera.takePicture();")
	@APIParam(params = { "" })
	public void getVideoWidth() {
		super.getVideoWidth();
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "camera.takePicture();")
	@APIParam(params = { "" })
	public void getVideoHeight() {
		super.getVideoHeight();
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "camera.takePicture();")
	@APIParam(params = { "boolean" })
	public void setLoop(boolean b) {
		super.setLoop(b);
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "camera.takePicture();")
	@APIParam(params = { "" })
	public int getDuration() {
		return super.getDuration();
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "camera.takePicture();")
	@APIParam(params = { "" })
	public int getCurrentPosition() {
		return super.getCurrentPosition();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "camera.takePicture();")
	@APIParam(params = { "milliseconds" })
	public void fadeAudioIn(int time) {
		super.fadeAudio(time, 1.0f);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "camera.takePicture();")
	@APIParam(params = { "milliseconds" })
	public void fadeAudioOut(int time) {
		super.fadeAudio(time, 0.0f);
	}

}
