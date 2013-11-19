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
 *
 */
abstract class AbstractGameActivity extends Activity implements OnTouchListener, RecognitionListener {
	
	// Speech recognition bits
	static {System.loadLibrary("pocketsphinx_jni");}
	RecognizerTask rec;
	Thread rec_thread;
	float speech_dur;
	boolean listening;
	
	private int successCount = 0;		// Keep track of how many successes had so far.
	private boolean gotResult = false; 	// Flag for prevention of multiple completion states.
	
	// To be set by children - TODO: replace with abstract getters?
	protected String subPattern = "";		// Determines what we are looking for, e.g. "L", "L-AH", "LOLLY"
	protected int maxCorrectMatches = 1;	// Number of matches to consider it a successful attempt
	protected int maxAttempts = 6;			// When this number of attempts is detected, consider the exercise failed
	abstract protected Mode getMode();		// Matching mode to use, PHONEME, SYLLABLE, or WORD
	
	
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
    
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set up speech recognition
		this.rec = new RecognizerTask(getApplicationContext(), this.getMode());
		this.rec_thread = new Thread(this.rec);
		this.listening = false;
		this.rec.setRecognitionListener(this);
		this.rec_thread.start();	
	}
	
	public void onDestroy(Bundle savedInstanceState) {
		this.rec.shutdown();		
	}
	

	// The activity will always be in one of these three states
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
    	switch (this.state) {
    	case RECORD:
    	case PLAY_THEN_RECORD:
    	case PLAY_THEN_RERUN:
        	this.promptAnim.stop();
        	this.prompt.clearAnimation();
    		this.rec.stop();
    		break;
    	case PLAY:
//    	case PLAY_THEN_RECORD:
//    	case PLAY_THEN_RERUN:
        	this.promptAnim.stop();
        	this.prompt.clearAnimation();
			this.player.stop();
			this.player.release();
			this.player = null;  
			break;
    	case IDLE:
    		// Nothing to do
    	}
    	
    	// Now set the expected conditions and update state
    	switch (newState) {
    	case RECORD:
        	this.prompt.setBackgroundResource(R.anim.anim_record_btn);
        	this.promptAnim = (AnimationDrawable) this.prompt.getBackground();
        	this.promptAnim.start();
    		this.prompt.setVisibility(View.VISIBLE);
        	this.rec.start();
            break;
    	case PLAY:
    	case PLAY_THEN_RECORD:
    	case PLAY_THEN_RERUN:
        	this.prompt.setBackgroundResource(R.anim.anim_play_btn);
        	this.promptAnim = (AnimationDrawable) this.prompt.getBackground();
	    	this.promptAnim.start();
    		this.prompt.setVisibility(View.VISIBLE);
			this.player = MediaPlayer.create(getApplicationContext(), this.playingRef);
			this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	            @Override
	            public void onCompletion(MediaPlayer mp) {
	    			if (newState == InteractionState.PLAY_THEN_RECORD) {
	    				setState(InteractionState.RECORD);
	    			}
	    			else if (newState == InteractionState.PLAY_THEN_RERUN) {
		    			wipeRecognizer();
		    			runGame();
	    				setState(InteractionState.RECORD);
	    			}	    			
	    			else {
	    				setState(InteractionState.IDLE);
	    			}
	            }
			});
			this.player.start();
		    break;
    	case IDLE:
    		this.prompt.setVisibility(View.INVISIBLE);
    	}
    	this.state = newState;  		
    }


	
	@Override
	/**
	 * Need to stop the recognizer consuming memory and cycles when 
	 * the app is unavailable and therefore shouldn't be doing any
	 * speech recognition or playing.
	 */
	public void onPause() {
	    super.onPause();
	    setState(InteractionState.IDLE);
	}
	
	/**
	 * Start the game going. Needs to happen once before recording can work.
	 */
	protected void runGame() {		//TODO Check if called from children, and make private if not
		this.successCount = 0;
		this.gotResult = false;
		this.listening = true;
		this.rec.start();
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
				while (successMatcher.find() /*&& (count < that.maxCorrectMatches)*/) {
					count++;
				}
				// count can revert, so go with the max count found so far (or update)
				if (count > that.successCount) {
					that.successCount = count;

					if (that.successCount < that.maxCorrectMatches) {
						that.rec.stop();	// Stop while acting on result, to avoid interference if speech is used
						Log.d(getClass().getName(), "*** Partial success with count: " + that.successCount);
						partSuccess();
						that.rec.start();
						return;
					}
					else if (!that.gotResult) {
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
					if ((count > that.maxAttempts) && (!that.gotResult)) {
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
		// No action needed
		// Consider stopping when failure conditions met, then checking here for success
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
		wipeRecognizer();
		// Update progress
		DBHelper db = new DBHelper(getApplicationContext());
		db.setProgress("Default", "L", gameName);
    	System.gc();	// Let the system know it's a good time to clean up memory
		// Advance if completed
		if (db.getComplete("Default", "L")) {
			Intent intent = new Intent(getApplicationContext(), LessonCompleteActivity.class); //TODO: got to final screen
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        startActivity(intent);
		}
		else {
			Intent intent = new Intent(getApplicationContext(), LessonCompleteActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        startActivity(intent);
		}
	}
	
	/**
	 * May be useful when doing extended recognition, to avoid overflows in the recognizer
	 * and keep recognition accuracy.
	 */
	protected void wipeRecognizer() {
		this.rec = new RecognizerTask(getApplicationContext(), getMode());
		this.rec_thread = new Thread(this.rec);
		this.rec.setRecognitionListener(this);
		this.rec_thread.start();	
	}
	
}



