package edu.voicelabs.vst;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import edu.voicelabs.vst.RecognizerTask.Mode;

public class ChooseGameActivity extends AbstractGameActivity implements OnTouchListener {
	// Layout elements
	protected RelativeLayout gameLayout;	

	// Menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	
	// Items
	private ImageButton buttonItem1;
	private ImageButton buttonItem2;
	private ImageButton buttonItem3;
	private ImageButton buttonItem4;    

	/**
	 * Simple struct for associated word data
	 *
	 */
	private class WordData {
		String displayWord;		// Text to use for display
		String matchWord;		// Text to use for speech matching
		int drawable;			// The reference of the image in res/drawable to use
		int speechAudio;		// The reference of the sound file in res/raw to use, e.g. R.raw.tmp_lolly
		
		public WordData(String displayWord, String matchWord, int drawable, int speechAudio) {
			this.displayWord = displayWord;
			this.matchWord = matchWord;
			this.drawable = drawable;
			this.speechAudio = speechAudio;
		}
	}
	private WordData[] words = {
		new WordData("Lemon", "LEMON", R.drawable.img_obj_lemon, R.raw.tmp_lemon),
		new WordData("Lamb", "LAMB", R.drawable.img_obj_lamb, R.raw.tmp_lamb),
		new WordData("Lettuce", "LETTUCE", R.drawable.img_obj_lettuce, R.raw.tmp_lettuce),
		new WordData("Lizard", "LIZARD", R.drawable.img_obj_lizzard, R.raw.tmp_lizard)
	};
	private int wordIndex;	// Set to the currently chosen word;
	private ImageButton chosenWordButton;
	
	private int wordCompletionCount = 0;	// Increment as each word is successfully completed, so we know when to finish
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Basic inherited fields to set
		subPattern = "L";
		maxCorrectMatches = 1;
		maxAttempts = 3;
		
		setContentView(R.layout.feed_game);
		
		// Menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		// UI
		this.prompt = (ImageView) findViewById(R.id.imageViewPrompt);
		
		//Food items
		this.buttonItem1 = (ImageButton) findViewById(R.id.btn_lemon);
		this.buttonItem2 = (ImageButton) findViewById(R.id.btn_lamb);
		this.buttonItem3 = (ImageButton) findViewById(R.id.btn_lettuce);
		this.buttonItem4 = (ImageButton) findViewById(R.id.btn_lizard);
		
		this.buttonItem1.setOnTouchListener(this);
		this.buttonItem2.setOnTouchListener(this);
		this.buttonItem3.setOnTouchListener(this);
		this.buttonItem4.setOnTouchListener(this);
		
		this.buttonItem1.setBackgroundResource(this.words[0].drawable);
		this.buttonItem2.setBackgroundResource(this.words[1].drawable);
		this.buttonItem3.setBackgroundResource(this.words[2].drawable);
		this.buttonItem4.setBackgroundResource(this.words[3].drawable);
		
		setState(InteractionState.IDLE);
	}
	
	protected Mode getMode() {
		return Mode.WORD;
	}
	
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_feed);
	}
	

	protected void fullSuccess(AbstractGameActivity activityToUpdate) {
		//ChooseGameActivity that = (ChooseGameActivity)activityToUpdate;
		//that.textViewMessage.setText("Got all the matches!");
		
		// Clear the word that was just done, or complete the game
		this.wordCompletionCount++;
		this.chosenWordButton.setVisibility(View.INVISIBLE);
		if (this.wordCompletionCount >= this.words.length) {
			this.playingRef = R.raw.leo_really_cool_16bit;
			//this.message.setText("Well Done!");
			setState(InteractionState.PLAY);
			runLessonCompletion();		// Last game, so go to the victory screen
		}
		else {			
			//this.message.setText("Now say " + this.words[this.wordIndex].displayWord + "!");
			this.playingRef = R.raw.leo_great_job_16bit;
			setState(InteractionState.PLAY);
		}				
		wipeRecognizer(); //TODO Put in rerun bit?
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		//ChooseGameActivity that = (ChooseGameActivity)activityToUpdate;
		//that.textViewMessage.setText("Matched " + successCount + " times!");

		// Encourage the same word - note this won't be executed if accepting a single positive attempt
		this.playingRef = R.raw.leo_great_job_16bit;
		//this.message.setText("Good, do it again!");
		setState(InteractionState.PLAY_THEN_RECORD);
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		//ChooseGameActivity that = (ChooseGameActivity)activityToUpdate;
		//that.textViewMessage.setText("Press Start to try again.");

		this.playingRef = this.words[this.wordIndex].speechAudio;
//		this.message.setText("Try it again!");
		setState(InteractionState.PLAY_THEN_RERUN);
		wipeRecognizer();
	}
	
	
	private void startGameForWord(int i, ImageButton ib) {
		setState(InteractionState.IDLE);
		wipeRecognizer();
		this.wordIndex = i;
		this.chosenWordButton = ib;
//		MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), this.words[this.wordIndex].speechAudio);
//		mediaPlayer.start();
		this.playingRef = this.words[this.wordIndex].speechAudio;
		setState(InteractionState.PLAY);
		this.subPattern = this.words[this.wordIndex].matchWord;
		runGame();	//TODO replace with setting state to RECORD or PLAY_THEN_RECORD, which starts the recogniser if it's not running?
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.buttonSkip) {
				// Skip to the games
				//stopPlaying();
				//stopRecording();
				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.buttonMenu) {
				// Skip to the Menu
				//stopPlaying();
				//stopRecording();
				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.buttonItem1) {
				startGameForWord(0, (ImageButton) v);
			}
			else if (v == this.buttonItem2) {
				startGameForWord(1, (ImageButton) v);
			}
			else if (v == this.buttonItem3) {
				startGameForWord(2, (ImageButton) v);
			}
			else if (v == this.buttonItem4) {	
				startGameForWord(3, (ImageButton) v);
			}
		}
		
		return false;
	}
}

