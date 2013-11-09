package edu.voicelabs.vst;

import edu.voicelabs.vst.RecognizerTask.Mode;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class PhonemeGameActivity extends AbstractGameActivity {
	
	//menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		subPattern = "L";
		maxCorrectMatches = 3;
		maxAttempts = 6;
		mode = Mode.PHONEME;
		
		setContentView(R.layout.phoneme_game);
		
		//menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		//import fonts
		TextView txt_phoneme = (TextView) findViewById(R.id.txt_game1_l);
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf");  
		txt_phoneme.setTypeface(font);  
		
	}
	

	protected void fullSuccess(AbstractGameActivity activityToUpdate) {
		PhonemeGameActivity that = (PhonemeGameActivity)activityToUpdate;
		that.textViewMessage.setText("Got all the matches!");
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		PhonemeGameActivity that = (PhonemeGameActivity)activityToUpdate;
		that.textViewMessage.setText("Matched " + successCount + " times!");
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		PhonemeGameActivity that = (PhonemeGameActivity)activityToUpdate;
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
		
		}
	return false;
	}
	
}

