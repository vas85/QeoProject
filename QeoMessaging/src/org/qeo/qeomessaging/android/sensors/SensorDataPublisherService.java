package org.qeo.qeomessaging.android.sensors;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.qeo.qeomessaging.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class SensorDataPublisherService extends Activity implements
		SensorEventListener {

	private final int interval_seconds = 1 * 1000;
	private SensorManager mSensorManager;
	private List<Sensor> mSensorList;
	private Hashtable<Integer, float[]> ht;
	private Sensor[] availableSensors;


	HashMap<Integer, String> sensorName = org.qeo.qeomessaging.android.sensors.Constants.sensorNameMapping;

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		setContentView(R.layout.activity_main);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

		ht = new Hashtable<Integer, float[]>();

		registerSensor(mSensorList);

	}


	
	// Method to register sensors
	public void registerSensor(List<Sensor> sensorList) {
		int numberOfSensors = sensorList.size();
		Log.d("Number of Sensors", numberOfSensors + "");

		availableSensors = new Sensor[numberOfSensors];

		for (int i = 0; i < numberOfSensors; i++) {
			availableSensors[i] = mSensorManager.getDefaultSensor(sensorList
					.get(i).getType());
		}
	}

	protected void onResume() {
		super.onResume();
		for (int i = 0; i < mSensorList.size(); i++) {
			mSensorManager.registerListener(this, availableSensors[i],
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO: Adding a parameter for accuracy to the sensor reading being
		// published ?
	}

	public void onSensorChanged(SensorEvent event) {

		// ht.put(event.sensor.getType(), event.values);
		// try {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_PRESSURE: {
			Log.d("Pressure", String.valueOf(event.values[0]) + "Pa");
			return;

		}
		case Sensor.TYPE_AMBIENT_TEMPERATURE: {

			Log.d("Temperature", String.valueOf(event.values[0]));
			return;
		}

		case Sensor.TYPE_LIGHT: {
			Log.d("Light", String.valueOf(event.values[0]) + "lux");
			return;
		}

		case Sensor.TYPE_RELATIVE_HUMIDITY: {
			Log.d("Relative Humidity", String.valueOf(event.values[0]) + "%");
			return;

		}

		default: {
			return;
			// Log.d(Constants.sensorNameMapping.get(event.sensor.getType()),
			// str(event.values[0]));
		}
		}

		// }
		// Thread.sleep(interval_seconds);

		// } catch (Exception e)
		// {
		// e.printStackTrace();
		// }

	}

}
