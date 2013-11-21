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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import edu.voicelabs.vst.RecognizerTask.Mode;

/**
 * The games use similar patterns of voice recognition, so this common behaviour is 
 * encapsulated here to ensure consistency and simplify the logic of concrete games.
 * 
 * A nice side effect of this is that control of the speech/listen prompt is
 * mostly automated.
 * 
 * @author James Manley
 * @author Dylan Kelly
 *
 */
abstract class AbstractGameActivity extends Activity implements OnTouchListener, RecognitionListener {
	
	// Speech recognition bits
	static {System.loadLibrary("pocketsphinx_jni");}
	private RecognizerTask rec;
	private Thread rec_thread;
	
	private int successCount = 0;		// Keep track of how many successes had so far.
	private boolean gotResult = false; 	// Flag for prevention of multiple completion states.
	
	// To be set by all child games
	protected String subPattern;				// Determines what we are looking for, e.g. "L", "L-AH", "LOLLY"
	protected static int maxCorrectMatches;		// Number of matches to consider it a successful attempt
	protected static int maxAttempts;			// When this number of attempts is detected, consider the exercise failed
	protected static Mode mode;					// Matching mode to use, PHONEME, SYLLABLE, or WORD
	
	abstract protected ViewGroup getGameLayout();	// Children to return the ViewGroup (usually the top level layout) containing all updateable UI elements
	abstract protected void fullSuccess();
	abstract protected void partSuccess();
	abstract protected void fullAttempts();
	
	// Used by the state control mechanism, most of these need to be set from child layouts
    protected MediaPlayer player;
    protected int playingRef;		// Ref of the audio resource set to play
	protected ImageView prompt;
	protected TextView message;
	protected AnimationDrawable promptAnim;
    
	
	/** Initialise the speech recogniser */
	
	/** Ensure the recogniser thread isn't left running when leaving the game */
	protected void onDestroy(Bundle savedInstanceState) {
		this.rec.shutdown();		
	}

	/** The activity will always be in one of these three states */
    protected static enum InteractionState {RECORD, PLAY, IDLE, PLAY_THEN_RECORD, PLAY_THEN_RERUN};
    private InteractionState state = InteractionState.IDLE;
    
