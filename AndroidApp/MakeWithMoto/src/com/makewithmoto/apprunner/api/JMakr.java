package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.util.Log;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;
import com.makewithmoto.hardware.MAKRBoard;

public class JMakr extends JInterface {
	

	private ReadThread mReadThread;
	private String receivedData;
	private MAKRBoard makr;
	private String TAG = "JMakr";
	
	boolean isStarted = false;
	private String callbackfn;

	public JMakr(Activity a) {
		super(a);
		makr = new MAKRBoard();
	}
	
	@JavascriptInterface
	@APIMethod(description = "initializes makr board", example = "makr.start();")	
	public void start(final String callbackfn) {
		
		if(!isStarted){
	         /* Create a receiving thread */
		    mReadThread = new ReadThread();
		    mReadThread.start();
		
            makr.start();
            
            isStarted = true;
            this.callbackfn = callbackfn;
		}
		
		
		
	}
	
	@JavascriptInterface
	@APIMethod(description = "clean up and poweroff makr board", example = "makr.stop();")
	public void stop() {
		if(isStarted){
		    isStarted = false;
		    if (mReadThread != null)
			    mReadThread.interrupt();
		    makr.stop(); 
		}
	}

	
	private class ReadThread extends Thread {
		

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				//receivedData = "";
				
				if(isStarted){
				    receivedData = makr.readSerial().trim();
				}
				
				Log.d("MAKr", "" + receivedData);
				
				if(receivedData != "") {
				    a.get().runOnUiThread(new Runnable() {
					    public void run() {

							Log.d(TAG,"Got data: "+receivedData);
							Log.d(TAG,"callback "+callbackfn);

						   // previous callback callback("OnSerialRead("+receivedData+");");   
						    callback(callbackfn, "\"" +  receivedData + "\"");   
					    }
				    });	
				}		
			}
		}
	}
	
	
	@JavascriptInterface
	@APIMethod(description = "sends commands to makr board", example = "makr.writeSerial(\"LEDON\");")
	public void writeSerial(String cmd) {
		if(isStarted){
		    makr.writeSerial(cmd);
		}
	}

	
	@JavascriptInterface
	@APIMethod(description = "resumes makr activity", example = "makr.resume();")
	public void resume() {
		makr.resume();
	}
	
	@JavascriptInterface
	@APIMethod(description = "pause makr activity", example = "makr.pause();")
	public void pause() {
		makr.pause();
	}

}