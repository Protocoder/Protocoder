package com.makewithmoto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
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
		//getMenuInflater().inflate(R.menu.license, menu);
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
