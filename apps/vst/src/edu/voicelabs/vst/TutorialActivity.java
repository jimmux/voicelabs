package edu.voicelabs.vst;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Adapted from example at http://developer.android.com/guide/topics/media/audio-capture.html
 * 
 * Modified for different interaction pattern:
 * 	On record button, record up to the time limit.
 * 	On play button, stop any running recording (if any) and play back (if any).
 * 
 */
public class TutorialActivity extends Activity implements OnTouchListener {
	
	private ImageButton buttonRecord;
	private ImageButton buttonPlay;
	private Button buttonSkip;
	
	// Audio record/playback
	private static int recordDuration = 3000;	// Maximum recording time
    private static String audioFileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private void startPlaying() {
    	// Stop player and recorder to avoid conflicts
    	stopRecording();
		stopPlaying();
    	
        this.player = new MediaPlayer();
        try {
        	this.player.setDataSource(audioFileName);
        	this.player.prepare();
        	this.player.start();
        } catch (IOException e) {
            Log.e(getClass().getName(), "prepare() failed");
        }
    }

    private void stopPlaying() {
    	if (this.player != null) {
    		this.player.release();
    		this.player = null;
    	}
    }

    private void startRecording() {
    	// Stop player and recorder to avoid conflicts
    	stopRecording();
		stopPlaying();
    	
    	this.recorder = new MediaRecorder();
    	this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    	this.recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    	this.recorder.setOutputFile(audioFileName);
    	this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    	this.recorder.setMaxDuration(recordDuration);
        try {
        	this.recorder.prepare();
        } catch (IOException e) {
            Log.e(getClass().getName(), "prepare() failed");
        }

        this.recorder.start();
    }

    private void stopRecording() {
    	if (this.recorder != null) {
    		this.recorder.stop();
    		this.recorder.release();
    		this.recorder = null;
    	}
    }

    public TutorialActivity() {
    	//JAM todo: use private path?
        audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.3gp";
//        audioFileName = getApplicationContext().getFilesDir().getAbsolutePath() + "/record.3gp";
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.recorder != null) {
        	this.recorder.release();
        	this.recorder = null;
        }

        if (this.player != null) {
        	this.player.release();
        	this.player = null;
        }
    }
	

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.tmp_tutorial);
		
		this.buttonRecord = (ImageButton) findViewById(R.id.buttonRecord);
		this.buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
		this.buttonSkip = (Button) findViewById(R.id.buttonSkip);
		
		this.buttonRecord.setOnTouchListener(this);
		this.buttonPlay.setOnTouchListener(this);
		this.buttonSkip.setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v == this.buttonRecord) {
				// Record a sample
				startRecording();
			}
			else if (v == this.buttonPlay) {
				// Play back whatever we have recorded
				startPlaying();
			}
			else if (v == this.buttonSkip) {
				// Skip to the games
				stopPlaying();
				stopRecording();
				Intent intent = new Intent(getApplicationContext(), LessonProgressActivity.class);
	            startActivity(intent); 
			}
		}
		
		return false;
	}
}
