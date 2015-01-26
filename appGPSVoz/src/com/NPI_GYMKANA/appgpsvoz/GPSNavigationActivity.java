package com.NPI_GYMKANA.appgpsvoz;

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
import android.widget.Toast;

public class GPSNavigationActivity extends Activity{

	private TextView mText, lat, lon;
	private ImageView image;
	//private static final String TAG = "GPSNavigationActivity";

	private LocationManager locationManager;
	private String provider;
	private GPSNavigation gpsNav;
	private Criteria criteria;
	private Location location;

	private float latitud, longitud;
	private Location destination;
	
	private float currentBearing, lastBearing;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps);
		mText = (TextView) findViewById(R.id.speech);
		lat = (TextView) findViewById(R.id.lat);
		lon = (TextView) findViewById(R.id.lon);
		image = (ImageView) findViewById(R.id.imagegps);
		
		/*Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    latitud = extras.getFloat("latitud", 0F);
		    longitud = extras.getFloat("longitud", 0F);
		    destination = new Location("Destino");
		    destination.setLatitude(latitud);
		    destination.setLongitude(longitud);
		}*/
		latitud = 37.19716626365634F;
	    longitud = -3.6242308062076067F;
	    destination = new Location("Destino");
	    destination.setLatitude(latitud);
	    destination.setLongitude(longitud);
		
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location provider
		criteria = new Criteria();
		//criteria.setAccuracy(Criteria.ACCURACY_COARSE); // Network
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // GPS
		criteria.setCostAllowed(false);
		// get the best provider depending on the criteria
		provider = locationManager.getBestProvider(criteria, false);
		// the last known location of this provider
		location = locationManager.getLastKnownLocation(provider);
		
		startGPSNavigation(latitud, longitud);

	}
	
	public void startGPSNavigation(float latitud, float longitud) {
		Toast.makeText(this.getApplicationContext(),
				"Navegación GPS hacia..." + latitud + " " + longitud,
				Toast.LENGTH_SHORT).show();
		gpsNav = new GPSNavigation();

		if (location != null) {
			gpsNav.onLocationChanged(location);
		} else {
			// leads to the settings because there is no last known location
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}
		// location updates: at least 1 meter and 200millsecs change
		locationManager.requestLocationUpdates(provider, 200, 1, gpsNav);
	}

	private class GPSNavigation implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// Initialize the location fields
			lat.setText("Latitude: " + String.valueOf(location.getLatitude()));
			lon.setText("Longitude: " + String.valueOf(location.getLongitude()));
			mText.setText(provider + " provider has been selected.");

			lastBearing = location.bearingTo(destination);
			
			// Create and exetute rotation animation
	        RotateAnimation rotation;
	        rotation = new RotateAnimation(
	                currentBearing,
	                -lastBearing,
	                Animation.RELATIVE_TO_SELF, 0.5f,
	                Animation.RELATIVE_TO_SELF, 0.5f);

	        rotation.setDuration(300);
	        rotation.setFillAfter(true);
	        image.startAnimation(rotation);
			
	        currentBearing = -lastBearing;
	        
			Toast.makeText(GPSNavigationActivity.this, "Location changed!",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Toast.makeText(GPSNavigationActivity.this,
					provider + "'s status changed to " + status + "!",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(GPSNavigationActivity.this,
					"Provider " + provider + " enabled!", Toast.LENGTH_SHORT)
					.show();

		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(GPSNavigationActivity.this,
					"Provider " + provider + " disabled!", Toast.LENGTH_SHORT)
					.show();
		}
	}

}