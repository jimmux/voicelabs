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

abstract class AbstractGameActivity extends Activity implements OnTouchListener, RecognitionListener {
	
	// Speech recognition bits
	static {System.loadLibrary("pocketsphinx_jni");}
	RecognizerTask rec;
	Thread rec_thread;
	float speech_dur;
	boolean listening;
	
	private int successCount = 0;		// Keep track of how many successes had so far.
	private boolean gotResult = false; 	// Flag for prevention of multiple completion states.
	
	// To be set by children - todo: replace with abstract getters
	protected String subPattern = "";		// Determines what we are looking for, e.g. "L", "L-AH", "LOLLY"
	protected int maxCorrectMatches = 1;	// Number of matches to consider it a successful attempt
	protected int maxAttempts = 6;			// When this number of attempts is detected, consider the exercise failed
	//protected Mode mode = Mode.WORD;		// Matching mode to use, PHONEME, SYLLABLE, or WORD
	abstract protected Mode getMode();
	
	abstract protected ViewGroup getGameLayout();	// Children to return the ViewGroup (usually the top level layout) containing all updateable UI elements
	abstract protected void fullSuccess(AbstractGameActivity activityToUpdate);
	abstract protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount);
	abstract protected void fullAttempts(AbstractGameActivity activityToUpdate);
	
	// Used by the state control mechanism, most of these need to be set from child layouts
    protected MediaPlayer player;
    protected int playingRef;		// Ref of the audio resource set to play
	protected ImageView prompt;
	protected TextView message;
	protected AnimationDrawable promptAnim;
    
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.practice_game);
		
		// Set up speech recognition
//		this.rec = new RecognizerTask(getApplicationContext(), this.mode);
		this.rec = new RecognizerTask(getApplicationContext(), this.getMode());
		this.rec_thread = new Thread(this.rec);
		this.listening = false;
		this.rec.setRecognitionListener(this);
		this.rec_thread.start();	
	}
	
	
//	public void onPause(Bundle savedInstanceState) {
//		this.rec.stop();
//		
//	}
	
	public void onResume(Bundle savedInstanceState) {
		//this.rec.start();		
	}
	
	public void onDestroy(Bundle savedInstanceState) {
		this.rec.shutdown();		
	}
	
	
	
	

	// The activity will always be in one of these three states
    protected static enum InteractionState {RECORD, PLAY, IDLE, PLAY_THEN_RECORD, PLAY_THEN_RERUN};
    private InteractionState state = InteractionState.IDLE;
    
    // Set the state to transition to
    protected synchronized void setState(final InteractionState newState) {
        Log.e(getClass().getName(), "*** Switching state from " + this.state + " to " + newState);
    	
    	// Cancel any current playing or recording (effectively set to IDLE conditions)
    	switch (this.state) {
    	case RECORD:
        	this.promptAnim.stop();
        	this.prompt.clearAnimation();
    		this.rec.stop();
    		break;
    	case PLAY:
    	case PLAY_THEN_RECORD:
    	case PLAY_THEN_RERUN:
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
    		// Nothing to do
    		this.prompt.setVisibility(View.INVISIBLE);
    	}
    	this.state = newState;  		
    }
	

	
	@Override
	public void onPause() {
	    super.onPause();
	    setState(InteractionState.IDLE);
	}
	
	
	
	
	
	protected void runGame() {		
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
				//Pattern successPattern = Pattern.compile("\\bL(-|\\b)");	//Todo: generalise for other matches
				Pattern successPattern = Pattern.compile("\\b" + that.subPattern + "(-|\\b)");	//Todo: generalise for other matches
				Pattern attemptPattern = Pattern.compile("\\b[A-Z]");
				int count;
				String speech = (hyp == null) ? "" : hyp;				
				
				Matcher successMatcher = successPattern.matcher(speech);
				count = 0;
				// Count how many successful matches we have
				while (successMatcher.find() && (count < that.maxCorrectMatches)) {
					count++;
				}
				// count can revert, so go with the max count found so far (or update)
				if (count > that.successCount) {
					that.successCount = count;		

					if (that.successCount < that.maxCorrectMatches) {
						that.rec.stop();	// Stop while acting on result, to avoid interference if speech is used
						Log.d(getClass().getName(), "*** Partial success with count: " + that.successCount);
						partSuccess(that, that.successCount);
						that.rec.start();
					}
					else if (!that.gotResult) {
						that.gotResult = true;
						that.rec.stop();
						Log.d(getClass().getName(), "*** Found enough results");
						fullSuccess(that);
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
						fullAttempts(that);
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

	public void onError(int err) {
		// No action needed
		Log.d(getClass().getName(), "*** game error");
	}
	
	/**
	 * May be called by a child class, if it is the last in the lesson.
	 * Shows a custom dialog completion screen, then returns to the phoneme select
	 * screen after a short period of time, or when touched.
	 */
	protected void runLessonCompletion() {		
		Intent intent = new Intent(getApplicationContext(), LessonCompleteActivity.class);
        startActivity(intent); 		
	}
	
	/**
	 * May be useful when doing extended recognition, to avoid overflows in the recognizer
	 */
	protected void wipeRecognizer() {
//		this.rec = new RecognizerTask(getApplicationContext(), mode);
		this.rec = new RecognizerTask(getApplicationContext(), getMode());
		this.rec_thread = new Thread(this.rec);
		this.rec.setRecognitionListener(this);
		this.rec_thread.start();	
	}
	
}



