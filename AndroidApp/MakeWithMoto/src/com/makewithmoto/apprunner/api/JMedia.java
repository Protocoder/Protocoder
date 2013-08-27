package com.makewithmoto.apprunner.api;

import java.io.File;
import java.util.Locale;

import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.media.Audio;


public class JMedia extends JInterface {

	public JMedia(AppRunnerActivity a) {
		super(a);
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
		String recognizedText = "";
		
		callback(callbackfn, recognizedText);

	}	
	
	
}
