package com.makewithmoto.media;

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

		mMediaPlayer
				.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent) {
						Log.d(TAG, "" + percent);
					}
				});

		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.d(TAG, "completed");
			}
		});

		// mMediaPlayer.reset();
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

	public static void speak(Context c, final String textMsg,
			final Locale locale) {
		// Initialize text-to-speech. This is an asynchronous operation.
		// The OnInitListener (second argument) is called after initialization
		// completes.
		mTts = new TextToSpeech(c, new OnInitListener() {

			// Implements TextToSpeech.OnInitListener.
			@Override
			public void onInit(int status) {
				// status can be either TextToSpeech.SUCCESS or
				// TextToSpeech.ERROR.
				if (status == TextToSpeech.SUCCESS) {
					// Set preferred language to US english.
					// Note that a language may not be available, and the result
					// will indicate this.
					// int result =
					// mTts.setLanguage(Locale.getAvailableLocales()[0]);
					// Try this someday for some interesting results.
					int result = mTts.setLanguage(locale);
					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						// Lanuage data is missing or the language is not
						// supported.
						Log.e(TAG, "Language is not available.");
					} else {
						// Check the documentation for other possible result
						// codes.
						// For example, the language may be available for the
						// locale,
						// but not for the specified country and variant.

						// Greet the user.
						mTts.speak(textMsg, TextToSpeech.QUEUE_FLUSH, // Drop
																		// all
																		// pending
																		// entries
																		// in
																		// the
																		// playback
																		// queue.
								null);

					}
				} else {
					// Initialization failed.
					Log.e(TAG, "Could not initialize TextToSpeech.");
				}
			}
		}); // TextToSpeech.OnInitListener
	}

	public final static int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	/**
	 * Fire an intent to start the speech recognition activity. onActivityResult
	 * is handled in BaseActivity
	 */
	private void startVoiceRecognitionActivity(Activity a) {

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me something!");
		a.startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	public static void setSpeakerOn(boolean b) {

		Class audioSystemClass;
		try {
			audioSystemClass = Class.forName("android.media.AudioSystem");
			Method setForceUse = audioSystemClass.getMethod("setForceUse",
					int.class, int.class);
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
