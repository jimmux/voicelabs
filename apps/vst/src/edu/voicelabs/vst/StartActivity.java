package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class StartActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonStart;
	private AnimationDrawable leoBlinkAnim;
	private MediaPlayer music; 
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		
		// Import fonts
		TextView txt_phoneme = (TextView) findViewById(R.id.txt_phoneme);
		TextView txt_friends = (TextView) findViewById(R.id.txt_friends);
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf");  
		txt_phoneme.setTypeface(font);  
		txt_friends.setTypeface(font); 
		
		this.imageButtonStart = (ImageButton) findViewById(R.id.imageButtonStart);
		this.imageButtonStart.setOnTouchListener(this);
		
		// Start loading assets
		Utilities utils = new Utilities(getApplicationContext());
		utils.setupSpeechData();
		
		// Initialise the database, creating profile data if not yet available
		DBHelper db = new DBHelper(getApplicationContext());
		db.initialiseWithDefaults(false);
	}
	
	/**
	 *  Show load screen, advance to start screen when ready.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus) {
			
			// Play intro music
			music = MediaPlayer.create(getApplicationContext(), R.raw.splash_music);
			// When it's finished playing back - then run game
			music.setOnCompletionListener(new OnCompletionListener() {
	            @Override
	            public void onCompletion(MediaPlayer mp) {						
	            	music.start();	
	            }
			});			
			// Play Phoneme recording
			music.start();	
			
			// Start fade in animations
			AnimationHelper.runAlphaAnimation(this, R.id.leo_fade_in, R.anim.anim_fade_in);
			AnimationHelper.runAlphaAnimation(this, R.id.txt_phoneme, R.anim.anim_txt_fade);
			AnimationHelper.runAlphaAnimation(this, R.id.txt_friends, R.anim.anim_txt_fade1);
			AnimationHelper.runAlphaAnimation(this, R.id.obj_sun, R.anim.anim_sun);
			AnimationHelper.runAlphaAnimation(this, R.id.imageButtonStart, R.anim.anim_start_btn);
			ImageView leofadein = (ImageView) findViewById(R.id.leo_fade_in); 
			
			leofadein.setBackgroundResource(0);
			
			
			this.leoBlinkAnim = AnimationHelper.runKeyframeAnimation(this, R.id.leo_fade_in, R.anim.anim_leo_blinkonly);
			imageButtonStart.setVisibility(View.VISIBLE);
	
		}
	}
	
	/** Handle all touch input */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.imageButtonStart) {
				// Go to the phoneme select screen
				Intent intent = new Intent(getApplicationContext(), PhonemeSelectActivity.class);
	            startActivity(intent); 
	            music.stop();
	            music.release();
	            music = null;
			}
		}
		
		return false;
	}
}
