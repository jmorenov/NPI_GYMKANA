package com.PuntoGestosQR.appgestosqr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	TextView result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button startButton = (Button) findViewById(R.id.button_start);
		startButton.setOnClickListener(this);
		
		result = (TextView) findViewById(R.id.result);
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.button_start) {
			try {
			    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
			    startActivityForResult(intent, 0);

			} catch (Exception e) {
				Toast.makeText(this, "Necesaria librería App Barcode...", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {           
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            String contents = data.getStringExtra("SCAN_RESULT");
	            result.setText(contents);
	        }
	        if(resultCode == RESULT_CANCELED){
	            //handle cancel
	        }
	    }
	}
}
