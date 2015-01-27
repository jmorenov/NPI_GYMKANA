package com.PuntoGPSVoz.appgpsvoz;

import com.NPI_GYMKANA.appgpsvoz.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button startButton = (Button) findViewById(R.id.button_start);
		startButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.button_start) {
			Intent voiceActivity = new Intent(this,
					VoiceRecognitionActivity.class);
			startActivityForResult(voiceActivity, 1);
		}
	}

	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				float latitud = data.getFloatExtra("latitud", 0F);
				float longitud = data.getFloatExtra("longitud", 0F);
				if (latitud != 0F && longitud != 0F)
					startGPSNavigation(latitud, longitud);
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
			}
		} else if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				TextView result = (TextView) findViewById(R.id.result);
				result.setText(R.string.result);
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
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