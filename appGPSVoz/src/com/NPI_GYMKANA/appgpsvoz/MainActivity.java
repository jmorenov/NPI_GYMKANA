package com.NPI_GYMKANA.appgpsvoz;

import java.util.ArrayList;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener  {

    private TextView mText, lat, lon;
    private VoiceRecognition vr;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
             super.onCreate(savedInstanceState);
             setContentView(R.layout.layout);
             Button speakButton = (Button) findViewById(R.id.btn_speak);     
             mText = (TextView) findViewById(R.id.speech);
             lat = (TextView) findViewById(R.id.lat);    
             lon = (TextView) findViewById(R.id.lon);    
             speakButton.setOnClickListener(this);
             vr = new VoiceRecognition(mText, lat, lon, this.getApplicationContext());
    }

    public void onClick(View v) {
             if (v.getId() == R.id.btn_speak) 
             {
                 vr.startRecognition();
             }
    }
    
    public void startGPSNavigation(float latitud, float longitud)
    {
	Toast.makeText(this.getApplicationContext(), latitud+" "+longitud, Toast.LENGTH_SHORT).show();
    }
    
}