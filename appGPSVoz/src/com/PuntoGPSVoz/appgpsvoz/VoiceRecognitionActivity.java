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

import java.util.ArrayList;
import java.util.List;

import com.NPI_GYMKANA.appgpsvoz.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


/**
 * Clase PatternActivity. Controla los patrones a detectar.
 *
 * @author Francisco Javier Moreno Vega
 * @author Alberto Quesada Aranda
 * @version 28.01.2015
 * @since 20.01.2015
 */
public class VoiceRecognitionActivity extends Activity implements
		OnClickListener {

	private static final int REQUEST_CODE = 1234;
	private TextView lat, lon;
	private static final String TAG = "VoiceRecognitionActivity";
	private float longitud, latitud;
	private Button speakButton, navigationButton;

            
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
		setContentView(R.layout.activity_voice);
		speakButton = (Button) findViewById(R.id.button_speak);
		navigationButton = (Button) findViewById(R.id.button_navigation);

		lat = (TextView) findViewById(R.id.lat);
		lon = (TextView) findViewById(R.id.lon);

		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			speakButton.setEnabled(false);
			speakButton.setText("Recognizer not present");
		}
		speakButton.setOnClickListener(this);
		navigationButton.setOnClickListener(this);
	}
            
            /**
             *
             */
	public void onClick(View v) {
		if (v.getId() == R.id.button_speak)
			startRecognition();
		else if (v.getId() == R.id.button_navigation) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra("latitud", latitud);
			returnIntent.putExtra("longitud", longitud);
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	}
            
            /**
             *
             */
	public void startRecognition() {
		Intent voice_intent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		voice_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		voice_intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				"voice.recognition.test");
		voice_intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		startActivityForResult(voice_intent, REQUEST_CODE);
	}

	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			checkResults(matches);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
            
            /**
             *
             */
	private void checkResults(ArrayList<String> matches) {
		checkData(matches, "latitud", lat);
		checkData(matches, "longitud", lon);
		speakButton.setText(getResources().getString(
				R.string.button_speak_again));
		if (latitud != 0F && longitud != 0F)
			navigationButton.setEnabled(true);
		else
			navigationButton.setEnabled(false);
	}
            
            /**
             *
             */
	private void checkData(ArrayList<String> data, String word, TextView text) {
		float aux = search(data, word);
		if (aux != 0F) {
			if (word == "latitud") {
				latitud = aux;
			} else if (word == "longitud") {
				longitud = aux;
			}
			text.setText(word + " : " + aux);
		}
	}
            
            /**
             *
             */
	private float search(final ArrayList<String> data, String word) {
		float f = 0F;
		boolean found = false;
		for (int i = 0; i < data.size(); i++) {
			Log.d(TAG, "result " + data.get(i));
			if (!found && data.get(i).contains(word)) {
				String s = data.get(i);
				s = s.replaceAll("menos", "-");
				s = s.replaceAll("con", ".");
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
					found = true;
				} catch (NumberFormatException e) {
					Log.e(TAG, result);
				}
			}
		}
		return f;
	}
}
