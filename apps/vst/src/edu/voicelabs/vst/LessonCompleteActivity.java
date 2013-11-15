package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class LessonCompleteActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonComplete;
	private TextView message;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lesson_complete);
		AnimationHelper.runKeyframeAnimation(this, R.id.imageBtnStarComplete, R.anim.anim_star_big);
		this.imageButtonComplete = (ImageButton) findViewById(R.id.imageBtnStarComplete);
		this.imageButtonComplete.setOnTouchListener(this);
		
		this.message = (TextView) findViewById(R.id.txt_message);
		this.message.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf"));
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {		
			if (v == this.imageButtonComplete) {
				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			}
		}
		return false;
	}

}
