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

package org.protocoder.apprunner.api.boards;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apprunner.PInterface;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.hardware.MAKRBoard;
import org.protocoder.utils.MLog;

import android.app.Activity;

public class PMakr extends PInterface {

	private ReadThread mReadThread;
	private String receivedData;
	private final MAKRBoard makr;
	private final String TAG = "JMakr";

	boolean isStarted = false;
	private startCB callbackfn;

	public PMakr(Activity a) {
		super(a);
		makr = new MAKRBoard();
	}

	// --------- getRequest ---------//
	public interface startCB {
		void event(String responseString);
	}

	@ProtocoderScript
	@APIMethod(description = "initializes makr board", example = "makr.start();")
	public void start(final startCB callbackfn2) {

		if (!isStarted) {
			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();

			makr.start();

			isStarted = true;
			this.callbackfn = callbackfn2;
		}

	}

	@ProtocoderScript
	@APIMethod(description = "clean up and poweroff makr board", example = "makr.stop();")
	public void stop() {
		if (isStarted) {
			isStarted = false;
			if (mReadThread != null) {
				mReadThread.interrupt();
			}
			makr.stop();
		}
	}

	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				// receivedData = "";

				if (isStarted) {
					receivedData = makr.readSerial().trim();
				}

				MLog.d("MAKr", "" + receivedData);

				if (receivedData != "") {
					a.get().runOnUiThread(new Runnable() {
						@Override
						public void run() {

							MLog.d(TAG, "Got data: " + receivedData);
							MLog.d(TAG, "callback " + callbackfn);

							// previous callback
							// callback("OnSerialRead("+receivedData+");");
							callbackfn.event(receivedData);
						}
					});
				}
			}
		}
	}

	@ProtocoderScript
	@APIMethod(description = "sends commands to makr board", example = "makr.writeSerial(\"LEDON\");")
	public void writeSerial(String cmd) {
		if (isStarted) {
			makr.writeSerial(cmd);
		}
	}

	@ProtocoderScript
	@APIMethod(description = "resumes makr activity", example = "makr.resume();")
	public void resume() {
		makr.resume();
	}

	@ProtocoderScript
	@APIMethod(description = "pause makr activity", example = "makr.pause();")
	public void pause() {
		makr.pause();
	}

}