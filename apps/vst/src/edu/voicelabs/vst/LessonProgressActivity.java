package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class LessonProgressActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonPhoneme;
	private ImageButton imageButtonSyllable;
	private ImageButton imageButtonWord;
	private ImageButton imageButtonChoose;
	private ImageButton imageButtonBack;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lesson_progress);
		
		this.imageButtonPhoneme = (ImageButton) findViewById(R.id.imageButtonPhoneme);
		this.imageButtonSyllable = (ImageButton) findViewById(R.id.imageButtonSyllable);
		this.imageButtonWord = (ImageButton) findViewById(R.id.imageButtonWord);
		this.imageButtonChoose = (ImageButton) findViewById(R.id.imageButtonChoose);
		this.imageButtonBack = (ImageButton) findViewById(R.id.imageButtonBack);
		
		this.imageButtonPhoneme.setOnTouchListener(this);
		this.imageButtonSyllable.setOnTouchListener(this);
		this.imageButtonWord.setOnTouchListener(this);
		this.imageButtonChoose.setOnTouchListener(this);
		this.imageButtonBack.setOnTouchListener(this);
	}
	
	// Show load screen, advance to start screen when ready.
		@Override
		public void onWindowFocusChanged(boolean hasFocus) {
			super.onWindowFocusChanged(hasFocus);
			
			if (hasFocus) {
				//start animations
				AnimationHelper.runAlphaAnimation(this, R.id.clouds, R.anim.anim_clouds);
				AnimationHelper.runAlphaAnimation(this, R.id.sunProgress, R.anim.anim_sun);
				//if (test if user has completed lesson){
				//AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonPhoneme, R.anim.anim_btn_red_star);
				//}else{ 
				AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonPhoneme, R.anim.anim_btn_red_circle1);
				AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonSyllable, R.anim.anim_btn_red_circle2);
				AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonWord, R.anim.anim_btn_red_circle3);
				AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonChoose, R.anim.anim_btn_red_circle4);
			}
		}	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {		
			if (v == this.imageButtonPhoneme) {
				AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonPhoneme, R.anim.anim_btn_red_circle_backwards);
				Intent intent = new Intent(getApplicationContext(), PhonemeGameActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonSyllable) {
				AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonSyllable, R.anim.anim_btn_red_circle_backwards);
				Intent intent = new Intent(getApplicationContext(), SyllableGameActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonWord) {
				AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonWord, R.anim.anim_btn_red_circle_backwards);
				Intent intent = new Intent(getApplicationContext(), WordGameActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonChoose) {
				AnimationHelper.runKeyframeAnimation(this, R.id.imageButtonChoose, R.anim.anim_btn_red_circle_backwards);
				Intent intent = new Intent(getApplicationContext(), ChooseGameActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonBack) {
				Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
	            startActivity(intent); 
			}
		}
		return false;
	}

}
