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

package org.protocoder.apprunner.api;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import org.json.JSONArray;
import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.apprunner.AppRunnerSettings;
import org.protocoder.apprunner.JInterface;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.apprunner.api.other.JPureData;
import org.protocoder.media.Audio;
import org.protocoder.media.AudioService;
import org.protocoder.sensors.WhatIsRunning;
import org.protocoder.utils.MLog;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.PdDispatcher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.speech.RecognizerIntent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class JMedia extends JInterface {

	String TAG = "JMedia";
	startVoiceRecognitionCB onVoiceRecognitionfn;

	public JMedia(AppRunnerActivity a) {
		super(a);

		a.addVoiceRecognitionListener(new onVoiceRecognitionListener() {

			@Override
			public void onNewResult(String text) {
				MLog.d(TAG, "" + text);
				onVoiceRecognitionfn.event(text);
			}

		});

	}

	@ProtocoderScript
	@APIMethod(description = "plays a sound", example = "media.playSound(fileName);")
	@APIParam(params = { "fileName" })
	public void playSound(String url) {

		if (url.startsWith("http://") == false) {
			url = AppRunnerSettings.get().project.getStoragePath() + File.separator + url;
		}
		Audio.playSound(url, 100);
	}

	@ProtocoderScript
	@APIMethod(description = "routes the audio through the speakers", example = "media.playSound(fileName);")
	@APIParam(params = { "" })
	public void setAudioOnSpeakers() {
		AudioManager audioManager = (AudioManager) a.get().getSystemService(Context.AUDIO_SERVICE);
		// audioManager.setMode(AudioManager.MODE_IN_CALL);
		audioManager.setSpeakerphoneOn(true);
	}

	@ProtocoderScript
	@APIMethod(description = "routes the audio through the speakers", example = "media.playSound(fileName);")
	@APIParam(params = { "volume" })
	public void setVolume(int volume) {
		a.get().setVolume(volume);
	}

	// --------- initPDPatch ---------//
	interface initPDPatchCB {
		void event(String string, String responseString);

		void event(String source, float x);

		void event(String source, JSONArray jsonArray);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName", "function(objectType, value)" })
	public JPureData initPDPatch(String fileName, final initPDPatchCB callbackfn) {
		String filePath = AppRunnerSettings.get().project.getStoragePath() + File.separator + fileName;

		PdReceiver receiver = new PdReceiver() {

			@Override
			public void print(String s) {
				MLog.d(TAG, "pd >>" + s);
				callbackfn.event("print", s);
			}

			@Override
			public void receiveBang(String source) {
				MLog.d(TAG, "bang");
				callbackfn.event("bang", source);
			}

			@Override
			public void receiveFloat(String source, float x) {
				MLog.d(TAG, "float: " + x);
				callbackfn.event(source, x);
			}

			@Override
			public void receiveList(String source, Object... args) {
				MLog.d(TAG, "list: " + Arrays.toString(args));

				JSONArray jsonArray = new JSONArray();
				for (Object arg : args) {
					jsonArray.put(arg);
				}

				callbackfn.event(source, jsonArray);
			}

			@Override
			public void receiveMessage(String source, String symbol, Object... args) {
				MLog.d(TAG, "message: " + Arrays.toString(args));
				callbackfn.event(source, symbol);
			}

			@Override
			public void receiveSymbol(String source, String symbol) {
				MLog.d(TAG, "symbol: " + symbol);
				callbackfn.event(source, symbol);
			}

			public void stop() {
				a.get().unbindService(AudioService.pdConnection);
			}
		};

		// create and install the dispatcher
		PdDispatcher dispatcher = new PdUiDispatcher() {

			@Override
			public void print(String s) {
				Log.i("Pd print", s);
			}

		};

		PdBase.setReceiver(dispatcher);

		// PdBase.setReceiver(receiver);
		PdBase.subscribe("android");
		// start pure data sound engine
		AudioService.file = filePath;
		Intent intent = new Intent((a.get()), PdService.class);
		// intent.putExtra("file", "qq.pd");

		(a.get()).bindService(intent, AudioService.pdConnection, Context.BIND_AUTO_CREATE);
		initSystemServices();
		WhatIsRunning.getInstance().add(AudioService.pdConnection);

		return new JPureData();
	}

	private void initSystemServices() {
		TelephonyManager telephonyManager = (TelephonyManager) a.get().getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (AudioService.pdService == null) {
					return;
				}
				if (state == TelephonyManager.CALL_STATE_IDLE) {
					AudioService.start();
				} else {
					AudioService.pdService.stopAudio();
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}

	MediaRecorder recorder;
	ProgressDialog mProgressDialog;
	boolean showProgress = false;

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName", "showProgressBoolean" })
	public void recordAudio(String fileName, boolean showProgress) {
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

		mProgressDialog = new ProgressDialog(a.get());
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

		recorder.start();

		if (showProgress == true) {
			mProgressDialog.show();
		}
	}

	public void stopRecording() {
		try {
			recorder.stop();
			recorder.reset();
			recorder.release();
		} catch (Exception e) {

		}

		if (showProgress) {
			mProgressDialog.dismiss();
			showProgress = false;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public void stopAudio() {

	}

	@ProtocoderScript
	@APIMethod(description = "text to speech", example = "media.textToSpeech('hello world');")
	@APIParam(params = { "text" })
	public void textToSpeech(String text) {
		Audio.speak(a.get(), text, Locale.getDefault());
	}

	// --------- startVoiceRecognition ---------//
	interface startVoiceRecognitionCB {
		void event(String responseString);
	}

	@ProtocoderScript
	@APIMethod(description = "start voice recognition", example = "media.startVoiceRecognition(function(text) { console.log(text) } );")
	@APIParam(params = { "function(recognizedText)" })
	public void startVoiceRecognition(final startVoiceRecognitionCB callbackfn) {
		onVoiceRecognitionfn = callbackfn;

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me something!");
		a.get().startActivityForResult(intent, AppRunnerActivity.VOICE_RECOGNITION_REQUEST_CODE);

	}

	public interface onVoiceRecognitionListener {
		public void onNewResult(String text);
	}

}
