package com.NPI_GYMKANA.appgpsvoz;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.TextView;

public class VoiceRecognition {

    private SpeechRecognizer sr;
    private TextView mText, lat, lon;
    private static final String TAG = "MyStt3Activity";
    
    public VoiceRecognition(TextView text, TextView lat, TextView lon, Context context) {
	mText = text;
	this.lat = lat;
	this.lon = lon;
	sr = SpeechRecognizer.createSpeechRecognizer(context);       
        sr.setRecognitionListener(new listener());        
    }
    
    class listener implements RecognitionListener          
    {
             public void onReadyForSpeech(Bundle params)
             {
                      Log.d(TAG, "onReadyForSpeech");
             }
             public void onBeginningOfSpeech()
             {
                      Log.d(TAG, "onBeginningOfSpeech");
             }
             public void onRmsChanged(float rmsdB)
             {
                      //Log.d(TAG, "onRmsChanged");
             }
             public void onBufferReceived(byte[] buffer)
             {
                      //Log.d(TAG, "onBufferReceived");
             }
             public void onEndOfSpeech()
             {
                      Log.d(TAG, "onEndofSpeech");
             }
             public void onError(int error)
             {
                      Log.d(TAG,  "error " +  error);
                      mText.setText("error " + error);
             }
             public void onResults(Bundle results)                   
             {
                      String str = new String();
                      Log.d(TAG, "onResults " + results);
                      ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                      lat.setEnabled(false);
                      lon.setEnabled(false);
                      float lo, la;
                      for (int i = 0; i < data.size(); i++)
                      {
                                Log.d(TAG, "result " + data.get(i));
                                str += data.get(i);
                                if(!lat.isEnabled() && data.get(i).toString().contains("latitud"))
                                {
                                    int index = data.get(i).toString().indexOf("latitud");
                                    int end = data.get(i).toString().indexOf("longitud");
                                    String latitud = data.get(i).toString().substring(index+7, end);
                                    //la = Float.parseFloat(latitud);
                                    lat.setText("Latitud: "+latitud);
                                    lat.setEnabled(true);
                                }
                                else if(!lon.isEnabled() && data.get(i).toString().contains("longitud"))
                                {
                                    int index = data.get(i).toString().indexOf("longitud");
                                    //int end = data.get(i).toString().indexOf("latitud");
                                    String longitud = data.get(i).toString().substring(index+8);
                                    //lo = Float.parseFloat(longitud);
                                    lon.setText("Longitud: "+longitud);
                                    lon.setEnabled(true);
                                }
                      }
                      mText.setText("results: "+String.valueOf(data.size())); 
                      
             }
             public void onPartialResults(Bundle partialResults)
             {
                      Log.d(TAG, "onPartialResults");
             }
             public void onEvent(int eventType, Bundle params)
             {
                      Log.d(TAG, "onEvent " + eventType);
             }
    }
    
    public void startRecognition()
    {
	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5); 
        sr.startListening(intent);
        Log.i("111111","11111111");
    }
    
}
