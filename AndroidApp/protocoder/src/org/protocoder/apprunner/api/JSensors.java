/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
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

package org.protocoder.apprunner.api;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.apprunner.JInterface;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.sensors.AccelerometerManager;
import org.protocoder.sensors.AccelerometerManager.AccelerometerListener;
import org.protocoder.sensors.GPSManager;
import org.protocoder.sensors.GPSManager.GPSListener;
import org.protocoder.sensors.GyroscopeManager;
import org.protocoder.sensors.GyroscopeManager.GyroscopeListener;
import org.protocoder.sensors.LightManager;
import org.protocoder.sensors.LightManager.LightListener;
import org.protocoder.sensors.MagneticManager;
import org.protocoder.sensors.MagneticManager.MagneticListener;
import org.protocoder.sensors.NFCUtil;
import org.protocoder.sensors.OrientationManager;
import org.protocoder.sensors.OrientationManager.OrientationListener;
import org.protocoder.sensors.PressureManager;
import org.protocoder.sensors.PressureManager.PressureListener;
import org.protocoder.sensors.ProximityManager;
import org.protocoder.sensors.ProximityManager.ProximityListener;
import org.protocoder.sensors.WhatIsRunning;

import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

public class JSensors extends JInterface {

	private AccelerometerManager accelerometerManager;
	private AccelerometerListener accelerometerListener;
	private OrientationManager orientationManager;
	private OrientationListener orientationListener;
	private LightManager lightManager;
	private LightListener lightListener;
	private ProximityManager proximityManager;
	private ProximityListener proximityListener;
	private boolean accelerometerStarted = false;
	private boolean gpsStarted = false;
	private GPSManager gpsManager;
	private GPSListener gpsListener;
	private String onNFCfn;
	private boolean gyroscopeStarted = false;
	private GyroscopeManager gyroscopeManager;
	private GyroscopeListener gyroscopeListener;
	private MagneticManager magneticManager;
	private MagneticListener magneticListener;
	private PressureManager pressureManager;
	private PressureListener pressureListener;
	private final int speed;

