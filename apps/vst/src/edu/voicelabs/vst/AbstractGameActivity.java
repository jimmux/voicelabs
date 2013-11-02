package edu.voicelabs.vst;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import edu.voicelabs.vst.RecognizerTask.Mode;

abstract class AbstractGameActivity extends Activity implements OnTouchListener, RecognitionListener {
	
	// Speech recognition bits
	static {System.loadLibrary("pocketsphinx_jni");}
	RecognizerTask rec;
	Thread rec_thread;
	Date start_date;
	float speech_dur;
	boolean listening;
	
	private int successCount;	// Keep track of how many successes had so far.

	// Layout elements
	protected TextView textViewMessage;
	protected Button buttonStart;
	
	// To be set by children
	protected String subPattern = "";		// Determines what we are looking for, e.g. "L", "L-AH", "LOLLY"
	protected int maxCorrectMatches = 1;	// Number of matches to consider it a successful attempt
	protected int maxAttempts = 6;			// When this number of attempts is detected, consider the exercise failed
	protected Mode mode = Mode.WORD;		// Matching mode to use, PHONEME, SYLLABLE, or WORD
	
	abstract protected void fullSuccess(AbstractGameActivity activityToUpdate);
	abstract protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount);
	abstract protected void fullAttempts(AbstractGameActivity activityToUpdate);
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.practice_game);
		
		this.textViewMessage = (TextView) findViewById(R.id.textViewMessage);	
		this.buttonStart = (Button) findViewById(R.id.buttonStart);	
		this.textViewMessage.setOnTouchListener(this);	
		this.buttonStart.setOnTouchListener(this);	
		
		// Set up speech recognition
		this.rec = new RecognizerTask(getApplicationContext(), mode);
		this.rec_thread = new Thread(this.rec);
		this.listening = false;
		this.rec.setRecognitionListener(this);
		this.rec_thread.start();	
	}
	
	public void onPause(Bundle savedInstanceState) {
		this.rec.stop();
	}
	
	public void onResume(Bundle savedInstanceState) {
		//this.rec.start();		
	}
	
	public void onDestroy(Bundle savedInstanceState) {
		this.rec.shutdown();		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.buttonStart) {
				runGame();
			}
		}	
		return false;
	}
	
	private void runGame() {
		this.textViewMessage.setText("Say 'L' three times");
		
		this.successCount = 0;
		
		this.start_date = new Date();
		this.listening = true;
		this.rec.start();
	}
	
	
	/** Called when partial results are generated. */
	public void onPartialResults(Bundle b) {
		final AbstractGameActivity that = this;
		final String hyp = b.getString("hyp");
		that.textViewMessage.post(new Runnable() {
			public void run() {		
				Pattern successPattern = Pattern.compile("\\bL(-|\\b)");
				Pattern attemptPattern = Pattern.compile("\\b[A-Z]");
				int count;
				
				// Check for the maximum number of attempts
				Matcher attemptMatcher = attemptPattern.matcher(hyp);
				count = 0;
				while (attemptMatcher.find()) {
					count++;
					if (count > maxAttempts) {
						fullAttempts(that);
						that.rec.stop();
						break;
					}
				}
				
				Matcher successMatcher = successPattern.matcher(hyp);
				count = 0;
				// Count how many successful matches we have
				while (successMatcher.find() && (count < maxCorrectMatches)) {
					count++;
				}
				// count can revert, so go with the max count found so far (or update)
				if (count > that.successCount) {
					successCount = count;
				}
				if (successCount < maxCorrectMatches) {
					partSuccess(that, successCount);
				}
				else {
					fullSuccess(that);
					that.rec.stop();
				}
							
			}
		});
	}

	/** Called when full results are generated. */
	public void onResults(Bundle b) {
		// No action needed
	}

	public void onError(int err) {
		// No action needed
	}

}



