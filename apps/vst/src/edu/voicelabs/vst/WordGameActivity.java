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
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.voicelabs.vst.RecognizerTask.Mode;

/**
 * Game to cycle through a collection of words that have the main phoneme in common.
 * 
 * @author James Manley
 * @author Dylan Kelly
 *
 */
public class WordGameActivity extends AbstractGameActivity implements OnTouchListener {
	
	// Layout elements
	protected RelativeLayout gameLayout;	

	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	private ImageButton buttonStart;
	private ImageButton wordObject;
	
	private ImageButton leoHelper;
	private boolean leoPressed = false;
	
	/**
	 * Simple struct for associated word data to loop through
	 *
	 */
	private class WordData {
		String displayWord;		// Text to use for display
		String matchWord;		// Text to use for speech matching
		int drawable;			// The reference of the image in res/drawable to use
		int speechAudio;		// The reference of the sound file in res/raw to use, e.g. R.raw.tmp_lolly
		
		public WordData(String displayWord, String matchWord, int drawable, int speechAudio) {
			this.displayWord = displayWord;
			this.matchWord = matchWord;
			this.drawable = drawable;
			this.speechAudio = speechAudio;
		}
	}
	private WordData[] words = {
		new WordData("Lemon", "LEMON", R.drawable.img_obj_lemon, R.raw.word_lemon),
		new WordData("Lamb", "LAMB", R.drawable.img_obj_lamb, R.raw.word_lamb),
		new WordData("Lettuce", "LETTUCE", R.drawable.img_obj_lettuce, R.raw.word_lettuce),
		new WordData("Lizard", "LIZARD", R.drawable.img_obj_lizzard, R.raw.word_lizzard),
		new WordData("Lightning", "LIGHTNING", R.drawable.img_obj_lightning, R.raw.word_lightning),
		new WordData("Lolly", "LOLLY", R.drawable.img_obj_lolly, R.raw.word_lolly),
		new WordData("Leaves", "LEAVES", R.drawable.img_obj_leaves, R.raw.word_leaves)
	};
	private int wordIndex = 0;

	/** Set expected values and assign interactive elements in the layout */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Basic inherited fields to set
		subPattern = "";
		maxCorrectMatches = 1;
		maxAttempts = 3;
		
		setContentView(R.layout.word_game);
		
		// Menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		// UI
		this.message = (TextView) findViewById(R.id.txt_game_3);
		this.message.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf"));
		
		this.prompt = (ImageView) findViewById(R.id.imageViewPrompt);

		this.wordObject = (ImageButton) findViewById(R.id.btn_game3_obj); 
		this.wordObject.setVisibility(View.INVISIBLE);
		
		//touch helper red circle
		this.leoHelper = (ImageButton) findViewById(R.id.leo_helper);
		this.leoHelper.setOnTouchListener(this);
		AnimationHelper.runKeyframeAnimation(this, R.id.leo_helper, R.anim.anim_btn_red_circle5);	
		
		// Click on leo to start the game
		this.buttonStart = (ImageButton) findViewById(R.id.buttonStartWord);	
		this.buttonStart.setOnTouchListener(this);		

		setState(InteractionState.IDLE);	
	}
	
	/** Set the type of recognition for this game */
	@Override
	protected Mode getMode() {
		return Mode.WORD;
	}
	
	/** 
	 * Get reference to top level layout element, so the recogniser thread
	 * knows what to update.
	 */
	@Override
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_word);
	}
	
	/**
	 * Move to the next word, and if all done, finish the game.
	 */
	@Override
	protected void fullSuccess() {	
		// Move to the next word, or complete the game
		this.wordIndex++;
		if (this.wordIndex >= this.words.length) {
			this.playingRef = R.raw.feedback_pos_really_cool;
			this.message.setText("Well Done!");
			setState(InteractionState.PLAY);
			
			// Last game, so go to the victory screen after 3 sec delay
			Handler handler = new Handler(); 
		    handler.postDelayed(new Runnable() { 
		    	public void run() { 
		    		runGameCompletion("Word");
		    	} 
		    }, 3000); 
		}
		else {
			Animation fadeInAnimation;
			this.playingRef = this.words[this.wordIndex].speechAudio;
			this.message.setText(this.words[this.wordIndex].displayWord);
			this.wordObject.setBackgroundResource(this.words[this.wordIndex].drawable);
			
			// Fade in
			fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in_obj);
			fadeInAnimation.reset();
			
			View objectView = findViewById(R.id.btn_game3_obj);

		    // Cancel any pending animation and start this one
	    	objectView.clearAnimation();
	    	objectView.startAnimation(fadeInAnimation);
	    	
			this.subPattern = this.words[this.wordIndex].matchWord;
			setState(InteractionState.PLAY_THEN_RERUN);
		}
	}
	
	/** 
	 * Currently set to full success on a single match, so never called.
	 * Left here to allow for adjustment, as needed by future implementations.
	 */
	@Override
	protected void partSuccess() {		
		// Encourage the same word - note this won't be executed if accepting a single positive attempt
		this.playingRef = R.raw.feedback_pos_great_job;
		this.message.setText("Good, do it again!");
		setState(InteractionState.PLAY_THEN_RECORD);
	}
	
	/** When maximum speech attempts detected, repeat the expected word to remind the user. */
	@Override
	protected void fullAttempts() {
		this.playingRef = this.words[this.wordIndex].speechAudio;
		this.message.setText("Try it again!");
		setState(InteractionState.PLAY_THEN_RERUN);
	}
	
	/** Touch Leo to start */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.buttonSkip) {
				// Skip to the games
				Intent intent = new Intent(getApplicationContext(), ChooseGameActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.buttonMenu) {
				// Skip to the Menu
				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			}
			else if ((v == this.buttonStart) || (v == this.leoHelper))  {
				// Start the game
				this.wordIndex = 0;
				this.message.setText(this.words[wordIndex].displayWord);
				this.wordObject.setBackgroundResource(this.words[wordIndex].drawable);
				
				//First run
				if (this.leoPressed == false){
					this.leoHelper.setVisibility(View.INVISIBLE);
					this.buttonStart.setVisibility(View.INVISIBLE);
					this.wordObject.setVisibility(View.VISIBLE);
					this.message.setText("Lemon");
					this.leoPressed = true;
				}
				
				// Fade in
				Animation fadeInAnimation;
				fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in_obj);
				fadeInAnimation.reset();
				
				View objectView = findViewById(R.id.btn_game3_obj);

			    // Cancel any pending animation and start this one
				objectView.clearAnimation();
				objectView.startAnimation(fadeInAnimation);
				
				this.playingRef = this.words[this.wordIndex].speechAudio;
				setState(InteractionState.PLAY_THEN_RECORD);
				
			}
		}
	return false;
	}
	
}

