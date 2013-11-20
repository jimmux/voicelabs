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
 * Game to cycle through a collection of syllables that have the main phoneme in common.
 * 
 * @author James Manley
 * @author Dylan Kelly
 *
 */
public class SyllableGameActivity extends AbstractGameActivity implements OnTouchListener {
	
	// Layout elements
	protected RelativeLayout gameLayout;	

	// Menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	
	private ImageButton buttonStart;
	
	// Loops through the set of syllables to speak
	private final String[] syllables = {"LA", "LI", "LU", "LE", "LO"};
	private final int[] syllableSounds = {R.raw.syllable_la, R.raw.syllable_li, R.raw.syllable_lu, R.raw.syllable_le, R.raw.syllable_lo};
	private int syllableIndex = 0;
	
	private boolean started = false;
	
	//Touch circle helper
	private ImageButton leoHelper;
	private boolean leoPressed = false;

	/** Set expected values and assign interactive elements in the layout */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		subPattern = "L";
		maxCorrectMatches = 1;
		maxAttempts = 2;
		
		setContentView(R.layout.syllable_game);
		
		// Menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		//UI
		this.message = (TextView) findViewById(R.id.txt_label_syllable);
		this.message.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf"));
		
		this.prompt = (ImageView) findViewById(R.id.imageViewPrompt);
		
		// Click on leo to start the game
		this.buttonStart = (ImageButton) findViewById(R.id.buttonStartWord);	
		this.buttonStart.setOnTouchListener(this);		

		setState(InteractionState.IDLE);
		
		// Touch helper circle
		this.leoHelper = (ImageButton) findViewById(R.id.leo_helper);
		this.leoHelper.setOnTouchListener(this);
		AnimationHelper.runKeyframeAnimation(this, R.id.leo_helper, R.anim.anim_btn_red_circle5);
		
		//Intro sound
		MediaPlayer leoInstructions = MediaPlayer.create(getApplicationContext(), R.raw.leo_now_your_turn);
		leoInstructions.start();
	}
	
	/** Set the type of recognition for this game */
	@Override
	protected Mode getMode() {
		return Mode.SYLLABLE;
	}
	
	/** 
	 * Get reference to top level layout element, so the recogniser thread
	 * knows what to update.
	 */
	@Override
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_syllable);
	}

	/**
	 * Move to the next syllable, and if all done, finish the game.
	 */
	@Override
	protected void fullSuccess() {
		// Move to the next syllable, or complete the game
		this.syllableIndex++;
		if (this.syllableIndex >= this.syllables.length) {
			this.playingRef = R.raw.feedback_pos_really_cool;
			this.message.setText("Finished!");
			setState(InteractionState.PLAY);
			
			// Last game, so go to the victory screen after 3 sec delay
			Handler handler = new Handler(); 
		    handler.postDelayed(new Runnable() { 
		         public void run() { 
		        	 runGameCompletion("Syllable");
		         } 
		    }, 2000); 
		}
		else {
			setState(InteractionState.IDLE);
			this.message.setText("Say " + this.syllables[this.syllableIndex]);
			MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.feedback_pos_super);
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	            @Override
	            public void onCompletion(MediaPlayer mp) {
	    			playingRef = syllableSounds[syllableIndex];
	    			setState(InteractionState.PLAY_THEN_RERUN);
	            }
			});
			mediaPlayer.start();
		}
	}
	
	/** 
	 * Currently set to full success on a single match, so never called.
	 * Left here to allow for adjustment, as needed by future implementations.
	 */
	@Override
	protected void partSuccess() {
		// Encourage the same syllable		
		this.playingRef = R.raw.feedback_partial_try_one_more;
		this.message.setText("Good, do it again!");
		setState(InteractionState.PLAY_THEN_RECORD);
	}
	
	/** When maximum speech attempts detected, repeat the expected syllable to remind the user. */
	@Override
	protected void fullAttempts() {
		this.playingRef = syllableSounds[this.syllableIndex];
		this.message.setText("Try it again!");
		setState(InteractionState.PLAY_THEN_RERUN);
//		wipeRecognizer();
	}
	
	/** Touch Leo to start */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.buttonSkip) {
				// Skip to the games
				Intent intent = new Intent(getApplicationContext(), WordGameActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.buttonMenu) {
				// Skip to the Menu
				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			}
			else if ((v == this.buttonStart) || (v == this.leoHelper)) {
				
				//First run
				if (leoPressed == false){
					this.leoHelper.setVisibility(View.INVISIBLE);			
					leoPressed = true;
					
				}
				
				
				//Change text to first syllable
//				this.syllableIndex = 0;
				this.message.setText(this.syllables[this.syllableIndex]);
				this.playingRef = syllableSounds[this.syllableIndex];
				if (this.started) {
					setState(InteractionState.PLAY_THEN_RECORD);
				}
				else {		
					this.started = true;
					setState(InteractionState.PLAY_THEN_RERUN);
					
					// Play animation manually 
					buttonStart.setBackgroundResource(R.anim.anim_leo_hand_to_ear);
					AnimationDrawable leoAnimation = (AnimationDrawable) buttonStart.getBackground();
					leoAnimation.start();
				}
			}
		}
	return false;
	}
}

