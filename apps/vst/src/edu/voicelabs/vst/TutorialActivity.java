package edu.voicelabs.vst;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

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
	private ImageButton buttonLeo;
	
	private ImageButton redCircle;
	
	// Audio record/playback
	private static int recordDuration = 3000;	// Maximum recording time
    private static String audioFileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    
    private AnimationDrawable recordAnim;
    private AnimationDrawable playAnim;
    private AnimationDrawable leoAnim;

	private VideoView videoView;	
	
	private boolean videoPlayed = false;	// Help to only control the video once

    // The activity will always be in one of these three states
    private static enum InteractionState {RECORD, PLAY, IDLE};
    private InteractionState state = InteractionState.IDLE;
    
    // Set the state to transition to
    private void setState(InteractionState newState) {
        Log.e(getClass().getName(), "*** Switching state from " + this.state + " to " + newState);
    	
    	// Cancel any current playing or recording (effectively set to IDLE conditions)
    	switch (this.state) {
    	case RECORD:
        	this.recordAnim.stop();
    		this.recorder.stop();
    		this.recorder.release();
    		this.recorder = null;
    		break;
    	case PLAY:
        	this.playAnim.stop();
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
        	this.recorder = new MediaRecorder();
        	this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        	this.recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        	this.recorder.setOutputFile(audioFileName);
        	this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        	this.recorder.setMaxDuration(recordDuration);
        	// Stop things when the maximum duration is reached
        	this.recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
			    @Override
			    public void onInfo(MediaRecorder mr, int what, int extra) {                     
			        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
			            setState(InteractionState.IDLE);
			        }          
			    }
			});
            try {
            	this.recorder.prepare();
            } catch (IOException e) {
                Log.e(getClass().getName(), "recording prepare() failed");
            }
            this.recorder.start();
            this.recordAnim.start();
            break;
    	case PLAY:
    		if (!(new File(audioFileName)).exists()) {
    			break;
    		}    		
		    this.player = new MediaPlayer();
        	// Stop things when the sample finishes playing
		    this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	            @Override
	            public void onCompletion(MediaPlayer mp) {
	            	setState(InteractionState.IDLE);
	            }
			});
		    try {
		    	this.player.setDataSource(audioFileName);
		    	this.player.prepare();
		    } catch (IOException e) {
		    	Log.e(getClass().getName(), "playing prepare() failed");
		    }
	    	this.player.start();
	    	this.playAnim.start();
		    break;
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
    
    public void playExample() {
    	buttonLeo.setBackgroundResource(R.drawable.leo_animation0015);
    	MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.leo_lll);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            	//go back to just blinking
            	buttonLeo.setBackgroundResource(R.anim.anim_leo_blinkonly);
    			AnimationDrawable talkAnimation = (AnimationDrawable) buttonLeo.getBackground();
    			talkAnimation.start();
        
            }
		});
    	//first run
    	final boolean uiVisible = buttonRecord.isShown();
    	
    	if(uiVisible == false){
    		redCircle.setVisibility(View.GONE);
        	buttonRecord.setVisibility(View.VISIBLE);
    		buttonPlay.setVisibility(View.VISIBLE);
    		AnimationHelper.runAlphaAnimation(this, R.id.buttonRecord, R.anim.anim_fade_in);
    		AnimationHelper.runAlphaAnimation(this, R.id.buttonPlay, R.anim.anim_fade_in);
    	}
    	mediaPlayer.start();
    	
    }
    
    
    public void introDemo() {
    	//play leo talking sound
//		//final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.leo_now_your_turn);
//		
//		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//            	//go back to just blinking
//            	mediaPlayer.release();
////            	buttonLeo.setBackgroundResource(R.anim.anim_leo_blinkonly);
////    			AnimationDrawable talkAnimation = (AnimationDrawable) buttonLeo.getBackground();
////    			talkAnimation.start();
//            	redCircle.setVisibility(View.INVISIBLE);
//        
//            }
//		});
//		
//		mediaPlayer.start();
    	//leo talking animation
		//play animation manually 
//		buttonLeo.setBackgroundResource(R.anim.anim_leo_talk);
//		AnimationDrawable leoAnim = (AnimationDrawable) buttonLeo.getBackground();
//		leoAnim.start();
	
    	
    	//Red dot animation
		//play animation manually 
    	buttonLeo.setVisibility(View.VISIBLE);
		redCircle.setVisibility(View.VISIBLE);
		AnimationHelper.runKeyframeAnimation(this, R.id.redCircle, R.anim.anim_btn_red_circle5);
		
		
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

		this.videoView = (VideoView) findViewById(R.id.tutorialVideo);	
		this.videoView.setVisibility(View.VISIBLE);
				
		this.buttonRecord = (ImageButton) findViewById(R.id.buttonRecord);
		this.buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		this.buttonLeo = (ImageButton) findViewById(R.id.buttonLeo);
		
		this.redCircle = (ImageButton) findViewById(R.id.redCircle);
		
		this.buttonRecord.setOnTouchListener(this);
		this.buttonPlay.setOnTouchListener(this);
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		this.buttonLeo.setOnTouchListener(this);
		this.redCircle.setOnTouchListener(this);
		this.videoView.setOnTouchListener(this);
		

		this.recordAnim = AnimationHelper.runKeyframeAnimation(this, R.id.buttonRecord, R.anim.anim_record_btn);
		this.recordAnim.stop();
		this.playAnim = AnimationHelper.runKeyframeAnimation(this, R.id.buttonPlay, R.anim.anim_play_btn);
		this.playAnim.stop();
		
		setState(InteractionState.IDLE);
	}
	
	// Show video, advance to interactive part when complete
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (videoPlayed) {
			return;
			
		}
		
		if (hasFocus) {						
	        StringBuilder uriPathBuilder = new StringBuilder();
	        uriPathBuilder.append("android.resource://");
	        uriPathBuilder.append(this.getPackageName ());
	        uriPathBuilder.append(File.separator);
	        uriPathBuilder.append("raw");
	        uriPathBuilder.append(File.separator);
	        uriPathBuilder.append("tutorial_intro");
	        Uri uri = Uri.parse (uriPathBuilder.toString());
	        videoView.setVideoURI(uri);
	        
	        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	            @Override
	            public void onCompletion(MediaPlayer mp) {
		            videoPlayed = true;
	            	videoView.setVisibility(View.GONE);
	            	
	            	introDemo();
	            	
	            	
	            }
			});
	        
	        videoView.start();
	        

		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		// ACTION_UP doesn't trigger on VideoView for some reason
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (v == this.videoView) {
				videoView.stopPlayback();
            	videoView.setVisibility(View.GONE);		//Todo: prompt to confirm skip
			}
		}
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if ((v == this.buttonLeo) ||  (v == this.redCircle)){
				// start by tapping on leo

				playExample();

			}
			else if (v == this.buttonRecord) {
				// Record a sample
				setState(InteractionState.RECORD);
			}
			else if (v == this.buttonPlay) {
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
