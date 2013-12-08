package org.qeo.qeomessaging.android.sensors;

import java.util.HashMap;

import android.hardware.Sensor;

public final class Constants {
	
	// Set this variable to use the public/testing url
	// false : Public URL
	// true : Testing URL
	public static final boolean DEBUG = true;
	
	
	public final static HashMap<Integer, String> sensorNameMapping;
	static {
		sensorNameMapping = new HashMap<Integer, String>();
		sensorNameMapping.put(Sensor.TYPE_ACCELEROMETER, "AccelerometerX");
		sensorNameMapping.put(Sensor.TYPE_AMBIENT_TEMPERATURE, "Temperature");
		sensorNameMapping.put(Sensor.TYPE_GRAVITY, "Gravity");
		sensorNameMapping.put(Sensor.TYPE_GYROSCOPE, "Gyroscope");
		sensorNameMapping.put(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, "Gyroscope");
		sensorNameMapping.put(Sensor.TYPE_LIGHT, "Light");
		sensorNameMapping.put(Sensor.TYPE_LINEAR_ACCELERATION,
				"LinearAcceleration");
		sensorNameMapping.put(Sensor.TYPE_MAGNETIC_FIELD, "MageneticField");
		sensorNameMapping.put(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED,
				"MageneticField");
		sensorNameMapping.put(Sensor.TYPE_PROXIMITY, "Proximity");
		sensorNameMapping.put(Sensor.TYPE_PRESSURE, "Pressure");
		sensorNameMapping.put(Sensor.TYPE_RELATIVE_HUMIDITY, "Humidity");
		sensorNameMapping.put(Sensor.TYPE_ROTATION_VECTOR, "RotationVector");
		sensorNameMapping.put(Sensor.TYPE_GAME_ROTATION_VECTOR, "GameRotationVector");
		// Deprecated from API LEVEL 8. 
		// The way to do this is to provide a configuration page to configure
		// what sensor values need to be sent
		
		sensorNameMapping.put(Sensor.TYPE_ORIENTATION, "OrientationVector");

	}
	
	

}
