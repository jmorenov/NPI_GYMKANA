package com.PuntoGestosQR.appgestosqr;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	TextView lat;
	TextView lon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button startButton = (Button) findViewById(R.id.button_start);
		startButton.setOnClickListener(this);

		lat = (TextView) findViewById(R.id.lat);
		lon = (TextView) findViewById(R.id.lon);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.button_start) {
			Intent intent = new Intent(this, PatternActivity.class);
			startActivityForResult(intent, 0);
		}
	}

	private void startQRReader() {
		try {
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 1);

		} catch (Exception e) {
			Toast.makeText(this, "Necesaria librería App Barcode...",
					Toast.LENGTH_SHORT).show();
		}
	}

	private float search(String s, String word) {
		float f = 0F;
		if (s.contains(word) || s.contains(word.toUpperCase()) || s.contains(word.toLowerCase())) {
			String result = "";
			for (int j = s.indexOf(word) + word.length(); j < s.length(); j++) {
				char c;
				if (Character.isDigit(s.charAt(j)) || s.charAt(j) == ','
						|| s.charAt(j) == '.' || s.charAt(j) == '-') {
					if (s.charAt(j) == ',')
						c = '.';
					else
						c = s.charAt(j);
					result += c;
				} else if (s.charAt(j) != ' ') // Letra
					break;
			}
			try {
				f = Float.parseFloat(result);
			} catch (NumberFormatException e) {
				Log.e("ERROR", result);
			}
		}
		return f;
	}

	private void getLatLon(String data) {

		/*lat.setText("Latitud: " + search(data, "LATITUD"));
		lon.setText("Longitud: " + search(data, "LONGITUD"));*/
		lat.setText(data);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				startQRReader();
			}
			if (resultCode == RESULT_CANCELED) {
				// handle cancel
			}
		} else if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				getLatLon(data.getStringExtra("SCAN_RESULT"));
			}
			if (resultCode == RESULT_CANCELED) {
				// handle cancel
			}
		}
	}
}
