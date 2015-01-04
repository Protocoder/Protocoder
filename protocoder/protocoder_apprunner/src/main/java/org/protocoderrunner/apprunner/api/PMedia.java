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

package org.protocoderrunner.apprunner.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.other.PAudioRecorder;
import org.protocoderrunner.apprunner.api.other.PMidi;
import org.protocoderrunner.apprunner.api.other.PPureData;
import org.protocoderrunner.media.Audio;
import org.protocoderrunner.media.AudioService;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.MLog;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.PdDispatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class PMedia extends PInterface {

    String TAG = "PMedia";

    private HeadSetReceiver headsetPluggedReceiver;
    private MicPluggedCB headsetCallbackfn;
    PAudioRecorder rec;

    public PMedia(Context c) {
		super(c);
        rec = new PAudioRecorder(getContext());

		WhatIsRunning.getInstance().add(this);
	}

	@ProtocoderScript
	@APIMethod(description = "Play a sound file", example = "media.playSound(fileName);")
	@APIParam(params = { "fileName" })
	public MediaPlayer playSound(String url) {

		if (url.startsWith("http://") == false) {
			url = AppRunnerSettings.get().project.getStoragePath() + File.separator + url;
		}
		MediaPlayer player = Audio.playSound(url, 100);
		WhatIsRunning.getInstance().add(player);
		return player;
	}


	@ProtocoderScript
	@APIMethod(description = "Set the main volume", example = "media.playSound(fileName);")
	@APIParam(params = { "volume" })
	public void setVolume(int volume) {
		AndroidUtils.setVolume(getContext(), volume);
	}

    @ProtocoderScript
    @APIMethod(description = "Routes the audio through the speakers", example = "media.playSound(fileName);")
    @APIParam(params = { "" })
    public void setAudioOnSpeakers(boolean b) {
        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(!b);
    }

    @ProtocoderScript
    @APIMethod(description = "Enable sounds effects (default false)", example = "")
    @APIParam(params = { "boolean" })
    public void enableSoundEffects(boolean b) {
        AndroidUtils.setEnableSoundEffects(getContext(), b);
    }

    // --------- initPDPatch ---------//
	interface initPDPatchCB {
		void event(PDReturn o);
	}

	class PDReturn {
		String type;
		protected String source;
		protected Object data;

	}

	@ProtocoderScript
	@APIMethod(description = "Loads and initializes a PureData patch http://www.puredata.info", example = "")
	@APIParam(params = { "fileName", "function(objectType, value)" })
	public PPureData initPDPatch(String fileName, final initPDPatchCB callbackfn) {
		String filePath = AppRunnerSettings.get().project.getStoragePath() + File.separator + fileName;

		PdUiDispatcher receiver = new PdUiDispatcher() {

			@Override
			public void print(String s) {
				MLog.d(TAG, "pd >>" + s);

				PDReturn o = new PDReturn();
				o.type = "print";
				o.data = s;

				callbackfn.event(o);
			}

			@Override
			public void receiveBang(String source) {
				MLog.d(TAG, "bang");

				PDReturn o = new PDReturn();
				o.type = "bang";
				o.source = source;

				callbackfn.event(o);
			}

			@Override
			public void receiveFloat(String source, float x) {
				MLog.d(TAG, "float: " + x);

				PDReturn o = new PDReturn();
				o.type = "float";
				o.source = source;
				o.data = x;

				callbackfn.event(o);
			}

			@Override
			public void receiveList(String source, Object... args) {
				MLog.d(TAG, "list: " + Arrays.toString(args));

				JSONArray jsonArray = new JSONArray();
				for (Object arg : args) {
					jsonArray.put(arg);
				}

				PDReturn o = new PDReturn();
				o.type = "list";
				o.source = source;
				o.data = jsonArray;

				callbackfn.event(o);
			}

			@Override
			public void receiveMessage(String source, String symbol, Object... args) {
				MLog.d(TAG, "message: " + Arrays.toString(args));

				JSONArray jsonArray = new JSONArray();
				for (Object arg : args) {
					jsonArray.put(arg);
				}

				PDReturn o = new PDReturn();
				o.type = "message";
				o.source = source;
				o.data = jsonArray;

				callbackfn.event(o);
			}

			@Override
			public void receiveSymbol(String source, String symbol) {
				MLog.d(TAG, "symbol: " + symbol);

				PDReturn o = new PDReturn();
				o.type = "symbol";
				o.source = source;
				o.data = symbol;

				callbackfn.event(o);
			}

		};

		// create and install the dispatcher
		PdDispatcher dispatcher = new PdUiDispatcher() {

			@Override
			public void print(String s) {
				Log.i("Pd print", s);
			}

		};

		//PdBase.setReceiver(dispatcher);

		PdBase.setReceiver(receiver);
		PdBase.subscribe("android");
		// start pure data sound engine
		AudioService.file = filePath;

		Intent intent = new Intent((getContext()), PdService.class);

		(getContext()).bindService(intent, AudioService.pdConnection, Context.BIND_AUTO_CREATE);
		initSystemServices();
		WhatIsRunning.getInstance().add(AudioService.pdConnection);

		return new PPureData();
	}

	private void initSystemServices() {
		TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
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

    boolean recording = false;

	@ProtocoderScript
	@APIMethod(description = "Record a sound with the microphone", example = "")
	@APIParam(params = { "fileName", "showProgressBoolean" })
	public void startAudioRecord(String fileName, boolean showProgress) {
        if (!recording) {
            recording = true;
            if (AppRunnerSettings.get().hasUi) {
                rec.startRecordingWithUi(getActivity());
            }

            rec.startRecording(fileName, showProgress & AppRunnerSettings.get().hasUi);
        }
	}

    @ProtocoderScript
	@APIMethod(description = "Record a sound with the microphone", example = "")
	@APIParam(params = { "fileName", "showProgressBoolean" })
	public void stopAudioRecord() {
        if (recording) {
            rec.stopRecording();
            recording = false;
        }

	}


	@ProtocoderScript
	@APIMethod(description = "Says a text with voice", example = "media.textToSpeech('hello world');")
	@APIParam(params = { "text" })
	public void textToSpeech(String text) {
        Audio.speak(getContext(), text, Locale.getDefault());
	}

	@ProtocoderScript
	@APIMethod(description = "Says a text with voice using a defined locale", example = "media.textToSpeech('hello world');")
	@APIParam(params = { "text", "Locale" })
	public void textToSpeech(String text, Locale locale) {
        Audio.speak(getContext(), text, locale);
	}


	// --------- startVoiceRecognition ---------//
	interface StartVoiceRecognitionCB {
		void event(String responseString);
	}

	@ProtocoderScript
	@APIMethod(description = "Fires the voice recognition and returns the best match", example = "media.startVoiceRecognition(function(text) { console.log(text) } );")
	@APIParam(params = { "function(recognizedText)" })
	public void startVoiceRecognition(final StartVoiceRecognitionCB callbackfn) {
        if (getActivity() == null) return;

        (getActivity()).addVoiceRecognitionListener(new onVoiceRecognitionListener() {

            @Override
            public void onNewResult(String text) {
                MLog.d(TAG, "" + text);
                callbackfn.event(text);
            }

        });

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me something!");
		getActivity().startActivityForResult(intent, getActivity().VOICE_RECOGNITION_REQUEST_CODE);

	}

    // --------- startVoiceRecognition ---------//
    interface StartVoiceRecognition2CB {
        void event(String status, String responseString);
    }
    public SpeechRecognizer startVoiceRecognition2(final StartVoiceRecognition2CB callbackfn) {

        String langMode = "en-US";
        langMode = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;
        SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(getContext());

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, langMode);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getClass().getPackage().getName());
        sr.startListening(intent);

        sr.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                callbackfn.event("ended", "");
            }

            @Override
            public void onError(int error) {
                callbackfn.event("error", "");
            }

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                for (int i = 0; i < matches.size(); i++) {
                    MLog.d(TAG, "result " + i + " " + matches.get(i));
                }

                callbackfn.event("result", matches.get(matches.size()));
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                for (int i = 0; i < matches.size(); i++) {
                    MLog.d(TAG, "partialResult " + i + " " + matches.get(i));
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        return sr;

       // sr.stopListening();
    }

	public interface onVoiceRecognitionListener {
		public void onNewResult(String text);
	}

	public void stop() {
        getContext().unregisterReceiver(headsetPluggedReceiver);
    }


    @ProtocoderScript
    @APIMethod(description = "Start a connected midi device", example = "media.startVoiceRecognition(function(text) { console.log(text) } );")
    @APIParam(params = { "function(recognizedText)" })
    public void startMidiDevice(final PMidi.MidiDeviceEventCB callbackfn) {
        PMidi pMidi = new PMidi(getContext(), callbackfn);
    }

    public boolean isHeadsetPlugged() {
        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        return audioManager.isWiredHeadsetOn();
    }

    interface MicPluggedCB {
        void event(boolean b);
    }

    public void startHeadsetListener(MicPluggedCB callbackfn) {
        WhatIsRunning.getInstance().add(this);
        headsetCallbackfn = callbackfn;

        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        headsetPluggedReceiver = new HeadSetReceiver();
        getContext().registerReceiver(headsetPluggedReceiver, filter);
    }

    private class HeadSetReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset unplugged");
                        headsetCallbackfn.event(false);
                        break;
                    case 1:
                        Log.d(TAG, "Headset plugged");
                        headsetCallbackfn.event(true);
                        break;
                }
            }
        }
    }

}

