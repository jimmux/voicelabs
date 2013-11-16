package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class SettingsActivity extends Activity implements OnTouchListener {
	
	private Button buttonExit;
	private Button buttonResetProfile;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		this.buttonExit = (Button) findViewById(R.id.buttonExit);
		this.buttonExit.setOnTouchListener(this);
		this.buttonExit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf"));

		this.buttonResetProfile = (Button) findViewById(R.id.buttonResetProfile);
		this.buttonResetProfile.setOnTouchListener(this);
		this.buttonResetProfile.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf"));
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {		
			if (v == this.buttonExit) {
				Intent intent = new Intent(getApplicationContext(), PhonemeSelectActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.buttonResetProfile) {
				// Recreate empty profile
				DBHelper db = new DBHelper(getApplicationContext());
				db.initialiseWithDefaults(true);
				this.buttonResetProfile.setText("Profile Reset!");
			}
		}
		return false;
	}

}
