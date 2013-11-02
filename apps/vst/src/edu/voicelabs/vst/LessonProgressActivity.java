package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

public class LessonProgressActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonPhoneme;
	private ImageButton imageButtonSyllable;
	private ImageButton imageButtonWord;
	private ImageButton imageButtonChoose;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lesson_progress);
		
		this.imageButtonPhoneme = (ImageButton) findViewById(R.id.imageButtonPhoneme);
		this.imageButtonSyllable = (ImageButton) findViewById(R.id.imageButtonSyllable);
		this.imageButtonWord = (ImageButton) findViewById(R.id.imageButtonWord);
		this.imageButtonChoose = (ImageButton) findViewById(R.id.imageButtonChoose);
		
		this.imageButtonPhoneme.setOnTouchListener(this);
		this.imageButtonSyllable.setOnTouchListener(this);
		this.imageButtonWord.setOnTouchListener(this);
		this.imageButtonChoose.setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {		
			if (v == this.imageButtonPhoneme) {
				Intent intent = new Intent(getApplicationContext(), PhonemeGameActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonSyllable) {
				Intent intent = new Intent(getApplicationContext(), SyllableGameActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonWord) {
				Intent intent = new Intent(getApplicationContext(), WordGameActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonChoose) {
				Intent intent = new Intent(getApplicationContext(), ChooseGameActivity.class);
	            startActivity(intent); 
			}
		}
		return false;
	}

}
