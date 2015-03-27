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
    private OnUpdateCB mCallbackUpdate;
    private OnFinishCB mCallbackOnFinish;
    private OnReadyCB mCallbackOnReady;

    public PVideo(Context c, final String videoFile) {
        super(c);
        this.c = c;


        addListener(new CustomVideoTextureView.VideoListener() {
            @Override
            public void onTimeUpdate(int ms, int totalDuration) {
                //MLog.d(TAG, "onUpdate");
                if (mCallbackUpdate != null) mCallbackUpdate.event(ms, totalDuration);
            }

            @Override
            public void onLoad(boolean ready) {
                load(videoFile);
            }

            @Override
            public void onReady(boolean ready) {
                //MLog.d(TAG, "onReady");
                if (mCallbackOnReady != null) mCallbackOnReady.event();
            }

            @Override
            public void onFinish(boolean finished) {
                //MLog.d(TAG, "onFinish");

                if (mCallbackOnFinish != null) mCallbackOnFinish.event();
            }
        });

        init();

    }

    @Override
    @ProtoMethod(description = "Plays a video", example = "")
    @ProtoMethodParam(params = {""})
    public void play() {
        super.play();
    }

    @Override
    @ProtoMethod(description = "Seeks to a certain position in the video", example = "")
    @ProtoMethodParam(params = {"milliseconds"})
    public void seekTo(int ms) {
        super.seekTo(ms);
    }

    @Override
    @ProtoMethod(description = "Pauses the video", example = "")
    @ProtoMethodParam(params = {""})
    public void pause() {
        super.pause();
    }

    @Override
    @ProtoMethod(description = "Stops the video", example = "")
    public void stop() {
        super.stop();
    }


    @ProtoMethod(description = "Loads a videoFile", example = "")
    @ProtoMethodParam(params = {"fileName"})
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
    @ProtoMethodParam(params = {"function()"})
    public void onLoaded(final OnReadyCB callbackfn) {
        mCallbackOnReady = callbackfn;

    }


    @ProtoMethod(description = "Callback that triggers when the video playing is finished", example = "")
    @ProtoMethodParam(params = {"function()"})
    public void onFinish(final OnFinishCB callback) {
        mCallbackOnFinish = callback;
    }


    @ProtoMethod(description = "Callback that gives information of the current video position", example = "")
    @ProtoMethodParam(params = {"function(milliseconds, totalDuration)"})
    public void onUpdate(final OnUpdateCB callbackfn) {
        mCallbackUpdate = callbackfn;
    }


    @ProtoMethod(description = "Sets the video volume", example = "")
    @ProtoMethodParam(params = {"volume"})
    public void setVolume(int vol) {
        super.setVolume(vol);
    }

    @Override
    @ProtoMethod(description = "Gets the video width", example = "")
    @ProtoMethodParam(params = {""})
    public void getVideoWidth() {
        super.getVideoWidth();
    }

    @Override
    @ProtoMethod(description = "Gets the video height", example = "")
    @ProtoMethodParam(params = {""})
    public void getVideoHeight() {
        super.getVideoHeight();
    }

    @Override
    @ProtoMethod(description = "Enables/Disables looping the video", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void setLoop(boolean b) {
        super.setLoop(b);
    }

    @Override
    @ProtoMethod(description = "Get the total duration of the video", example = "")
    @ProtoMethodParam(params = {""})
    public int getDuration() {
        return super.getDuration();
    }

    @Override
    @ProtoMethod(description = "Gets the current position of the video", example = "")
    @ProtoMethodParam(params = {""})
    public int getCurrentPosition() {
        return super.getCurrentPosition();
    }


    @ProtoMethod(description = "Fades in the audio in the given milliseconds", example = "")
    @ProtoMethodParam(params = {"milliseconds"})
    public void fadeAudioIn(int time) {
        super.fadeAudio(time, 1.0f);
    }


    @ProtoMethod(description = "Fades out the audio in the given milliseconds", example = "")
    @ProtoMethodParam(params = {"milliseconds"})
    public void fadeAudioOut(int time) {
        super.fadeAudio(time, 0.0f);
    }

}
