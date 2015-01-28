/**
 * Copyright 2014 Javier Moreno, Alberto Quesada
 *
 * This file is part of appGPSVoz.
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

package com.PuntoGPSVoz.appgpsvoz;

import com.NPI_GYMKANA.appgpsvoz.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

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
		setContentView(R.layout.activity_main);

		Button startButton = (Button) findViewById(R.id.button_start);
		startButton.setOnClickListener(this);
	}

    /**
     *
     */
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

    /**
     *
     */
	public void startGPSNavigation(float latitud, float longitud) {
		Intent GPSActivity = new Intent(this, GPSNavigationActivity.class);
		GPSActivity.putExtra("latitud", latitud);
		GPSActivity.putExtra("longitud", longitud);
		startActivityForResult(GPSActivity, 2);
	}

}