package com.makewithmoto.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;

public class VoiceRecognitionActivity extends FragmentActivity {

	private static final String TAG = "VOICE"; 
	
	final int VOICE_RECOGNITION_REQUEST_CODE = 1234; 


	static Context c;
	private TextToSpeech mTts;

	String textMsg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		c = getApplicationContext();

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		// , FLAG_SHOW_WHEN_LOCKED, FLAG_TURN_SCREEN_ON


		// setContentView(R.layout.imageview);
		startVoiceRecognitionActivity(); 
		
	}

	/**
	 * Fire an intent to start the speech recognition activity.
	 */
	private void startVoiceRecognitionActivity() {


		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me something!"); 
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE); 
		
	} 
	
	   
    /**
     * Handle the results from the recognition activity. 
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
      
            Object[] _object = new Object[matches.size()]; 
            
            int counter = 0; 
            for (String _string : matches) {
				Log.d(TAG, "" + _string); 
				_object[counter++] = _string; 
				
			} 
            
            //TODO send back result 
            //osc.send(OSCMessageType.OSC_LISTEN_RESULT, _object); 
            
        } 
        
        finish(); 

        super.onActivityResult(requestCode, resultCode, data);
    } 
    

}
