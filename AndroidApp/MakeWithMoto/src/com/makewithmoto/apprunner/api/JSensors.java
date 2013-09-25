package com.makewithmoto.apprunner.api;

import android.location.Location;
import android.util.Log;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.sensors.AccelerometerManager;
import com.makewithmoto.sensors.AccelerometerManager.AccelerometerListener;
import com.makewithmoto.sensors.GPSManager;
import com.makewithmoto.sensors.GPSManager.GPSListener;
import com.makewithmoto.sensors.LightManager;
import com.makewithmoto.sensors.LightManager.LightListener;
import com.makewithmoto.sensors.OrientationManager;
import com.makewithmoto.sensors.OrientationManager.OrientationListener;
import com.makewithmoto.sensors.ProximityManager;
import com.makewithmoto.sensors.ProximityManager.ProximityListener;
import com.makewithmoto.sensors.WhatIsRunning;

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


	public JSensors(AppRunnerActivity mwmActivity) {
		super(mwmActivity);

		((AppRunnerActivity) a.get()).addNFCListener(new onNFCListener() {
			@Override
			public void onNewTag(String id) {
				callback(onNFCfn, "\"" + id + "\"");
			}
		});

	}

	@JavascriptInterface
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
			accelerometerManager.start();
			WhatIsRunning.getInstance().add(accelerometerManager);

			accelerometerStarted = true;
		}
	}

	@JavascriptInterface
	public void stopAccelerometer() {
		Log.d(TAG, "Called stopAccelerometer");
		if (accelerometerStarted) {
			accelerometerManager.removeListener(accelerometerListener);
			accelerometerManager.stop();
			accelerometerStarted = false;
		}
	}

	@JavascriptInterface
	public void startGPS(final String callbackfn) {
		if (!gpsStarted) {

			gpsManager = new GPSManager(a.get());
			gpsListener = new GPSListener() {

				@Override
				public void onSpeedChanged(float speed) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLocationChanged(double lat, double lon,
						double alt, float speed, float bearing) {

					callback(callbackfn, lat, lon, alt, speed, bearing);

				}

				@Override
				public void onGPSStatus(boolean isGPSFix) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGPSSignalGood() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGPSSignalBad() {
					// TODO Auto-generated method stub

				}
			};

			gpsManager.addListener(gpsListener);
			gpsManager.start();
			WhatIsRunning.getInstance().add(gpsManager);

			gpsStarted = true;
		}
	}

	@JavascriptInterface
	public void stopGPS() {
		Log.d(TAG, "Called stopGPS");
		if (gpsStarted) {
			gpsManager.removeListener(gpsListener);
			gpsManager.stop();
			gpsStarted = false;
		}
	}

	@JavascriptInterface
	public Location getLastKnownLocation() {
		return gpsManager.getLastKnownLocation();
	}

	@JavascriptInterface
	public String getLocationName(double lat, double lon) {
		return gpsManager.getLocationName(lat, lon);
	}

	@JavascriptInterface
	public double getDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
		return gpsManager.getDistance(startLatitude, startLongitude, endLatitude, endLongitude);
	}

	@JavascriptInterface
	@APIMethod(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")
	public void onNFC(final String fn) {
		((AppRunnerActivity) a.get()).initializeNFC();

		onNFCfn = fn;
	}

	public interface onNFCListener {
		public void onNewTag(String id);
	}

	@JavascriptInterface
	public void startOrientation(final String callbackfn) {
		orientationManager = new OrientationManager(a.get());

		orientationListener = new OrientationListener() {

			@Override
			public void onOrientation(float pitch, float roll, float yaw) {
				callback(callbackfn, pitch, roll, yaw);
			}
		};
		orientationManager.addListener(orientationListener);
		orientationManager.start();
		WhatIsRunning.getInstance().add(orientationManager);

	}

	@JavascriptInterface
	public void stopOrientation() {
		orientationManager.removeListener(orientationListener);
		orientationManager.stop();
	}
	
	@JavascriptInterface
	public void startLightIntensity(final String callbackfn) {
		lightManager = new LightManager(a.get());
		
		lightListener = new LightListener() {
			
			@Override
			public void onLightChanged(float f) {
				callback(callbackfn, f);
			}
		};
	

		lightManager.addListener(lightListener);
		lightManager.start();
		WhatIsRunning.getInstance().add(lightManager);
		
	}
	
	@JavascriptInterface
	public void stopLightIntensity() {
		lightManager.removeListener(lightListener);
		lightManager.stop();
	}
	
	@JavascriptInterface
	public void startProximity(final String callbackfn) {
		proximityManager = new ProximityManager(a.get());
		
		proximityListener = new ProximityListener() {
			
			@Override
			public void onDistanceChanged(float distance) {
				callback(callbackfn, distance);				
			}
		};
		
		
		proximityManager.addListener(lightListener);
		proximityManager.start();
		WhatIsRunning.getInstance().add(proximityManager);
		
	}
	
	@JavascriptInterface
	public void stopProximity() {
		proximityManager.removeListener(lightListener);
		proximityManager.stop();
	}

	@Override
	public void destroy() {

	}

}
