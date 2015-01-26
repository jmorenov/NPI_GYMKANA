package com.NPI_GYMKANA.appgpsvoz;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout);
		
		/*Intent voiceActivity = new Intent(this, VoiceRecognitionActivity.class);
		startActivityForResult(voiceActivity, 1);*/
		startGPSNavigation(0F, 0F);
	}

	/**
     * Handle the results from the voice recognition activity.
     */
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 1) {
	        if(resultCode == RESULT_OK){
	            float latitud = data.getFloatExtra("latitud", 0F);
	            float longitud = data.getFloatExtra("longitud", 0F);
	            if(latitud != 0F && longitud != 0F)
	            	startGPSNavigation(latitud, longitud);
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        }
	    }
	    else if (requestCode == 2) {
	        if(resultCode == RESULT_OK){
	            finish();
	        }
	        if (resultCode == RESULT_CANCELED) {
	            //Write your code if there's no result
	        }
	    }
	}
	
	public void startGPSNavigation(float latitud, float longitud) {
		Intent GPSActivity = new Intent(this, GPSNavigationActivity.class);
		GPSActivity.putExtra("latitud", latitud);
		GPSActivity.putExtra("longitud", longitud);
		startActivityForResult(GPSActivity, 2);
	}

}