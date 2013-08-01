package com.makewithmoto.sensors;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;

import com.makewithmoto.apprunner.logger.L;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class GPSManagerOriginal extends CustomSensorManager {
	
	public interface GPSListener extends CustomSensorListener {

		public void onLocationChanged(double lat, double lon, double alt, float speed, float bearing);
		public void onSpeedChanged(float speed);
		public void onGPSSignalGood(); 
		public void onGPSSignalBad();
		public void onGPSStatus(boolean isGPSFix);
		
	}

	
	protected static final String TAG = "GPSManager";
	LocationManager locationManager;

	private boolean isGPSFix;
	private Location mLastLocation;
	private long mLastLocationMillis;

	public GPSManagerOriginal(Context c) {
		super(c);

	}

	// gps
	@Override
	public void start() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		//criteria.setBearingAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setSpeedRequired(false);
        
		// criteria.setSpeedRequired(true);
		String provider;
		locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		provider = locationManager.getBestProvider(criteria, false);

		LocationListener listener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				L.d(TAG, "the gps status is: " + status);

				//TODO add a listener to see when the GPS is on or not
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
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				c.startActivity(intent);
			}

			@Override
			public void onLocationChanged(Location location) {

				for (CustomSensorListener l : listeners) {
					((GPSListener) l).onLocationChanged(location.getLatitude(), location.getLongitude(), location.getAltitude(),
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

}