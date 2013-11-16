package edu.voicelabs.vst;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class AnimationHelper {

	/**
	 * Handles all subclasses of View : TextView, Button, ImageView etc..
	 * given the component's id in their layout file
	 * Taken from http://mobile.dzone.com/articles/android-special-effects-alpha
	 * */
	public static void runAlphaAnimation(Activity act, int viewId, int animationFile) {

	    // Load animation XML resource under res/anim
	    Animation animation  = AnimationUtils.loadAnimation(act, animationFile);	
	    if(animation == null){
		return; // here, we don't care
	    }
	    // Reset initialization state
	    animation.reset();	  
	    // Find View by its id attribute in the XML
	    View v = act.findViewById(viewId);
	    // Cancel any pending animation and start this one
	    if (v != null){
	      v.clearAnimation();
	      v.startAnimation(animation);
	    }	    	  
	}
	
	public static AnimationDrawable runKeyframeAnimation(Activity act, int viewId, int animationFile)  {
		// Find View by its id attribute
		ImageView theView = (ImageView) act.findViewById(viewId);
		theView.setBackgroundResource(animationFile);
		AnimationDrawable theAnimation = (AnimationDrawable) theView.getBackground();
		theAnimation.start();
		
		return theAnimation;
	}
	
}