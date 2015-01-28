/**
 * Copyright 2014 Javier Moreno, Alberto Quesada
 *
 * This file is part of appGPSVoz.
 *
 * appGPSVoz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * appGPSVoz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with appGPSVoz.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.PuntoGPSVoz.appgpsvoz;

import com.NPI_GYMKANA.appgpsvoz.R;

import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Clase PatternActivity. Controla los patrones a detectar.
 *
 * @author Francisco Javier Moreno Vega
 * @author Alberto Quesada Aranda
 * @version 28.01.2015
 * @since 20.01.2015
 */
public class GPSNavigationActivity extends Activity implements
		SensorEventListener {

	private TextView head, lat, lon, dis;
	private ImageView image;

	private LocationManager locationManager;
	private String provider;
	private GPSNavigation gpsNav;
	private Criteria criteria;
	private Location location;
	private SensorManager mSensorManager;

	private float latitud, longitud;
	private Location destination, loc;
	private GeomagneticField geoField;

	private float bearing;
	private float currentHeading, lastHeading;
	private float heading;
	private float distance;

	/**
	 * Mínima distancia necesaria (en metros) para determinar que se ha llegado
	 * al destino.
	 */
	private static float MIN_DISTANCE_TO_DESTINATION = 100;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps);
		head = (TextView) findViewById(R.id.heading);
		lat = (TextView) findViewById(R.id.lat);
		lon = (TextView) findViewById(R.id.lon);
		dis = (TextView) findViewById(R.id.distance);
		image = (ImageView) findViewById(R.id.imagegps);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			latitud = extras.getFloat("latitud", 0F);
			longitud = extras.getFloat("longitud", 0F);
		}
		destination = new Location("Destino");
		destination.setLatitude(latitud);
		destination.setLongitude(longitud);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location provider
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		// criteria.setAccuracy(Criteria.ACCURACY_COARSE); // Network
		// criteria.setAccuracy(Criteria.ACCURACY_FINE); // GPS
		criteria.setCostAllowed(false);
		// get the best provider depending on the criteria
		provider = locationManager.getBestProvider(criteria, false);
		// the last known location of this provider
		location = locationManager.getLastKnownLocation(provider);

		gpsNav = new GPSNavigation();
		if (location != null)
			gpsNav.onLocationChanged(location);
		else {
			// leads to the settings because there is no last known location
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}
		// location updates: at least 1 meter and 200millsecs change
		locationManager.requestLocationUpdates(provider, 200, 1, gpsNav);

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
	 * Method called when the app pauses his activity
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// Stop listening the sensor
		mSensorManager.unregisterListener(this);
	}

	/**
	 * Method called when the sensor status changes
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (loc != null) {
			heading = Math.round(event.values[0]);
			geoField = new GeomagneticField(Double.valueOf(loc.getLatitude())
					.floatValue(), Double.valueOf(loc.getLongitude())
					.floatValue(), Double.valueOf(loc.getAltitude())
					.floatValue(), System.currentTimeMillis());
			heading += geoField.getDeclination();
			heading = (bearing - heading) * -1;
			heading = normalizeDegree(heading);

			head.setText("Heading: " + heading);
			lastHeading = heading;

			RotateAnimation rotation;
			rotation = new RotateAnimation(currentHeading, -lastHeading,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);

			rotation.setDuration(300);
			rotation.setFillAfter(true);
			image.startAnimation(rotation);
			currentHeading = -lastHeading;
		}
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
	 * Normaliza los grados del heading de la navegación.
	 * 
	 * @param value
	 *            Grados a normalizar.
	 * 
	 * @return grados normalizados.
	 */
	private float normalizeDegree(float value) {
		if (value >= 0.0f && value <= 180.0f) {
			return value;
		} else {
			return 180 + (180 + value);
		}
	}

	/**
	 * Método llamado cuando se ha llegado al destino. Termina la ejecución de
	 * la activity.
	 */
	private void destinationReached() {
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	/**
	 * Clase que implementa el LocationListener necesario para detectar los
	 * cambios en la posición.
	 */
	private class GPSNavigation implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			if (loc != null)
				loc = location;
			else {
				loc = new Location("Location");
				loc = location;
			}

			bearing = loc.bearingTo(destination);
			distance = loc.distanceTo(destination);

			// Initialize the location fields
			lat.setText("Latitude: " + String.valueOf(location.getLatitude()));
			lon.setText("Longitude: " + String.valueOf(location.getLongitude()));
			dis.setText("Distance: " + distance + " m");

			if (distance < MIN_DISTANCE_TO_DESTINATION)
				destinationReached();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}
	}

}