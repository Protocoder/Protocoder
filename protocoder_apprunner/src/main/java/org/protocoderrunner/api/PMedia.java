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

package org.protocoderrunner.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.protocoderrunner.api.media.PAudioPlayer;
import org.protocoderrunner.api.media.PAudioRecorder;
import org.protocoderrunner.api.media.PMidi;
import org.protocoderrunner.api.media.PPureData;
import org.protocoderrunner.api.media.PWave;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apidoc.annotation.ProtoObject;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.media.Audio;
import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.base.utils.MLog;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;

@ProtoObject
public class PMedia extends ProtoBase {

    String TAG = PMedia.class.getSimpleName();

    private HeadSetReceiver headsetPluggedReceiver;
    private MicPluggedCB headsetCallbackfn;
    PAudioPlayer player;
    PAudioRecorder rec;
    boolean recording = false;

    public PMedia(AppRunner appRunner) {
        super(appRunner);
        rec = new PAudioRecorder(appRunner);
    }

    //public PAudioPlayer loadSound(String url, PAudioPlayer.LoadSoundCB callbackfn) {
    //	return loadPlayer(url, callbackfn);
    //}

    @ProtoMethod(description = "Play a sound file giving its filename", example = "media.playSound(fileName);")
    @ProtoMethodParam(params = {"fileName"})
    public PAudioPlayer playSound(String url) {
        PAudioPlayer pAudioPlayer = new PAudioPlayer(getAppRunner(), url, false);

        return pAudioPlayer;
    }

    @ProtoMethod(description = "Play a sound file giving its filename. The second parameter indicates if the player can be reused or not.", example = "media.playSound(fileName);")
    @ProtoMethodParam(params = {"fileName", "autoFinish"})
    public PAudioPlayer playSound(String url, boolean reuse) {
        PAudioPlayer pAudioPlayer = new PAudioPlayer(getAppRunner(), url, reuse);

        return pAudioPlayer;
    }

    @ProtoMethod(description = "Set the main volume", example = "")
    @ProtoMethodParam(params = {"volume"})
    public void volume(int volume) {
        AndroidUtils.setVolume(getContext(), volume);
    }


    @ProtoMethod(description = "Routes the audio through the speakers", example = "media.playSound(fileName);")
    @ProtoMethodParam(params = {""})
    public void audioOnSpeakers(boolean b) {
        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(!b);
    }

    @ProtoMethod(description = "Enable sounds effects (default false)", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void enableSoundEffects(boolean b) {
        AndroidUtils.setEnableSoundEffects(getContext(), b);
    }

    /*
    @ProtoMethod(description = "Loads and initializes a PureData patch http://www.puredata.info using libpd", example = "")
    @ProtoMethodParam(params = {"fileName", "micChannels", "outputChannels", "sampleRate", "buffer"})
    public PPureData initPdPatch(String fileName, int micChannels, int outputChannels, int sampleRate, int buffer) {
        AudioServicePd.settingsSampleRate = sampleRate;
        AudioServicePd.settingsMicChannels = micChannels;
        AudioServicePd.settingsOutputChannels = outputChannels;
        AudioServicePd.settingsBuffer = buffer;

        return this.initPdPatch(fileName);
    }
    */

    @ProtoMethod(description = "Loads and initializes a PureData patch http://www.puredata.info using libpd", example = "")
    @ProtoMethodParam(params = {"fileName"})
    public PPureData initLibPd() {
        PPureData pPureData = new PPureData(getAppRunner());

        /*
        String filePath = getAppRunner().getProject().getFullPathForFile(fileName);
        pPureData.loadPatch(filePath);
        pPureData.start();

        pPureData.initPdService();
        */

        return pPureData;
    }

    @ProtoMethod(description = "Record a sound with the microphone", example = "")
    @ProtoMethodParam(params = {"fileName", "showProgressBoolean"})
    public void recordSound(String fileName, boolean showProgress) {
        if (!recording) {
            recording = true;
            if (getAppRunner().hasUserInterface) {
                rec.startRecordingWithUi(getActivity());
            }

            rec.startRecording(fileName, showProgress & getAppRunner().hasUserInterface);
        }
    }

    @ProtoMethod(description = "Record a sound with the microphone", example = "")
    @ProtoMethodParam(params = {"fileName", "showProgressBoolean"})
    public void stopRecordingSound() {
        if (recording) {
            rec.stopRecording();
            recording = false;
        }

    }


    @ProtoMethod(description = "Says a text with voice", example = "media.textToSpeech('hello world');")
    @ProtoMethodParam(params = {"text"})
    public void textToSpeech(String text) {
        Audio.speak(getContext(), text, Locale.getDefault());
    }


    @ProtoMethod(description = "Says a text with voice using a defined locale", example = "media.textToSpeech('hello world');")
    @ProtoMethodParam(params = {"text", "Locale"})
    public void textToSpeech(String text, Locale locale) {
        Audio.speak(getContext(), text, locale);
    }


    // --------- startVoiceRecognition ---------//
    interface StartVoiceRecognitionCB {
        void event(String responseString);
    }


    @ProtoMethod(description = "Fires the voice recognition and returns the best match", example = "media.startVoiceRecognition(function(text) { console.log(text) } );")
    @ProtoMethodParam(params = {"function(recognizedText)"})
    public void voiceRecognition(final StartVoiceRecognitionCB callbackfn) {
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

    @ProtoMethod(description = "Start a connected midi device", example = "media.startVoiceRecognition(function(text) { console.log(text) } );")
    @ProtoMethodParam(params = {"function(recognizedText)"})
    public PMidi connectMidiDevice() {
        PMidi pMidi = new PMidi(getAppRunner());

        return pMidi;
    }

    public boolean isHeadsetPlugged() {
        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        return audioManager.isWiredHeadsetOn();
    }

    interface MicPluggedCB {
        void event(boolean b);
    }

    public void headsetListener(MicPluggedCB callbackfn) {
        headsetCallbackfn = callbackfn;

        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        headsetPluggedReceiver = new HeadSetReceiver();
        getContext().registerReceiver(headsetPluggedReceiver, filter);
    }

    public PWave createWave() {
        PWave pWave = new PWave(getAppRunner());
        return pWave;
    }

    private class HeadSetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
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


    public Bitmap generateQRCode(String text) {
        Bitmap bmp = null;

        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // H = 30% damage

        int size = 256;

        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hintMap);

            int width = bitMatrix.getWidth();
            bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < width; y++) {
                    bmp.setPixel(y, x, bitMatrix.get(x, y) == true ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bmp;
    }

    public void scanQRcode(byte[] data, Camera camera) {
        Camera.Size size = camera.getParameters().getPreviewSize();

        // Create BinaryBitmap
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, size.width, size.height, 0, 0, size.width, size.height, false);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        // Read QR Code
        Reader reader = new MultiFormatReader();
        Result result = null;
        try {
            result = reader.decode(bitmap);
            String text = result.getText();

            MLog.d(TAG, "result: " + text);
        } catch (NotFoundException e) {
        } catch (ChecksumException e) {
        } catch (FormatException e) {
        }
    }

    @Override
    public void __stop() {
         getContext().unregisterReceiver(headsetPluggedReceiver);
    }
}

