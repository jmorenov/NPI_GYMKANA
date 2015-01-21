package com.NPI_GYMKANA.appfotobrujula;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	Toast toast = Toast.makeText(getApplicationContext(), "Primera prueba", Toast.LENGTH_SHORT);
    	toast.show();
    }
}
