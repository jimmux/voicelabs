package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;



public class StartActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonStart;
	private Button buttonGoToDebug;
	private AnimationDrawable leoBlinkAnim;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		
		//import fonts
		TextView txt_phoneme = (TextView) findViewById(R.id.txt_phoneme);
		TextView txt_friends = (TextView) findViewById(R.id.txt_friends);
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf");  
		txt_phoneme.setTypeface(font);  
		txt_friends.setTypeface(font); 
		
		this.imageButtonStart = (ImageButton) findViewById(R.id.imageButtonStart);
		this.buttonGoToDebug = (Button) findViewById(R.id.buttonGoToDebug);
		
		this.imageButtonStart.setOnTouchListener(this);
		this.buttonGoToDebug.setOnTouchListener(this);
		
		// Start loading assets
		Utilities utils = new Utilities(getApplicationContext());
		utils.SetupSpeechData();
		
		// Initialise the database
		DBHelper db = new DBHelper(getApplicationContext());
		db.InitialiseWithDefaults();
	}
	
	// Show load screen, advance to start screen when ready.
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus) {
			//start fade in animations
			AnimationHelper.runAlphaAnimation(this, R.id.leo_fade_in, R.anim.anim_fade_in);
			AnimationHelper.runAlphaAnimation(this, R.id.txt_phoneme, R.anim.anim_txt_fade);
			AnimationHelper.runAlphaAnimation(this, R.id.txt_friends, R.anim.anim_txt_fade1);
			AnimationHelper.runAlphaAnimation(this, R.id.obj_sun, R.anim.anim_sun);
			//clear image resource first to prevent dupes
			ImageView leofadein = (ImageView) findViewById(R.id.leo_fade_in); 
			
			leofadein.setBackgroundResource(0);
			
			this.leoBlinkAnim = AnimationHelper.runKeyframeAnimation(this, R.id.leo_fade_in, R.anim.anim_leo_blinkonly);
			//imageButtonStart.setVisibility(View.INVISIBLE);
			//SystemClock.sleep(10000);
			imageButtonStart.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.imageButtonStart) {
				// Go to the phoneme select screen
				Intent intent = new Intent(getApplicationContext(), PhonemeSelectActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.buttonGoToDebug) {
				// Go to the debug screen
				Intent intent = new Intent(getApplicationContext(), DebugScreenActivity.class);
	            startActivity(intent); 
			}
		}
		
		return false;
	}
}
