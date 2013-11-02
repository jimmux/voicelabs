package edu.voicelabs.vst;

import edu.voicelabs.vst.RecognizerTask.Mode;
import android.os.Bundle;

public class ChooseGameActivity extends AbstractGameActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		subPattern = "L";
		maxCorrectMatches = 1;
		maxAttempts = 3;
		mode = Mode.WORD;
	}
	

	protected void fullSuccess(AbstractGameActivity activityToUpdate) {
		ChooseGameActivity that = (ChooseGameActivity)activityToUpdate;
		that.textViewMessage.setText("Got all the matches!");
		runLessonCompletion();		// Last game, so return to select screen
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		ChooseGameActivity that = (ChooseGameActivity)activityToUpdate;
		that.textViewMessage.setText("Matched " + successCount + " times!");
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		ChooseGameActivity that = (ChooseGameActivity)activityToUpdate;
		that.textViewMessage.setText("Press Start to try again.");
	}
	

}

