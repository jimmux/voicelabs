package edu.voicelabs.vst;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.voicelabs.vst.RecognizerTask.Mode;

abstract class AbstractGameActivity extends Activity implements OnTouchListener, RecognitionListener {
	
	// Speech recognition bits
	static {System.loadLibrary("pocketsphinx_jni");}
	RecognizerTask rec;
	Thread rec_thread;
//	Date start_date;
	float speech_dur;
	boolean listening;
	
	private int successCount = 0;		// Keep track of how many successes had so far.
	private boolean gotResult = false; 	// Flag for prevention of multiple completion states.

	// Layout elements
	protected RelativeLayout gameLayout;		//
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
		
		this.gameLayout = (RelativeLayout) findViewById(R.id.game_layout);
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
		this.gotResult = false;
		
//		this.start_date = new Date();
		this.listening = true;
		this.rec.start();
	}
	
	
	/** Called when partial results are generated. */
	public void onPartialResults(Bundle b) {
		final AbstractGameActivity that = this;
		final String hyp = b.getString("hyp");
		//that.textViewMessage.post(new Runnable() {
		that.gameLayout.post(new Runnable() {
			public void run() {		
				Pattern successPattern = Pattern.compile("\\bL(-|\\b)");
				Pattern attemptPattern = Pattern.compile("\\b[A-Z]");
				int count;
				String speech = (hyp == null) ? "" : hyp;
				
				Matcher successMatcher = successPattern.matcher(speech);
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
					Log.d(getClass().getName(), "*** Partial success with count: " + successCount);
					partSuccess(that, successCount);
				}
				else if (!gotResult) {
					gotResult = true;
					that.rec.stop();
					Log.d(getClass().getName(), "*** Found enough results");
					fullSuccess(that);
					return;
				}
				
				// Check for the maximum number of attempts
				Matcher attemptMatcher = attemptPattern.matcher(speech);
				count = 0;
				while (attemptMatcher.find()) {
					count++;
					if ((count > maxAttempts) && (!gotResult)) {
						gotResult = true;
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
	 * 
	 * Adapted from instructions at http://www.helloandroid.com/tutorials/how-display-custom-dialog-your-android-application
	 */
	protected void runLessonCompletion() {		
		

		Intent intent = new Intent(getApplicationContext(), LessonCompleteActivity.class);
        startActivity(intent); 
		
//        LayoutInflater inflater = (LayoutInflater) AbstractGameActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(
//        	R.layout.lesson_complete, 
//        	(ViewGroup) findViewById(R.id.lesson_complete)
//        );
//        PopupWindow pw = new PopupWindow(layout, 300, 470, true);
//        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
//        
//        layout.setOnClickListener(completeScreenClickListener);	
		
		
		//set up dialog
//        Dialog dialog = new Dialog(AbstractGameActivity.this);
//        dialog.setContentView(R.layout.lesson_complete);
        //dialog.setTitle("This is my custom dialog box");
        //dialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

//		LayoutInflater inflater = (LayoutInflater) AbstractGameActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View layout = inflater.inflate(
//			R.layout.lesson_complete, 
//		 	(ViewGroup) findViewById(R.id.lesson_complete)
//		);
//		layout.setOnClickListener(completeScreenClickListener);	
		
		//now that the dialog is set up, it's time to show it    
//		dialog.show();
		
		
	}
//	private OnClickListener completeScreenClickListener = new OnClickListener() {
//	    public void onClick(View v) {	    	
//			Intent intent = new Intent(getApplicationContext(), PhonemeSelectActivity.class);
//            startActivity(intent); 
//	    }
//	};

}



