package com.NPI_GYMKANA.appgpsvoz;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class GPSNavigation implements SensorEventListener{

 // Create longitude and latitude variables
    private double latitude;
    private double longitude;
 
    // Create distance
    private double distance;
    
 // Degrees
    private float currentDegree = 0.0f;
    private float lastDegree = 0.0f;
 
    // Sensors and location
    private SensorManager mSensorManager;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;
    private Location dest;
    
    double latitudeIn, longitudeIn;
    
    public GPSNavigation() {
	// Initial distance is 0
        distance = 0;
        
        latitudeIn = 37.19678168548899;
        longitudeIn = -3.62465459523194;
        // Initialize sensors
        
        //mSensorManager = (SensorManager) MainActivity.class.getSystemService(SENSOR_SERVICE);
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        dest = new Location("loc");
        dest.setLatitude(latitudeIn);
        dest.setLongitude(longitudeIn);
        
       
    }

    public void startSensor()
    {
	 // Continue listening the orientation sensor
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
    }
    
    public void stopSensor()
    {
	// Stop listening the sensor
        mSensorManager.unregisterListener(this);
    }
    
    /**
     * Method called when the sensor status changes
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
 
        // Get the degree
        //lastDegree = Math.round(event.values[0]);
        lastDegree = lastLocation.bearingTo(dest);
        
        // Create and exetute rotation animation
        RotateAnimation rotation;
        rotation = new RotateAnimation(
                currentDegree,
                -lastDegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
 
        rotation.setDuration(300);
        rotation.setFillAfter(true);
        //image.startAnimation(rotation);
 
        // Update current degree
        currentDegree = -lastDegree;
 
        // Update GPS coordinates
        updateCoordinates();
 
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
            public void onStatusChanged(String provider, int status, Bundle extras) {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
 
            @Override
            public void onProviderEnabled(String provider) {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
 
            @Override
            public void onProviderDisabled(String provider) {
            }
 
            /**
             * When user location change, we compute distances and update coordinates texts
             */
            @Override
            public void onLocationChanged(Location location) {
 
                // First time we make lastLocation=location to later compute distances
                if (i == 0) {
                    lastLocation = location;
                    i++;
                }
 
                // Get location from GPS
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
 
                // Compute distance to lastLocation
                if (location.distanceTo(lastLocation) > 15.0) {
                    distance += location.distanceTo(lastLocation);
                    //distanceText.setText(String.valueOf(distance + " meters"));
                }
 
                // Update and show location
                latitude = location.getLatitude();
                longitude = location.getLongitude();
 
                if (location.distanceTo(lastLocation) > 15.0) {
                    lastLocation = location;
                }
            }
        };
 
        // Ask GPS updates every 10000 ms
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
    }
 
    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link android.hardware.SensorManager SensorManager} for details.
     *
     * @param sensor   Sensor type
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
 
    }
}
