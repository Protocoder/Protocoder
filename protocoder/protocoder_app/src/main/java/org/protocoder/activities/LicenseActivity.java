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

package org.protocoder.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.TextView;

import org.protocoder.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LicenseActivity extends Activity {


    String txtAndroidWebSockets;
    String txtSvgAndroid;
    String txtCommonsLang;
    String txtCommonsNet;
    String txtNetUtil;
    String txtEventBus;
    String txtHttpClient;
    String txtIoio;
    String txtMail;
    String txtOsmDroid;
    String txtLibPd;
    String txtPhysicaloid;
    String txtProcessing;
    String txtGson;
    String txtUsbSerial;
    String txtRhino;
    String txtNanoHttpd;
    String txtZip4j;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_license);


        //final ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setTitle("");
        //progressDialog.show();

        final TextView websocketsLicense = (TextView) findViewById(R.id.websockets);
        final TextView svgAndroidLicense = (TextView) findViewById(R.id.svg_android);
        final TextView commonslangLicense = (TextView) findViewById(R.id.commonslang);
        final TextView commonsnetLicense = (TextView) findViewById(R.id.commonsnet);
        final TextView netutilLicense = (TextView) findViewById(R.id.netutil);
        final TextView eventbusLicense = (TextView) findViewById(R.id.eventbus);
        final TextView httpClientLicense = (TextView) findViewById(R.id.httpclient);
        final TextView ioioLicense = (TextView) findViewById(R.id.ioio);
        final TextView mailLicense = (TextView) findViewById(R.id.mail);
        final TextView osmdroidLicense = (TextView) findViewById(R.id.osmdroid);
        final TextView libpdLicense = (TextView) findViewById(R.id.libpd);
        final TextView physicaloidLicense = (TextView) findViewById(R.id.physicaloid);
        final TextView processingLicense = (TextView) findViewById(R.id.processing);
        final TextView gsonLicense = (TextView) findViewById(R.id.gson);
        final TextView usbSerialLicense = (TextView) findViewById(R.id.usb_serial);
        final TextView rhinoLicense = (TextView) findViewById(R.id.rhino);
        final TextView nanoLicense = (TextView) findViewById(R.id.nano);
        final TextView zip4jLicense = (TextView) findViewById(R.id.zip4j);


        final Handler handler = new Handler();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                txtAndroidWebSockets = readFile(R.raw.license_android_websockets);
                txtSvgAndroid = readFile(R.raw.license_svg_android);
                txtCommonsLang = readFile(R.raw.license_commons_lang);
                txtCommonsNet = readFile(R.raw.license_commons_net);
                txtNetUtil = readFile(R.raw.license_netutil);
                txtEventBus = readFile(R.raw.license_eventbus);
                txtHttpClient = readFile(R.raw.license_httpclient);
                txtIoio = readFile(R.raw.license_ioiolib);
                txtMail = readFile(R.raw.license_mail);
                txtOsmDroid = readFile(R.raw.license_osmdroid);
                txtLibPd = readFile(R.raw.license_libpd);
                txtPhysicaloid = readFile(R.raw.license_physicaloid);
                txtProcessing = readFile(R.raw.license_processing);
                txtGson = readFile(R.raw.license_gson);
                txtUsbSerial = readFile(R.raw.license_usbserial);
                txtRhino = readFile(R.raw.license_mozilla_rhino);
                txtNanoHttpd = readFile(R.raw.license_nano_httpd);
                txtZip4j = readFile(R.raw.license_zip4j);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        websocketsLicense.setText(txtAndroidWebSockets);
                        svgAndroidLicense.setText(txtSvgAndroid);
                        commonslangLicense.setText(txtCommonsLang);


                        commonsnetLicense.setText(txtCommonsNet);
                        netutilLicense.setText(txtNetUtil);
                        eventbusLicense.setText(txtEventBus);
                        httpClientLicense.setText(txtHttpClient);
                        ioioLicense.setText(txtIoio);
                        mailLicense.setText(txtMail);
                        osmdroidLicense.setText(txtOsmDroid);
                        libpdLicense.setText(txtLibPd);
                        physicaloidLicense.setText(txtPhysicaloid);

                        processingLicense.setText(txtProcessing);

                        gsonLicense.setText(txtGson);
                        usbSerialLicense.setText(txtUsbSerial);
                        rhinoLicense.setText(txtRhino);
                        nanoLicense.setText(txtNanoHttpd);
                        zip4jLicense.setText(txtZip4j);

                    //    progressDialog.dismiss();
                    }
                });
            }
        });
        t.start();


    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.license, menu);
		return true;
	}

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
			e.printStackTrace();
		}
		return byteArrayOutputStream.toString();
	}

}
