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

import android.media.AudioManager;
import android.media.MediaPlayer;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;

import java.io.File;
import java.io.IOException;

public class PAudioPlayer {

    private final String TAG = "PAudioPlayer";
    private MediaPlayer mMediaPlayer;
    private OnFinishCB mOnFinishCallbackfn;

    public interface LoadSoundCB {
        void event();
    }

    public interface OnFinishCB {
        void event();
    }

    public PAudioPlayer(String url) {

        WhatIsRunning.getInstance().add(this);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setLooping(false);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                //MLog.d(TAG, "prepared");
                mMediaPlayer.start();
            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //MLog.d(TAG, "" + percent);
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                //MLog.d(TAG, "completed");

                if (mOnFinishCallbackfn != null) {
                    mOnFinishCallbackfn.event();
                }
                //mMediaPlayer.reset();
            }
        });

        try {
            if (url.startsWith("http://") == false) {
                url = AppRunnerSettings.get().project.getStoragePath() + File.separator + url;
            }

            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaPlayer.prepareAsync();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //mMediaPlayer.setVolume(100, 100);

    }



    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = { "" })
    public PAudioPlayer onFinish(PAudioPlayer.OnFinishCB callbackfn) {
        mOnFinishCallbackfn = callbackfn;

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = { "" })
    public PAudioPlayer loop(boolean b) {
        mMediaPlayer.setLooping(b);

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = { "" })
    public PAudioPlayer seekTo(int ms) {
        mMediaPlayer.seekTo(ms);

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = { "" })
    public boolean isLooping() {
        return mMediaPlayer.isLooping();
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = { "" })
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = { "" })
    public PAudioPlayer pause() {
        mMediaPlayer.pause();

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = { "" })
    public PAudioPlayer resume() {
        mMediaPlayer.start();

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = { "" })
    public int position() {
        return mMediaPlayer.getCurrentPosition();
    }



    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = { "" })
    public int duration() {
        return mMediaPlayer.getDuration();
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = { "" })
    public PAudioPlayer volume(float vol) {
        mMediaPlayer.setVolume(vol, vol);

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = { "" })
    public PAudioPlayer stop() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;

        return this;
    }


}
