/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
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

package org.protocoder.media;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class Audio {

    private static final String TAG = "AudioPlayer";

    public static MediaPlayer playSound(String url, int volume) {
	Log.d(TAG, "playing " + url);

	final MediaPlayer mMediaPlayer = new MediaPlayer();
	mMediaPlayer.setLooping(false);
	mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

	    @Override
	    public void onPrepared(MediaPlayer mp) {
		Log.d(TAG, "prepared");
		mMediaPlayer.start();

	    }
	});

	mMediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

	    @Override
	    public void onBufferingUpdate(MediaPlayer mp, int percent) {
		Log.d(TAG, "" + percent);
	    }
	});

	mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

	    @Override
	    public void onCompletion(MediaPlayer mp) {
		Log.d(TAG, "completed");
		mMediaPlayer.reset();
	    }
	});

	try {

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
	mMediaPlayer.setVolume(volume, volume);

	return mMediaPlayer;
    }

    public static void setVolume(MediaPlayer mMediaPlayer, int value) {
	float volume = (float) (value) / 100;
	Log.d(TAG, "" + volume);
	mMediaPlayer.setVolume(volume, volume);

    }

    static TextToSpeech mTts;

    public static void speak(Context c, final String textMsg, final Locale locale) {
	mTts = new TextToSpeech(c, new OnInitListener() {
	    @Override
	    public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {

		    int result = mTts.setLanguage(locale);
		    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
			// Lanuage data is missing or the language is not
			// supported.
			Log.e(TAG, "Language is not available.");
		    } else {

			mTts.speak(textMsg, TextToSpeech.QUEUE_FLUSH, null);

		    }
		} else {
		    // Initialization failed.
		    Log.e(TAG, "Could not initialize TextToSpeech.");
		}
	    }
	});
    }

    public final static int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    /**
     * Fire an intent to start the speech recognition activity. onActivityResult is handled in BaseActivity
     */
    private void startVoiceRecognitionActivity(Activity a) {

	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me something!");
	a.startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    public static void setSpeakerOn(boolean b) {

	Class audioSystemClass;
	try {
	    audioSystemClass = Class.forName("android.media.AudioSystem");
	    Method setForceUse = audioSystemClass.getMethod("setForceUse", int.class, int.class);
	    // First 1 == FOR_MEDIA, second 1 == FORCE_SPEAKER. To go back to
	    // the default
	    // behavior, use FORCE_NONE (0).
	    setForceUse.invoke(null, 1, 1);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (NoSuchMethodException e) {
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    e.printStackTrace();
	}

    }

}
