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

package org.protocoderrunner.media;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import org.protocoderrunner.utils.MLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class Audio {

    private static final String TAG = "AudioPlayer";

    public static void setVolume(MediaPlayer mMediaPlayer, int value) {
        float volume = (float) (value) / 100;
        MLog.d(TAG, "" + volume);
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
     * Fire an intent to start the speech recognition activity. onActivityResult
     * is handled in BaseActivity
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
