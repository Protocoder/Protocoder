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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.PInterface;

import java.io.File;

public class PAudioRecorder extends PInterface {


    MediaRecorder recorder;
    ProgressDialog mProgressDialog;
    boolean showProgress = false;

    public PAudioRecorder(Context c) {
        super(c);
        WhatIsRunning.getInstance().add(this);
    }


    @ProtoMethod(description = "Starts recording", example = "")
    @ProtoMethodParam(params = { "showProgressBoolean" })
    public PAudioRecorder startRecording(String fileName, boolean showProgress) {
        this.showProgress = showProgress;

        recorder = new MediaRecorder();
        // ContentValues values = new ContentValues(3);
        // values.put(MediaStore.MediaColumns.TITLE, fileName);
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // recorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
        recorder.setAudioEncodingBitRate(16);
        recorder.setAudioSamplingRate(44100);

        recorder.setOutputFile(AppRunnerSettings.get().project.getStoragePath() + File.separator + fileName);
        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (showProgress && getActivity() != null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle("Record!");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Stop recording",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int whichButton) {
                            mProgressDialog.dismiss();
                            stopRecording();
                        }
                    });

            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface p1) {
                    stopRecording();
                }
            });
            mProgressDialog.show();
        }

        recorder.start();


        return this;
    }




    @ProtoMethod(description = "Stops recording", example = "")
    @ProtoMethodParam(params = { "" })
    public void stopRecording() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }
        } catch (Exception e) {

        }

        if (showProgress && getActivity() != null) {
            mProgressDialog.dismiss();
            showProgress = false;
        }

    }


    public void stop() {
        stopRecording();
    }


    public void startRecordingWithUi(AppRunnerActivity activity) {
        super.setActivity(activity);
    }

}
