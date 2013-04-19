/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

/**
 * 
 * @author sihai
 *
 */
public interface MessageClickListener {

	public abstract void onCancelButtonClicked(long l);

    public abstract void onMediaImageClick(String s, String s1);

    public abstract void onRetryButtonClicked(long l);

    public abstract void onUserImageClicked(String s);
}
