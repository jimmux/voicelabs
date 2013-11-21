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
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Simple splash screen with music and animation.
 * 
 * @author James Manley
 * @author Dylan Kelly
 *
 */
public class StartActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonStart;
	private MediaPlayer music; 
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		
		// Import fonts
		TextView txt_phoneme = (TextView) findViewById(R.id.txt_phoneme);
		TextView txt_friends = (TextView) findViewById(R.id.txt_friends);
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf");  
		txt_phoneme.setTypeface(font);  
		txt_friends.setTypeface(font); 
		
		this.imageButtonStart = (ImageButton) findViewById(R.id.imageButtonStart);
		this.imageButtonStart.setOnTouchListener(this);
		
		// Start loading assets
		Utilities utils = new Utilities(getApplicationContext());
		utils.setupSpeechData();
		
		// Initialise the database, creating profile data if not yet available
		DbHelper db = new DbHelper(getApplicationContext());
		db.initialiseWithDefaults(false);
	}
	
	/**
	 *  Show load screen, advance to start screen when ready.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus) {
			
			// Play intro music
			music = MediaPlayer.create(getApplicationContext(), R.raw.splash_music);
			// When it's finished playing back - then run game
			music.setOnCompletionListener(new OnCompletionListener() {
	            @Override
	            public void onCompletion(MediaPlayer mp) {						
	            	music.start();	
	            }
			});			
			// Play Phoneme recording
			music.start();	
			
			// Start fade in animations
			AnimationHelper.runAlphaAnimation(this, R.id.leo_fade_in, R.anim.anim_fade_in);
			AnimationHelper.runAlphaAnimation(this, R.id.txt_phoneme, R.anim.anim_txt_fade);
			AnimationHelper.runAlphaAnimation(this, R.id.txt_friends, R.anim.anim_txt_fade1);
			AnimationHelper.runAlphaAnimation(this, R.id.obj_sun, R.anim.anim_sun);
			AnimationHelper.runAlphaAnimation(this, R.id.imageButtonStart, R.anim.anim_start_btn);
			ImageView leofadein = (ImageView) findViewById(R.id.leo_fade_in); 
			
			leofadein.setBackgroundResource(0);
			
			AnimationHelper.runKeyframeAnimation(this, R.id.leo_fade_in, R.anim.anim_leo_blinkonly);
			imageButtonStart.setVisibility(View.VISIBLE);
		}
	}
	
	/** Handle all touch input */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.imageButtonStart) {
				// Go to the phoneme select screen
				Intent intent = new Intent(getApplicationContext(), PhonemeSelectActivity.class);
	            startActivity(intent); 
	            music.stop();
	            music.release();
	            music = null;
			}
		}
		
		return false;
	}
}
