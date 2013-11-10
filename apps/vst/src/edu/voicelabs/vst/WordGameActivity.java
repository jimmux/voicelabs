package edu.voicelabs.vst;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.voicelabs.vst.RecognizerTask.Mode;

public class WordGameActivity extends AbstractGameActivity implements OnTouchListener {
	
	// Layout elements
	protected RelativeLayout gameLayout;	

	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	private ImageButton buttonStart;
	
	private ImageButton buttonPlay;
	private ImageButton wordObject;
	private TextView txtLabel;
	
    private AnimationDrawable speakAnim;
	
	//Objects
//	private ImageButton objLemon;
//	private ImageButton objLettuce;
	
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
		new WordData("Lettuce", "LETTUCE", R.drawable.img_obj_lettuce, R.raw.tmp_lettuce)
		//new WordData("Lizard", "LIZARD", R.drawable.img_obj_lizard, R.raw.tmp_lizard),
		//new WordData("Lolly", "LOLLY", R.drawable.img_obj_lolly, R.raw.tmp_lolly)
	};
	private int wordIndex = 0;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Basic inherited fields to set
		subPattern = "";
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
		
		// Animated prompt
		//this.imageViewSpeak = (ImageView) findViewById(R.id.imageViewSpeak);
		this.speakAnim = AnimationHelper.runKeyframeAnimation(this, R.id.buttonRecord, R.anim.anim_btn_speak);
		this.speakAnim.stop();
	
	}
	
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_word);
	}
	

	protected void fullSuccess(AbstractGameActivity activityToUpdate) {
		//WordGameActivity that = (WordGameActivity)activityToUpdate;
		//that.textViewMessage.setText("Got all the matches!");
		
		// Move to the next word, or complete the game
		this.wordIndex++;
		this.speakAnim.stop();
		if (this.wordIndex >= this.words.length) {
			Toast.makeText(getApplicationContext(), "Game complete!", Toast.LENGTH_SHORT).show();	
			this.txtLabel.setText("Well Done!");
		}
		else {
			Toast.makeText(getApplicationContext(), "Say the next word!", Toast.LENGTH_SHORT).show();
			MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), words[wordIndex].speechAudio);
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	            @Override
	            public void onCompletion(MediaPlayer mp) {
	            	speakAnim.start();
	    			txtLabel.setText(words[wordIndex].displayWord);
	    			wordObject.setBackgroundResource(words[wordIndex].drawable);
	    			subPattern = words[wordIndex].matchWord;
	    			wipeRecognizer();////
	    			runGame();
	            }
			});
			mediaPlayer.start();
		}
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		//WordGameActivity that = (WordGameActivity)activityToUpdate;
		//that.textViewMessage.setText("Matched " + successCount + " times!");

		
		// Encourage the same word
		Toast.makeText(getApplicationContext(), "Matched " + successCount + " times!", Toast.LENGTH_SHORT).show();
		this.speakAnim.stop();
		MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), words[wordIndex].speechAudio);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            	speakAnim.start();
            }
		});
		mediaPlayer.start();
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		//WordGameActivity that = (WordGameActivity)activityToUpdate;
		//that.textViewMessage.setText("Press Start to try again.");

		this.speakAnim.stop();
		Toast.makeText(getApplicationContext(), "Try again.", Toast.LENGTH_SHORT).show();
		txtLabel.setText("Keep trying!");
		MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tmp_lizard);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            	speakAnim.start();
            	txtLabel.setText(words[wordIndex].displayWord);
        		runGame();
            }
		});
		mediaPlayer.start();
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
				this.wordIndex = 0;
				this.txtLabel.setText(this.words[wordIndex].displayWord);
				
				//Play Object sound
				MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), words[wordIndex].speechAudio);
				mediaPlayer.start();
				
				//Playing animation
				//Animate Playback icon while sound is playing
				//first unhide playback icon
				//Change Text to next syllable - cycle through array?
				
				
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
				//this.wordObject.setBackgroundResource(R.drawable.img_obj_lettuce);
				//this.txtLabel.setText("Lettuce");
				//else if voice recognition unsuccessful then go back to start of object 
				
				
				
			
			}
		
		}
	return false;
	}
	
}

