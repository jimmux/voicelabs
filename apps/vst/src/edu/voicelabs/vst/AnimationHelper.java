/*
 * Copyright (c) VoiceLabs (James Manley and Dylan Kelly), 2013
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of VoiceLabs.
 */

package edu.voicelabs.vst;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Convenience class to assist in common animation setup.
 * 
 * @author Dylan Kelly
 *
 */

public class AnimationHelper {

	/**
	 * Handles all subclasses of View : TextView, Button, ImageView etc..
	 * given the component's id in their layout file
	 * Taken from http://mobile.dzone.com/articles/android-special-effects-alpha
	 * 
	 * */
	public static void runAlphaAnimation(Activity act, int viewId, int animationFile) {
	    // Load animation XML resource under res/anim
	    Animation animation  = AnimationUtils.loadAnimation(act, animationFile);	
	    if (animation == null) {
	    	return;
	    }
	    // Reset initialization state
	    animation.reset();	  
	    // Find View by its id attribute in the XML
	    View v = act.findViewById(viewId);
	    // Cancel any pending animation and start this one
	    if (v != null) {
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