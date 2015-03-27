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

package org.protocoder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;

import org.protocoder.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LicenseActivity extends AppBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        setToolbar();
        setToolbarBack();

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

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    // getMenuInflater().inflate(R.menu.license, menu);
    //	return true;
    //
    // }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // Up button pressed
                Intent intentHome = new Intent(this, SetPreferenceActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentHome);
                // overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set, R.anim.splash_slide_out_anim_reverse_set);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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
