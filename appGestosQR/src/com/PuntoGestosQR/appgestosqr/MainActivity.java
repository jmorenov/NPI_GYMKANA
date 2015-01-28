/**
 * Copyright 2014 Javier Moreno, Alberto Quesada
 *
 * This file is part of appGestosQR.
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


/**
 * Clase MainActivity. Controla la ejecución de la app, llamando a la creación
 * de la interfaz.
 *
 * @author Francisco Javier Moreno Vega
 * @author Alberto Quesada Aranda
 * @version 28.01.2015
 * @since 20.01.2015
 */
public class MainActivity extends Activity implements OnClickListener {

	TextView lat;
	TextView lon;

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

    /**
     *
    */
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

    /**
     *
     */
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

    /**
     *
     */
	private void getLatLon(String data) {
        lat.setText(data);
	}
    
    /**
     *
     */
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
