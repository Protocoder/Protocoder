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

package org.protocoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class LicenseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_license);

		// Websockets
		TextView websocketsLicense = (TextView) findViewById(R.id.websockets);
		websocketsLicense.setText(readFile(R.raw.license_android_websockets));

		// NetUtil
		TextView netutilLicense = (TextView) findViewById(R.id.netutil);
		netutilLicense.setText(readFile(R.raw.license_netutil));

		// libpd
		TextView libpdLicense = (TextView) findViewById(R.id.libpd);
		libpdLicense.setText(readFile(R.raw.license_libpd));

		// commons-lang
		TextView commonslangLicense = (TextView) findViewById(R.id.commonslang);
		commonslangLicense.setText(readFile(R.raw.license_commons_lang));

		// commons-net
		TextView commonsnetLicense = (TextView) findViewById(R.id.commonsnet);
		commonsnetLicense.setText(readFile(R.raw.license_commons_net));

		// eventbus
		TextView eventbusLicense = (TextView) findViewById(R.id.eventbus);
		eventbusLicense.setText(readFile(R.raw.license_eventbus));

		// gson
		TextView gsonLicense = (TextView) findViewById(R.id.gson);
		gsonLicense.setText(readFile(R.raw.license_gson));

		// ioio
		TextView ioioLicense = (TextView) findViewById(R.id.ioio);
		ioioLicense.setText(readFile(R.raw.license_ioiolib));

		// rhino
		TextView rhinoLicense = (TextView) findViewById(R.id.rhino);
		rhinoLicense.setText(readFile(R.raw.license_mozilla_rhino));

		// nano
		TextView nanoLicense = (TextView) findViewById(R.id.nano);
		nanoLicense.setText(readFile(R.raw.license_nano));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.license, menu);
		return true;
	}

	/**
	 * Returns a string from a txt file resource
	 * 
	 * @return
	 */
	private String readFile(int resource) {
		InputStream inputStream = getResources().openRawResource(resource);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return byteArrayOutputStream.toString();
	}

}
