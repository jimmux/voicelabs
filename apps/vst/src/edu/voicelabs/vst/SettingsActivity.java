package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnTouchListener {
	
	private ImageButton buttonExit;
	private ImageButton buttonResetProfile;
	private ImageButton buttonAbout;
	private ImageButton buttonLicense;
	
	private TextView txtLicense;
	private ScrollView txtContainer;
	
	private Boolean licenseContext = false;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		this.buttonExit = (ImageButton) findViewById(R.id.buttonExit);
		this.buttonExit.setOnTouchListener(this);

		this.buttonResetProfile = (ImageButton) findViewById(R.id.btn_reset_profile);
		this.buttonResetProfile.setOnTouchListener(this);

		this.buttonAbout = (ImageButton) findViewById(R.id.btn_about);
		this.buttonAbout.setOnTouchListener(this);
		
		this.buttonLicense = (ImageButton) findViewById(R.id.btn_license);
		this.buttonLicense.setOnTouchListener(this);
		
		this.txtLicense = (TextView) findViewById(R.id.txt_license);
		this.txtContainer = (ScrollView) findViewById(R.id.txt_scroll_view);
	}
	
	public void hideStuff(){
		txtContainer.setVisibility(View.VISIBLE);
		buttonResetProfile.setVisibility(View.GONE);
		buttonAbout.setVisibility(View.GONE);
		buttonLicense.setVisibility(View.GONE);
	}
	
	public void showStuff(){
		txtContainer.setVisibility(View.GONE);
		buttonResetProfile.setVisibility(View.VISIBLE);
		buttonAbout.setVisibility(View.VISIBLE);
		buttonLicense.setVisibility(View.VISIBLE);
	}	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {		
			if (v == this.buttonExit) {
				if (licenseContext == true){
					showStuff();
					licenseContext = false;
				}else{
					Intent intent = new Intent(getApplicationContext(), PhonemeSelectActivity.class);
		            startActivity(intent); 
				}

			}
			else if (v == this.buttonResetProfile) {
				// Recreate empty profile
				DBHelper db = new DBHelper(getApplicationContext());
				db.initialiseWithDefaults(true);		
				Toast.makeText(getApplicationContext(), "Profile Reset!", Toast.LENGTH_SHORT).show();
			}
			else if (v == this.buttonAbout) {
				licenseContext = true;
				txtLicense.setText(this.getResources().getString(R.string.about));
				hideStuff();
				
			}
			
			else if (v == this.buttonLicense) {
				licenseContext = true;
				txtLicense.setText(this.getResources().getString(R.string.license));
				hideStuff();
			}
		}
		return false;
	}

}
