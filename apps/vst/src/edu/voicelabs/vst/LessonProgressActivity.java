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
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

/**
 * Activity to display the ongoing progress of the user toward completion of all the games for a
 * particular phoneme. Games are started by selecting the corresponding marker.
 * 
 * @author James Manley
 * @author Dylan Kelly
 *
 */
public class LessonProgressActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonPhoneme;
	private ImageButton imageButtonSyllable;
	private ImageButton imageButtonWord;
	private ImageButton imageButtonChoose;
	private ImageButton imageButtonBack;
	
	/** Assign layout elements */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lesson_progress);
		
		this.imageButtonPhoneme = (ImageButton) findViewById(R.id.imageButtonPhoneme);
		this.imageButtonSyllable = (ImageButton) findViewById(R.id.imageButtonSyllable);
		this.imageButtonWord = (ImageButton) findViewById(R.id.imageButtonWord);
		this.imageButtonChoose = (ImageButton) findViewById(R.id.imageButtonChoose);
		this.imageButtonBack = (ImageButton) findViewById(R.id.imageButtonBack);
		
		this.imageButtonPhoneme.setOnTouchListener(this);
		this.imageButtonSyllable.setOnTouchListener(this);
		this.imageButtonWord.setOnTouchListener(this);
		this.imageButtonChoose.setOnTouchListener(this);
		this.imageButtonBack.setOnTouchListener(this);
	}
	
	/** Wait until the user actually sees the activity to start the anmations */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus) {
			//start animations
			AnimationHelper.runAlphaAnimation(this, R.id.clouds, R.anim.anim_clouds);
			AnimationHelper.runAlphaAnimation(this, R.id.sunProgress, R.anim.anim_sun);
			
			final DBHelper db = new DBHelper(getApplicationContext());
			
			// fire phoneme circle after delay
			final Handler phonemeCircleHandler = new Handler();		
			phonemeCircleHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					imageButtonPhoneme.setBackgroundResource(db.getProgress("Default", "L", "Phoneme") ? R.anim.anim_star_small : R.anim.anim_btn_red_circle1);
	    			AnimationDrawable phonemeLevelAnimation = (AnimationDrawable) imageButtonPhoneme.getBackground();
	    			phonemeLevelAnimation.start();	
				}
				
			}, 1000);
			
			// fire syllable circle after delay
			final Handler syllableCircleHandler = new Handler();		
			syllableCircleHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					imageButtonSyllable.setBackgroundResource(db.getProgress("Default", "L", "Syllable") ? R.anim.anim_star_small : R.anim.anim_btn_red_circle1);
	    			AnimationDrawable syllableLevelAnimation = (AnimationDrawable) imageButtonSyllable.getBackground();
	    			syllableLevelAnimation.start();	
				}
				
			}, 2000);
			
			// fire word circle after delay
			final Handler wordCircleHandler = new Handler();	
			wordCircleHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					imageButtonWord.setBackgroundResource(db.getProgress("Default", "L", "Word") ? R.anim.anim_star_small : R.anim.anim_btn_red_circle1);
	    			AnimationDrawable wordLevelAnimation = (AnimationDrawable) imageButtonWord.getBackground();
	    			wordLevelAnimation.start();	
				}
				
			}, 3000);
			
			// fire feed circle after delay
			final Handler feedCircleHandler = new Handler();			
			feedCircleHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					imageButtonChoose.setBackgroundResource(db.getProgress("Default", "L", "Choose") ? R.anim.anim_star_small : R.anim.anim_btn_red_circle1);
	    			AnimationDrawable feedLevelAnimation = (AnimationDrawable) imageButtonChoose.getBackground();
	    			feedLevelAnimation.start();	
				}
				
			}, 4000);
		}
	}	
	
	/** Touch map points to go to the respective game */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {		
			DBHelper db = new DBHelper(getApplicationContext());
			if (v == this.imageButtonPhoneme) {
				if (!db.getProgress("Default", "L", "Phoneme")) {
					AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonPhoneme, R.anim.anim_btn_red_circle_backwards);
				}
				Intent intent = new Intent(getApplicationContext(), PhonemeGameActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonSyllable) {
				if (!db.getProgress("Default", "L", "Syllable")) {
					AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonSyllable, R.anim.anim_btn_red_circle_backwards);
				}
				Intent intent = new Intent(getApplicationContext(), SyllableGameActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent); 
			}
			else if (v == this.imageButtonWord) {
				if (!db.getProgress("Default", "L", "Word")) {
					AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonWord, R.anim.anim_btn_red_circle_backwards);
				}
				Intent intent = new Intent(getApplicationContext(), WordGameActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonChoose) {
				if (!db.getProgress("Default", "L", "Choose")) {
					AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonChoose, R.anim.anim_btn_red_circle_backwards);
				}
				Intent intent = new Intent(getApplicationContext(), ChooseGameActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonBack) {
				Intent intent = new Intent(getApplicationContext(), PhonemeSelectActivity.class);
	            startActivity(intent); 
			}
		}
		return false;
	}

}
