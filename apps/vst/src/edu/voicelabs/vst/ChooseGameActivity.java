package edu.voicelabs.vst;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import edu.voicelabs.vst.RecognizerTask.Mode;

public class ChooseGameActivity extends AbstractGameActivity implements OnTouchListener {
	// Layout elements
	protected RelativeLayout gameLayout;
	private ImageButton leo;

	// Menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	
	// Items
	private ImageButton buttonItem1;
	private ImageButton buttonItem2;
	private ImageButton buttonItem3;
	private ImageButton buttonItem4; 
	
	private AnimationDrawable leoAnimation;
	
	private boolean feedIntroPlayed = false; 

	/**
	 * Simple struct for associated word data
	 *
	 */
	private class WordData {
//		String displayWord;		// Text to use for display
		String matchWord;		// Text to use for speech matching
		int drawable;			// The reference of the image in res/drawable to use
		int speechAudio;		// The reference of the sound file in res/raw to use, e.g. R.raw.tmp_lolly
		
		public WordData(String displayWord, String matchWord, int drawable, int speechAudio) {
//			this.displayWord = displayWord;
			this.matchWord = matchWord;
			this.drawable = drawable;
			this.speechAudio = speechAudio;
		}
	}
	private WordData[] words = {
		new WordData("Lemon", "LEMON", R.drawable.img_obj_feed_lemon, R.raw.word_lemon),
		new WordData("Lettuce", "LETTUCE", R.drawable.img_obj_feed_lettuce, R.raw.word_lettuce),
		new WordData("Lizard", "LIZARD", R.drawable.img_obj_feed_lizzard, R.raw.word_lizzard),
		new WordData("Lamb", "LAMB", R.drawable.img_obj_feed_lamb, R.raw.word_lamb)

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
		this.leo = (ImageButton) findViewById(R.id.buttonStartWord);
		this.leo.setBackgroundResource(R.anim.anim_leo_eat);
		this.leoAnimation = (AnimationDrawable) this.leo.getBackground();
		
		//Food items
		this.buttonItem1 = (ImageButton) findViewById(R.id.btn_lemon);
		this.buttonItem2 = (ImageButton) findViewById(R.id.btn_lettuce);
		this.buttonItem3 = (ImageButton) findViewById(R.id.btn_lizzard);
		this.buttonItem4 = (ImageButton) findViewById(R.id.btn_lamb);
		
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
	

	protected void fullSuccess() {		
		// Clear the word that was just done, and complete the game if all done
		this.wordCompletionCount++;
		
		int itm = this.chosenWordButton.getId();
		Animation animation;
		if ((itm == buttonItem3.getId())||(itm == buttonItem4.getId())){
			animation  = AnimationUtils.loadAnimation(this, R.anim.anim_obj_slide_r_to_l);
		}
		else {
			animation  = AnimationUtils.loadAnimation(this, R.anim.anim_obj_slide_l_to_r);
		}
		// Reset initialization state
	    animation.reset();	  
	    // Find View by its id attribute in the XML
	    View v = chosenWordButton;
	    // Cancel any pending animation and start this one
	    v.clearAnimation();
	    v.startAnimation(animation);	    	  
	   
		this.chosenWordButton.setVisibility(View.INVISIBLE);
	    // Leo eats!
    	// Play animation manually 
		leoAnimation.stop();
		leoAnimation.start();
		if (this.wordCompletionCount >= this.words.length) {
			this.playingRef = R.raw.feedback_pos_really_cool;
			setState(InteractionState.PLAY);
			
			// Last game, so go to the victory screen after 3 sec delay
			Handler handler = new Handler(); 
		    handler.postDelayed(new Runnable() { 
		    	public void run() { 
		    		runGameCompletion("Choose");
		    	} 
		    }, 3000); 
		}
		else {			
			this.playingRef = R.raw.feedback_pos_great_job;
			setState(InteractionState.PLAY);
//			setState(InteractionState.PLAY_THEN_RERUN); //TODO: check
		}				
//		wipeRecognizer(); //TODO Put in rerun bit? 
	}
	
	protected void partSuccess() {
		// Encourage the same word - note this won't be executed if accepting a single positive attempt
		this.playingRef = R.raw.feedback_pos_well_done;
		setState(InteractionState.PLAY_THEN_RECORD);
	}
	
	protected void fullAttempts() {
		this.playingRef = this.words[this.wordIndex].speechAudio;
		setState(InteractionState.PLAY_THEN_RERUN);
//		wipeRecognizer();
	}
	
	private void setupGame() {
		buttonItem1.setVisibility(View.VISIBLE);
		buttonItem2.setVisibility(View.VISIBLE);
		buttonItem3.setVisibility(View.VISIBLE);
		buttonItem4.setVisibility(View.VISIBLE);
		
		AnimationHelper.runAlphaAnimation(this, R.id.btn_lemon, R.anim.anim_fade_in);
		AnimationHelper.runAlphaAnimation(this, R.id.btn_lettuce, R.anim.anim_fade_in);
		AnimationHelper.runAlphaAnimation(this, R.id.btn_lamb, R.anim.anim_fade_in);
		AnimationHelper.runAlphaAnimation(this, R.id.btn_lizzard, R.anim.anim_fade_in);
		
		//TODO implement circle helper for feed game items here?
		
	}
	
	
	private void startGameForWord(int i, ImageButton ib) {
		setState(InteractionState.IDLE);
		wipeRecognizer(); //TODO Check
		this.wordIndex = i;
		this.chosenWordButton = ib;
		this.subPattern = this.words[this.wordIndex].matchWord;
		this.playingRef = this.words[this.wordIndex].speechAudio;
		
		//swap ib for highlight state resource
		
		if (ib == buttonItem1){
			buttonItem1.setBackgroundResource(R.drawable.img_obj_feed_lemon_hl);
		}else if (ib == buttonItem2){
			buttonItem2.setBackgroundResource(R.drawable.img_obj_feed_lettuce_hl);
		}else if (ib == buttonItem3){
			buttonItem3.setBackgroundResource(R.drawable.img_obj_feed_lizzard_hl);
		}else if (ib == buttonItem4){
			buttonItem4.setBackgroundResource(R.drawable.img_obj_feed_lamb_hl);
		}
		
		
		leoAnimation = (AnimationDrawable) this.leo.getBackground();
		leoAnimation.selectDrawable(0);
		/*
		setState(InteractionState.PLAY);
		runGame();	//TODO replace with setting state to PLAY_THEN_RECORD, which starts the recogniser if it's not running?
		*/
		setState(InteractionState.PLAY_THEN_RERUN);
	}
	
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		//TODO set leo to talk
	
		if (hasFocus && !this.feedIntroPlayed) {
			MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.leo_after_all_that_work);
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
	            @Override
	            public void onCompletion(MediaPlayer mp) {
	            	//TODO set leo back to idle
	        		setupGame();
	            }
			});
			mediaPlayer.start();
			//leoAnimation.start();
			this.feedIntroPlayed = true;
		}
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

