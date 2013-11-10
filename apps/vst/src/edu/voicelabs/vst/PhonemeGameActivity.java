package edu.voicelabs.vst;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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


	//private ImageView imageViewSpeak;
    private AnimationDrawable speakAnim;

	
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
		this.buttonRecord = (ImageButton) findViewById(R.id.buttonSpeak);
		this.phonemeText = (TextView) findViewById(R.id.txt_game1_l);
		
		this.buttonPlay.setOnTouchListener(this);
		this.buttonRecord.setOnTouchListener(this);
		
		//click on leo to start the game
		this.buttonStart = (ImageButton) findViewById(R.id.buttonStart);	
		this.buttonStart.setOnTouchListener(this);
		

		// Animated prompt
		//this.imageViewSpeak = (ImageView) findViewById(R.id.imageViewSpeak);
		this.speakAnim = AnimationHelper.runKeyframeAnimation(this, R.id.buttonSpeak, R.anim.anim_btn_speak);
		this.speakAnim.stop();

		//Sounds
		
		lllSound = MediaPlayer.create(this, R.raw.leo_lll);
		


		
	}
	
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_phoneme);
	}

	protected void fullSuccess(AbstractGameActivity activityToUpdate) {
		//PhonemeGameActivity that = (PhonemeGameActivity) activityToUpdate;  //Todo: now able to reference the layout directly?
		//that.textViewMessage.setText("Got all the matches!");
		Toast.makeText(getApplicationContext(), "Got all the matches!", Toast.LENGTH_SHORT).show();
		MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.leo_really_cool);
		mediaPlayer.start();
		this.speakAnim.stop();
		buttonRecord.setVisibility(View.INVISIBLE);
		


	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		//PhonemeGameActivity that = (PhonemeGameActivity) activityToUpdate;
		//that.textViewMessage.setText("Matched " + successCount + " times!");
		Toast.makeText(getApplicationContext(), "Matched " + successCount + " times!", Toast.LENGTH_SHORT).show();
		this.speakAnim.stop();
		buttonRecord.setVisibility(View.INVISIBLE);
		MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.leo_great_job);
		mediaPlayer.start();
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            	// Todo: consider using state based version of games, to keep in a "reacting" state 
            	// and stop processing speech while playing feedback
            	speakAnim.start();
            }
		});
				
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		//PhonemeGameActivity that = (PhonemeGameActivity) activityToUpdate;
		//that.textViewMessage.setText("Press Start to try again.");
		this.speakAnim.stop();
		buttonRecord.setVisibility(View.INVISIBLE);
		Toast.makeText(getApplicationContext(), "Press Start to try again.", Toast.LENGTH_SHORT).show();

		MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.leo_well_done);
		mediaPlayer.start();

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
				AnimationHelper.runKeyframeAnimation(this, R.id.buttonSpeak, R.anim.anim_record_btn);

				
				//Show text
				phonemeText.setVisibility(View.VISIBLE);
				buttonPlay.setVisibility(View.VISIBLE);
				
				//Playback sound

				MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tmp_l);
				mediaPlayer.start();

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
				
			

				

				
				
//				// Start the game
//				runGame();
//				this.speakAnim.start();
				
			
			}
		
		}
	return false;
	}
	
}

