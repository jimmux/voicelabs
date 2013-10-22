package edu.voicelabs.vst;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import edu.voicelabs.vst.RecognizerTask.Mode;

public class PracticeGameActivity extends Activity implements OnTouchListener, RecognitionListener {
	
	// Speech recognition bits
	static {System.loadLibrary("pocketsphinx_jni");}
	RecognizerTask rec;
	Thread rec_thread;
	Date start_date;
	float speech_dur;
	boolean listening;
		
	private TextView textViewMessage;
	private Button buttonStart;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.practice_game);
		
		this.textViewMessage = (TextView) findViewById(R.id.textViewMessage);	
		this.buttonStart = (Button) findViewById(R.id.buttonStart);	
		this.textViewMessage.setOnTouchListener(this);	
		this.buttonStart.setOnTouchListener(this);	

		
		// Set up speech recognition
		//this.rec = new RecognizerTask(getApplicationContext());
		this.rec = new RecognizerTask(getApplicationContext(), Mode.PHONEME);
		this.rec_thread = new Thread(this.rec);
		this.listening = false;
		this.rec.setRecognitionListener(this);
		this.rec_thread.start();	
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.buttonStart) {
				runGame();
			}
		}	
		return false;
	}
	
	private void runGame() {
		this.textViewMessage.setText("Say 'L' three times");
		
		this.start_date = new Date();
		this.listening = true;
		this.rec.start();
		
//		while (new Date().getTime() - start_date.getTime() < 10000 ) {
//			
//		}
//		this.rec.stop();
//		
//		this.textViewMessage.setText("Done");
	}
	
	
	/** Called when partial results are generated. */
	public void onPartialResults(Bundle b) {
		final PracticeGameActivity that = this;
		final String hyp = b.getString("hyp");
		that.textViewMessage.post(new Runnable() {
			public void run() {
				if (hyp.matches("(.*\\bL.*){3,}")) {
					that.textViewMessage.setText("Match 3!");
				}
				else if (hyp.matches("(.*\\bL.*){2}")) {
					that.textViewMessage.setText("Match 2!");
				}
				else if (hyp.matches("(.*\\bL.*){1}")) {
					that.textViewMessage.setText("Match 1!");
				}
				else {
					that.textViewMessage.setText("Try again...");	
				}	
				
//				if (new Date().getTime() - that.start_date.getTime() < 50000 ) {
//					that.textViewMessage.setText("Done!");
//					that.rec.stop();
//				}
			}
		});
	}

	/** Called when full results are generated. */
	public void onResults(Bundle b) {
//		final String hyp = b.getString("hyp");
//		final PracticeGameActivity that = this;
//		this.edit_text.post(new Runnable() {
//			public void run() {
//				that.edit_text.setText(hyp);
//				Date end_date = new Date();
//				long nmsec = end_date.getTime() - that.start_date.getTime();
//				float rec_dur = (float)nmsec / 1000;
//				that.performance_text.setText(String.format("%.2f seconds %.2f xRT",
//															that.speech_dur,
//															rec_dur / that.speech_dur));
//				Log.d(getClass().getName(), "Hiding Dialog");
//				that.rec_dialog.dismiss();
//			}
//		});
	}

	public void onError(int err) {
//		final PracticeGameActivity that = this;
//		that.edit_text.post(new Runnable() {
//			public void run() {
//				that.rec_dialog.dismiss();
//			}
//		});
	}

}
