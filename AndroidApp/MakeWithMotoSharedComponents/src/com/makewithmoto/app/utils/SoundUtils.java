package com.makewithmoto.app.utils;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class SoundUtils {

	private static final String TAG = "AudioPlayer";


	public static MediaPlayer playSound(String url, int volume) {
		final MediaPlayer mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setLooping(true);
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
	
	public static void speak(Context c, final String textMsg, final Locale locale) { 
		// Initialize text-to-speech. This is an asynchronous operation.
        // The OnInitListener (second argument) is called after initialization completes.
		mTts = new TextToSpeech(c, new OnInitListener() {
			
        	
        	    // Implements TextToSpeech.OnInitListener.
        	    public void onInit(int status) {
        	        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        	        if (status == TextToSpeech.SUCCESS) {
        	            // Set preferred language to US english.
        	            // Note that a language may not be available, and the result will indicate this.
        	           // int result = mTts.setLanguage(Locale.getAvailableLocales()[0]); 
        	            // Try this someday for some interesting results.
        	            int result = mTts.setLanguage(locale); 
        	            if (result == TextToSpeech.LANG_MISSING_DATA ||
        	                result == TextToSpeech.LANG_NOT_SUPPORTED) {
        	               // Lanuage data is missing or the language is not supported.
        	               Log.e(TAG, "Language is not available.");
        	            } else {
        	                // Check the documentation for other possible result codes.
        	                // For example, the language may be available for the locale,
        	                // but not for the specified country and variant.
        	            	
        	                // Greet the user.
        	            	  mTts.speak(textMsg,
        	            	            TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
        	            	            null); 
        	            	        
        	            }
        	        } else {
        	            // Initialization failed.
        	            Log.e(TAG, "Could not initialize TextToSpeech.");
        	        } 
        	    } 
        });  // TextToSpeech.OnInitListener 
	}



}
