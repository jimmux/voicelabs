package edu.voicelabs.vst;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.voicelabs.vst.RecognizerTask.Mode;


public class PhonemeGameActivity extends AbstractGameActivity implements OnTouchListener {
	
	// Layout elements
	protected RelativeLayout gameLayout;		

	//menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	private ImageButton buttonStart;
	
	//sounds
	private MediaPlayer lllSound;
	private MediaPlayer successSound;
	
	// UI
	private ImageButton buttonRecord;
	private ImageButton buttonPlay;
	private TextView phonemeText;
	
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Basic inherited fields to set
		this.subPattern = "L";
		this.maxCorrectMatches = 3;
		this.maxAttempts = 6;
		this.mode = Mode.PHONEME;
		
		setContentView(R.layout.phoneme_game);
		
		//import fonts
		TextView txt_phoneme = (TextView) findViewById(R.id.txt_game1_l);
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf");  
		txt_phoneme.setTypeface(font);  
		
		//menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		//UI
		this.buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
		this.buttonRecord = (ImageButton) findViewById(R.id.buttonRecord);
		this.phonemeText = (TextView) findViewById(R.id.txt_game1_l);
		
		this.buttonPlay.setOnTouchListener(this);
		this.buttonRecord.setOnTouchListener(this);
		
		//click on leo to start the game
		this.buttonStart = (ImageButton) findViewById(R.id.buttonStart);	
		this.buttonStart.setOnTouchListener(this);
		
		//Sounds
		
		lllSound = MediaPlayer.create(this, R.raw.leo_lll);
		//TODO would be nice if these were randomized
		successSound = MediaPlayer.create(this, R.raw.leo_really_cool);
		
	}
	
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_phoneme);
	}
	
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		if (event.getAction() == MotionEvent.ACTION_UP) {
//			if (v == this.buttonStart) {
//				this.textViewMessage.setText("Say 'L' three times");
//				runGame();
//			}
//		}	
//		return false;
//	}

	protected void fullSuccess(AbstractGameActivity activityToUpdate) {
		PhonemeGameActivity that = (PhonemeGameActivity) activityToUpdate;  //Todo: now able to reference the layout directly?
		//that.textViewMessage.setText("Got all the matches!");
		Toast.makeText(getApplicationContext(), "Got all the matches!", Toast.LENGTH_SHORT).show();
		
		//TODO On success play feedback sound + animate leo + then go to progress screen
		successSound.start();
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		PhonemeGameActivity that = (PhonemeGameActivity) activityToUpdate;
		//that.textViewMessage.setText("Matched " + successCount + " times!");
		Toast.makeText(getApplicationContext(), "Matched " + successCount + " times!", Toast.LENGTH_SHORT).show();
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		PhonemeGameActivity that = (PhonemeGameActivity) activityToUpdate;
		//that.textViewMessage.setText("Press Start to try again.");
		Toast.makeText(getApplicationContext(), "Press Start to try again.", Toast.LENGTH_SHORT).show();
		//TODO negative response handler
	}
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_UP) {

			if (v == this.buttonSkip) {
				// Skip to the games

				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			}
			
			else if (v == this.buttonMenu) {
				// Skip to the Menu

				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			
			}
			
			else if (v == this.buttonStart) {
				
				//get busy playing
				AnimationHelper.runKeyframeAnimation(this, R.id.buttonPlay, R.anim.anim_play_btn);
				AnimationHelper.runKeyframeAnimation(this, R.id.buttonRecord, R.anim.anim_record_btn);
				
				//Show text
				phonemeText.setVisibility(View.VISIBLE);
				buttonPlay.setVisibility(View.VISIBLE);
				
				//Playback sound
				//when it's finished playing back - then run game
				lllSound.setOnCompletionListener(new OnCompletionListener() {
					
		            @Override
		            public void onCompletion(MediaPlayer mp) {
		            	
						//Hide text
						phonemeText.setVisibility(View.INVISIBLE);
						buttonPlay.setVisibility(View.INVISIBLE);
						
						//play animation manually 
						buttonStart.setBackgroundResource(R.anim.anim_leo_hand_to_ear);
						AnimationDrawable leoAnimation = (AnimationDrawable) buttonStart.getBackground();
						leoAnimation.start();
						
						buttonRecord.setVisibility(View.VISIBLE);
						
						// Start the voice recognition
		            	runGame();
		            }

		            });
				
				//play Phoneme recording
				lllSound.start();
				
			

				
				
				//
				
			
			}
		
		}
	return false;
	}
	
}

