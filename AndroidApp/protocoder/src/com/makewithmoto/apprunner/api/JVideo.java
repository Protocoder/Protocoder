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

package com.makewithmoto.apprunner.api;

import android.app.Activity;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;
import com.makewithmoto.apprunner.api.widgets.JViewInterface;
import com.makewithmoto.base.AppSettings;
import com.makewithmoto.fragments.VideoPlayerFragment;
import com.makewithmoto.fragments.VideoPlayerFragment.VideoListener;

public class JVideo extends JInterface implements JViewInterface {

	private VideoPlayerFragment videoFragment;


	public JVideo(Activity a, VideoPlayerFragment videoFragment) {
		super(a);
		
		this.videoFragment = videoFragment;
	}


	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void play() {
		videoFragment.play();
	}	

	@JavascriptInterface
	@APIMethod(description = "", example = "")
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
	@APIMethod(description = "", example = "camera.takePicture();")
	public void onUpdate(final String callbackfn) {
		videoFragment.addListener(new VideoListener() {
			
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
	public void setVolume(int vol) {
		videoFragment.setVolume(vol);
	}	
	@JavascriptInterface
	@APIMethod(description = "", example = "camera.takePicture();")
	public void setLoop(boolean b) {
		videoFragment.setLoop(b);
	}	
	@JavascriptInterface
	@APIMethod(description = "", example = "camera.takePicture();")
	public int getDuration() {
		return videoFragment.getDuration();
	}	
	
	
	@JavascriptInterface
	@APIMethod(description = "", example = "camera.takePicture();")
	public int getCurrentPosition() {
		return videoFragment.getCurrentPosition();
	}

	
	@Override
	public void move(float x, float y) { 
		videoFragment.getView().animate().x(x).setDuration(AppSettings.animSpeed);
		videoFragment.getView().animate().y(y).setDuration(AppSettings.animSpeed);

	} 
	
	@Override
	public void rotate(float deg) { 
		videoFragment.getView().animate().rotation(deg).setDuration(AppSettings.animSpeed);
	}
	
}
