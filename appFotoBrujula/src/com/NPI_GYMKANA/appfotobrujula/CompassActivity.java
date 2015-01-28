/**
 * Copyright 2014 Javier Moreno, Alberto Quesada
 *
 * This file is part of appFotoBrujula.
 *
 * appFotoBrujula is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * appFotoBrujula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with appFotoBrujula.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.NPI_GYMKANA.appfotobrujula;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Clase CompassActivity. Controla los sensores de la brújula y la interfaz de esta.
 *
 * @author Francisco Javier Moreno Vega
 * @author Alberto Quesada Aranda
 * @version 28.01.2015
 * @since 20.01.2015
 */
public class CompassActivity extends Activity implements OnClickListener,
		SensorEventListener {

	private Button north, east, west, south;
	private ImageView image;

	private SensorManager mSensorManager;
	private float currentHeading, lastHeading;
	private float degrees;

	/**
	 * Called when the activity is starting. This is where most initialization
	 * should go: calling setContentView(int) to inflate the activity's UI,
	 * using findViewById(int) to programmatically interact with widgets in the
	 * UI, calling managedQuery(android.net.Uri, String[], String, String[],
	 * String) to retrieve cursors for data being displayed, etc.
	 * <p>
	 * You can call finish() from within this function, in which case
	 * onDestroy() will be immediately called without any of the rest of the
	 * activity lifecycle (onStart(), onResume(), onPause(), etc) executing.
	 *
	 *
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle).
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compass);

		north = (Button) findViewById(R.id.button_north);
		east = (Button) findViewById(R.id.button_east);
		west = (Button) findViewById(R.id.button_west);
		south = (Button) findViewById(R.id.button_south);

		north.setOnClickListener(this);
		east.setOnClickListener(this);
		west.setOnClickListener(this);
		south.setOnClickListener(this);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	/**
	 * Called when a view has been clicked.
	 * 
	 * @param view
	 *            The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
		setContentView(R.layout.activity_compass_2);
		image = (ImageView) findViewById(R.id.imagegps);
		if (v.getId() == R.id.button_north) {
			degrees = 0;
			
		} else if (v.getId() == R.id.button_east) {
			degrees = 90;

		} else if (v.getId() == R.id.button_west) {
			degrees = 270;
		} else if (v.getId() == R.id.button_south) {
			degrees = 180;
		}
	}

	/**
	 * Method called when the sensor status changes
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {

		if (image != null) {
			lastHeading = Math.round(event.values[0]);

			RotateAnimation rotation;
			rotation = new RotateAnimation(currentHeading, -lastHeading+degrees,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);

			rotation.setDuration(300);
			rotation.setFillAfter(true);
			image.startAnimation(rotation);
			currentHeading = -lastHeading+degrees;

			if ((int) lastHeading == degrees) {
				Toast.makeText(this, R.string.result, Toast.LENGTH_SHORT).show();
				Intent returnIntent = new Intent();
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		}
	}

	/**
	 * Method called when the app resumes his activity
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		// Continue listening the orientation sensor
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
	}

	/**
	 * Called when the accuracy of the registered sensor has changed.
	 * <p/>
	 * <p>
	 * See the SENSOR_STATUS_* constants in
	 * {@link android.hardware.SensorManager SensorManager} for details.
	 *
	 * @param sensor
	 *            Sensor type
	 * @param accuracy
	 *            The new accuracy of this sensor, one of
	 *            {@code SensorManager.SENSOR_STATUS_*}
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	/**
	 * Method called when the app pauses his activity
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// Stop listening the sensor
		mSensorManager.unregisterListener(this);
	}

}
