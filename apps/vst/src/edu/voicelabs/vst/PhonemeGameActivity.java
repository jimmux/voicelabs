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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.voicelabs.vst.RecognizerTask.Mode;


public class PhonemeGameActivity extends AbstractGameActivity implements OnTouchListener {
	
	// Layout elements
	protected RelativeLayout gameLayout;		

	// Menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	private ImageButton buttonStart;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Basic inherited fields to set
		this.subPattern = "L";
		this.maxCorrectMatches = 3;
		this.maxAttempts = 6;
		
		setContentView(R.layout.phoneme_game);		
		
		// Menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		//UI
		this.message = (TextView) findViewById(R.id.txt_game1_l);
		this.message.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf"));
		
		this.prompt = (ImageView) findViewById(R.id.imageViewPrompt);
		
		// Click on leo to start the game
		this.buttonStart = (ImageButton) findViewById(R.id.buttonStartWord);	
		this.buttonStart.setOnTouchListener(this);		

		setState(InteractionState.IDLE);
	}

	protected Mode getMode() {
		return Mode.PHONEME;
	}
	
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_phoneme);
	}

	protected void fullSuccess(AbstractGameActivity activityToUpdate) {		
		this.playingRef = R.raw.leo_really_cool_16bit;
		this.message.setText("You got it!");
		setState(InteractionState.PLAY);
		wipeRecognizer();
		runGameCompletion("Phoneme");
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {  //TODO pass of activity no longer needed?		
		this.playingRef = R.raw.leo_great_job_16bit;
		this.message.setText("Great! Say it again.");
		setState(InteractionState.PLAY_THEN_RECORD);
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		this.playingRef = R.raw.leo_negative2;		//TODO Need a "try again" type of response - repeat the phoneme? sad sound?
		this.message.setText("Keep trying...");
		setState(InteractionState.PLAY_THEN_RECORD);
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
				MediaPlayer lllSound = MediaPlayer.create(getApplicationContext(), R.raw.leo_lll);
				// When it's finished playing back - then run game
				lllSound.setOnCompletionListener(new OnCompletionListener() {
		            @Override
		            public void onCompletion(MediaPlayer mp) {						
						// Play animation manually 
						buttonStart.setBackgroundResource(R.anim.anim_leo_hand_to_ear);
						AnimationDrawable leoAnimation = (AnimationDrawable) buttonStart.getBackground();
						leoAnimation.start();
						
						// Start the voice recognition
		            	runGame();
		            	setState(InteractionState.RECORD);
		            }
				});			
				// Play Phoneme recording
				lllSound.start();	
			}
		
		}
	return false;
	}
	
}

