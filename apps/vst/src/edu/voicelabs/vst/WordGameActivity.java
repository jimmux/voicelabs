package edu.voicelabs.vst;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.voicelabs.vst.RecognizerTask.Mode;

public class WordGameActivity extends AbstractGameActivity implements OnTouchListener {
	
	//TODO Add speaking of current word when the object is touched.
	
	// Layout elements
	protected RelativeLayout gameLayout;	

	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	private ImageButton buttonStart;
	private ImageButton wordObject;
	
	/**
	 * Simple struct for associated word data to loop through
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
		new WordData("Lemon", "LEMON", R.drawable.img_obj_lemon, R.raw.word_lemon),
		new WordData("Lamb", "LAMB", R.drawable.img_obj_lamb, R.raw.word_lamb),
		new WordData("Lettuce", "LETTUCE", R.drawable.img_obj_lettuce, R.raw.word_lettuce),
		new WordData("Lizard", "LIZARD", R.drawable.img_obj_lizzard, R.raw.word_lizzard),
		new WordData("Lightning", "LIGHTNING", R.drawable.img_obj_lightning, R.raw.word_lightning),
		new WordData("Lolly", "LOLLY", R.drawable.img_obj_lolly, R.raw.word_lolly),
		//new WordData("lips", "LIPS", R.drawable.img_obj_lips, R.raw.word_lips),
		new WordData("Leaves", "LEAVES", R.drawable.img_obj_leaves, R.raw.word_leaves)
	};
	private int wordIndex = 0;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Basic inherited fields to set
		subPattern = "";
		maxCorrectMatches = 1;
		maxAttempts = 3;
		
		setContentView(R.layout.word_game);
		
		// Menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		// UI
		this.message = (TextView) findViewById(R.id.txt_game_3);
		this.message.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf"));
		this.message.setText("Say the words");
		
		this.prompt = (ImageView) findViewById(R.id.imageViewPrompt);

		this.wordObject = (ImageButton) findViewById(R.id.btn_game3_obj); 
		this.wordObject.setVisibility(View.INVISIBLE);
		
		// Click on leo to start the game
		this.buttonStart = (ImageButton) findViewById(R.id.buttonStartWord);	
		this.buttonStart.setOnTouchListener(this);		

		setState(InteractionState.IDLE);	
	}
	
	protected Mode getMode() {
		return Mode.WORD;
	}
	
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_word);
	}
	
	protected void fullSuccess(AbstractGameActivity activityToUpdate) {	
		// Move to the next word, or complete the game
		this.wordIndex++;
		if (this.wordIndex >= this.words.length) {
			this.playingRef = R.raw.feedback_pos_really_cool;
			this.message.setText("Well Done!");
			setState(InteractionState.PLAY);
			//TODO Set visual back to Leo
			
			 Handler handler = new Handler(); 
			    handler.postDelayed(new Runnable() { 
			         public void run() { 
			        	 runLessonCompletion();  // Last game, so go to the victory screen
			         } 
			    }, 3000); 
		}
		else {			
			this.playingRef = this.words[this.wordIndex].speechAudio;
			this.message.setText("Now say " + this.words[this.wordIndex].displayWord + "!");
			this.wordObject.setBackgroundResource(this.words[this.wordIndex].drawable);
			this.subPattern = this.words[this.wordIndex].matchWord;
			setState(InteractionState.PLAY_THEN_RERUN);
		}				
		wipeRecognizer(); //TODO Put in rerun bit?
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		//WordGameActivity that = (WordGameActivity)activityToUpdate;
		//that.textViewMessage.setText("Matched " + successCount + " times!");
		
		// Encourage the same word - note this won't be executed if accepting a single positive attempt
		this.playingRef = R.raw.feedback_pos_great_job;
		this.message.setText("Good, do it again!");
		setState(InteractionState.PLAY_THEN_RECORD);
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		//WordGameActivity that = (WordGameActivity)activityToUpdate;
		//that.textViewMessage.setText("Press Start to try again.");

		this.playingRef = this.words[this.wordIndex].speechAudio;
		this.message.setText("Try it again!");
		setState(InteractionState.PLAY_THEN_RERUN);
		wipeRecognizer();
	}
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.buttonSkip) {
				// Skip to the games
				Intent intent = new Intent(getApplicationContext(), ChooseGameActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.buttonMenu) {
				// Skip to the Menu
				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.buttonStart) {
				// Start the game
				runGame();	//TODO replace with setting state to PLAY_THEN_RECORD, which starts the recogniser if it's not running?
				this.wordIndex = 0;
				this.message.setText(this.words[wordIndex].displayWord);
				this.wordObject.setBackgroundResource(this.words[wordIndex].drawable);
				
				//Play Object sound
				MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), this.words[this.wordIndex].speechAudio);
				mediaPlayer.start();
				
				this.buttonStart.setVisibility(View.INVISIBLE);
				this.wordObject.setVisibility(View.VISIBLE);
			}
		
		}
	return false;
	}
	
}

