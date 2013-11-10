package edu.voicelabs.vst;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Adapted from example at http://developer.android.com/guide/topics/media/audio-capture.html
 * 
 * Modified for different interaction pattern:
 * 	On record button, record up to the time limit.
 * 	On play button, stop any running recording (if any) and play back (if any).
 * 
 */
public class TutorialActivity extends Activity implements OnTouchListener {
	
	private ImageButton buttonRecord;
	private ImageButton buttonPlay;
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	
	// Audio record/playback
	private static int recordDuration = 3000;	// Maximum recording time
    private static String audioFileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    
    private AnimationDrawable recordAnim;
    private AnimationDrawable playAnim;
    private AnimationDrawable leoBlinkAnim;

    // The activity will always be in one of these three states
    private static enum InteractionState {RECORD, PLAY, IDLE};
    private InteractionState state = InteractionState.IDLE;
    
    // Set the state to transition to
    private void setState(InteractionState newState) {
    	// Cancel any current playing or recording (effectively set to IDLE conditions)    	
    	switch (this.state) {
    	case RECORD:
        	recordAnim.stop();
    		this.recorder.stop();
    		this.recorder.release();
    		this.recorder = null;
    	case PLAY:
        	playAnim.stop();
			this.player.stop();
			this.player.release();
			this.player = null;  
    	case IDLE:
    		// Nothing to do
    	}
    	
    	// Now set the expected conditions and update state
    	switch (newState) {
    	case RECORD:
        	this.recorder = new MediaRecorder();
        	this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        	this.recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        	this.recorder.setOutputFile(audioFileName);
        	this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        	this.recorder.setMaxDuration(recordDuration);
            try {
            	this.recorder.prepare();
            } catch (IOException e) {
                Log.e(getClass().getName(), "prepare() failed");
            }
            this.recorder.start();
    	case PLAY:
          this.player = new MediaPlayer();
          try {
          	this.player.setDataSource(audioFileName);
          	this.player.prepare();
          	this.player.start();
          } catch (IOException e) {
              Log.e(getClass().getName(), "prepare() failed");
          }
    	case IDLE:
    		// Nothing to do	
    	}
    	this.state = newState;  		
    }


    public TutorialActivity() {
    	//JAM todo: use private path?
    	//JAM todo: at least make sure old file is gone, or it can be played back
        audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.3gp";
//        audioFileName = getApplicationContext().getFilesDir().getAbsolutePath() + "/record.3gp";
    }

    @Override
    public void onPause() {
        super.onPause();
        setState(InteractionState.IDLE);
    }
	

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.tutorial);
		
		//import fonts
/*  		TextView txt_lemon = (TextView) findViewById(R.id.txt_game_3_lemon);
  		Typeface fontMabel = Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf");  
  		txt_lemon.setTypeface(fontMabel);  */
		
		//make leo blink - DK
		//leoBlinkAnim = AnimationHelper.runKeyframeAnimation(this, R.id.buttonLeo, R.anim.anim_leo_blinkonly);
		

		
		this.buttonRecord = (ImageButton) findViewById(R.id.buttonRecord);
		//this.buttonRecord = (ImageButton) findViewById(R.id.buttonRecord);

		this.buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		//this.buttonRecord.setOnTouchListener(this);
		this.buttonPlay.setOnTouchListener(this);
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		setState(InteractionState.IDLE);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.buttonRecord) {
				//clear image resource first to prevent dupes
				this.buttonRecord.setImageResource(0);
				//play record button animation
				recordAnim = AnimationHelper.runKeyframeAnimation(this, R.id.buttonRecord, R.anim.anim_record_btn);
				// Record a sample
				setState(InteractionState.RECORD);
			}
			else if (v == this.buttonPlay) {
				//clear image resource first to prevent dupes
				 this.buttonPlay.setImageResource(0);
				//play play button animation
				playAnim = AnimationHelper.runKeyframeAnimation(this, R.id.buttonPlay, R.anim.anim_play_btn);
				// Play back whatever we have recorded
				setState(InteractionState.PLAY);
			}
			else if (v == this.buttonSkip) {
				// Skip to the games
				setState(InteractionState.IDLE);
				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			}
			
			else if (v == this.buttonMenu) {
				// Skip to the Menu
				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			}
		}
		
		return false;
	}
}
