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

package org.protocoderrunner.api.media;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.AppRunnerActivity;
import org.protocoderrunner.api.ProtoBase;

public class PAudioRecorder extends ProtoBase {

    MediaRecorder recorder;
    ProgressDialog mProgressDialog;
    boolean showProgress = false;

    public PAudioRecorder(AppRunner appRunner) {
        super(appRunner);
    }

    @ProtoMethod(description = "Starts recording", example = "")
    @ProtoMethodParam(params = {"showProgressBoolean"})
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

        recorder.setOutputFile(getAppRunner().getProject().getFullPathForFile(fileName));
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
    @ProtoMethodParam(params = {""})
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

    @Override
    public void __stop() {
        stopRecording();
    }
}
