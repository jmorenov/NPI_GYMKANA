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

public class VoiceRecognitionActivity extends Activity implements
		OnClickListener {

	private static final int REQUEST_CODE = 1234;
	private TextView lat, lon;
	private static final String TAG = "VoiceRecognitionActivity";
	private float longitud, latitud;
	private Button speakButton, navigationButton;

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
