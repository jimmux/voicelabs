package edu.voicelabs.vst;

import edu.voicelabs.vst.RecognizerTask.Mode;
import android.os.Bundle;

public class SyllableGameActivity extends AbstractGameActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		subPattern = "L";
		maxCorrectMatches = 3;
		maxAttempts = 6;
		mode = Mode.SYLLABLE;
		
		setContentView(R.layout.syllable_game);
	}
	

	protected void fullSuccess(AbstractGameActivity activityToUpdate) {
		SyllableGameActivity that = (SyllableGameActivity)activityToUpdate;
		that.textViewMessage.setText("Got all the matches!");
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		SyllableGameActivity that = (SyllableGameActivity)activityToUpdate;
		that.textViewMessage.setText("Matched " + successCount + " times!");
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		SyllableGameActivity that = (SyllableGameActivity)activityToUpdate;
		that.textViewMessage.setText("Press Start to try again.");
	}
	

}

