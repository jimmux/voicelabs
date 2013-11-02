package edu.voicelabs.vst;

import edu.voicelabs.vst.RecognizerTask.Mode;
import android.os.Bundle;

public class WordGameActivity extends AbstractGameActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		subPattern = "LION";
		maxCorrectMatches = 1;
		maxAttempts = 3;
		mode = Mode.WORD;
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
	

}

