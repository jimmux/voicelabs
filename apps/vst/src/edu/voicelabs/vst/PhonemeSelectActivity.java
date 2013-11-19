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
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Simple opening screen to let the user hoose which phoneme to practice, or access the settings
 * 
 * @author James Manley
 * @author Dylan Kelly
 *
 */
public class PhonemeSelectActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonL;
	private ImageButton imageButtonS;
	private ImageButton imageButtonCH;
	
	// Update to show completion
	private ImageView imageViewProgressL;
	
	private ImageButton buttonGoToSettings;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.phoneme_select);
		
		//import fonts
		TextView txt_settings = (TextView) findViewById(R.id.txt_settings);
		TextView txt_level_l = (TextView) findViewById(R.id.txt_level_l);
		TextView txt_level_s = (TextView) findViewById(R.id.txt_level_s);
		TextView txt_level_ch = (TextView) findViewById(R.id.txt_level_ch);
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf");  
		
		txt_settings.setTypeface(font);  
		txt_level_l.setTypeface(font);
		txt_level_s.setTypeface(font);
		txt_level_ch.setTypeface(font);
		
		this.imageButtonL = (ImageButton) findViewById(R.id.imageButtonPhonemeL);
		this.imageButtonS = (ImageButton) findViewById(R.id.imageButtonPhonemeS);
		this.imageButtonCH = (ImageButton) findViewById(R.id.imageButtonPhonemeCH);
		
		this.imageViewProgressL = (ImageView) findViewById(R.id.imageViewProgressL);
		
		this.buttonGoToSettings = (ImageButton) findViewById(R.id.img_settings);
		
		this.imageButtonL.setOnTouchListener(this);
		this.imageButtonS.setOnTouchListener(this);
		this.imageButtonCH.setOnTouchListener(this);
		
		this.buttonGoToSettings.setOnTouchListener(this);
		
		this.imageViewProgressL.setVisibility(View.INVISIBLE);
	}
	
	/** Wait for the view to be visible before animating the progress star (if completed) */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		// Show and animate the progress star, if completed.
		DBHelper db = new DBHelper(getApplicationContext());
		if (db.getComplete("Default", "L")) {
			AnimationHelper.runKeyframeAnimation(this, R.id.imageViewProgressL, R.anim.anim_star_small);
			this.imageViewProgressL.setVisibility(View.VISIBLE);
		}
	}

	/** Got to available lesson when pressed, or notify when it isn't impemented yet */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.imageButtonL) {
				// Go to the lesson
				Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonS) {
				// Go nowhere (yet)
				//TODO play a lock closing sound (and maybe shake to indicate it's locked?) - I'll try and source something - DK
				AnimationHelper.runAlphaAnimation(this, R.id.btn_lockedS, R.anim.anim_shake);
				Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_SHORT).show();
			}
			else if (v == this.imageButtonCH) {
				// Go nowhere (yet)
				//play a lock closing sound - I'll try and source something - DK
				AnimationHelper.runAlphaAnimation(this, R.id.btn_lockedCh, R.anim.anim_shake);
				Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_SHORT).show();
			}
			else if (v == this.buttonGoToSettings) {
				// Go to the video
				Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	            startActivity(intent); 
			}
		}
		
		return false;
	}

}
