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

package org.protocoderrunner.apprunner.api;

import android.hardware.SensorManager;
import android.location.Location;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.sensors.AccelerometerManager;
import org.protocoderrunner.sensors.GPSManager;
import org.protocoderrunner.sensors.GyroscopeManager;
import org.protocoderrunner.sensors.LightManager;
import org.protocoderrunner.sensors.MagneticManager;
import org.protocoderrunner.sensors.NFCUtil;
import org.protocoderrunner.sensors.OrientationManager;
import org.protocoderrunner.sensors.PressureManager;
import org.protocoderrunner.sensors.ProximityManager;
import org.protocoderrunner.sensors.StepManager;
import org.protocoderrunner.sensors.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

public class PSensors extends PInterface {

	private AccelerometerManager accelerometerManager;
	private AccelerometerManager.AccelerometerListener accelerometerListener;
	private OrientationManager orientationManager;
	private OrientationManager.OrientationListener orientationListener;
	private LightManager lightManager;
	private LightManager.LightListener lightListener;
	private ProximityManager proximityManager;
	private ProximityManager.ProximityListener proximityListener;
	private boolean accelerometerStarted = false;
	private boolean gpsStarted = false;
	private GPSManager gpsManager;
	private GPSManager.GPSListener gpsListener;
	private onNFCCB onNFCfn;
	private boolean gyroscopeStarted = false;
	private GyroscopeManager gyroscopeManager;
	private GyroscopeManager.GyroscopeListener gyroscopeListener;
	private MagneticManager magneticManager;
	private MagneticManager.MagneticListener magneticListener;
	private PressureManager pressureManager;
	private PressureManager.PressureListener pressureListener;
    private StepManager stepManager;
	private StepManager.StepListener stepListener;
	private int sensorsSpeed;

	public PSensors(AppRunnerActivity mwmActivity) {
		super(mwmActivity);

		sensorsSpeed = SensorManager.SENSOR_DELAY_FASTEST;

		a.get().addNFCReadListener(new onNFCListener() {
			@Override
			public void onNewTag(String id, String data) {
				onNFCfn.event(id, data);
			}
		});

	}

	// --------- accelerometer ---------//
	interface startAccelerometerCB {
		void event(float x, float y, float z);
	}

	@ProtocoderScript
	@APIMethod(description = "Set the speed of all sensors 'slow', 'fast', 'normal'", example = "")
	@APIParam(params = { "function(x, y, z)" })
	public void setAllSensorsSpeed(String speed) {
		if (speed.equals("slow")) {
			this.sensorsSpeed = SensorManager.SENSOR_DELAY_UI;
		} else if (speed.equals("fast")) {
			this.sensorsSpeed = SensorManager.SENSOR_DELAY_FASTEST;
		} else {
			this.sensorsSpeed = SensorManager.SENSOR_DELAY_NORMAL;
		}

	}

