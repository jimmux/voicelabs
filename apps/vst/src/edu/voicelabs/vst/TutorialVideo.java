package edu.voicelabs.vst;

import java.io.File;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class TutorialVideo extends Activity {
	
	private VideoView videoView;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.tutorial_movie);
		
		videoView = (VideoView) findViewById(R.id.tutorialVideo);		

        String videoName = "tutorial_intro";

        StringBuilder uriPathBuilder = new StringBuilder ();
        uriPathBuilder.append ("android.resource://");
        uriPathBuilder.append (this.getPackageName ());
        uriPathBuilder.append (File.separator);
        uriPathBuilder.append ("raw");
        uriPathBuilder.append (File.separator);
        uriPathBuilder.append (videoName);
        Uri uri = Uri.parse (uriPathBuilder.toString ());

        videoView.setVideoURI(uri);
        videoView.start();
	}
}
