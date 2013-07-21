package com.makewithmoto.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerManager extends CustomSensorManager implements WhatIsRunningInterface {

	public interface AccelerometerListener extends CustomSensorListener {

		public void onAccelerometerChanged(float x, float y, float z); 
		public void onShake(float force); 
		
	}

	
	
	private final static String TAG = "Accel";

	private float currentValueX;
	private float previousValueX;
	private float currentValueY;
	private float previousValueY;
	private float currentValueZ;
	private float previousValueZ;



	public AccelerometerManager(Context c) {
		super(c); 
		
		// register
		sensormanager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
		sensor = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		listener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) { 
				//listener
				for (CustomSensorListener l : listeners) {
					((AccelerometerListener)l).onAccelerometerChanged(event.values[0], event.values[1], event.values[2]);
					
					float force = (float) Math.sqrt(Math.pow(event.values[0], 2)
													+ Math.pow(event.values[1], 2)
													+ Math.pow(event.values[2], 2));
				
					((AccelerometerListener)l).onShake(force);
				}
				
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				switch (accuracy) {
				case SensorManager.SENSOR_STATUS_UNRELIABLE:
					break;
				case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
					break;
				case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
					break;
				case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
					break;
				}
			}

		};

	}

	public float total() {

		return currentValueX + currentValueY + currentValueZ;
	}

	public float totalDiff() {

		return Math.abs((currentValueX + currentValueY + currentValueZ)
				- (currentValueX + currentValueY + currentValueZ));
	}

	public float diffX() {
		return Math.abs(currentValueX - previousValueX);
	}

	public float diffY() {
		return Math.abs(currentValueY - previousValueY);
	}

	public float diffZ() {
		return Math.abs(currentValueZ - previousValueZ);
	}

	


}