	public JSensors(AppRunnerActivity mwmActivity) {
		super(mwmActivity);

		speed = SensorManager.SENSOR_DELAY_NORMAL;

		a.get().addNFCReadListener(new onNFCListener() {
			@Override
			public void onNewTag(String id, String data) {
				callback(onNFCfn, "\"" + id + "\"", "\"" + data + "\"");
			}
		});

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(x, y, z)" })
	public void startAccelerometer(final String callbackfn) {
		if (!accelerometerStarted) {
			accelerometerManager = new AccelerometerManager(a.get());
			accelerometerListener = new AccelerometerListener() {

				@Override
				public void onShake(float force) {

				}

				@Override
				public void onAccelerometerChanged(float x, float y, float z) {
					callback(callbackfn, x, y, z);

				}
			};
			accelerometerManager.addListener(accelerometerListener);
			accelerometerManager.start(speed);
			WhatIsRunning.getInstance().add(accelerometerManager);

			accelerometerStarted = true;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public void stopAccelerometer() {
		Log.d(TAG, "Called stopAccelerometer");
		if (accelerometerStarted) {
			accelerometerManager.removeListener(accelerometerListener);
			accelerometerManager.stop();
			accelerometerStarted = false;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "", "function(x, y, z)" })
	public void startGyroscope(final String callbackfn) {
		if (!gyroscopeStarted) {
			gyroscopeManager = new GyroscopeManager(a.get());
			gyroscopeListener = new GyroscopeManager.GyroscopeListener() {

				@Override
				public void onGyroscopeChanged(float x, float y, float z) {
					callback(callbackfn, x, y, z);
				}
			};
			gyroscopeManager.addListener(gyroscopeListener);
			gyroscopeManager.start(speed);
			WhatIsRunning.getInstance().add(gyroscopeManager);

			gyroscopeStarted = true;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public void stopGyroscope() {
		if (gyroscopeStarted) {
			gyroscopeManager.removeListener(gyroscopeListener);
			gyroscopeManager.stop();
			gyroscopeStarted = false;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(lat, lon, alt, speed, bearing)" })
	public void startGPS(final String callbackfn) {

		if (!gpsStarted) {

			gpsManager = new GPSManager(a.get());
			gpsListener = new GPSListener() {

				@Override
				public void onSpeedChanged(float speed) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLocationChanged(double lat, double lon, double alt, float speed, float bearing) {

					callback(callbackfn, lat, lon, alt, speed, bearing);

				}

				@Override
				public void onGPSStatus(boolean isGPSFix) {
				}

				@Override
				public void onGPSSignalGood() {
				}

				@Override
				public void onGPSSignalBad() {
				}
			};

			gpsManager.addListener(gpsListener);
			gpsManager.start();
			WhatIsRunning.getInstance().add(gpsManager);

			gpsStarted = true;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public void stopGPS() {
		Log.d(TAG, "Called stopGPS");
		if (gpsStarted) {
			gpsManager.removeListener(gpsListener);
			gpsManager.stop();
			gpsStarted = false;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public Location getLastKnownLocation() {
		return gpsManager.getLastKnownLocation();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "latitude", "longitude" })
	public String getLocationName(double lat, double lon) {
		return gpsManager.getLocationName(lat, lon);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "endLatitude", "endLongitude", "endLatitude", "endLongitude" })
	public double getDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
		return gpsManager.getDistance(startLatitude, startLongitude, endLatitude, endLongitude);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(id, data)" })
	public void onNFC(final String fn) {
		a.get().initializeNFC();

		onNFCfn = fn;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function()" })
	public void writeNFC(String data, final String fn) {
		NFCUtil.nfcMsg = data;
		a.get().initializeNFC();

		a.get().addNFCWrittenListener(new onNFCWrittenListener() {

			@Override
			public void onNewTag() {
				callback(fn, true);
			}
		});

		// Construct the data to write to the tag
		// Should be of the form [relay/group]-[rid/gid]-[cmd]
		// String nfcMessage = data;

		// When an NFC tag comes into range, call the main activity which
		// handles writing the data to the tag
		// NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(a.get());

		// Intent nfcIntent = new Intent(a.get(),
		// AppRunnerActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// nfcIntent.putExtra("nfcMessage", nfcMessage);
		// PendingIntent pi = PendingIntent.getActivity(a.get(), 0, nfcIntent,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		// IntentFilter tagDetected = new
		// IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

		// nfcAdapter.enableForegroundDispatch((Activity) a.get(), pi, new
		// IntentFilter[] {tagDetected}, null);
	}

	public interface onNFCWrittenListener {
		public void onNewTag();
	}

	public interface onNFCListener {
		public void onNewTag(String id, String nfcMessage);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(msg)" })
	public void nfcWrite(final String fn) {
		a.get().initializeNFC();

		onNFCfn = fn;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(pitch, roll, yaw)" })
	public void startOrientation(final String callbackfn) {
		orientationManager = new OrientationManager(a.get());

		orientationListener = new OrientationListener() {

			@Override
			public void onOrientation(float pitch, float roll, float yaw) {
				callback(callbackfn, pitch, roll, yaw);
			}
		};
		orientationManager.addListener(orientationListener);
		orientationManager.start(speed);
		WhatIsRunning.getInstance().add(orientationManager);

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public void stopOrientation() {
		orientationManager.removeListener(orientationListener);
		orientationManager.stop();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(intensity)" })
	public void startLightIntensity(final String callbackfn) {
		lightManager = new LightManager(a.get());

		lightListener = new LightListener() {

			@Override
			public void onLightChanged(float f) {
				callback(callbackfn, f);
			}
		};

		lightManager.addListener(lightListener);
		lightManager.start(speed);
		WhatIsRunning.getInstance().add(lightManager);

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public void stopLightIntensity() {
		lightManager.removeListener(lightListener);
		lightManager.stop();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(proximity)" })
	public void startProximity(final String callbackfn) {
		proximityManager = new ProximityManager(a.get());

		proximityListener = new ProximityListener() {

			@Override
			public void onDistanceChanged(float distance) {
				callback(callbackfn, distance);
			}
		};

		proximityManager.addListener(proximityListener);
		proximityManager.start(speed);
		WhatIsRunning.getInstance().add(proximityManager);

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void stopProximity() {
		proximityManager.removeListener(lightListener);
		proximityManager.stop();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(value)" })
	public void startMagnetic(final String callbackfn) {
		magneticManager = new MagneticManager(a.get());

		magneticListener = new MagneticManager.MagneticListener() {

			@Override
			public void onMagneticChanged(float f) {
				callback(callbackfn, f);
			}
		};

		magneticManager.addListener(magneticListener);
		magneticManager.start(speed);
		WhatIsRunning.getInstance().add(magneticManager);

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void stopMagnetic() {
		magneticManager.removeListener(magneticListener);
		magneticManager.stop();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(value)" })
	public void startBarometer(final String callbackfn) {
		pressureManager = new PressureManager(a.get());

		pressureListener = new PressureManager.PressureListener() {

			@Override
			public void onPressureChanged(float f) {
				callback(callbackfn, f);
			}
		};

		pressureManager.addListener(pressureListener);
		pressureManager.start(speed);
		WhatIsRunning.getInstance().add(pressureManager);

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void stopPressure() {
		pressureManager.removeListener(pressureListener);
		pressureManager.stop();
	}

	@Override
	public void destroy() {

	}

}
