/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import com.galaxy.meetup.client.android.ui.activity.StreamOneUpActivity;

/**
 * 
 * @author sihai
 *
 */
public interface StreamOneUpCallbacks {

	public abstract void addScreenListener(StreamOneUpActivity.OnScreenListener onscreenlistener);

    public abstract void toggleFullScreen();
}
