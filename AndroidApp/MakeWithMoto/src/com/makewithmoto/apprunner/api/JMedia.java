package com.makewithmoto.apprunner.api;

import java.io.File;
import java.util.Locale;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.media.Audio;


public class JMedia extends JInterface {

	String TAG = "JMedia";
	String onVoiceRecognitionfn;

	public JMedia(AppRunnerActivity a) {
		super(a);
		

		((AppRunnerActivity) a).addVoiceRecognitionListener(new onVoiceRecognitionListener() {


			@Override
			public void onNewResult(String text) {
				Log.d(TAG, "" + text);
				callback(onVoiceRecognitionfn, "\"" +  text + "\"");
			}

		});


	}

//	@JavascriptInterface
//	@APIAnnotation(description = "plays a video", example = "media.playVieo(fileName);")
	public void playVideo(String file) {
		
	}	
	
	@JavascriptInterface
	@APIMethod(description = "plays a sound", example = "media.playSound(fileName);")
	public void playSound(String url) {
	
		if (url.startsWith("http://") == false) {
			url = ((AppRunnerActivity) a.get()).getCurrentDir() + File.separator + url;
		}
		Audio.playSound(url, 100);
	}	
	
	
	@JavascriptInterface
	@APIMethod(description = "text to speech", example = "media.textToSpeech('hello world');")
	public void textToSpeech(String text) {
		Audio.speak(a.get(), text, Locale.getDefault());
	}	
	
	//@JavascriptInterface
	//@APIAnnotation(description = "start voice recognition", example = "media.startVoiceRecognition(function(text) { console.log(text) } );")
	public void startVoiceRecognition(final String callbackfn) {
		onVoiceRecognitionfn = callbackfn;

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me something!"); 
		((AppRunnerActivity) a.get()).startActivityForResult(intent, ((AppRunnerActivity) a.get()).VOICE_RECOGNITION_REQUEST_CODE ); 
		
		
	}	
	
	

	public interface onVoiceRecognitionListener {
		public void onNewResult(String text);
	}
	

}
