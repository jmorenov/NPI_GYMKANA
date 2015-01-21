package com.NPI_GYMKANA.appfotobrujula;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	
    	LocationManager milocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	LocationListener milocListener = new MiLocationListener();
    	milocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, milocListener);
    	
    	final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/"; 
        File newdir = new File(dir); 
        newdir.mkdirs();
    	
    	loc.getLatitude();
    	loc.getLongitude();
    	String coordenadas = “Mis coordenadas son: ” + “Latitud = ” + loc.getLatitude() + “Longitud = ” + loc.getLongitude();
    	Toast.makeText( getApplicationContext(),coordenadas,Toast.LENGTH_LONG).show();
    	
    	Button capture = (Button) findViewById(R.id.btnCapture);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // here,counter will be incremented each time,and the picture taken by camera will be stored as 1.jpg,2.jpg and likewise.
                count++;
                String file = dir+count+".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                } catch (IOException e) {}       

                Uri outputFileUri = Uri.fromFile(newfile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        });
    	
    }
}
