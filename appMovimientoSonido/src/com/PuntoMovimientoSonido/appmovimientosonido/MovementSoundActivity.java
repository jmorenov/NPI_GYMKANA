/**
 * Copyright 2014 Javier Moreno, Alberto Quesada
 *
 * This file is part of appMovimientoSonido.
 *
 * MultiTouch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MultiTouch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MultitTouch.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.PuntoMovimientoSonido.appmovimientosonido;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Clase MovementSoundActivity. Controla la acción asociada al movimiento.
 *
 * @author Francisco Javier Moreno Vega
 * @author Alberto Quesada Aranda
 * @version 28.01.2015
 * @since 20.01.2015
 */
public class MovementSoundActivity extends Activity implements
		SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mAcceleSensor;
	private TextView result;

	private double cX;
	/*
	 * private double cY; private double cZ;
	 */
	private boolean first = true;
	private boolean left = false;
	private boolean movement_ok = false;

            
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
		setContentView(R.layout.activity_movement_sound);
		result = (TextView) findViewById(R.id.action_result);

		// Get an instance of the sensor service
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAcceleSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		PackageManager PM = this.getPackageManager();
		boolean accelerometer = PM
				.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);

		if (accelerometer) {
			Toast.makeText(getApplicationContext(),
					"Accelerometer sensor is present in this device",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(
					getApplicationContext(),
					"Sorry, can't do nothing...Your device doesn't have accelerometer sensor",
					Toast.LENGTH_LONG).show();
		}
	}

    /**
    *
    */
	@Override
	protected void onResume() {
		super.onResume(); // registro del listener
		mSensorManager.registerListener(this, mAcceleSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

    /**
    *
    */
	@Override
	protected void onStop() { // anular el registro del listener
		mSensorManager.unregisterListener(this);
		super.onStop();
	}

    /**
    *
    */
	private void playSound() {
		result.setText(R.string.playing_sound);
		MediaPlayer mp1 = MediaPlayer.create(MovementSoundActivity.this,
				R.raw.sound_mp3);
		mp1.start();
		mp1.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				mp.release();
				Intent returnIntent = new Intent();
				setResult(RESULT_OK, returnIntent);
				finish();
			};
		});
	}

    /**
    *
    */
	@Override
	public final void onSensorChanged(SensorEvent event) {
		double X = event.values[0];
		/*
		 * double Y = event.values[1]; double Z = event.values[2];
		 */

		if (first) {
			cX = X;
			/*
			 * cY = Y; cZ = Z;
			 */
			first = false;
		} else if (!movement_ok) {
			if (X > cX + 20) {
				Log.d("MOVIMIENTO", "Izquierda: " + cX + " " + X);
				result.setText("Izquierda");
				left = true;
			} else if (X < cX - 20) {
				Log.d("MOVIMIENTO", "Derecha: " + cX + " " + X);
				result.setText("Derecha");
				if (left)
					movement_ok = true;
				else
					left = false;
			}
			/*
			 * else if(Z > cZ + 20) { Log.d("MOVIMIENTO", "Abajo: " + cZ + " " +
			 * Z); result.setText("Abajo"); } else if(Z < cZ - 20) {
			 * Log.d("MOVIMIENTO", "Arriba: " + cZ + " " + Z);
			 * result.setText("Arriba"); }
			 */
			cX = X;

			if (movement_ok)
				playSound();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	};
}
