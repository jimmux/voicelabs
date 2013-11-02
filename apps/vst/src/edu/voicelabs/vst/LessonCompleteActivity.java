package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class LessonCompleteActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonComplete;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lesson_complete);
		this.imageButtonComplete = (ImageButton) findViewById(R.id.imageButtonComplete);
		this.imageButtonComplete.setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {		
			if (v == this.imageButtonComplete) {
				Intent intent = new Intent(getApplicationContext(), PhonemeSelectActivity.class);
	            startActivity(intent); 
			}
		}
		return false;
	}

}
