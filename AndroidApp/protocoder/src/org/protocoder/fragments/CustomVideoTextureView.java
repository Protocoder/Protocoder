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

package org.protocoder.fragments;

import java.io.IOException;
import java.util.Vector;

import org.protocoder.utils.MLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Handler;
import android.view.Surface;
import android.view.TextureView;

@SuppressLint("NewApi")
public class CustomVideoTextureView extends TextureView implements TextureView.SurfaceTextureListener,
		OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener {

	protected static final String TAG = "VideoTextureFragment";
	private final Context c;
	private MediaPlayer mMediaPlayer;
	private TextureView mPreview;
	Vector<VideoListener> listeners = new Vector<VideoListener>();
	Runnable r;
	protected Handler handler;
	private Surface s;
	private boolean playingVideo = false;
	private float currentVolume;
	private Runnable fadeRunnable;

	public interface VideoListener {

		public void onReady(boolean ready);

		public void onFinish(boolean finished);

		public void onTimeUpdate(int ms, int totalDuration);
	}

	public CustomVideoTextureView(Context context) {
		super(context);
		this.c = context;
		this.setSurfaceTextureListener(this);

		handler = new Handler();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

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
			currentVolume = 1.0f;
			// mPreview.animate().rotation(200).alpha((float) 0.5).scaleX(0.5f)
			// .scaleY(0.5f).setDuration(5000);

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
		String path = "android.resource://" + c.getPackageName() + videoFile;
		// loadVideo(path);
	}

	public void unloadVideo() {
		// mp_.stop();
		for (VideoListener l : listeners) {
			l = null;
		}
		handler.removeCallbacks(r);
		handler.removeCallbacks(fadeRunnable);
		mMediaPlayer.stop();
		mMediaPlayer.release();
		playingVideo = false;

	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
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
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
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
					l.onTimeUpdate(mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
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
		// mVideoView.stopPlayback();

	}

	public int getCurrentPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	public void getVideoWidth() {
		mMediaPlayer.getVideoWidth();
	}

	public void getVideoHeight() {
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

	public void fadeAudio(int time, float finalVolume) {

		MLog.d(TAG, "->" + finalVolume + " " + time);

		final float incr = (finalVolume - currentVolume) / time;

		fadeRunnable = new Runnable() {
			@Override
			public void run() {
				currentVolume += incr;
				MLog.d(TAG, "" + currentVolume + " " + incr);
				if (currentVolume >= 0.0f || currentVolume <= 1.0f) {
					MLog.d(TAG, "qq");
					mMediaPlayer.setVolume(currentVolume, currentVolume);
					handler.post(this);
				} else {
					handler.removeCallbacks(this);
				}
			}
		};
		handler.post(fadeRunnable);
	}

}
