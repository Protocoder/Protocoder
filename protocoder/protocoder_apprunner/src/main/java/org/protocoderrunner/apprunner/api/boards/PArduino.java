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

import org.protocoderrunner.apidoc.annotation.APIMethod;
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

	@ProtocoderScript
	@APIMethod(description = "initializes arduino board", example = "arduino.start();")
	public void start() {
        try {
            mPhysicaloid = new Physicaloid(a.get());
            open();
            WhatIsRunning.getInstance().add(this);
            MLog.network(a.get(), TAG, "The device is ready");
        } catch (Exception e) {
            MLog.network(a.get(), TAG, "Error starting Arduino: " + e.getMessage() + e);
        }
	}

    // Opens a device and communicate USB UART by default settings
    public void open() {
        if (mPhysicaloid.isOpened()) {
            MLog.network(a.get(), TAG, "The device is opened");
            return;
        }

        if (mPhysicaloid.open()) {
            MLog.network(a.get(), TAG, "Device opened");
        } else {
            MLog.network(a.get(), TAG, "Cannot open the device");
        }
    }

    // Closes a device
    public void close() {
        if (mPhysicaloid.close()) {
            MLog.network(a.get(), TAG, "Device closed");
        } else {
            MLog.network(a.get(), TAG, "Cannot close the device");
        }
    }

    @ProtocoderScript
    @APIMethod(description = "sends commands to arduino board", example = "arduino.writeSerial(\"LEDON\");")
    public void writeSerial(String cmd) {
        if (mPhysicaloid.isOpened()) {
            byte[] buf = cmd.getBytes();
            mPhysicaloid.write(buf, buf.length);
            mPhysicaloid.close();
            MLog.network(a.get(), TAG, "Command sent to the device");
        } else {
            MLog.network(a.get(), TAG, "Cannot write to the device. The device is not opened");
        }
    }

    @ProtocoderScript
    @APIMethod(description = "read from the arduino board", example = "")
    public String readSerial() {
        String str = "";
        if (mPhysicaloid.isOpened()) {
            byte[] buf = new byte[256];
            int readSize = mPhysicaloid.read(buf);

            if(readSize>0) {
                try {
                    str = new String(buf, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    MLog.e(TAG,e.toString());
                }
            }
        } else {
            MLog.network(a.get(), TAG, "Cannot read from the device. The device is not opened");
        }
        return str;
    }

    // Uploads a binary file to a device on background process. No need to open().
    // * @param board board profile e.g. Packages.com.physicaloid.lib.Boards.ARDUINO_UNO
    // * @param fileName a binary file name e.g. Blink.hex
    //
    @ProtocoderScript
    @APIMethod(description = "Uploads a binary file to a device on background process. No need to open().", example = "")
    public void upload(Boards board, String fileName) {

        if (mPhysicaloid.isOpened()) {
            // TODO
            // Check if the fileName includes the .hex extension
            String filePath = AppRunnerSettings.get().project.getStoragePath() + File.separator + fileName;

            // TODO
            // Check if the file exists

            try {
                //****************************************************************
                // TODO : set board type and assets file.
                // TODO : copy .hex file to porject_dir/assets directory.
                mPhysicaloid.upload(board, filePath);
                //****************************************************************
            } catch (RuntimeException e) {
                MLog.e(TAG, e.toString());
            }
        } else {
            MLog.network(a.get(), TAG, "Cannot upload the sketch. The device is not opened");
        }
    }

    public void stop() {
        close();
    }
}