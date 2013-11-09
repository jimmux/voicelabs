package edu.voicelabs.vst;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.voicelabs.vst.RecognizerTask.Mode;

public class SyllableGameActivity extends AbstractGameActivity implements OnTouchListener {
	
	// Layout elements
	protected RelativeLayout gameLayout;	

	//menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	private ImageButton buttonStart;
	
	private TextView txtSyllable;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		subPattern = "L";
		maxCorrectMatches = 3;
		maxAttempts = 6;
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
		

	}
	
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout);
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
		SyllableGameActivity that = (SyllableGameActivity) activityToUpdate;
//		that.textViewMessage.setText("Got all the matches!");
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		SyllableGameActivity that = (SyllableGameActivity) activityToUpdate;
//		that.textViewMessage.setText("Matched " + successCount + " times!");
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		SyllableGameActivity that = (SyllableGameActivity) activityToUpdate;
//		that.textViewMessage.setText("Press Start to try again.");
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
				
				//If correct move to next sound
				
				//Change Text to next syllable - cycle through array?
				this.txtSyllable.setText("Le");
			
			}
		
		}
	return false;
	}
}

