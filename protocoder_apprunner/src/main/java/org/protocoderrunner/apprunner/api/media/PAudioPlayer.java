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

package org.protocoderrunner.apprunner.api.media;

import android.media.AudioManager;
import android.media.MediaPlayer;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;

import java.io.File;
import java.io.IOException;

public class PAudioPlayer {

    private final String TAG = PAudioPlayer.class.getSimpleName();
    private boolean mReusePlayer = true;
    private MediaPlayer mMediaPlayer;
    private OnFinishCB mOnFinishCallbackfn;

    public interface LoadSoundCB {
        void event();
    }

    public interface OnFinishCB {
        void event();
    }

    public PAudioPlayer(String url, boolean reusePlayer) {

        WhatIsRunning.getInstance().add(this);
        mReusePlayer = reusePlayer;

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

                if (!mReusePlayer) {
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
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
    @ProtoMethodParam(params = {""})
    public PAudioPlayer onFinish(PAudioPlayer.OnFinishCB callbackfn) {
        mOnFinishCallbackfn = callbackfn;

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PAudioPlayer loop(boolean b) {
        mMediaPlayer.setLooping(b);

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PAudioPlayer seekTo(int ms) {
        mMediaPlayer.seekTo(ms);

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public boolean isLooping() {
        return mMediaPlayer.isLooping();
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PAudioPlayer pause() {
        mMediaPlayer.pause();

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PAudioPlayer resume() {
        mMediaPlayer.start();

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public int position() {
        return mMediaPlayer.getCurrentPosition();
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public int duration() {
        return mMediaPlayer.getDuration();
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PAudioPlayer volume(float vol) {
        mMediaPlayer.setVolume(vol, vol);

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PAudioPlayer stop() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;

        return this;
    }


}
