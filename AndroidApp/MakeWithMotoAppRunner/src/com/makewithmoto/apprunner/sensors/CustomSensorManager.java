package com.makewithmoto.apprunner.sensors;

import java.util.Vector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class CustomSensorManager {

	private final static String TAG = "CustomSensor";


	Context c;
	private static Boolean supported;
	private static boolean running = false;

	boolean sensorSupported;
	
	Sensor sensor;

	Vector<CustomSensorListener> listeners; 


	SensorManager sensormanager;
	SensorEventListener listener;

	public CustomSensorManager() {
		listeners = new Vector<CustomSensorListener>();
		sensormanager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);

	}
	
	public boolean isListening() {
		return false;
	}

	public void start() {
		running = true;
		sensorSupported = sensormanager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
	}

	public void stop() {
		running = false;
		sensormanager.unregisterListener(listener);
	}
	
	public void addListener(CustomSensorListener listener) {
		listeners.add(listener);
	}

	public void removeListener(CustomSensorListener listener) {
		listeners.remove(listener);
	}


}