	@ProtocoderScript
	@APIMethod(description = "Start the accelerometer. Returns x, y, z", example = "")
	@APIParam(params = { "function(x, y, z)" })
	public void startAccelerometer(final startAccelerometerCB callbackfn) {
		if (!accelerometerStarted) {
			accelerometerManager = new AccelerometerManager(a.get());
			accelerometerListener = new AccelerometerManager.AccelerometerListener() {

				@Override
				public void onShake(float force) {

				}

				@Override
				public void onAccelerometerChanged(float x, float y, float z) {
					callbackfn.event(x, y, z);

				}
			};
			accelerometerManager.addListener(accelerometerListener);
			accelerometerManager.start(sensorsSpeed);
			WhatIsRunning.getInstance().add(accelerometerManager);

			accelerometerStarted = true;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "Stop the accelerometer", example = "")
	@APIParam(params = { "" })
	public void stopAccelerometer() {
		MLog.d(TAG, "Called stopAccelerometer");
		if (accelerometerStarted) {
			accelerometerManager.removeListener(accelerometerListener);
			accelerometerManager.stop();
			accelerometerStarted = false;
		}
	}

	// --------- gyroscope ---------//
	interface startGyroscopeCB {
		void event(float x, float y, float z);
	}

	@ProtocoderScript
	@APIMethod(description = "Start the gyroscope. Returns x, y, z", example = "")
	@APIParam(params = { "function(x, y, z)" })
	public void startGyroscope(final startGyroscopeCB callbackfn) {
		if (!gyroscopeStarted) {
			gyroscopeManager = new GyroscopeManager(a.get());
			gyroscopeListener = new GyroscopeManager.GyroscopeListener() {

				@Override
				public void onGyroscopeChanged(float x, float y, float z) {
					callbackfn.event(x, y, z);
				}
			};
			gyroscopeManager.addListener(gyroscopeListener);
			gyroscopeManager.start(sensorsSpeed);
			WhatIsRunning.getInstance().add(gyroscopeManager);

			gyroscopeStarted = true;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "Stops the gyroscope", example = "")
	@APIParam(params = { "" })
	public void stopGyroscope() {
		if (gyroscopeStarted) {
			gyroscopeManager.removeListener(gyroscopeListener);
			gyroscopeManager.stop();
			gyroscopeStarted = false;
		}
	}

	// --------- gps ---------//
	interface startGPSCB {
		void event(double lat, double lon, double alt, float speed, float bearing);
	}

	@ProtocoderScript
	@APIMethod(description = "Start the accelerometer. Returns lat, lon, alt, speed, bearing", example = "")
	@APIParam(params = { "function(lat, lon, alt, speed, bearing)" })
	public void startGPS(final startGPSCB callbackfn) {

		if (!gpsStarted) {

			gpsManager = new GPSManager(a.get());
			gpsListener = new GPSManager.GPSListener() {

				@Override
				public void onSpeedChanged(float speed) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onLocationChanged(double lat, double lon, double alt, float speed, float bearing) {
					callbackfn.event(lat, lon, alt, speed, bearing);
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
	@APIMethod(description = "Stop the GPS", example = "")
	@APIParam(params = { "" })
	public void stopGPS() {
		MLog.d(TAG, "Called stopGPS");
		if (gpsStarted) {
			gpsManager.removeListener(gpsListener);
			gpsManager.stop();
			gpsStarted = false;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "Get the last known location", example = "")
	@APIParam(params = { "" })
	public Location getLastKnownLocation() {
		return gpsManager.getLastKnownLocation();
	}

	@ProtocoderScript
	@APIMethod(description = "Get the location name of a given latitude and longitude", example = "")
	@APIParam(params = { "latitude", "longitude" })
	public String getLocationName(double lat, double lon) {
		return gpsManager.getLocationName(lat, lon);
	}

	@ProtocoderScript
	@APIMethod(description = "Get the distance from two points", example = "")
	@APIParam(params = { "startLatitude", "starLongitude", "endLatitude", "endLongitude" })
	public double getDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
		return gpsManager.getDistance(startLatitude, startLongitude, endLatitude, endLongitude);
	}

	// --------- onNFC ---------//
	interface onNFCCB {
		void event(String id, String responseString);
	}

	@ProtocoderScript
	@APIMethod(description = "Gives back data when a NFC tag is approached", example = "")
	@APIParam(params = { "function(id, data)" })
	public void onNFC(final onNFCCB fn) {
		a.get().initializeNFC();

		onNFCfn = fn;
	}

	// --------- nfc ---------//
	interface writeNFCCB {
		void event(boolean b);
	}

	@ProtocoderScript
	@APIMethod(description = "Write into a NFC tag the given text", example = "")
	@APIParam(params = { "function()" })
	public void writeNFC(String data, final writeNFCCB fn) {
		NFCUtil.nfcMsg = data;
		a.get().initializeNFC();

		a.get().addNFCWrittenListener(new onNFCWrittenListener() {

			@Override
			public void onNewTag() {
				fn.event(true);
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

//	@ProtocoderScript
//	@APIMethod(description = "", example = "")
//	@APIParam(params = { "function(msg)" })
//	public void nfcWrite(final onNFCCB fn) {
//		a.get().initializeNFC();
//
//		onNFCfn = fn;
//	}

	// --------- orientation ---------//
	interface startOrientationCB {
		void event(float pitch, float roll, float yaw);
	}

	@ProtocoderScript
	@APIMethod(description = "Start the orientation sensor. Returns pitch, roll, yaw", example = "")
	@APIParam(params = { "function(pitch, roll, yaw)" })
	public void startOrientation(final startOrientationCB callbackfn) {
		orientationManager = new OrientationManager(a.get());

		orientationListener = new OrientationManager.OrientationListener() {

			@Override
			public void onOrientation(float pitch, float roll, float yaw) {
				callbackfn.event(pitch, roll, yaw);
			}
		};
		orientationManager.addListener(orientationListener);
		orientationManager.start(sensorsSpeed);
		WhatIsRunning.getInstance().add(orientationManager);

	}

	@ProtocoderScript
	@APIMethod(description = "Stops the orientation sensor", example = "")
	@APIParam(params = { "" })
	public void stopOrientation() {
		orientationManager.removeListener(orientationListener);
		orientationManager.stop();
	}

	// --------- light ---------//
	interface startLightIntensityCB {
		void event(float f);
	}

	@ProtocoderScript
	@APIMethod(description = "Start the light sensor. Returns the intensity. The value per device might vary", example = "")
	@APIParam(params = { "function(intensity)" })
	public void startLightIntensity(final startLightIntensityCB callbackfn) {
		lightManager = new LightManager(a.get());

		lightListener = new LightManager.LightListener() {

			@Override
			public void onLightChanged(float f) {
				callbackfn.event(f);
			}
		};

		lightManager.addListener(lightListener);
		lightManager.start(sensorsSpeed);
		WhatIsRunning.getInstance().add(lightManager);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "Stop light sensor" })
	public void stopLightIntensity() {
		lightManager.removeListener(lightListener);
		lightManager.stop();
	}

	// --------- proximity ---------//
	interface startProximityCB {
		void event(float distance);
	}

	@ProtocoderScript
	@APIMethod(description = "Start the proximity sensor. Returns a proximty value. It might differ per device", example = "")
	@APIParam(params = { "function(proximity)" })
	public void startProximity(final startProximityCB callbackfn) {
		proximityManager = new ProximityManager(a.get());

		proximityListener = new ProximityManager.ProximityListener() {

			@Override
			public void onDistanceChanged(float distance) {
				callbackfn.event(distance);
			}
		};

		proximityManager.addListener(proximityListener);
		proximityManager.start(sensorsSpeed);
		WhatIsRunning.getInstance().add(proximityManager);

	}

	@ProtocoderScript
	@APIMethod(description = "Stop proximity sensor", example = "")
	public void stopProximity() {
		proximityManager.removeListener(lightListener);
		proximityManager.stop();
	}

	// --------- magnetic ---------//
	interface startMagneticCB {
		void event(float f);
	}

	@ProtocoderScript
	@APIMethod(description = "Start the magnetic sensor", example = "")
	@APIParam(params = { "function(value)" })
	public void startMagnetic(final startMagneticCB callbackfn) {
		magneticManager = new MagneticManager(a.get());

		magneticListener = new MagneticManager.MagneticListener() {

			@Override
			public void onMagneticChanged(float f) {
				callbackfn.event(f);
			}
		};

		magneticManager.addListener(magneticListener);
		magneticManager.start(sensorsSpeed);
		WhatIsRunning.getInstance().add(magneticManager);

	}

	@ProtocoderScript
	@APIMethod(description = "Stop the magnetic sensor", example = "")
	public void stopMagnetic() {
		magneticManager.removeListener(magneticListener);
		magneticManager.stop();
	}

	// --------- barometer ---------//
    interface startBarometerCB {
        void event(float f);
    }

	@ProtocoderScript
	@APIMethod(description = "Start the barometer", example = "")
	@APIParam(params = { "function(value)" })
	public void startBarometer(final startBarometerCB callbackfn) {
		pressureManager = new PressureManager(a.get());

		pressureListener = new PressureManager.PressureListener() {

			@Override
			public void onPressureChanged(float f) {
				callbackfn.event(f);
			}
		};

		pressureManager.addListener(pressureListener);
		pressureManager.start(sensorsSpeed);
		WhatIsRunning.getInstance().add(pressureManager);

	}

	@ProtocoderScript
	@APIMethod(description = "Stop the barometer", example = "")
	public void stopBarometer() {
		pressureManager.removeListener(pressureListener);
		pressureManager.stop();
	}

    // --------- barometer ---------//
    interface startStepCounterCB {
        void event();
    }

    @ProtocoderScript
	@APIMethod(description = "Start the step counter. Not superacurate and only few devices", example = "")
	@APIParam(params = { "function(value)" })
	public void startStepCounter(final startStepCounterCB callbackfn) {
		stepManager = new StepManager(a.get());

		stepListener = new StepManager.StepListener() {

			@Override
			public void onStepMade() {
				callbackfn.event();
			}
		};

		stepManager.addListener(stepListener);
		stepManager.start(sensorsSpeed);
		WhatIsRunning.getInstance().add(stepManager);

	}

	@ProtocoderScript
	@APIMethod(description = "Stop the step counter", example = "")
	public void stopStepCount() {
		stepManager.removeListener(stepListener);
		stepManager.stop();
	}

	@Override
	public void destroy() {

	}

}
