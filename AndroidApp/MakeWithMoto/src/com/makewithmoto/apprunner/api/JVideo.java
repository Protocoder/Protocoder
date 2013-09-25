package com.makewithmoto.apprunner.api;

import android.app.Activity;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;
import com.makewithmoto.fragments.VideoPlayerFragment;
import com.makewithmoto.fragments.VideoPlayerFragment.VideoListener;

public class JVideo extends JInterface {

	private VideoPlayerFragment videoFragment;


	public JVideo(Activity a, VideoPlayerFragment videoFragment) {
		super(a);
		
		this.videoFragment = videoFragment;
	}

	
	@JavascriptInterface
	@APIMethod(description = "", example = "camera.takePicture();")
	public void play() {
		videoFragment.play();
	}	

	@JavascriptInterface
	@APIMethod(description = "", example = "camera.takePicture();")
	public void seekTo(int ms) {
		videoFragment.seekTo(ms);
	}	
	
	@JavascriptInterface
	@APIMethod(description = "", example = "camera.takePicture();")
	public void pause() {
		videoFragment.pause();
	}	
	
	@JavascriptInterface
	@APIMethod(description = "", example = "camera.takePicture();")
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
	
	
	
}
