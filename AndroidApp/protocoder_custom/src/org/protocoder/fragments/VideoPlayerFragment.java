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

package org.protocoder.fragments;

import java.util.Vector;

import org.protocoder.R;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

@SuppressLint("NewApi")
public class VideoPlayerFragment extends Fragment {

    private View v;
    private VideoView mVideoView;
    Vector<VideoListener> listeners = new Vector<VideoListener>();
    Runnable r;
    protected Handler handler;
    protected MediaPlayer mp_;

    public interface VideoListener {

	public void onReady(boolean ready);

	public void onFinish(boolean finished);

	public void onTimeUpdate(int ms, int totalDuration);
    }

    /**
     * Called when the activity is first created.
     * 
     * @return
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	super.onCreateView(inflater, container, savedInstanceState);

	v = inflater.inflate(R.layout.fragment_videoplayer, container, false);
	mVideoView = (VideoView) v.findViewById(R.id.video_view);

	final FrameLayout fl = (FrameLayout) v.findViewById(R.id.video_parent);

	fl.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// fl.animate().scaleX(0.5f).scaleY(0.5f).setDuration(5000);
	    }
	});
	Log.d("mm", "onCreateView");

	handler = new Handler();

	return v;

    }

    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	Log.d("mm", "onActivityCreated");

	for (VideoListener l : listeners) {
	    l.onReady(true);
	}

    }

    @Override
    public void onDestroy() {
	super.onDestroy();

	// mp_.stop();
	for (VideoListener l : listeners) {
	    l = null;
	}
	handler.removeCallbacks(r);
    }

    public void loadExternalVideo(String path) {
	loadVideo(path);
    }

    public void loadResourceVideo(String videoFile) {
	String path = "android.resource://" + getActivity().getPackageName() + videoFile;
	loadVideo(path);
    }

    public void loadVideo(final String path) {
	/*
	 * Alternatively,for streaming media you can use mVideoView.setVideoURI(Uri.parse(URLstring));
	 */

	mVideoView.setVideoPath(path);
	MediaController mediaController = new MediaController(getActivity());
	mediaController.setAnchorView(mVideoView);
	mVideoView.setMediaController(null);

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
		mp_.setLooping(true);

		// mVideoView.animate().rotation(200).alpha((float) 0.5)
		// .scaleX(0.2f).scaleY(0.2f).setDuration(2000);

		r = new Runnable() {

		    @Override
		    public void run() {
			for (VideoListener l : listeners) {
			    try {
				l.onTimeUpdate(mp_.getCurrentPosition(), mp_.getDuration());

			    } catch (Exception e) {
			    }

			}
			handler.postDelayed(this, 1000);
		    }
		};

		handler.post(r);
	    }
	});

	// mp_.setO

	mVideoView.setOnCompletionListener(new OnCompletionListener() {

	    public void onCompletion(MediaPlayer mp) {

		// finish();
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

    public void setLoop(boolean b) {
	mp_.setLooping(b);
    }

    public void close() {
	handler.removeCallbacks(r);
	// mVideoView.stopPlayback();

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

    public void seekTo(int ms) {
	mp_.seekTo(ms);
    }

    public int getDuration() {
	return mp_.getDuration();
    }

    public int getCurrentPosition() {
	return mp_.getCurrentPosition();
    }

    public void getWidth() {
	mp_.getVideoWidth();
    }

    public void getHeight() {
	mp_.getVideoHeight();
    }

    public void play() {
	mp_.start();
    }

    public void pause() {
	mp_.pause();
    }

    public void stop() {
	mp_.stop();
    }

}
