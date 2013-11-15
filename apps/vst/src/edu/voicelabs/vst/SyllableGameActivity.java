package edu.voicelabs.vst;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.voicelabs.vst.RecognizerTask.Mode;

public class SyllableGameActivity extends AbstractGameActivity implements OnTouchListener {
	
	// Layout elements
	protected RelativeLayout gameLayout;	

	// Menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	
	private ImageButton buttonStart;
	
	// Loops through the set of syllables to speak
	private final String[] syllables = {"LA", "LI", "LU", "LE", "LO"};

	private final int[] syllableSounds = {R.raw.leo_la, R.raw.leo_li, R.raw.leo_lu, R.raw.leo_le, R.raw.leo_lo};

	private int syllableIndex = 0;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		subPattern = "L";
		maxCorrectMatches = 2;
		maxAttempts = 4;
		
		setContentView(R.layout.syllable_game);
		
		// Menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		//UI
		this.message = (TextView) findViewById(R.id.txt_label_syllable);
		this.message.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf"));
		
		this.prompt = (ImageView) findViewById(R.id.imageViewPrompt);
		
		// Click on leo to start the game
		this.buttonStart = (ImageButton) findViewById(R.id.buttonStartWord);	
		this.buttonStart.setOnTouchListener(this);		

		setState(InteractionState.IDLE);
	}
	

	protected Mode getMode() {
		return Mode.SYLLABLE;
	}
	
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_syllable);
	}

	protected void fullSuccess(AbstractGameActivity activityToUpdate) {
		// Move to the next syllable, or complete the game
		this.syllableIndex++;
		if (this.syllableIndex >= this.syllables.length) {
			this.playingRef = R.raw.leo_really_cool_16bit;
			this.message.setText("Finished!");
			setState(InteractionState.PLAY);
			//TODO Wait 3 secs - then go to LessonProgress 
			//TODO Update player progress to reflect that they have completed this stage
			
			 Handler handler = new Handler(); 
			    handler.postDelayed(new Runnable() { 
			         public void run() { 
			        	 runLessonCompletion();  
			         } 
			    }, 2000); 

			
			
		}
		else {			
			this.playingRef = syllableSounds[syllableIndex];
			this.message.setText("Now say " + syllables[syllableIndex] + "!");
			setState(InteractionState.PLAY_THEN_RERUN);
		}				
		wipeRecognizer();
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		// Encourage the same syllable		
		this.playingRef = R.raw.leo_great_job_16bit;
		this.message.setText("Good, do it again!");
		setState(InteractionState.PLAY_THEN_RECORD);
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		this.playingRef = syllableSounds[syllableIndex];
		this.message.setText("Try it again!");
		setState(InteractionState.PLAY_THEN_RERUN);
		wipeRecognizer();
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
				//Change text to first syllable
				this.syllableIndex = 0;
				this.message.setText(this.syllables[syllableIndex]);
				this.playingRef = syllableSounds[syllableIndex];
				setState(InteractionState.PLAY_THEN_RECORD);
				
				// Play animation manually 
				buttonStart.setBackgroundResource(R.anim.anim_leo_hand_to_ear);
				AnimationDrawable leoAnimation = (AnimationDrawable) buttonStart.getBackground();
				leoAnimation.start();
			}
		
		}
	return false;
	}
}

