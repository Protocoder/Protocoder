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

package org.protocoderrunner.apprunner.api.sensors;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.apprunner.logger.L;
import org.protocoderrunner.utils.MLog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class PGPS {


    interface GPSListener {
        void event(double lat, double lon, double alt, float speed, float bearing);
    }


    protected static final String TAG = "GPSManager";
    private final Context c;
    LocationManager locationManager;
    String provider;

    private boolean isGPSFix;
    private Location mLastLocation;
    private long mLastLocationMillis;
    private LocationListener listener;
    public boolean running;

    private GPSListener mGPSListener;


    // The minimum distance to change Updates in meters
    // private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10
    // meters

    // The minimum time between updates in milliseconds
    // private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1
    // minute

    public PGPS(Context c) {
        this.c = c;
    }


    @ProtoMethod(description = "Start the gps. Returns lat, lon, alt, speed, bearing", example = "")
    @ProtoMethodParam(params = {"function(lat, lon, alt, speed, bearing)"})
    public void start() {
        if (running) {
            return;
        }

        WhatIsRunning.getInstance().add(this);


        MLog.d(TAG, "starting GPS");

        // criteria.setSpeedRequired(true);
        locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            MLog.d(TAG, "GPS not enabled");
            showSettingsAlert();
        } else {
            MLog.d(TAG, "GPS enabled");

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
            public void onStatusChanged(String provider, int status, Bundle extras) {
                L.d(TAG, "the gps status is: " + status);

                // TODO add listener to see when the GPS is on or not
                switch (status) {
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        if (mLastLocation != null) {
                            isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;
                        }

                        if (isGPSFix) { // A fix has been acquired.
                            // Do something.
                        } else { // The fix has been lost.
                            // Do something.
                        }

                        //for (CustomSensorListener l : listeners) {
                        //	((GPSListener) l).onGPSStatus(isGPSFix);
                        //}

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
                MLog.d(TAG, "updated ");

                mGPSListener.event(location.getLatitude(), location.getLongitude(),
                        location.getAltitude(), location.getSpeed(), location.getAccuracy());

                if (location == null) {
                    return;
                }

                mLastLocationMillis = SystemClock.elapsedRealtime();
                mLastLocation = location;

            }
        };

        locationManager.requestLocationUpdates(provider, 100, 0.1f, listener);
    }


    @ProtoMethod(description = "Start the accelerometer. Returns x, y, z", example = "")
    @ProtoMethodParam(params = {"function(x, y, z)"})
    public void onChange(final GPSListener callbackfn) {
        start();

        mGPSListener = callbackfn;
    }


    @ProtoMethod(description = "Get the last known location", example = "")
    @ProtoMethodParam(params = {""})
    public Location getLastKnownLocation() {
        return locationManager.getLastKnownLocation(provider);

    }


    @ProtoMethod(description = "Get the location name of a given latitude and longitude", example = "")
    @ProtoMethodParam(params = {"latitude", "longitude"})
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
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    private void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to the settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                c.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @ProtoMethod(description = "Get the distance from two points", example = "")
    @ProtoMethodParam(params = {"startLatitude", "starLongitude", "endLatitude", "endLongitude"})
    public double getDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = null;
        // Location.distanceBetween(startLatitude, startLongitude, endLatitude,
        // endLongitude, results);

        Location locationA = new Location("point A");

        locationA.setLatitude(startLatitude);
        locationA.setLongitude(startLongitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(endLatitude);
        locationB.setLongitude(endLongitude);

        float distance = locationA.distanceTo(locationB);

        return distance;
    }

    public void stop() {
        running = false;
        locationManager.removeUpdates(listener);
    }

}