/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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

import org.protocoderrunner.utils.MLog;

import java.io.IOException;

@SuppressLint("NewApi")
public class CustomVideoTextureView extends TextureView implements TextureView.SurfaceTextureListener,
        OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener {

    protected static final String TAG = "VideoTextureFragment";
    private final Context c;
    private MediaPlayer mMediaPlayer;
    private TextureView mPreview;
    VideoListener mListener;
    Runnable mTimeUpdateRunnable;
    protected Handler handler;
    private Surface s;
    private boolean playingVideo = false;
    private float currentVolume;
    private Runnable fadeRunnable;
    private boolean isUpdating = false;

    public interface VideoListener {
        public void onLoad(boolean ready);
        public void onReady(boolean ready);
        public void onFinish(boolean finished);
        public void onTimeUpdate(int ms, int totalDuration);
    }

    public CustomVideoTextureView(Context context) {
        super(context);
        this.c = context;
    }

    public void init() {
        this.setSurfaceTextureListener(this);
        handler = new Handler();
        setBackgroundColor(Color.parseColor("#FFFFFF"));
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
        mListener.onReady(true);
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
            //mMediaPlayer.start();
            currentVolume = 1.0f;

            mTimeUpdateRunnable = new Runnable() {
                @Override
                public void run() {
                    //MLog.d(TAG, "update ");
                    if (mListener != null) mListener.onTimeUpdate(mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
                    handler.postDelayed(this, 100);
                }
            };
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
        mListener = null;

        handler.removeCallbacks(mTimeUpdateRunnable);
        handler.removeCallbacks(fadeRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.release();
        playingVideo = false;

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        s = new Surface(surface);

        if (mListener != null) {
            mListener.onLoad(true);
        }

        w = width;
        h = height;

        //AndroidUtils.setViewGenericShadow(this, width, height);

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;

    }

    int w, h;

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        w = width;
        h = height;
        //AndroidUtils.setViewGenericShadow(this, width, height);

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
        mMediaPlayer.setLooping(false);
        //MLog.d(TAG, "onPrepared");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mListener != null) mListener.onFinish(true);
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
        handler.removeCallbacks(mTimeUpdateRunnable);
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
        //MLog.d(TAG, "play");
        mMediaPlayer.start();
        handler.postDelayed(mTimeUpdateRunnable, 200);
    }

    public void pause() {
        handler.removeCallbacks(mTimeUpdateRunnable);
        mMediaPlayer.pause();
    }

    public void stop() {
        handler.removeCallbacks(mTimeUpdateRunnable);
        mMediaPlayer.stop();
    }

    public void seekTo(int ms) {
        mMediaPlayer.seekTo(ms);
    }

    public void addListener(VideoListener videoListener) {
        //MLog.d(TAG, "adding videolistener");
        mListener = videoListener;
    }

    public void removeListener(VideoListener videoListener) {
        mListener = null;
    }

    public void fadeAudio(int time, float finalVolume) {

        //MLog.d(TAG, "->" + finalVolume + " " + time);

        final float incr = (finalVolume - currentVolume) / time;

        fadeRunnable = new Runnable() {
            @Override
            public void run() {
                currentVolume += incr;
                //MLog.d(TAG, "" + currentVolume + " " + incr);
                if (currentVolume >= 0.0f || currentVolume <= 1.0f) {
                    //MLog.d(TAG, "qq");
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
