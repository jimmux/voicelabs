package edu.voicelabs.vst;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import edu.voicelabs.vst.RecognizerTask.Mode;

public class ChooseGameActivity extends AbstractGameActivity implements OnTouchListener {
	// Layout elements
	protected RelativeLayout gameLayout;	

	//menu
	private ImageButton buttonSkip;
	private ImageButton buttonMenu;
	
	//items
	private ImageButton buttonItem1;
	private ImageButton buttonItem2;
	private ImageButton buttonItem3;
	private ImageButton buttonItem4;
	
	//playback icons
	private ImageButton playBack1;
	private ImageButton playBack2;
	private ImageButton playBack3;
	private ImageButton playBack4;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Basic inherited fields to set
		subPattern = "L";
		maxCorrectMatches = 1;
		maxAttempts = 3;
		mode = Mode.WORD;
		
		setContentView(R.layout.feed_game);
		
		//menu
		this.buttonSkip = (ImageButton) findViewById(R.id.buttonSkip);
		this.buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
		
		this.buttonSkip.setOnTouchListener(this);
		this.buttonMenu.setOnTouchListener(this);
		
		//Food items
		this.buttonItem1 = (ImageButton) findViewById(R.id.btn_lemon);
		this.buttonItem2 = (ImageButton) findViewById(R.id.btn_lettuce);
		this.buttonItem3 = (ImageButton) findViewById(R.id.btn_leg);
		this.buttonItem4 = (ImageButton) findViewById(R.id.btn_lamb);
		
		this.buttonItem1.setOnTouchListener(this);
		this.buttonItem2.setOnTouchListener(this);
		this.buttonItem3.setOnTouchListener(this);
		this.buttonItem4.setOnTouchListener(this);
		
		//playback items
		this.playBack1 = (ImageButton) findViewById(R.id.btn_play_1);
		this.playBack2 = (ImageButton) findViewById(R.id.btn_play_2);
		this.playBack3 = (ImageButton) findViewById(R.id.btn_play_3);
		this.playBack4 = (ImageButton) findViewById(R.id.btn_play_4);
		

	}
	
	protected ViewGroup getGameLayout() {
		return (ViewGroup) findViewById(R.id.game_layout_feed);
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
		ChooseGameActivity that = (ChooseGameActivity)activityToUpdate;
		//that.textViewMessage.setText("Got all the matches!");
		runLessonCompletion();		// Last game, so return to select screen
	}
	
	protected void partSuccess(AbstractGameActivity activityToUpdate, int successCount) {
		ChooseGameActivity that = (ChooseGameActivity)activityToUpdate;
		//that.textViewMessage.setText("Matched " + successCount + " times!");
	}
	
	protected void fullAttempts(AbstractGameActivity activityToUpdate) {
		ChooseGameActivity that = (ChooseGameActivity)activityToUpdate;
		//that.textViewMessage.setText("Press Start to try again.");
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
				
				
				//Animate Playback icon while sound is playing
				//first unhide playback icon
				//this should really test for whether sound is playing but you get the idea
				
				final boolean playbackVisible = playBack1.isShown();
				
				if (playbackVisible == false){
					// Play sound
					playBack1.setVisibility(View.VISIBLE);
					AnimationHelper.runKeyframeAnimation(this, R.id.btn_play_1, R.anim.anim_play_btn);
					//System.out.println("turned on");
					
				}else {
					// Stop sound
					playBack1.setVisibility(View.INVISIBLE);
					//System.out.println("turned off");
				}
				
				

			}
			
			else if (v == this.buttonItem2) {
				//Animate Playback icon while sound is playing
				//first unhide playback icon
				
				final boolean playbackVisible = playBack2.isShown();
				
				if (playbackVisible == false){
					// Play sound
					playBack2.setVisibility(View.VISIBLE);
					AnimationHelper.runKeyframeAnimation(this, R.id.btn_play_2, R.anim.anim_play_btn);
					//System.out.println("turned on");
					
				}else {
					// Stop sound
					playBack2.setVisibility(View.INVISIBLE);
					//System.out.println("turned off");
				}
				

			}
			
			else if (v == this.buttonItem3) {
				//Animate Playback icon while sound is playing
				//first unhide playback icon
				
				final boolean playbackVisible = playBack3.isShown();
				
				if (playbackVisible == false){
					// Play sound
					playBack3.setVisibility(View.VISIBLE);
					AnimationHelper.runKeyframeAnimation(this, R.id.btn_play_3, R.anim.anim_play_btn);
					//System.out.println("turned on");
					
				}else {
					// Stop sound
					playBack3.setVisibility(View.INVISIBLE);
					//System.out.println("turned off");
				}
				

			}
			
			else if (v == this.buttonItem4) {
				
				//Animate Playback icon while sound is playing
				//first unhide playback icon
				
				final boolean playbackVisible = playBack4.isShown();
				
				if (playbackVisible == false){
					// Play sound
					playBack4.setVisibility(View.VISIBLE);
					AnimationHelper.runKeyframeAnimation(this, R.id.btn_play_4, R.anim.anim_play_btn);
					//System.out.println("turned on");
					
				}else {
					// Stop sound
					playBack4.setVisibility(View.INVISIBLE);
					//System.out.println("turned off");
				}
			

			}
		}
		
		return false;
	}
}

