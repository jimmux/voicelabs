package edu.voicelabs.vst;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.graphics.drawable.AnimationDrawable;

public class AnimationHelper {

	/**
	 * handles all subclasses of View : TextView, Button, ImageView etc..
	 * given the component's id in their layout file
	 * Taken from http://mobile.dzone.com/articles/android-special-effects-alpha
	 * */
	public static void runAlphaAnimation(Activity act, int viewId, int animationFile) {

	    // load animation XML resource under res/anim
	    Animation animation  = AnimationUtils.loadAnimation(act, animationFile);	
	    if(animation == null){
		return; // here, we don't care
	    }
	    // reset initialization state
	    animation.reset();	  
	    // find View by its id attribute in the XML
	    View v = act.findViewById(viewId);
	    // cancel any pending animation and start this one
	    if (v != null){
	      v.clearAnimation();
	      v.startAnimation(animation);
	    }	    	  
	}
	
	public static AnimationDrawable runKeyframeAnimation(Activity act, int viewId, int animationFile)  {

		// find View by its id attribute
		ImageView theView = (ImageView) act.findViewById(viewId);
	   
		if(theView == null){
		 // here, we don't care
	    }
		// load animation XML resource under res/anim
		theView.setBackgroundResource(animationFile);
	    
		AnimationDrawable theAnimation = (AnimationDrawable) theView.getBackground();
		
		theAnimation.start();
		
		return theAnimation;
		
	}
	
	
	
}