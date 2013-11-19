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

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.VideoView;

/**
 * Run through the explanatory video, then have a brief interactive session allowing the
 * user to record and play back their own voice, so they can listen for the sounds they
 * are making.
 * 
 * Adapted from example at http://developer.android.com/guide/topics/media/audio-capture.html
 * 
 * Modified for different interaction pattern:
 * 	On record button, record up to the time limit.
 * 	On play button, stop any running recording (if any) and play back (if any).
 * 
 * @author James Manley
 * @author Dylan Kelly
 * 
 */
public class TutorialActivity extends Activity implements OnTouchListener {
	
	private ImageButton buttonRecord;
	private ImageButton buttonPlay;
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	private ImageButton buttonLeo;
	
	private ImageButton leoHelper;
	private ImageButton playHelper;
	private ImageButton recordHelper;
	
	// Audio record/playback
	private static int recordDuration = 3000;	// Maximum recording time
    private static String audioFileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    
    private AnimationDrawable recordAnim;
    private AnimationDrawable playAnim;

	private VideoView videoView;	
	
	private boolean recordPressed = false;
	private boolean playPressed = false;
	private boolean leoPressed = false;
	
	private boolean videoPlayed = false;	// Help to only control the video once

    // The activity will always be in one of these three states
    private static enum InteractionState {RECORD, PLAY, IDLE};
    private InteractionState state = InteractionState.IDLE;
    
    /** Set the state to transition to */
    private synchronized void setState(InteractionState newState) {
        Log.e(getClass().getName(), "*** Switching state from " + this.state + " to " + newState);
    	
    	// Cancel any current playing or recording (effectively set to IDLE conditions)
    	switch (this.state) {
    	case RECORD:
        	this.recordAnim.stop();
        	this.recordAnim.selectDrawable(0);
    		this.recorder.stop();
    		this.recorder.release();
    		this.recorder = null;
    		break;
    	case PLAY:
        	this.playAnim.stop();
        	this.playAnim.selectDrawable(0);
			this.player.stop();
			this.player.release();
			this.player = null;  
			break;
    	case IDLE:
    		this.recordAnim.stop();
    		this.playAnim.stop();
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
    		this.recordAnim.stop();
    		this.playAnim.stop();
    	}
    	this.state = newState;  		
    }


    /** Constructor, simply assigns the path for storage of temporary samples */
    public TutorialActivity() {
        audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.3gp";
    }
    
    /** Play the example sound for the user to imitate */
    public void playExample() {
    	buttonLeo.setBackgroundResource(R.drawable.leo_animation0015);
    	MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.phoneme_lll);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            	// Go back to just blinking
            	buttonLeo.setBackgroundResource(R.anim.anim_leo_blinkonly);
    			AnimationDrawable talkAnimation = (AnimationDrawable) buttonLeo.getBackground();
    			talkAnimation.start();
            }
		});
    	
    	buttonRecord.setVisibility(View.VISIBLE);
		buttonPlay.setVisibility(View.VISIBLE);
		recordAnim.stop();
		playAnim.stop();
		leoHelper.setVisibility(View.GONE);
    	
    	mediaPlayer.start();
    }
    
    
    private void introDemo() {
    	// Red dot animation
		// Play animation manually 
    	buttonLeo.setVisibility(View.VISIBLE);
    	leoHelper.setVisibility(View.VISIBLE);
		AnimationHelper.runKeyframeAnimation(this, R.id.leo_helper, R.anim.anim_btn_red_circle5);		
    }


    /** Stop any recording when leaving the activity */
    @Override
    public void onPause() {
        super.onPause();
        setState(InteractionState.IDLE);
    }
	

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.tutorial);
		
		this.videoView = (VideoView) findViewById(R.id.tutorialVideo);	
		this.videoView.setVisibility(View.VISIBLE);
				
		this.buttonRecord = (ImageButton) findViewById(R.id.buttonRecord);
		this.buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		this.buttonLeo = (ImageButton) findViewById(R.id.buttonLeo);
		
		this.leoHelper = (ImageButton) findViewById(R.id.leo_helper);
		this.playHelper = (ImageButton) findViewById(R.id.play_helper);
		this.recordHelper = (ImageButton) findViewById(R.id.record_helper);
		
		this.buttonRecord.setOnTouchListener(this);
		this.recordHelper.setOnTouchListener(this);
		this.buttonPlay.setOnTouchListener(this);
		this.playHelper.setOnTouchListener(this);
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		this.buttonLeo.setOnTouchListener(this);
		this.leoHelper.setOnTouchListener(this);
		this.videoView.setOnTouchListener(this);
		
		buttonRecord.setBackgroundResource(R.anim.anim_record_btn);
		recordAnim = (AnimationDrawable) buttonRecord.getBackground();
		
		buttonPlay.setBackgroundResource(R.anim.anim_play_btn);
		playAnim = (AnimationDrawable) buttonPlay.getBackground();	
		
		setState(InteractionState.IDLE);
	}
	
	/** Show video, advance to interactive part when complete */
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
            	videoView.setVisibility(View.GONE);
            	introDemo();
			}
		}
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if ((v == this.buttonLeo) ||  (v == this.leoHelper)){
				// Start by tapping on Leo
				if (leoPressed == false){
					recordHelper.setVisibility(View.VISIBLE);
					AnimationHelper.runKeyframeAnimation(this, R.id.record_helper, R.anim.anim_btn_red_circle5);
					leoPressed = true;
				}
				recordAnim.stop();
				playAnim.stop();
				playExample();
			}
			else if ((v == this.buttonRecord) || (v == this.recordHelper)) {
				// First run
				if (recordPressed == false){
					playHelper.setVisibility(View.VISIBLE);
					AnimationHelper.runKeyframeAnimation(this, R.id.play_helper, R.anim.anim_btn_red_circle5);
					recordPressed = true;
					recordHelper.setVisibility(View.GONE);
				}else {
					recordHelper.setVisibility(View.GONE);
				}
				
				// Record a sample
				setState(InteractionState.RECORD);
			}
			else if ((v == this.buttonPlay) || (v == this.playHelper)) {
				// Play back whatever we have recorded
				if (playPressed == false){
					playHelper.setVisibility(View.GONE);
					playPressed = true;
				}
				setState(InteractionState.PLAY);
			}
			else if (v == this.buttonSkip) {
				// Skip to the games
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
