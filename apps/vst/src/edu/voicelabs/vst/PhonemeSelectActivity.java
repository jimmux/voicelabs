/**
 * 
 */
package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * @author James Manley <jimmux@gmail.com>
 *
 */
public class PhonemeSelectActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonL;
	private ImageButton imageButtonS;
	private ImageButton imageButtonCH;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.phoneme_select);
		
		this.imageButtonL = (ImageButton) findViewById(R.id.imageButtonPhonemeL);
		this.imageButtonS = (ImageButton) findViewById(R.id.imageButtonPhonemeS);
		this.imageButtonCH = (ImageButton) findViewById(R.id.imageButtonPhonemeCH);
		
		this.imageButtonL.setOnTouchListener(this);
		this.imageButtonS.setOnTouchListener(this);
		this.imageButtonCH.setOnTouchListener(this);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.imageButtonL) {
				// Go to the lesson
				Intent intent = new Intent(getApplicationContext(), PhonemeGameActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonS) {
				// Go nowhere (yet)
				Toast.makeText(getApplicationContext(), "Not yet!", Toast.LENGTH_SHORT).show();
			}
			else if (v == this.imageButtonCH) {
				// Go nowhere (yet)
				Toast.makeText(getApplicationContext(), "Not yet!", Toast.LENGTH_SHORT).show();
			}
		}
		
		return false;
	}

}
