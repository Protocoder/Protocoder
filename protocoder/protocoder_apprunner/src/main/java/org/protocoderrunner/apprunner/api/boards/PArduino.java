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

package org.protocoderrunner.apprunner.api.boards;

import android.app.Activity;

import com.physicaloid.lib.Boards;
import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.Physicaloid.UploadCallBack;
import com.physicaloid.lib.programmer.avr.UploadErrors;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.sensors.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class PArduino extends PInterface {

	private final String TAG = "PArduino";
    private Physicaloid mPhysicaloid;

	public PArduino(Activity a) {
		super(a);

	}

    // Initializes arduino board
	public void start(int bauds, onReadCB callbackfn) {
        MLog.network(a.get(), "PArduino", "start ");

        mPhysicaloid = new Physicaloid(a.get());
        MLog.network(a.get(), "PArduino", "physicaloid q" + mPhysicaloid);

        open(bauds);
        MLog.network(a.get(), "PArduino", "post open " + mPhysicaloid);

        onRead(callbackfn);
        MLog.network(a.get(), "PArduino", "post onRead " + mPhysicaloid);

        WhatIsRunning.getInstance().add(this);
	}

    // Opens a device and communicate USB UART by default settings
    public void open(int bauds) {
        if (mPhysicaloid.isOpened()) {
            MLog.network(a.get(), TAG, "The device is opened");
            return;
        }

        if (mPhysicaloid.open()) {
            MLog.network(a.get(), TAG, "Device opened");
        } else {
            MLog.network(a.get(), TAG, "Cannot open the device");
        }
        mPhysicaloid.setBaudrate(bauds);

    }

    // Closes a device
    public void close() {
        if (mPhysicaloid.close()) {
            //  clear read callback
            mPhysicaloid.clearReadListener();
            MLog.network(a.get(), TAG, "Device closed");
        } else {
            MLog.network(a.get(), TAG, "Cannot close the device");
        }
    }

    @ProtocoderScript
    @APIMethod(description = "sends commands to arduino board", example = "arduino.write(\"LEDON\");")
    public void write(String cmd) {
        if (mPhysicaloid.isOpened()) {
            byte[] buf = cmd.getBytes();
            mPhysicaloid.write(buf, buf.length);
            MLog.network(a.get(), TAG, "Command sent to the device");
        } else {
            MLog.network(a.get(), TAG, "Cannot write to the device. The device is not opened");
        }
    }

    @ProtocoderScript
    @APIMethod(description = "reads from the arduino board", example = "arduino.read()")
    public String read() {
        String str = "";
        if (mPhysicaloid.isOpened()) {
            byte[] buf = new byte[256];
            int readSize = mPhysicaloid.read(buf);

            if(readSize>0) {
                try {
                    str = new String(buf, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    MLog.network(a.get(), TAG, e.toString());
                }
            }
        } else {
            MLog.network(a.get(), TAG, "Cannot read from the device. The device is not opened");
        }
        return str;
    }

    // --------- onReadCB ---------//
    public interface onReadCB {
        void event(String responseString);
    }

    @ProtocoderScript
    @APIMethod(description = "adds a read callback that is called when one or more bytes are read", example = "")
    @APIParam(params = { "function(data)" })
    public void onRead(final onReadCB callbackfn) {
        if (mPhysicaloid.isOpened()) {
            mPhysicaloid.addReadListener(new ReadLisener() {
                String readStr;

                // callback when reading one or more size buffer
                @Override
                public void onRead(int size) {
                    byte[] buf = new byte[size];

                    mPhysicaloid.read(buf, size);
                    try {
                        readStr = new String(buf, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        MLog.network(a.get(), TAG, e.toString());
                        return;
                    }

                    callbackfn.event(readStr);
                }
            });
        }
    }

    // --------- uploadCallback ---------//
    public interface uploadCallback {
        void event(Boolean error);
    }

    // Uploads a binary file to a device on background process. No need to open().
    // * @param board board profile e.g. Packages.com.physicaloid.lib.Boards.ARDUINO_UNO
    // * @param fileName a binary file name e.g. Blink.hex
    // * @param callbackfn callback when the upload has been completed (success, fail or error)
    //
    @ProtocoderScript
    @APIMethod(description = "uploads a binary file to a device on background process", example = "")
    @APIParam(params = { "board", "fileName", "function(error)" })
    public void upload(Boards board, String fileName, final uploadCallback callbackfn) {
        if (mPhysicaloid.isOpened()) {
            // Build the absolute path
            String filePath = AppRunnerSettings.get().project.getStoragePath() + File.separator + fileName;

            // Check if the fileName includes the .hex extension
            if (!filePath.toLowerCase().endsWith(".hex")) {
                MLog.network(a.get(), TAG, "Cannot upload the sketch. The file must have a .hex extension");
                return;
            }

            try {
                mPhysicaloid.upload(board, filePath, new UploadCallBack() {
                    String responseStr;

                    @Override
                    public void onPreUpload() {
                        MLog.network(a.get(), TAG, "Upload : Start");
                    }

                    @Override
                    public void onUploading(int value) {
                        MLog.network(a.get(), TAG, "Upload : " + value + " %");
                    }

                    @Override
                    public void onPostUpload(boolean success) {
                        if(success) {
                            MLog.network(a.get(), TAG, "Upload : Successful");

                            callbackfn.event(null);
                        } else {
                            MLog.network(a.get(), TAG, "Upload fail");

                            callbackfn.event(new Boolean(true));
                        }
                    }

                    @Override
                    public void onCancel() {
                        MLog.network(a.get(), TAG, "Cancel uploading");
                    }

                    @Override
                    public void onError(UploadErrors err) {
                        MLog.network(a.get(), TAG, "Error  : " + err.toString());

                        callbackfn.event(new Boolean(true));
                    }
                });
            } catch (RuntimeException e) {
                MLog.network(a.get(), TAG, e.toString());
            }

        } else {
            MLog.network(a.get(), TAG, "Cannot upload the sketch. The device is not opened");
        }
    }

    @ProtocoderScript
    @APIMethod(description = "uploads a binary file to a device on background process", example = "")
    @APIParam(params = { "board", "fileName" })
    public void upload(Boards board, String fileName) {

        final uploadCallback callbackfn = new uploadCallback() {
            @Override
            public void event(Boolean error) {

            }
        };

        upload(board, fileName, callbackfn);
    }

    @ProtocoderScript
    @APIMethod(description = "sets baud rate", example = "arduino.setBaudrate(9600)")
    @APIParam(params = { "baudrate" })
    public boolean setBaudrate(int baudrate) {
        try {
            return mPhysicaloid.setBaudrate(baudrate);
        } catch (RuntimeException e) {
            MLog.network(a.get(), TAG, e.toString());
            return false;
        }
    }

    @ProtocoderScript
    @APIMethod(description = "returns a list of the supported devices that you can use with the \"upload\" function", example = "arduino.getSupportedDevices()")
    public Boards[] getSupportedDevices() {
        Boards[] boards = new Boards[]{
                Boards.ARDUINO_UNO,
                Boards.ARDUINO_DUEMILANOVE_328,
                Boards.ARDUINO_DUEMILANOVE_168,
                Boards.ARDUINO_NANO_328,
                Boards.ARDUINO_NANO_168,
                Boards.ARDUINO_MEGA_2560_ADK,
                Boards.ARDUINO_MEGA_1280,
                Boards.ARDUINO_MINI_328,
                Boards.ARDUINO_MINI_168,
                Boards.ARDUINO_ETHERNET,
                Boards.ARDUINO_FIO,
                Boards.ARDUINO_BT_328,
                Boards.ARDUINO_BT_168,
                Boards.ARDUINO_LILYPAD_328,
                Boards.ARDUINO_LILYPAD_168,
                Boards.ARDUINO_PRO_5V_328,
                Boards.ARDUINO_PRO_5V_168,
                Boards.ARDUINO_PRO_33V_328,
                Boards.ARDUINO_PRO_33V_168,
                Boards.ARDUINO_NG_168,
                Boards.ARDUINO_NG_8,
                Boards.BALANDUINO,
                Boards.POCKETDUINO,
                Boards.PERIDOT,
                Boards.NONE};

        return boards;
    }

    @ProtocoderScript
    @APIMethod(description = "sets serial configuration", example = "")
    @APIParam(params = { "settings" })
    public void setConfig(UartConfig settings) {
        try {
            mPhysicaloid.setConfig(settings);
        } catch (RuntimeException e) {
            MLog.network(a.get(), TAG, e.toString());
        }
    }

    @ProtocoderScript
    @APIMethod(description = "sets data bits", example = "")
    @APIParam(params = { "dataBits" })
    public boolean setDataBits(int dataBits) {
        try {
            return mPhysicaloid.setDataBits(dataBits);
        } catch (RuntimeException e) {
            MLog.network(a.get(), TAG, e.toString());
            return false;
        }
    }

    @ProtocoderScript
    @APIMethod(description = "sets parity bits", example = "")
    @APIParam(params = { "parity" })
    public boolean setParity(int parity) {
        try {
            return mPhysicaloid.setParity(parity);
        } catch (RuntimeException e) {
            MLog.network(a.get(), TAG, e.toString());
            return false;
        }
    }

    @ProtocoderScript
    @APIMethod(description = "sets stop bits", example = "")
    @APIParam(params = { "stopBits" })
    public boolean setStopBits(int stopBits) {
        try {
            return mPhysicaloid.setStopBits(stopBits);
        } catch (RuntimeException e) {
            MLog.network(a.get(), TAG, e.toString());
            return false;
        }
    }

    @ProtocoderScript
    @APIMethod(description = "sets flow control DTR/RTS", example = "")
    @APIParam(params = { "stopBits" })
    public boolean setDtrRts(boolean dtrOn, boolean rtsOn) {
        try {
            return mPhysicaloid.setDtrRts(dtrOn, rtsOn);
        } catch (RuntimeException e) {
            MLog.network(a.get(), TAG, e.toString());
            return false;
        }
    }

    public void stop() {
        close();
    }
}