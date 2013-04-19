/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 
 * @author sihai
 *
 */
public class HideViewAnimationListener implements AnimationListener {

	private final View view;
	
	HideViewAnimationListener(View view1) {
		view = view1;
	}

	public final void onAnimationEnd(Animation animation) {
		view.setVisibility(8);
	}

	public final void onAnimationRepeat(Animation animation) {
	}

	public final void onAnimationStart(Animation animation) {
	}
}
