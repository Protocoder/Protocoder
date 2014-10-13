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

import android.content.Context;

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
    private boolean started = false;
    private String msg = "";

    public static final Boards ARDUINO_UNO = Boards.ARDUINO_UNO;
    public static final Boards ARDUINO_DUEMILANOVE_328 = Boards.ARDUINO_DUEMILANOVE_328;
    public static final Boards ARDUINO_DUEMILANOVE_168 = Boards.ARDUINO_DUEMILANOVE_168;
    public static final Boards ARDUINO_NANO_328 = Boards.ARDUINO_NANO_328;
    public static final Boards ARDUINO_NANO_168 = Boards.ARDUINO_NANO_168;
    public static final Boards ARDUINO_MEGA_2560_ADK = Boards.ARDUINO_MEGA_2560_ADK;
    public static final Boards ARDUINO_MEGA_1280 = Boards.ARDUINO_MEGA_1280;
    public static final Boards ARDUINO_MINI_328 = Boards.ARDUINO_MINI_328;
    public static final Boards ARDUINO_MINI_168 = Boards.ARDUINO_MINI_168;
    public static final Boards ARDUINO_ETHERNET = Boards.ARDUINO_ETHERNET;
    public static final Boards ARDUINO_FIO = Boards.ARDUINO_FIO;
    public static final Boards ARDUINO_BT_328 = Boards.ARDUINO_BT_328;
    public static final Boards ARDUINO_BT_168 = Boards.ARDUINO_BT_168;
    public static final Boards ARDUINO_LILYPAD_328 = Boards.ARDUINO_LILYPAD_328;
    public static final Boards ARDUINO_LILYPAD_168 = Boards.ARDUINO_LILYPAD_168;

    public static final Boards ARDUINO_PRO_5V_328 = Boards.ARDUINO_PRO_5V_328;
    public static final Boards ARDUINO_PRO_5V_168 = Boards.ARDUINO_PRO_5V_168;
    public static final Boards ARDUINO_PRO_33V_328 = Boards.ARDUINO_PRO_33V_328;
    public static final Boards ARDUINO_PRO_33V_168 = Boards.ARDUINO_PRO_33V_168;
    public static final Boards ARDUINO_NG_168 = Boards.ARDUINO_NG_168;
    public static final Boards ARDUINO_NG_8 = Boards.ARDUINO_NG_8;
    public static final Boards BALANDUINO = Boards.BALANDUINO;
    public static final Boards POCKETDUINO = Boards.POCKETDUINO;
    public static final Boards PERIDOT = Boards.PERIDOT;

    public static final Boards FREADUINO = Boards.ARDUINO_UNO;
    public static final Boards BQ_ZUM = Boards.ARDUINO_BT_328;

    public static final Boards NONE = Boards.NONE;


    public PArduino(Context a) {
		super(a);
    }

    public void start() {
        WhatIsRunning.getInstance().add(this);
        mPhysicaloid = new Physicaloid(a.get());
        open();
    }

        // Initializes arduino board
	public void start(int bauds, onReadCB callbackfn) {
        WhatIsRunning.getInstance().add(this);
        if (!started) {
            started = true;
            MLog.d("PArduino", "start ");

            mPhysicaloid = new Physicaloid(a.get());
            open();
            mPhysicaloid.setBaudrate(bauds);

            onRead(callbackfn);
        }
	}

    // Opens a device and communicate USB UART by default settings
    public void open() {
        if (mPhysicaloid.isOpened()) {
            MLog.d(TAG, "The device is opened");
            return;
        }

        if (mPhysicaloid.open()) {
            MLog.d(TAG, "Device opened");
        } else {
            MLog.d(TAG, "Cannot open the device");
        }

    }

    // Closes a device
    public void close() {
        if (mPhysicaloid.close()) {
            //  clear read callback
            mPhysicaloid.clearReadListener();
            MLog.d(TAG, "Device closed");
        } else {
            MLog.d(TAG, "Cannot close the device");
        }
    }

    @ProtocoderScript
    @APIMethod(description = "sends commands to arduino board", example = "arduino.write(\"LEDON\");")
    public void write(String cmd) {
        if (mPhysicaloid.isOpened()) {
            byte[] buf = cmd.getBytes();
            mPhysicaloid.write(buf, buf.length);
            MLog.d(TAG, "Command sent to the device");
        } else {
            MLog.d(TAG, "Cannot write to the device. The device is not opened");
        }
    }

    // --------- onReadCB ---------//
    public interface onReadCB {
        void event(String responseString);
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
                    MLog.d(TAG, e.toString());
                }
            }
        } else {
            MLog.d(TAG, "Cannot read from the device. The device is not opened");
        }
        return str;
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
                        MLog.d(TAG, e.toString());
                        return;
                    }

                    msg = msg + readStr;
                    //MLog.network(a.get(), TAG, "msg " + msg);
                    //MLog.network(a.get(), TAG, "readStr " + readStr);

                    int newLineIndex = msg.indexOf('\n');
                    //MLog.network(a.get(), TAG, "index " + newLineIndex);
                    String msgReturn = "";

                    if (newLineIndex != -1) {
                        msgReturn = msg.substring(0, newLineIndex);
                        msg = msg.substring(newLineIndex + 1, msg.length());
                        //MLog.network(a.get(), TAG, "msgReturn " + msgReturn);

                    }
                    if (msgReturn.trim().equals("") == false) {
                        final String finalMsgReturn = msgReturn;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callbackfn.event(finalMsgReturn);
                            }
                        });
                    }
                }
            });
        }
    }

    // --------- uploadCallback ---------//
    public interface uploadCB {
        void event(int progress);
    }

    // Uploads a binary file to a device on background process. No need to open().
    // * @param board board profile e.g. Packages.com.physicaloid.lib.Boards.ARDUINO_UNO
    // * @param fileName a binary file name e.g. Blink.hex
    // * @param callbackfn callback when the upload has been completed (success, fail or error)
    //
    @ProtocoderScript
    @APIMethod(description = "uploads a binary file to a device on background process", example = "")
    @APIParam(params = { "board", "fileName", "function(error)" })
    public void upload(Boards board, String fileName, final uploadCB callbackfn) {
        if (mPhysicaloid.isOpened()) {
            // Build the absolute path
            String filePath = AppRunnerSettings.get().project.getStoragePath() + File.separator + fileName;

            callbackfn.event(0);
            // Check if the fileName includes the .hex extension
            if (!filePath.toLowerCase().endsWith(".hex")) {
                MLog.d(TAG, "Cannot upload the sketch. The file must have a .hex extension");

                return;
            }

            try {
                mPhysicaloid.upload(board, filePath, new UploadCallBack() {
                    String responseStr;

                    @Override
                    public void onPreUpload() {
                        MLog.d(TAG, "Upload : Start");
                    }

                    @Override
                    public void onUploading(int value) {
                        uploadCallbackEvent(value, callbackfn);
                    }

                    @Override
                    public void onPostUpload(boolean success) {
                        //MLog.network(a.get(), TAG, "5");

                        if(success) {
                       //     uploadCallbackEvent(100, callbackfn);
                        } else {

                            uploadCallbackEvent(-1, callbackfn);
                        }
                    }

                    @Override
                    public void onCancel() {
                        uploadCallbackEvent(-1, callbackfn);
                    }

                    @Override
                    public void onError(UploadErrors err) {
                        MLog.d(TAG, "Error  : " + err.toString());

                        uploadCallbackEvent(-1, callbackfn);
                    }
                });
            } catch (RuntimeException e) {
                MLog.d(TAG, e.toString());
            }

        } else {
            MLog.d(TAG, "Cannot upload the sketch. The device is not opened");
        }
    }

    private void uploadCallbackEvent(final int value, final uploadCB callbackfn) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callbackfn.event(value);
            }
        });
    }

    @ProtocoderScript
    @APIMethod(description = "uploads a binary file to a device on background process", example = "")
    @APIParam(params = { "board", "fileName" })
    public void upload(Boards board, String fileName) {

        upload(board, fileName, new uploadCB() {
            @Override
            public void event(int progress) {

            }
        });
    }

    @ProtocoderScript
    @APIMethod(description = "sets baud rate", example = "arduino.setBaudrate(9600)")
    @APIParam(params = { "baudrate" })
    public boolean setBaudrate(int baudrate) {
        try {
            return mPhysicaloid.setBaudrate(baudrate);
        } catch (RuntimeException e) {
            return false;
        }
    }

    @ProtocoderScript
    @APIMethod(description = "returns a list of the supported devices that you can use with the \"upload\" function", example = "arduino.getSupportedDevices()")
    public Boards[] getSupportedDevices() {
        MLog.d(TAG, Boards.ARDUINO_BT_328.toString());

        return com.physicaloid.lib.Boards.values();
        /*
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
        */
    }

    @ProtocoderScript
    @APIMethod(description = "sets serial configuration", example = "")
    @APIParam(params = { "settings" })
    public void setConfig(UartConfig settings) {
        try {
            mPhysicaloid.setConfig(settings);
        } catch (RuntimeException e) {
            MLog.d(TAG, e.toString());
        }
    }

    @ProtocoderScript
    @APIMethod(description = "sets data bits", example = "")
    @APIParam(params = { "dataBits" })
    public boolean setDataBits(int dataBits) {
        try {
            return mPhysicaloid.setDataBits(dataBits);
        } catch (RuntimeException e) {
            MLog.d(TAG, e.toString());
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
            MLog.d(TAG, e.toString());
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
            MLog.d(TAG, e.toString());
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
            MLog.d(TAG, e.toString());
            return false;
        }
    }

    public void stop() {
        close();
    }
}