/**
 * Copyright 2014 Javier Moreno, Alberto Quesada
 *
 * This file is part of appGestosQR.
 *
 * appGestosQR is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * appGestosQR is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with appGestosQR.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.PuntoGestosQR.appgestosqr;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Clase PatternActivity. Controla los patrones a detectar.
 *
 * @author Francisco Javier Moreno Vega
 * @author Alberto Quesada Aranda
 * @version 28.01.2015
 * @since 20.01.2015
 */
public class PatternActivity extends Activity implements OnClickListener,
		OnTouchListener {

	/**
	 * Definición de los patrones.
	 */
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

	int[][] patterns = { pattern_Z, pattern_L, pattern_M, pattern_N, pattern_V };
	String[] names = { "Z", "L", "M", "N", "V" };

	private static String TAG = "PatternActivity";

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

		setContentView(R.layout.activity_pattern);

		for (int i = 0; i < ids.length; i++) {
			ImageView im = (ImageView) findViewById(ids[i]);
			im.setOnClickListener(this);
			im.setOnTouchListener(this);
			pattern.add(im);
		}
	}

	/**
	 * Método que se llama cuando se detecta una pulsación en la pantalla.
	 *
	 * Implement this method to handle touch screen motion events.
	 *
	 * If this method is used to detect click actions, it is recommended that
	 * the actions be performed by implementing and calling performClick(). This
	 * will ensure consistent system behavior, including:
	 *
	 * - obeying click sound preferences - dispatching OnClickListener calls -
	 * handling ACTION_CLICK when accessibility features are enabled
	 *
	 * @param event
	 *            The motion event
	 *
	 * @return True if the event was handled, false otherwise.
	 *
	 */

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

		// ACTION_UP indica que se ha levantado el dedo de la pantalla, y por
		// tanto damos por concluido el recorrido del patrón.
		if (event.getAction() == MotionEvent.ACTION_UP) {
			checkStatus();
		}

		return false;
	}

	/**
	 * Comprueba la correción del patrón, y si este es correcto da por
	 * finalizada la aplicación, si reinicia la lectura del patrón.
	 *
	 */
	private void checkStatus() {
		boolean finish = false;
		Log.d(TAG, pattern_input.toString());
		int color;
		if (!checkPatterns())
			color = Color.RED;
		else {
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

		if (finish) {
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

	/**
	 * Comprueba si el recorrido del patrón realizado concuerda con alguno de
	 * los patrones definidos.
	 * 
	 * @return true si el patrón es correcto
	 * 
	 * @see checkPattern
	 */
	private boolean checkPatterns() {

		for (int i = 0; i < patterns.length; i++) {
			if (checkPattern(patterns[i], names[i])) {
				String result = "Patrón correcto: " + names[i];
				Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
				return true;
			}
		}

		return false;
	}

	/**
	 * Comprueba si el recorrido del patrón realizado concuerda con pattern.
	 *
	 * @return true si el patrón es correcto.
	 */
	private boolean checkPattern(int[] pattern, String name) {
		if (pattern_input.size() == pattern.length) {
			for (int i = 0; i < pattern_input.size(); i++) {
				if (pattern_input.get(i).intValue() != pattern[i])
					return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Colorea los puntos del patrón.
	 *
	 * @param c
	 *            Color con el que se colorean los puntos del patrón.
	 * 
	 */
	private void drawPattern(int c) {
		for (int i = 0; i < pattern.size(); i++)
			if (pattern_input.contains(new Integer(i)))
				pattern.get(i).setColorFilter(c);
	}

	/**
	 * Reinicia el color de los puntos del patrón.
	 *
	 */
	private void cleanPattern() {
		for (int i = 0; i < pattern.size(); i++)
			pattern.get(i).setColorFilter(null);
		pattern_input.clear();
	}

	Rect outRect = new Rect();
	int[] location = new int[2];

	/**
	 * Comprueba si la posición actual del toque en la pantalla pertenece a una
	 * vista determinada.
	 *
	 * @param view
	 *            Vista a comprobar.
	 * @param x
	 *            Posición x del punto.
	 * @param y
	 *            Posición y del punto.
	 * 
	 * @return true si la posición pertenece a la vista.
	 * 
	 * @see onTouch
	 */
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
