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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apprunner.PInterface;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.sensors.WhatIsRunning;
import org.protocoder.utils.MLog;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

public class PArduino extends PInterface {

	private String receivedData;
	private final String TAG = "PArduino";

    private static UsbSerialPort sPort = null;

    boolean isStarted = false;
	private UsbSerialDriver driver;
	private SerialInputOutputManager.Listener mListener;
	private SerialInputOutputManager mSerialIoManager;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
	String msg = "";

	public PArduino(Activity a) {
		super(a);

	}

	// --------- getRequest ---------//
	public interface startCB {
		void event(String responseString);
	}

	@ProtocoderScript
	@APIMethod(description = "initializes makr board", example = "makr.start();")
	public void start(int bauds, final startCB callbackfn) {
		WhatIsRunning.getInstance().add(this);
		if (!isStarted) {
            // Get UsbManager from Android.
            UsbManager manager = (UsbManager) a.get().getSystemService(Context.USB_SERVICE);
            UsbDeviceConnection connection = manager.openDevice(sPort.getDriver().getDevice());

            if (connection == null) {
                //fail
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(bauds, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);


                mListener = new SerialInputOutputManager.Listener() {

                    @Override
                    public void onRunError(Exception e) {
                        MLog.d(TAG, "Runner stopped.");
                    }

                    @Override
                    public void onNewData(final byte[] data) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                String readMsg = new String(data, 0, data.length);
                                msg = msg + readMsg;
                                MLog.d(TAG, "preMsg " + msg);

                                int newLineIndex = msg.indexOf('\n');
                                MLog.d(TAG, "index " + newLineIndex);
                                String msgReturn = "";
                                if (newLineIndex != -1) {
                                    msgReturn = msg.substring(0, newLineIndex);
                                    msg = msg.substring(newLineIndex + 1, msg.length());
                                    MLog.d(TAG, "msgReturn " + msgReturn);
                                    MLog.d(TAG, "postMsg " + msg);
                                }

                                MLog.d(TAG, msg);
                                if (msgReturn.trim().equals("") == false) {
                                    callbackfn.event(msgReturn);
                                }
                            }
                        });
                    }
                };

                startIoManager();
                isStarted = true;

            } catch (IOException e) {
                MLog.e(TAG, "Error setting up device: " + e.getMessage() + e);
                //mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
            onDeviceStateChange();

        }

	}

	private void stopIoManager() {
        if (mSerialIoManager != null) {
            MLog.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
	}

	private void startIoManager() {
        if (sPort != null) {
            MLog.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
	}

	private void onDeviceStateChange() {
		stopIoManager();
		startIoManager();
	}

	@ProtocoderScript
	@APIMethod(description = "clean up and poweroff makr board", example = "makr.stop();")
	public void stop() {
		if (isStarted) {
			isStarted = false;

            stopIoManager();
            if (sPort != null) {
                try {
                    sPort.close();
                } catch (IOException e) {
                    // Ignore.
                }
                sPort = null;
            }


		}
	}

	@ProtocoderScript
	@APIMethod(description = "sends commands to makr board", example = "makr.writeSerial(\"LEDON\");")
	public void writeSerial(String cmd) {
		if (isStarted) {
			try {
				sPort.write(cmd.getBytes(), 1000);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@ProtocoderScript
	@APIMethod(description = "resumes makr activity", example = "makr.resume();")
	public void resume() {

	}

	@ProtocoderScript
	@APIMethod(description = "pause makr activity", example = "makr.pause();")
	public void pause() {

	}

}