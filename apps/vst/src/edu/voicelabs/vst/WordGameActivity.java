package edu.voicelabs.vst;

import edu.voicelabs.vst.RecognizerTask.Mode;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class WordGameActivity extends AbstractGameActivity {
	
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	private ImageButton buttonStart;
	
	private ImageButton buttonPlay;
	private ImageButton wordObject;
	private TextView txtLabel;
	
	//Objects
	private ImageButton objLemon;
	private ImageButton objLettuce;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		subPattern = "LION";
		maxCorrectMatches = 1;
		maxAttempts = 3;
		mode = Mode.WORD;
		
		setContentView(R.layout.word_game);
		
		TextView txt_phoneme = (TextView) findViewById(R.id.txt_game_3);
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf");  
		txt_phoneme.setTypeface(font);  
	
		//menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		//click on the object to start the game
		this.buttonStart = (ImageButton) findViewById(R.id.btn_game3_obj);	
		this.buttonStart.setOnTouchListener(this);
		
		//playback button
		this.buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
		
		//text Label
		this.txtLabel = (TextView) findViewById(R.id.txt_game_3);
		
		//word object
		this.wordObject = (ImageButton) findViewById(R.id.btn_game3_obj); 
	
	}
	

	protected void fullSuccess(AbstractGameActivity activityToUpdate) {
		WordGameActivity that = (WordGameActivity)activityToUpdate;
		that.textViewMessage.setText("Got all the matches!");
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		WordGameActivity that = (WordGameActivity)activityToUpdate;
		that.textViewMessage.setText("Matched " + successCount + " times!");
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		WordGameActivity that = (WordGameActivity)activityToUpdate;
		that.textViewMessage.setText("Press Start to try again.");
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
				
//Play Object sound
				
//Playing animation
//Animate Playback icon while sound is playing
//first unhide playback icon
				
				
				final boolean playbackVisible = buttonPlay.isShown();
				
				if (playbackVisible == false){
					// Play sound
					buttonPlay.setVisibility(View.VISIBLE);
					AnimationHelper.runKeyframeAnimation(this, R.id.buttonPlay, R.anim.anim_play_btn);
					System.out.println("turned on");
					
				}else {
					// Stop sound
					buttonPlay.setVisibility(View.INVISIBLE);
					System.out.println("turned off");
				}
//Start voice recognition (WORD)
				
//speaking animation
				
//if voice recognition success then 				
//move to next object
				
//swap item and text for next item
				this.wordObject.setBackgroundResource(R.drawable.img_obj_lettuce);
				this.txtLabel.setText("Lettuce");
//else if voice recognition unsuccessful then go back to start of object 
				
				
				
			
			}
		
		}
	return false;
	}
	
}
	
	


