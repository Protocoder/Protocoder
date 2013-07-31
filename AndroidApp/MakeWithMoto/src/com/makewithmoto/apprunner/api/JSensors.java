package com.makewithmoto.apprunner.api;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.apprunner.logger.L;
import com.makewithmoto.sensors.AccelerometerManager;
import com.makewithmoto.sensors.AccelerometerManager.AccelerometerListener;
import com.makewithmoto.sensors.GPSManager;
import com.makewithmoto.sensors.OrientationManager;
import com.makewithmoto.sensors.OrientationManager.OrientationListener;
import com.makewithmoto.sensors.WhatIsRunning;

public class JSensors extends JInterface {

	private AccelerometerManager accelerometerManager;
	private AccelerometerListener accelerometerListener;
	private OrientationManager orientationManager;
	private OrientationListener orientationListener;
	private boolean AccelerometerStarted = false;
	private GPSManager gpsManager;
	private LocationListener gpsListener;
	private LocationManager locationManager;


	public JSensors(AppRunnerActivity mwmActivity) {
		super(mwmActivity);
	}

	@JavascriptInterface

	public void startAccelerometer(final String callbackfn) {
		if(!AccelerometerStarted){
		    accelerometerManager = new AccelerometerManager(a.get());
		    accelerometerListener = new AccelerometerListener() {

			    @Override
			    public void onShake(float force) {

			    }

			    @Override
			    public void onAccelerometerChanged(float x, float y, float z) {
				    	callback(callbackfn,x,y,z);	
			    }
		    };
		    accelerometerManager.addListener(accelerometerListener);
		    accelerometerManager.start();
		    WhatIsRunning.getInstance().add(accelerometerManager);
		    
		    AccelerometerStarted = true;
		}
	}

	@JavascriptInterface
	public void stopAccelerometer() {
		Log.d(TAG, "Called stopAccelerometer");
		if(AccelerometerStarted){
		    accelerometerManager.removeListener(accelerometerListener);
		    accelerometerManager.stop();
		    AccelerometerStarted = false;
		}
	}

	
	
	@JavascriptInterface
	public void startGPS(final String callbackfn) {
		
		
	    gpsManager = new GPSManager(a.get());
	    
	    gpsManager.start();
	    
	    Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setBearingAccuracy(Criteria.ACCURACY_FINE);
		// criteria.setSpeedRequired(true);
		String provider;
		locationManager = (LocationManager) a.get().getSystemService(Context.LOCATION_SERVICE);
		provider = locationManager.getBestProvider(criteria, false);
	    
	    gpsListener = new LocationListener() {
	    	@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				L.d(TAG, "the gps status is: " + status);
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onLocationChanged(Location location) {
				
				Location l = gpsManager.getLocation();
				
				if(l != null){
		            Geocoder gcd = new Geocoder(a.get(), Locale.getDefault());
		            List<Address> addresses;
		            try {
		                addresses = gcd.getFromLocation(l.getLatitude(),
		            	    	l.getLongitude(), 1);
		                String gpsCity = addresses.get(0).getLocality();
			            String lat = ""+l.getLatitude();
			            String lng = ""+l.getLongitude();
			        
					    callback(callbackfn,lat,lng,gpsCity);
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
				}
				else{
					//callback(callbackfn,"","","");
				}
			}
	    	
	    };
	  
	    locationManager.requestLocationUpdates(provider, 100, 0.1f, gpsListener);
	}
	
	@JavascriptInterface
	public void stopGPS() {
		locationManager.removeUpdates(gpsListener);
		gpsManager.stopGPS(); 
	}
	

	
	
	
	
	@JavascriptInterface
	public void startOrientation(final String callback) {
		orientationManager = new OrientationManager(a.get());

		orientationListener = new OrientationListener() {

			@Override
			public void onOrientation(float pitch, float roll, float z) {
				// TODO Auto-generated method stub

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



	@Override
	public void destroy() {
		
	}

}
