package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;

public class StartActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonStart;
	private Button buttonGoToDebug;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		
		this.imageButtonStart = (ImageButton) findViewById(R.id.imageButtonStart);
		this.buttonGoToDebug = (Button) findViewById(R.id.buttonGoToDebug);
		
		this.imageButtonStart.setOnTouchListener(this);
		this.buttonGoToDebug.setOnTouchListener(this);
		
		// Start loading assets
		Utilities utils = new Utilities(getApplicationContext());
		utils.SetupSpeechData();
		
		// Initialise the database
		DBHelper db = new DBHelper(getApplicationContext());
		db.InitialiseWithDefaults();
	}
	
	// Show load screen, advance to start screen when ready.
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus) {
			//imageButtonStart.setVisibility(View.INVISIBLE);
			//SystemClock.sleep(10000);
			imageButtonStart.setVisibility(View.VISIBLE);	
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.imageButtonStart) {
				// Go to the phoneme select screen
				Intent intent = new Intent(getApplicationContext(), PhonemeSelectActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.buttonGoToDebug) {
				// Go to the debug screen
				Intent intent = new Intent(getApplicationContext(), DebugScreenActivity.class);
	            startActivity(intent); 
			}
		}
		
		return false;
	}
}
