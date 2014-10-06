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

import android.app.Activity;
import android.content.Context;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.widgets.PViewInterface;
import org.protocoderrunner.fragments.CustomVideoTextureView;

import java.io.File;

public class PVideo extends CustomVideoTextureView implements PViewInterface {

	protected Context c;

	public PVideo(Context c) {
		super(c);
		this.c = c;
	}

	@Override
    @ProtocoderScript
    @APIMethod(description = "Plays a video", example = "")
    @APIParam(params = { "" })
	public void play() {
		super.play();
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "Seeks to a certain position in the video", example = "")
	@APIParam(params = { "milliseconds" })
	public void seekTo(int ms) {
		super.seekTo(ms);
	}

	@Override
    @ProtocoderScript
    @APIMethod(description = "Pauses the video", example = "")
    @APIParam(params = { "" })
	public void pause() {
		super.pause();
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "Stops the video", example = "")
	public void stop() {
		super.stop();
	}

	@ProtocoderScript
	@APIMethod(description = "Loads a videoFile", example = "")
	@APIParam(params = { "fileName" })
	public void load(String videoFile) {
		super.loadExternalVideo(AppRunnerSettings.get().project.getStoragePath() + File.separator + videoFile);
	}


    // --------- onUpdate ---------//
    public interface OnUpdateCB {
        void event(OnUpdateCB callbackfn, int ms, int totalDuration);
    }

    @ProtocoderScript
	@APIMethod(description = "Callback that gives information of the current video position", example = "")
	@APIParam(params = { "function(milliseconds, totalDuration)" })
	public void onUpdate(final OnUpdateCB callbackfn) {
		super.addListener(new CustomVideoTextureView.VideoListener() {

			@Override
			public void onTimeUpdate(int ms, int totalDuration) {
				callbackfn.event(callbackfn, ms, totalDuration);

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
	@APIMethod(description = "Sets the video volume", example = "")
	@APIParam(params = { "volume" })
	public void setVolume(int vol) {
		super.setVolume(vol);
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "Gets the video width", example = "")
	@APIParam(params = { "" })
	public void getVideoWidth() {
		super.getVideoWidth();
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "Gets the video height", example = "")
	@APIParam(params = { "" })
	public void getVideoHeight() {
		super.getVideoHeight();
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "Enables/Disables looping the video", example = "")
	@APIParam(params = { "boolean" })
	public void setLoop(boolean b) {
		super.setLoop(b);
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "Get the total duration of the video", example = "")
	@APIParam(params = { "" })
	public int getDuration() {
		return super.getDuration();
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "Gets the current position of the video", example = "")
	@APIParam(params = { "" })
	public int getCurrentPosition() {
		return super.getCurrentPosition();
	}

	@ProtocoderScript
	@APIMethod(description = "Fades in the audio in the given milliseconds", example = "")
	@APIParam(params = { "milliseconds" })
	public void fadeAudioIn(int time) {
		super.fadeAudio(time, 1.0f);
	}

	@ProtocoderScript
	@APIMethod(description = "Fades out the audio in the given milliseconds", example = "")
	@APIParam(params = { "milliseconds" })
	public void fadeAudioOut(int time) {
		super.fadeAudio(time, 0.0f);
	}

}
