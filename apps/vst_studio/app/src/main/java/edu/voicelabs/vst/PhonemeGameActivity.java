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

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.voicelabs.vst.RecognizerTask.Mode;

/**
 * Simplest game, requiring the user to say the phoneme twice in six attempts.
 * These values are used to counter the small possibility of false positives
 * and provide correction only after a reasonable number of attempts
 * (with room for noise).
 * 
 * @author James Manley
 * @author Dylan Kelly
 *
 */
public class PhonemeGameActivity extends AbstractGameActivity implements OnTouchListener {
	
	// Layout elements
	protected RelativeLayout gameLayout;		

	// Menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	private ImageButton buttonStart;
	
	//helper red circle
	private ImageButton leoHelper;
	private boolean leoPressed = false;


	/** Constructor */
	public PhonemeGameActivity() {
		super();

		// Basic inherited fields to set
		subPattern = "L";
		maxCorrectMatches = 2;
		maxAttempts = 6;
		mode = Mode.PHONEME;
	}
	
	/** Set expected values and assign interactive elements in the layout */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.phoneme_game);		
		
		// Menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		//UI
		this.message = (TextView) findViewById(R.id.txt_game1_l);
		this.message.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf"));
		
		this.prompt = (ImageView) findViewById(R.id.imageViewPrompt);
		
		// Click on leo to start the game
		this.buttonStart = (ImageButton) findViewById(R.id.buttonStartWord);	
		this.buttonStart.setOnTouchListener(this);
		
		//touch helper red circle
		this.leoHelper = (ImageButton) findViewById(R.id.leo_helper);
		this.leoHelper.setOnTouchListener(this);
		AnimationHelper.runKeyframeAnimation(this, R.id.leo_helper, R.anim.anim_btn_red_circle5);	

		setState(InteractionState.IDLE);
		
		//Intro sound
		MediaPlayer leoInstructions = MediaPlayer.create(getApplicationContext(), R.raw.leo_now_your_turn);
		leoInstructions.start();
	}
	
	/** 
	 * Get reference to top level layout element, so the recogniser thread
	 * knows what to update.
	 */
	@Override
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_phoneme);
	}

	/** Finish the game */
	@Override
	protected void fullSuccess() {		
		this.playingRef = R.raw.feedback_pos_really_cool;
		this.message.setText("You got it!");
		setState(InteractionState.PLAY);

		Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	        	 runGameCompletion("Phoneme");
	         } 
	    }, 3000); 
	}
	
	/** Positive assurance */
	@Override
	protected void partSuccess() {		
		this.playingRef = R.raw.feedback_partial_try_one_more;
		this.message.setText("Great! Say it again.");
		setState(InteractionState.PLAY_THEN_RECORD);
	}
	
	/** Let user know they aren't getting it, without being discouraging */
	@Override
	protected void fullAttempts() {
		this.playingRef = R.raw.feedback_neg_have_another_go;
		this.message.setText("Keep trying...");
		setState(InteractionState.PLAY_THEN_RERUN);
	}
	
	/** Repeat the phoneme if Leo is touched */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.buttonSkip) {
				// Skip to the games
				setState(InteractionState.IDLE);
				Intent intent = new Intent(getApplicationContext(), SyllableGameActivity.class);
	            startActivity(intent); 
			} else if (v == this.buttonMenu) {
				// Skip to the Menu
				setState(InteractionState.IDLE);
				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			} else if ((v == this.buttonStart) || (v == this.leoHelper)) {
				//First run
				if (leoPressed == false){
					this.leoHelper.setVisibility(View.INVISIBLE);			
					leoPressed = true;
					
					// Play animation manually 
					buttonStart.setBackgroundResource(R.anim.anim_leo_hand_to_ear);
					AnimationDrawable leoAnimation = (AnimationDrawable) buttonStart.getBackground();
					leoAnimation.start();
				}
				this.playingRef = R.raw.phoneme_lll;
				setState(InteractionState.PLAY_THEN_RECORD);
			}
		}
	return false;
	}
	
}

