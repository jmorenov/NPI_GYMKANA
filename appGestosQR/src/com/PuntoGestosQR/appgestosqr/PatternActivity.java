package com.PuntoGestosQR.appgestosqr;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PatternActivity extends Activity implements OnClickListener,
		OnTouchListener {

	ArrayList<ImageView> pattern = new ArrayList<ImageView>();
	int[] ids = { R.id.pattern_1, R.id.pattern_2, R.id.pattern_3,
			R.id.pattern_4, R.id.pattern_5, R.id.pattern_6, R.id.pattern_7,
			R.id.pattern_8, R.id.pattern_9 };

	ArrayList<Integer> pattern_input = new ArrayList<Integer>();
	int[] pattern_Z = { 0, 1, 2, 4, 6, 7, 8 };
	int[] pattern_L = { 0, 3, 6, 7, 8 };
	int[] pattern_M = { 6, 3, 0, 4, 2, 5, 8 };
	int[] pattern_N = { 6, 3, 0, 4, 8, 5, 2 };
	int[] pattern_V = { 0, 7, 2 };

	int [][] patterns = {pattern_Z,
						pattern_L,
						pattern_M,
						pattern_N,
						pattern_V};
	String [] names = {"Z", "L", "M", "N", "V"};
	
	
	private static String TAG = "PatternActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pattern);

		for (int i = 0; i < ids.length; i++) {
			ImageView im = (ImageView) findViewById(ids[i]);
			im.setOnClickListener(this);
			im.setOnTouchListener(this);
			pattern.add(im);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (pattern.contains(v)) {
			int x = (int) event.getRawX();
			int y = (int) event.getRawY();

			for (int i = 0; i < pattern.size(); i++) {
				if (inViewInBounds(pattern.get(i), x, y)) {
					Log.d(TAG, "ID: " + i);
					pattern.get(i).setColorFilter(Color.YELLOW);
					if (pattern_input.size() == 0
							|| pattern_input.get(pattern_input.size() - 1)
									.intValue() != i)
						pattern_input.add(new Integer(i));
				}
			}
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			checkStatus();
		}

		return false;
	}

	private void checkStatus()
	{
		boolean finish = false;
		Log.d(TAG, pattern_input.toString());
		int color;
		if (!checkPatterns())
			color = Color.RED;
		else
		{
			color = Color.GREEN;
			finish = true;
		}
		drawPattern(color);
		new CountDownTimer(500, 50) {
			@Override
			public void onTick(long arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFinish() {
				cleanPattern();
			}
		}.start();
		
		if(finish)
		{
			new CountDownTimer(500, 50) {
				@Override
				public void onTick(long arg0) {
					// TODO Auto-generated method stub
				}
	
				@Override
				public void onFinish() {
					Intent returnIntent = new Intent();
					setResult(RESULT_OK, returnIntent);
					finish();
				}
			}.start();
		}
	}
	
	private boolean checkPatterns() {
		
		for(int i=0; i<patterns.length; i++)
		{
			if(checkPattern(patterns[i], names[i]))
			{
				String result = "Patrón correcto: " + names[i];
				Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		
		return false;
	}

	private boolean checkPattern(int [] pattern, String name)
	{
		if(pattern_input.size() == pattern.length)
		{
			for (int i = 0; i < pattern_input.size(); i++) {
				if(pattern_input.get(i).intValue() != pattern[i])
					return false;
			}
			return true;
		}
		return false;
	}
	
	private void drawPattern(int c) {
		for (int i = 0; i < pattern.size(); i++)
			if (pattern_input.contains(new Integer(i)))
				pattern.get(i).setColorFilter(c);
	}

	private void cleanPattern() {
		for (int i = 0; i < pattern.size(); i++)
			pattern.get(i).setColorFilter(null);
		pattern_input.clear();
	}

	Rect outRect = new Rect();
	int[] location = new int[2];

	private boolean inViewInBounds(ImageView view, int x, int y) {
		view.getDrawingRect(outRect);
		view.getLocationOnScreen(location);
		outRect.offset(location[0], location[1]);
		return outRect.contains(x, y);
	}

	@Override
	public void onClick(View v) {
	}
}
