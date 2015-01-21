package com.NPI_GYMKANA.appfotobrujula;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	// TextViews
	private TextView northDirection;
	private TextView latitudeText;
	private TextView longitudeText;
	private TextView distanceText;

	// Create longitude and latitude variables
	private double latitude;
	private double longitude;

	// Create distance
	private double distance;

	// Initialize ImageView
	private ImageView image;

	// Degrees
	private float currentDegree = 0.0f;
	private float lastDegree = 0.0f;

	// Sensors and location
	private SensorManager mSensorManager;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location lastLocation;

	private boolean brujula_start = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	public void click(View view) {
		setContentView(R.layout.brujula);
		brujula_start = true;
		startBrujula();
		// startActivity(new Intent(this, CameraActivity.class));
	}

	public void startBrujula() {
		// Fill the image with the compass
		image = (ImageView) findViewById(R.id.imageViewCompass);

		// Initial distance is 0
		distance = 0;

		// Set other texts
		northDirection = (TextView) findViewById(R.id.northDirection);
		latitudeText = (TextView) findViewById(R.id.latitudeText);
		longitudeText = (TextView) findViewById(R.id.longitudeText);
		distanceText = (TextView) findViewById(R.id.distanceText);

		// Initialize sensors
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mSensorManager.registerListener(this,
		mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_GAME);
	}

	/**
	 * Method called when the app resumes his activity
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// Continue listening the orientation sensor
		if (brujula_start)
			mSensorManager
					.registerListener(this, mSensorManager
							.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
							SensorManager.SENSOR_DELAY_GAME);
	}

	/**
	 * Method called when the app pauses his activity
	 */
	@Override
	protected void onPause() {
		super.onPause();

		// Stop listening the sensor
		if (brujula_start)
			mSensorManager.unregisterListener(this);
	}

	/**
	 * Method called when the sensor status changes
	 */
	@Override
    public void onSensorChanged(SensorEvent event) {
    	if(brujula_start)
    	{
        // Get the degree
        lastDegree = Math.round(event.values[0]);
        double azimuth = lastDegree;
        //Log.d("GRADOS: ", ""+lastDegree);
        Location target = new Location("Target");
        	target.setLongitude(Location.convert("037:11:49.8"));
        	target.setLatitude(Location.convert("003:37:27.2"));
        		GeomagneticField geoField = new GeomagneticField(
        		             (float) target.getLatitude(),
        		             (float) target.getLongitude(),
        		             (float) target.getAltitude(),
        		             System.currentTimeMillis());
        		azimuth = geoField.getDeclination(); // converts magnetic north into true north
        		//double bearing = currentLoc.bearingTo(target); // (it's already in degrees)
        		double direction = azimuth;
        		
        Log.d("DIRECCION", direction+"");
        // Say if we must turn left or right to go north
        if (lastDegree > 0 && lastDegree < 180) {
            northDirection.setText("Turn left");
        } else {
            northDirection.setText("Turn right");
        }

        // Create and exetute rotation animation
        RotateAnimation rotation;
        rotation = new RotateAnimation(
                currentDegree,
                (float)-direction,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotation.setDuration(300);
        rotation.setFillAfter(true);
        image.startAnimation(rotation);

        // Update current degree
        currentDegree = -lastDegree;

        // Update GPS coordinates
        updateCoordinates();
    	}
    }

	/**
	 * Update the GPS cordinates
	 */
	private void updateCoordinates() {
		// Make a location listener
		locationListener = new LocationListener() {

			// Aux
			int i = 0;

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				lastLocation = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}

			@Override
			public void onProviderEnabled(String provider) {
				lastLocation = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			/**
			 * When user location change, we compute distances and update
			 * coordinates texts
			 */
			@Override
			public void onLocationChanged(Location location) {

				// First time we make lastLocation=location to later compute
				// distances
				if (i == 0) {
					lastLocation = location;
					i++;
				}

				// Get location from GPS
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);

				// Compute distance to lastLocation
				if (location.distanceTo(lastLocation) > 15.0) {
					distance += location.distanceTo(lastLocation);
					distanceText.setText(String.valueOf(distance + " meters"));
				}

				// Update and show location
				latitude = location.getLatitude();
				longitude = location.getLongitude();

				if (latitude > 0) {
					latitudeText.setText(String.valueOf(latitude) + " N");
				} else {
					latitudeText.setText(String.valueOf(-latitude) + " S");
				}

				if (longitude > 0) {
					longitudeText.setText(String.valueOf(longitude) + " E");
				} else {
					longitudeText.setText(String.valueOf(-longitude) + " W");
				}

				if (location.distanceTo(lastLocation) > 15.0) {
					lastLocation = location;
				}
			}
		};

		// Ask GPS updates every 10000 ms
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				10000, 0, locationListener);
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
}