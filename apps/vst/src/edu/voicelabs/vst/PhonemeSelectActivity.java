/**
 * 
 */
package edu.voicelabs.vst;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author James Manley <jimmux@gmail.com>
 *
 */
public class PhonemeSelectActivity extends Activity implements OnTouchListener {
	
	private ImageButton imageButtonL;
	private ImageButton imageButtonS;
	private ImageButton imageButtonCH;
	
	private ImageButton buttonGoToSettings;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.phoneme_select);
		
		//import fonts
		TextView txt_settings = (TextView) findViewById(R.id.txt_settings);
		TextView txt_level_l = (TextView) findViewById(R.id.txt_level_l);
		TextView txt_level_s = (TextView) findViewById(R.id.txt_level_s);
		TextView txt_level_ch = (TextView) findViewById(R.id.txt_level_ch);
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Mabel.ttf");  
		
		txt_settings.setTypeface(font);  
		txt_level_l.setTypeface(font);
		txt_level_s.setTypeface(font);
		txt_level_ch.setTypeface(font);
		
		this.imageButtonL = (ImageButton) findViewById(R.id.imageButtonPhonemeL);
		this.imageButtonS = (ImageButton) findViewById(R.id.imageButtonPhonemeS);
		this.imageButtonCH = (ImageButton) findViewById(R.id.imageButtonPhonemeCH);
		
		this.buttonGoToSettings = (ImageButton) findViewById(R.id.img_settings);
		
		this.imageButtonL.setOnTouchListener(this);
		this.imageButtonS.setOnTouchListener(this);
		this.imageButtonCH.setOnTouchListener(this);
		
		this.buttonGoToSettings.setOnTouchListener(this);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.imageButtonL) {
				// Go to the lesson
				Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
	            startActivity(intent); 
			}
			else if (v == this.imageButtonS) {
				// Go nowhere (yet)
				//TODO play a lock closing sound (and maybe shake to indicate it's locked?) - I'll try and source something - DK
				AnimationHelper.runAlphaAnimation(this, R.id.btn_lockedS, R.anim.anim_shake);
				Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_SHORT).show();
			}
			else if (v == this.imageButtonCH) {
				// Go nowhere (yet)
				//play a lock closing sound - I'll try and source something - DK
				AnimationHelper.runAlphaAnimation(this, R.id.btn_lockedCh, R.anim.anim_shake);
				Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_SHORT).show();
			}
			else if (v == this.buttonGoToSettings) {
				// Go to the video
				Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
	            startActivity(intent); 
			}
		}
		
		return false;
	}

}