    /**
     * Update the audio state of the game, which is basically either playing a sound, 
     * listening for speech, or doing nothing.
     * 
     * This helps to coordinate commonly paired activities, like visual indicators
     * while the sound plays/records.
     * 
     * @param newState
     */
    protected synchronized void setState(final InteractionState newState) {
        Log.d(getClass().getName(), "*** Switching state from " + this.state + " to " + newState);
    	
    	// Cancel any current playing or recording (effectively set to IDLE conditions)
        if (this.prompt != null) {
	    	this.prompt.clearAnimation();
        }
        if (this.promptAnim != null) {
	    	this.promptAnim.stop();
        }
    	if (this.rec != null) {
    		this.rec.stop();	
    	}
		if (this.player != null) {
			this.player.stop();
			this.player.release();
			this.player = null;  
		}        
        
    	
    	// Now set the visual prompt and update state
    	switch (newState) {
    	case RECORD:
    		// Set up speech recognition if not already done
    		if (this.rec == null) {
				this.rec = new RecognizerTask(getApplicationContext(), mode);
				this.rec_thread = new Thread(this.rec);
				this.rec.setRecognitionListener(this);
				this.rec_thread.start();	
				Log.d(getClass().getName(), "*** New recognizer with: mode " + mode);
    		}
        	this.rec.start();
    		
        	this.prompt.setBackgroundResource(R.anim.anim_record_btn);
        	this.promptAnim = (AnimationDrawable) this.prompt.getBackground();
        	this.promptAnim.start();
    		this.prompt.setVisibility(View.VISIBLE);
            break;
    	case PLAY:
    	case PLAY_THEN_RECORD:
    	case PLAY_THEN_RERUN:
        	this.prompt.setBackgroundResource(R.anim.anim_play_btn);
        	this.promptAnim = (AnimationDrawable) this.prompt.getBackground();
	    	this.promptAnim.start();
    		this.prompt.setVisibility(View.VISIBLE);
			this.player = MediaPlayer.create(getApplicationContext(), this.playingRef);
			// When playing is done, go to the next state
			this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	            @Override
	            public void onCompletion(MediaPlayer mp) {
	    			if (newState == InteractionState.PLAY_THEN_RECORD) {
	    				setState(InteractionState.RECORD);
	    			} else if (newState == InteractionState.PLAY_THEN_RERUN) {
//	    				hardResetRecognizer();
//		    			runGame();
//	    				setState(InteractionState.RECORD);
	    				
//	    				killRecognizer();
	    				rec = null;
	    				successCount = 0;
	    				gotResult = false;
	    				setState(InteractionState.RECORD);
	    			} else {
	    				setState(InteractionState.IDLE);
	    			}
	            }
			});
			this.player.start();
		    break;
    	case IDLE:
    		this.prompt.setVisibility(View.INVISIBLE);
//			rec = null;
//			successCount = 0;
//			gotResult = false;
    	}
    	this.state = newState;  		
    }

	
	/**
	 * Need to stop the recogniser consuming memory and cycles when 
	 * the app is unavailable and therefore shouldn't be doing any
	 * speech recognition or playing.
	 */
	@Override
	public void onPause() {
	    super.onPause();
		if (rec != null) {
		    setState(InteractionState.IDLE);
		    this.rec.shutdown();
		}
	}
	
	/** Called when partial results are generated. */
	public void onPartialResults(Bundle b) {
		final AbstractGameActivity that = this;
		final String hyp = b.getString("hyp");
		that.getGameLayout().post(new Runnable() {
			public void run() {	
				Pattern successPattern = Pattern.compile("\\b" + that.subPattern + "(-|\\b)");
				Pattern attemptPattern = Pattern.compile("\\b[A-Z]");
				int count;
				String speech = (hyp == null) ? "" : hyp;				
				
				Matcher successMatcher = successPattern.matcher(speech);
				count = 0;
				// Count how many successful matches we have
				while (successMatcher.find()) {
					count++;
				}
				// count can revert, so go with the max count found so far (or update)
				if (count > that.successCount) {
					that.successCount = count;
					
					if (that.successCount < maxCorrectMatches) {
						that.rec.stop();	// Stop while acting on result, to avoid interference if speech is used
						Log.d(getClass().getName(), "*** Partial success with count: " + that.successCount);
						partSuccess();
						that.rec.start();
						return;
					} else if (!that.gotResult) {
						that.gotResult = true;
						that.rec.stop();
						Log.d(getClass().getName(), "*** Found enough results");
						fullSuccess();
						return;
					}
				}
				
				// Check for the maximum number of attempts
				Matcher attemptMatcher = attemptPattern.matcher(speech);
				count = 0;
				while (attemptMatcher.find()) {
					count++;
					if ((count > maxAttempts) && (!that.gotResult)) {
						that.gotResult = true;
						that.rec.stop();
						Log.d(getClass().getName(), "*** Ran out of attempts");
						fullAttempts();
					}
				}
							
			}
		});
	}

	/** Called when full results are generated. */
	public void onResults(Bundle b) {
		// No action needed.
		// Marginal improvement could be had be checking the same as partial results, but not worth
		// the extra complexity.
	}
	
	/** Handle recognition errors */
	public void onError(int err) {
		// No action needed
		Log.e(getClass().getName(), "*** game error");
	}
	
	/**
	 * May be called by a child class to update progress in the database.
	 * 
	 * If it is the last in the lesson,
	 * shows a custom dialog completion screen, then returns to the phoneme select
	 * screen after a short period of time, or when touched.
	 * 
	 * @param gameName
	 */
	protected void runGameCompletion(String gameName) {
		setState(InteractionState.IDLE);
		
		// Update progress
		DbHelper db = new DbHelper(getApplicationContext());
		db.setProgress("Default", "L", gameName);
    	System.gc();	// Let the system know it's a good time to clean up memory
		// Advance if completed
		if (db.getComplete("Default", "L")) {
			Intent intent = new Intent(getApplicationContext(), LessonCompleteActivity.class); //TODO: got to final screen
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        startActivity(intent);
		} else {
			Intent intent = new Intent(getApplicationContext(), LessonCompleteActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        startActivity(intent);
		}
	}
	
}



