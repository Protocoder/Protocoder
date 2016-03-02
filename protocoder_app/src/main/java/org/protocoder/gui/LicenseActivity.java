/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoder.gui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import org.protocoder.R;
import org.protocoderrunner.base.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LicenseActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        //setToolbar();
        //setToolbarBack();

        //final ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setTitle("");
        //progressDialog.show();

        setLicense(R.id.websockets, R.raw.license_android_websockets);
        setLicense(R.id.svg_android, R.raw.license_svg_android);
        setLicense(R.id.commonslang, R.raw.license_commons_lang);
        setLicense(R.id.commonsnet, R.raw.license_commons_net);
        setLicense(R.id.netutil, R.raw.license_netutil);
        setLicense(R.id.eventbus, R.raw.license_eventbus);
        setLicense(R.id.httpclient, R.raw.license_httpclient);
        setLicense(R.id.ioio, R.raw.license_ioiolib);
        setLicense(R.id.mail, R.raw.license_mail);
        setLicense(R.id.osmdroid, R.raw.license_osmdroid);
        setLicense(R.id.libpd, R.raw.license_libpd);
        setLicense(R.id.physicaloid, R.raw.license_physicaloid);
        setLicense(R.id.processing, R.raw.license_processing);
        setLicense(R.id.gson, R.raw.license_gson);
        setLicense(R.id.usb_serial, R.raw.license_usbserial);
        setLicense(R.id.rhino, R.raw.license_mozilla_rhino);
        setLicense(R.id.nano, R.raw.license_nano_httpd);
        setLicense(R.id.zip4j, R.raw.license_zip4j);
    }

    private void setLicense(int res, final int text) {
        final TextView v = (TextView) findViewById(res);

        final Handler handler = new Handler();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String txt = readFile(text);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        v.setText(txt);
                    }
                });

            }
        });
        t.start();
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
