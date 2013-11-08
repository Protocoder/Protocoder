/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
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

package org.protocoder.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class SysFs {
	
	static final String TAG = "SysFS";
	
	public static boolean write(String filename, String data) {
		try {
			File mpuFile = new File(filename);
			if(mpuFile.canWrite()) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
				bw.write(data);
				bw.close();
			} else {
				Process p = Runtime.getRuntime().exec("su");

				DataOutputStream dos = new DataOutputStream(p.getOutputStream());
				dos.writeBytes("echo " + data + " > " + filename + "\n");
				dos.writeBytes("exit");
				dos.flush();
				dos.close();

				if(p.waitFor() != 0) {
					Log.i(TAG, "Could not write to " + filename + " (exit: " + p.exitValue() + ")");
					InputStream in = p.getErrorStream();
					StringBuilder sb = new StringBuilder();
			        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			        String line = bufferedReader.readLine();
			        while (line != null) {
			        	sb.append(line); sb.append('\n');
			        	line = bufferedReader.readLine();
			        }
					Log.i(TAG, sb.toString());
				}
			}
		} catch (IOException ex) {
			Log.i(TAG, "Error: " + ex.getMessage());
			return false;
		} catch (InterruptedException e) {
			Log.i(TAG, "Error writing with root permission");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String read(String filename) {
		String value;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			value = br.readLine();
			br.close();
		} catch (IOException ex) {
			return "-1";
		}
		return value;
	}
	
}
