package com.makewithmoto.fragments;

import java.io.IOException;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.makewithmoto.R;

@SuppressLint("NewApi")
public class VideoTextureFragment extends Fragment implements
		TextureView.SurfaceTextureListener, OnBufferingUpdateListener,
		OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener {

	private MediaPlayer mMediaPlayer;
	private TextureView mPreview;
	Vector<VideoListener> listeners = new Vector<VideoListener>();
	Runnable r;
	protected Handler handler;
	private Surface s;
	private boolean playingVideo = false;

	public interface VideoListener {

		public void onReady(boolean ready);

		public void onFinish(boolean finished);

		public void onTimeUpdate(int ms, int totalDuration);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_texturevideo, container,
				false);

		mPreview = (TextureView) v.findViewById(R.id.video_view2);
		mPreview.setSurfaceTextureListener(this);

		handler = new Handler();

		return v;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("mm", "onActivityCreated");

	

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		unloadVideo();
	}

	public void loadExternalVideo(String path) {
		if (playingVideo) { 
			unloadVideo();			
		}
		loadVideo(path);
	}

	public void loadVideo(String path) {

		playingVideo = true;
		
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.setSurface(s);
			mMediaPlayer.prepare();
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnVideoSizeChangedListener(this);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.start();
			//mPreview.animate().rotation(200).alpha((float) 0.5).scaleX(0.5f)
			//		.scaleY(0.5f).setDuration(5000);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public void loadResourceVideo(String videoFile) {
		String path = "android.resource://" + getActivity().getPackageName()
				+ videoFile;
	//	loadVideo(path);
	}

	
	public void unloadVideo() { 
		// mp_.stop();
		for (VideoListener l : listeners) {
			l = null;
		}
		handler.removeCallbacks(r);
		mMediaPlayer.stop();
		mMediaPlayer.release();
		playingVideo = false;
	}
	


	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		s = new Surface(surface);

		for (VideoListener l : listeners) {
			l.onReady(true);
		}
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		return false;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
			int height) {
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mMediaPlayer = mp;
		mMediaPlayer.setLooping(true);

		r = new Runnable() {

			@Override
			public void run() {
				for (VideoListener l : listeners) {
					l.onTimeUpdate(mMediaPlayer.getCurrentPosition(),
							mMediaPlayer.getDuration());
				}
				handler.postDelayed(this, 1000);
			}
		};

		handler.post(r);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {

		// finish();
		for (VideoListener l : listeners) {
			l.onFinish(true);
		}

	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
	}
	
	
	
	
	public void setVolume(float volume) {
		if (mMediaPlayer != null) {
			mMediaPlayer.setVolume(volume, volume);
			
		}	
	}
	
	public void setLoop(boolean b) { 
		mMediaPlayer.setLooping(b);
	}
	
	public void close() {
		handler.removeCallbacks(r);
		//mVideoView.stopPlayback();

	}
	
	public int getCurrentPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	public void getWidth() {
		mMediaPlayer.getVideoWidth();
	}

	public void getHeight() {
		mMediaPlayer.getVideoHeight();
	}

	public void play() {
		mMediaPlayer.start();
	}

	public void pause() {
		mMediaPlayer.pause();
	}

	public void stop() {
		mMediaPlayer.stop();
	}


	public void seekTo(int ms) { 
		mMediaPlayer.seekTo(ms);
	}
	
	
	public void addListener(VideoListener videoListener) {
		listeners.add(videoListener);
	}

	public void removeListener(VideoListener videoListener) {
		listeners.remove(videoListener);
	}

}
