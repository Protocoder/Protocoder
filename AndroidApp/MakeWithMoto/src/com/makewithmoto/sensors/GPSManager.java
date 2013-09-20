package com.makewithmoto.sensors;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.makewithmoto.apprunner.logger.L;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class GPSManager {

	public interface GPSListener extends CustomSensorListener {

		public void onLocationChanged(double lat, double lon, double alt,
				float speed, float bearing);

		public void onSpeedChanged(float speed);

		public void onGPSSignalGood();

		public void onGPSSignalBad();

		public void onGPSStatus(boolean isGPSFix);

	}

	protected static final String TAG = "GPSManager";
	private Context c;
	LocationManager locationManager;
	String provider;
	Vector<GPSListener> listeners;

	private boolean isGPSFix;
	private Location mLastLocation;
	private long mLastLocationMillis;
	private LocationListener listener;
	private boolean running;
	
    // The minimum distance to change Updates in meters
    //private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
 
    // The minimum time between updates in milliseconds
    //private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
 

	public GPSManager(Context c) { 
		this.c = c;
		listeners = new Vector<GPSListener>();

	}

	// gps
	public void start() {
		Log.d(TAG, "starting GPS"); 
		
		// criteria.setSpeedRequired(true);
		locationManager = (LocationManager) c
				.getSystemService(Context.LOCATION_SERVICE);
		
		Log.d(TAG, "qq " + locationManager); 
		
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) { 
			Log.d(TAG, "GPS not enabled");
			showSettingsAlert();
		} else { 
			Log.d(TAG, "GPS enabled");
			
		}
		running = true;
		
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// criteria.setBearingAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(true);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		criteria.setSpeedRequired(false);

		provider = locationManager.getBestProvider(criteria, false);

		listener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				L.d(TAG, "the gps status is: " + status);

				// TODO add a listener to see when the GPS is on or not
				switch (status) {
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					if (mLastLocation != null)
						isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;

					if (isGPSFix) { // A fix has been acquired.
						// Do something.
					} else { // The fix has been lost.
						// Do something.
					}

					for (CustomSensorListener l : listeners) {
						((GPSListener) l).onGPSStatus(isGPSFix);
					}

					break;
				case GpsStatus.GPS_EVENT_FIRST_FIX:
					// Do something.
					isGPSFix = true;

					break;
				}
			}

			@Override
			public void onProviderEnabled(String provider) {

			}

			@Override
			public void onProviderDisabled(String provider) {
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				c.startActivity(intent);
			}

			@Override
			public void onLocationChanged(Location location) {
				Log.d(TAG, "updated "); 

				for (CustomSensorListener l : listeners) {
					((GPSListener) l).onLocationChanged(location.getLatitude(),
							location.getLongitude(), location.getAltitude(),
							location.getSpeed(), location.getAccuracy());
				}

				if (location == null)
					return;

				mLastLocationMillis = SystemClock.elapsedRealtime();
				mLastLocation = location;

			}
		};

		locationManager.requestLocationUpdates(provider, 100, 0.1f, listener);
	}

	
	public Location getLastKnownLocation() { 
		return locationManager.getLastKnownLocation(provider);
		
	} 
	
	
	
	/**
	 * Function to get location name
	 * */
	public String getLocationName(double lat, double lon) {
		String gpsLocation = "";
		Geocoder gcd = new Geocoder(c, Locale.getDefault());
		List<Address> addresses;
		try {
			addresses = gcd.getFromLocation(lat, lon, 1);
			gpsLocation = addresses.get(0).getLocality();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return gpsLocation;
	}
	
	   /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert() { 
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to the settings menu?");
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                c.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }
 
    
 

	public void stop() {
		running = false;
			
		for (GPSListener l : listeners) { 
			listeners.remove(l);
			locationManager.removeUpdates(listener);
		} 
	}
	
    
    public void addListener(GPSListener listener) {
		listeners.add(listener);
	}

	public void removeListener(GPSListener listener) {
		listeners.remove(listener);
	}


}