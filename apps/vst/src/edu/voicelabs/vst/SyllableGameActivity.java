package edu.voicelabs.vst;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
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

public class SyllableGameActivity extends AbstractGameActivity implements OnTouchListener {
	
	// Layout elements
	protected RelativeLayout gameLayout;	

	//menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	private ImageButton buttonStart;
	
    private AnimationDrawable speakAnim;
	
	private TextView txtSyllable;
	
	// Loops through the set of syllables to speak
	private final String[] syllables = {"LA", "LI", "LU", "LE", "LO"};
	//TODO Add in sounds for each phoneme so the user knows what to say
	//private final String[] syllableSounds = {"R.Raw.leo_la","R.Raw.leo_li","R.Raw.leo_lu","R.Raw.leo_le","R.Raw.leo_lo"};
	private int syllableIndex = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		subPattern = "L";
		maxCorrectMatches = 2;
		maxAttempts = 4;
		mode = Mode.SYLLABLE;
		
		setContentView(R.layout.syllable_game);
		//text Label
		this.txtSyllable = (TextView) findViewById(R.id.txt_game1_syllable);
		
		//import fonts
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf");  
		txtSyllable.setTypeface(font);  
		
		//menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		//click on leo to start the game
		this.buttonStart = (ImageButton) findViewById(R.id.buttonStart);	
		this.buttonStart.setOnTouchListener(this);	
		
		// Animated prompt
		//this.imageViewSpeak = (ImageView) findViewById(R.id.imageViewSpeak);
		this.speakAnim = AnimationHelper.runKeyframeAnimation(this, R.id.buttonSpeak, R.anim.anim_btn_speak);
		this.speakAnim.stop();
		

	}
	
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_syllable);
	}

	protected void fullSuccess(AbstractGameActivity activityToUpdate) {
//		SyllableGameActivity that = (SyllableGameActivity) activityToUpdate;
		
		// Move to the next syllable, or complete the game
		this.syllableIndex++;
		this.speakAnim.stop();
		if (this.syllableIndex >= this.syllables.length) {
			Toast.makeText(getApplicationContext(), "Game complete!", Toast.LENGTH_SHORT).show();	
			this.txtSyllable.setText("Well Done!");
		}
		else {
			Toast.makeText(getApplicationContext(), "Say the next sound!", Toast.LENGTH_SHORT).show();
			MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tmp_lolly);
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	            @Override
	            public void onCompletion(MediaPlayer mp) {
	            	speakAnim.start();
	    			txtSyllable.setText(syllables[syllableIndex]);
	    			wipeRecognizer();////
	    			runGame();
	            }
			});
			mediaPlayer.start();
		}		
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		//SyllableGameActivity that = (SyllableGameActivity) activityToUpdate;
//		that.textViewMessage.setText("Matched " + successCount + " times!");
		
		// Encourage the same syllable		
		Toast.makeText(getApplicationContext(), "Matched " + successCount + " times!", Toast.LENGTH_SHORT).show();
		this.speakAnim.stop();
		MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tmp_lettuce);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            	speakAnim.start();
            }
		});
		mediaPlayer.start();
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		//SyllableGameActivity that = (SyllableGameActivity) activityToUpdate;
//		that.textViewMessage.setText("Press Start to try again.");
		this.speakAnim.stop();
		Toast.makeText(getApplicationContext(), "Try again.", Toast.LENGTH_SHORT).show();
		txtSyllable.setText("Keep trying!");
		MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tmp_lizard);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            	speakAnim.start();
        		txtSyllable.setText(syllables[syllableIndex]);
        		runGame();
            }
		});
		mediaPlayer.start();
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
				// Start the game
				runGame();
				
				//Show text
				
				//Playback sound
				MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tmp_l);
				mediaPlayer.start();
				
				//If correct move to next sound
				
				//Change Text to next syllable - cycle through array?
				this.syllableIndex = 0;
				this.txtSyllable.setText(this.syllables[0]);
			
			}
		
		}
	return false;
	}
}

