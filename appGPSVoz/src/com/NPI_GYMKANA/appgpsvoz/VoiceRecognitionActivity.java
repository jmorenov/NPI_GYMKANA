package com.NPI_GYMKANA.appgpsvoz;

import java.util.ArrayList;
import java.util.List;

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

public class VoiceRecognitionActivity extends Activity implements OnClickListener {

	//private SpeechRecognizer sr;
	private static final int REQUEST_CODE = 1234;
	private TextView lat, lon;
	private static final String TAG = "VoiceRecognitionActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout);
		Button speakButton = (Button) findViewById(R.id.btn_speak);
		lat = (TextView) findViewById(R.id.lat);
		lon = (TextView) findViewById(R.id.lon);
		 
        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
		
		speakButton.setOnClickListener(this);
		/*sr = SpeechRecognizer.createSpeechRecognizer(this
				.getApplicationContext());
		sr.setRecognitionListener(new VoiceRecognition());*/

	}

	public void onClick(View v) {
		if (v.getId() == R.id.btn_speak) {
			startRecognition();
		}
	}

	public void startRecognition() {
		Intent voice_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		voice_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		voice_intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				"voice.recognition.test");
		voice_intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		//sr.startListening(voice_intent);
		startActivityForResult(voice_intent, REQUEST_CODE);
	}
	
	/**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            float longitud, latitud;
            latitud = search(matches, "latitud", lat);
            longitud = search(matches, "longitud", lon);
            if (latitud != 0F && longitud != 0F)
            {
            	Intent returnIntent = new Intent();
            	returnIntent.putExtra("latitud",latitud);
            	returnIntent.putExtra("longitud",longitud);
            	setResult(RESULT_OK,returnIntent);
            	finish();
            }
            /*wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    matches));*/
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private boolean isDigit(char c) {
		if (Character.isDigit(c))
			return true;
		return false;
	}

	private float search(final ArrayList<String> data, String word,
			TextView text) {
		float f = 0F;
		text.setEnabled(false);
		for (int i = 0; i < data.size(); i++) {
			Log.d(TAG, "result " + data.get(i));
			if (!text.isEnabled() && data.get(i).contains(word)) {
				String s = data.get(i);
				s = s.replaceAll("menos", "-");
				s = s.replaceAll("con", ",");
				String result = "";
				for (int j = s.indexOf(word) + word.length(); j < s
						.length(); j++) {
					char c;
					if (isDigit(s.charAt(j)) || s.charAt(j) == ','
							|| s.charAt(j) == '.' || s.charAt(j) == '-' ) {
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
					text.setText(word + ": " + result);
					text.setEnabled(true);
				} catch (NumberFormatException e) {
					Log.e(TAG, result);
					text.setText(word + ": ERROR.");
				}
			}
		}
		return f;
	}
	
	/*private class VoiceRecognition implements RecognitionListener {
		public void onReadyForSpeech(Bundle params) {
		}

		public void onBeginningOfSpeech() {
		}

		public void onRmsChanged(float rmsdB) {
		}

		public void onBufferReceived(byte[] buffer) {
		}

		public void onEndOfSpeech() {
		}

		public void onError(int error) {
			Log.d(TAG, "error " + error);
			mText.setText("error " + error);
		}

		public void onResults(Bundle results) {
			ArrayList<String> data = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			float longitud, latitud;
			latitud = search(data, "latitud", lat);
			longitud = search(data, "longitud", lon);
			if (latitud != 0F && longitud != 0F)
				startGPSNavigation(latitud, longitud);
		}

		public void onPartialResults(Bundle partialResults) {
		}

		public void onEvent(int eventType, Bundle params) {
		}
	}*/
    
}
