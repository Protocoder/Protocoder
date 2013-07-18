package com.makewithmoto.fragments;

import java.util.Vector;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.makewithmoto.base.BaseFragment;
import com.makewithmoto.sharedcomponents.R;


public class VideoPlayerFragment extends BaseFragment {

	private View v;
	private VideoView mVideoView;
	Vector<VideoListener> listeners = new Vector<VideoListener>();
	private MediaPlayer mp_;
 


	/**
	 * Called when the activity is first created.
	 * 
	 * @return
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		v = inflater.inflate(R.layout.fragment_videoplayer, container, false); 
		mVideoView = (VideoView) v.findViewById(R.id.surface_view);
		Log.d("mm", "onCreateView");

		
		return v;
		
	}	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("mm", "onActivityCreated");

		for (VideoListener l : listeners) {
			l.onReady(true);
		}
		


	}

	public void initVideo(String videoFile) {
		Log.d("mm", "initVideo");


		//TODO: load file from sdcard 
		String path = "android.resource://" + getActivity().getPackageName() + videoFile; 
		//String path = Environment.getExternalStorageDirectory() + "/arprototype/video"; 
		//Log.d("qq", path); 
		
		/*
		 * Alternatively,for streaming media you can use
		 * mVideoView.setVideoURI(Uri.parse(URLstring));
		 */
		mVideoView.setVideoPath(path);
		//MediaController mediaController = new MediaController(this);
		// mediaController.setAnchorView(mVideoView);
		//mVideoView.setMediaController(mediaController);
		
		mVideoView.requestFocus();
		mVideoView.setKeepScreenOn(true);

		mVideoView.start();

		mVideoView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				close();
			}
		});

		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			

			@Override
			public void onPrepared(MediaPlayer mp) { 
				mp_ = mp; 
			}
		});
		
		
		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {

				//finish();
				for (VideoListener l : listeners) {
					l.onFinish(true);
				}
				

			}
		});

	}

	public void setVolume(float volume) {
		if (mp_ != null) {
			mp_.setVolume(volume, volume);
			
		}
		
	}
	
	public void close() {

		mVideoView.stopPlayback();

	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

		}
		return true;
	}
	
	public void addListener(VideoListener videoListener) {
		listeners.add(videoListener);
	}

	public void removeListener(VideoListener videoListener) {
		listeners.remove(videoListener);
	}


	public interface VideoListener {

		public void onReady(boolean ready);
		public void onFinish(boolean finished);

	}

	
}
