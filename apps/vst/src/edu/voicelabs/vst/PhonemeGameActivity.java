package edu.voicelabs.vst;

import edu.voicelabs.vst.RecognizerTask.Mode;
import android.os.Bundle;

public class PhonemeGameActivity extends AbstractGameActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		subPattern = "L";
		maxCorrectMatches = 3;
		maxAttempts = 10;
		mode = Mode.PHONEME;
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
	

}

