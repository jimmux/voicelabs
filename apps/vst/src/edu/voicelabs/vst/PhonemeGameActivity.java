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
import android.widget.Toast;
import edu.voicelabs.vst.RecognizerTask.Mode;


public class PhonemeGameActivity extends AbstractGameActivity implements OnTouchListener {
	
	// Layout elements
	protected RelativeLayout gameLayout;		

	//menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	private ImageButton buttonStart;
	
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
		
		//click on leo to start the game
		this.buttonStart = (ImageButton) findViewById(R.id.buttonStart);	
		this.buttonStart.setOnTouchListener(this);
		
		
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
				
				//
				
			
			}
		
		}
	return false;
	}
	
}

