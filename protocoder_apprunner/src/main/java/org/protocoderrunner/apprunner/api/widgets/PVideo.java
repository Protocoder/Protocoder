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

package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.fragments.CustomVideoTextureView;
import org.protocoderrunner.utils.MLog;

import java.io.File;

public class PVideo extends CustomVideoTextureView implements PViewInterface {

	protected Context c;

	public PVideo(Context c) {
		super(c);
		this.c = c;
	}

	@Override

    @ProtoMethod(description = "Plays a video", example = "")
    @ProtoMethodParam(params = { "" })
	public void play() {
		super.play();
	}

	@Override

	@ProtoMethod(description = "Seeks to a certain position in the video", example = "")
	@ProtoMethodParam(params = { "milliseconds" })
	public void seekTo(int ms) {
		super.seekTo(ms);
	}

	@Override

    @ProtoMethod(description = "Pauses the video", example = "")
    @ProtoMethodParam(params = { "" })
	public void pause() {
		super.pause();
	}

	@Override

	@ProtoMethod(description = "Stops the video", example = "")
	public void stop() {
		super.stop();
	}


	@ProtoMethod(description = "Loads a videoFile", example = "")
	@ProtoMethodParam(params = { "fileName" })
	public void load(String videoFile) {
		super.loadExternalVideo(AppRunnerSettings.get().project.getStoragePath() + File.separator + videoFile);
	}


    // --------- onUpdate ---------//
    public interface OnUpdateCB {
        void event(int ms, int totalDuration);
    }

    // --------- onUpdate ---------//
    public interface OnReadyCB {
        void event();
    }

    // --------- onUpdate ---------//
    public interface OnFinishCB {
        void event();
    }


    @ProtoMethod(description = "Callback that triggers when the video is loaded", example = "")
    @ProtoMethodParam(params = { "function()" })
    public void onLoaded(final OnReadyCB callbackfn) {
        super.addListener(new VideoListener() {
            @Override
            public void onReady(boolean ready) {
                callbackfn.event();
            }

            @Override
            public void onFinish(boolean finished) {

            }

            @Override
            public void onTimeUpdate(int ms, int totalDuration) {

            }
        });

    }


    @ProtoMethod(description = "Callback that triggers when the video playing is finished", example = "")
    @ProtoMethodParam(params = { "function()" })
    public void onFinish(final OnFinishCB callback) {
        super.addListener(new VideoListener() {
            @Override
            public void onReady(boolean ready) {

            }

            @Override
            public void onFinish(boolean finished) {
                callback.event();
            }

            @Override
            public void onTimeUpdate(int ms, int totalDuration) {

            }
        });

    }


	@ProtoMethod(description = "Callback that gives information of the current video position", example = "")
	@ProtoMethodParam(params = { "function(milliseconds, totalDuration)" })
	public void onUpdate(final OnUpdateCB callbackfn) {

		super.addListener(new CustomVideoTextureView.VideoListener() {
			@Override
			public void onTimeUpdate(int ms, int totalDuration) {
                MLog.d(TAG, "onUpdate");

                callbackfn.event(ms, totalDuration);
			}

			@Override
			public void onReady(boolean ready) {
			}

			@Override
			public void onFinish(boolean finished) {

			}
		});
	}


	@ProtoMethod(description = "Sets the video volume", example = "")
	@ProtoMethodParam(params = { "volume" })
	public void setVolume(int vol) {
		super.setVolume(vol);
	}

	@Override

	@ProtoMethod(description = "Gets the video width", example = "")
	@ProtoMethodParam(params = { "" })
	public void getVideoWidth() {
		super.getVideoWidth();
	}

	@Override

	@ProtoMethod(description = "Gets the video height", example = "")
	@ProtoMethodParam(params = { "" })
	public void getVideoHeight() {
		super.getVideoHeight();
	}

	@Override

	@ProtoMethod(description = "Enables/Disables looping the video", example = "")
	@ProtoMethodParam(params = { "boolean" })
	public void setLoop(boolean b) {
		super.setLoop(b);
	}

	@Override

	@ProtoMethod(description = "Get the total duration of the video", example = "")
	@ProtoMethodParam(params = { "" })
	public int getDuration() {
		return super.getDuration();
	}

	@Override

	@ProtoMethod(description = "Gets the current position of the video", example = "")
	@ProtoMethodParam(params = { "" })
	public int getCurrentPosition() {
		return super.getCurrentPosition();
	}


	@ProtoMethod(description = "Fades in the audio in the given milliseconds", example = "")
	@ProtoMethodParam(params = { "milliseconds" })
	public void fadeAudioIn(int time) {
		super.fadeAudio(time, 1.0f);
	}


	@ProtoMethod(description = "Fades out the audio in the given milliseconds", example = "")
	@ProtoMethodParam(params = { "milliseconds" })
	public void fadeAudioOut(int time) {
		super.fadeAudio(time, 0.0f);
	}

}
