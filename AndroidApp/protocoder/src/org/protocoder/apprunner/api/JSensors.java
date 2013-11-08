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

package org.protocoder.apprunner.api;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.apprunner.JInterface;
import org.protocoder.apprunner.JavascriptInterface;
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
import org.protocoder.sensors.OrientationManager;
import org.protocoder.sensors.OrientationManager.OrientationListener;
import org.protocoder.sensors.PressureManager;
import org.protocoder.sensors.PressureManager.PressureListener;
import org.protocoder.sensors.ProximityManager;
import org.protocoder.sensors.ProximityManager.ProximityListener;
import org.protocoder.sensors.WhatIsRunning;

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
	@APIMethod(description = "", example = "")
	@APIParam( params = {"function(x, y, z)"} )
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
	@APIMethod(description = "", example = "")
	@APIParam( params = {""} )
	public void stopAccelerometer() {
		Log.d(TAG, "Called stopAccelerometer");
		if (accelerometerStarted) {
			accelerometerManager.removeListener(accelerometerListener);
			accelerometerManager.stop();
			accelerometerStarted = false;
		}
	}
	
	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"", "function(x, y, z)"} )
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
			gyroscopeManager.start();
			WhatIsRunning.getInstance().add(gyroscopeManager);
			
			gyroscopeStarted = true;
		}
	}
	
	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {""} )
	public void stopGyroscope() {
		if (gyroscopeStarted) {
			gyroscopeManager.removeListener(gyroscopeListener);
			gyroscopeManager.stop();
			gyroscopeStarted = false;
		}
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"function(lat, lon, alt, speed, bearing)"} )
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

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {""} )
	public void stopGPS() {
		Log.d(TAG, "Called stopGPS");
		if (gpsStarted) {
			gpsManager.removeListener(gpsListener);
			gpsManager.stop();
			gpsStarted = false;
		}
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {""} )
	public Location getLastKnownLocation() {
		return gpsManager.getLastKnownLocation();
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"latitude", "longitude"} )
	public String getLocationName(double lat, double lon) {
		return gpsManager.getLocationName(lat, lon);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"endLatitude", "endLongitude", "endLatitude", "endLongitude"} )
	public double getDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
		return gpsManager.getDistance(startLatitude, startLongitude, endLatitude, endLongitude);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"function(id)"} )
	public void onNFC(final String fn) {
		((AppRunnerActivity) a.get()).initializeNFC();

		onNFCfn = fn;
	}

	public interface onNFCListener {
		public void onNewTag(String id);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"function(pitch, roll, yaw)"} )
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
	@APIMethod(description = "", example = "")
	@APIParam( params = {""} )
	public void stopOrientation() {
		orientationManager.removeListener(orientationListener);
		orientationManager.stop();
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"function(intensity)"} )
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
	@APIMethod(description = "", example = "")
	@APIParam( params = {""} )
	public void stopLightIntensity() {
		lightManager.removeListener(lightListener);
		lightManager.stop();
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"function(proximity)"} )
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
	@APIMethod(description = "", example = "")
	public void stopProximity() {
		proximityManager.removeListener(lightListener);
		proximityManager.stop();
	}
	
	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"function(value)"} )
	public void startMagnetic(final String callbackfn) {
		magneticManager = new MagneticManager(a.get());
		
		magneticListener = new MagneticManager.MagneticListener() {
			
			@Override
			public void onMagneticChanged(float f) {
				callback(callbackfn, f);					
			}
		};
		
		
		magneticManager.addListener(magneticListener);
		magneticManager.start();
		WhatIsRunning.getInstance().add(magneticManager);
		
	}
	
	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void stopMagnetic() {
		magneticManager.removeListener(magneticListener);
		magneticManager.stop();
	}
	
	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"function(value)"} )
	public void startPressure(final String callbackfn) {
		pressureManager = new PressureManager(a.get());
		
		pressureListener = new PressureManager.PressureListener() {
			
			@Override
			public void onPressureChanged(float f) {
				callback(callbackfn, f);	
			}
		};
		
		
		pressureManager.addListener(pressureListener);
		pressureManager.start();
		WhatIsRunning.getInstance().add(pressureManager);
		
	}
	
	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void stopPressure() {
		pressureManager.removeListener(pressureListener);
		pressureManager.stop();
	}

	@Override
	public void destroy() {

	}

}
