package com.makewithmoto.apprunner.api;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import org.json.JSONArray;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.PdDispatcher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.speech.RecognizerIntent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.apprunner.AppRunnerSettings;
import com.makewithmoto.media.Audio;
import com.makewithmoto.media.AudioService;
import com.makewithmoto.sensors.WhatIsRunning;

public class JMedia extends JInterface {

	String TAG = "JMedia";
	String onVoiceRecognitionfn;

	public JMedia(AppRunnerActivity a) {
		super(a);

		((AppRunnerActivity) a)
				.addVoiceRecognitionListener(new onVoiceRecognitionListener() {

					@Override
					public void onNewResult(String text) {
						Log.d(TAG, "" + text);
						callback(onVoiceRecognitionfn, "\"" + text + "\"");
					}

				});

	}

	// @JavascriptInterface
	// @APIAnnotation(description = "plays a video", example =
	// "media.playVieo(fileName);")
	public void playVideo(String file) {

	}

	@JavascriptInterface
	@APIMethod(description = "plays a sound", example = "media.playSound(fileName);")
	public void playSound(String url) {

		if (url.startsWith("http://") == false) {
			url = AppRunnerSettings.get().project.getUrl() 
					+ File.separator + url;
		}
		Audio.playSound(url, 100);
	}

	@JavascriptInterface
	@APIMethod(description = "plays a sound", example = "media.playSound(fileName);")
	public JPureData initPDPatch(String fileName, final String callbackfn) {
		String filePath = AppRunnerSettings.get().project.getUrl() + File.separator + fileName;

		PdReceiver receiver = new PdReceiver() {

			@Override
			public void print(String s) {
				Log.d("qq", "pd >>" + s);
				// callback(callbackfn, "print", s);
			}

			@Override
			public void receiveBang(String source) {
				Log.d("qq", "bang");
				// callback(callbackfn, "bang", source);
			}

			@Override
			public void receiveFloat(String source, float x) {
				Log.d("qq", "float: " + x);
				// callback(callbackfn, source, x);
			}

			@Override
			public void receiveList(String source, Object... args) {
				Log.d("qq", "list: " + Arrays.toString(args));

				JSONArray jsonArray = new JSONArray();
				for (int i = 0; i < args.length; i++) {
					jsonArray.put(args[i]);
				}

				// callback(callbackfn, source, jsonArray);
			}

			@Override
			public void receiveMessage(String source, String symbol,
					Object... args) {
				Log.d("qq", "message: " + Arrays.toString(args));
				// callback(callbackfn, source, symbol);
			}

			@Override
			public void receiveSymbol(String source, String symbol) {
				Log.d("qq", "symbol: " + symbol);
				// callback(callbackfn, source, symbol);
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

		(a.get()).bindService(intent, AudioService.pdConnection,
				(a.get()).BIND_AUTO_CREATE);
		initSystemServices();
		WhatIsRunning.getInstance().add(AudioService.pdConnection);

		return new JPureData();
	}

	private void initSystemServices() {
		TelephonyManager telephonyManager = (TelephonyManager) a.get()
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (AudioService.pdService == null)
					return;
				if (state == TelephonyManager.CALL_STATE_IDLE) {
					AudioService.start();
				} else {
					AudioService.pdService.stopAudio();
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@JavascriptInterface
	@APIMethod(description = "text to speech", example = "media.textToSpeech('hello world');")
	public void recordAudio(String fileName, boolean showProgress) {
		final MediaRecorder recorder = new MediaRecorder();
		// ContentValues values = new ContentValues(3);
		// values.put(MediaStore.MediaColumns.TITLE, fileName);
		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		recorder.setOutputFile(AppRunnerSettings.get().project.getUrl()
				+ File.separator + fileName);
		try {
			recorder.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}

		final ProgressDialog mProgressDialog = new ProgressDialog(a.get());
		mProgressDialog.setTitle("Record!");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setButton("Stop recording",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mProgressDialog.dismiss();
						recorder.stop();
						recorder.reset();
						recorder.release();
					}
				});

		mProgressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface p1) {
						recorder.stop();
						recorder.reset();
						recorder.release();
					}
				});

		recorder.start();

		if (showProgress == true) {
			mProgressDialog.show();
		}
	}

	@JavascriptInterface
	@APIMethod(description = "text to speech", example = "media.textToSpeech('hello world');")
	public void textToSpeech(String text) {
		Audio.speak(a.get(), text, Locale.getDefault());
	}

	// @JavascriptInterface
	// @APIAnnotation(description = "start voice recognition", example =
	// "media.startVoiceRecognition(function(text) { console.log(text) } );")
	public void startVoiceRecognition(final String callbackfn) {
		onVoiceRecognitionfn = callbackfn;

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me something!");
		((AppRunnerActivity) a.get()).startActivityForResult(intent,
				((AppRunnerActivity) a.get()).VOICE_RECOGNITION_REQUEST_CODE);

	}

	public interface onVoiceRecognitionListener {
		public void onNewResult(String text);
	}

}
