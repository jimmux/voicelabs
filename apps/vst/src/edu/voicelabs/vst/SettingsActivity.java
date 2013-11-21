/*
 * Copyright (c) VoiceLabs (James Manley and Dylan Kelly), 2013
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of VoiceLabs.
 */

package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Simple activity just to wipe the profile and show licence information.
 * 
 * @author James Manley
 * @author Dylan Kelly
 *
 */
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
				} else {
					Intent intent = new Intent(getApplicationContext(), PhonemeSelectActivity.class);
		            startActivity(intent); 
				}
			} else if (v == this.buttonResetProfile) {
				licenseContext = true;
				// Recreate empty profile
				DbHelper db = new DbHelper(getApplicationContext());
				db.initialiseWithDefaults(true);		
				Toast.makeText(getApplicationContext(), "Profile Reset!", Toast.LENGTH_SHORT).show();
			} else if (v == this.buttonAbout) {
				licenseContext = true;
				txtLicense.setText(this.getResources().getString(R.string.about));
				hideStuff();
			} else if (v == this.buttonLicense) {
				licenseContext = true;
				txtLicense.setText(this.getResources().getString(R.string.license));
				hideStuff();
			}
		}
		return false;
	}

}
