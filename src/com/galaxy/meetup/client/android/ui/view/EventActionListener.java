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
public interface EventActionListener {

	public abstract void onAddPhotosClicked();

    public abstract void onAvatarClicked(String s);

    public abstract void onExpansionToggled(boolean flag);

    public abstract void onHangoutClicked();

    public abstract void onInstantShareToggle(boolean flag);

    public abstract void onInviteMoreClicked();

    public abstract void onLinkClicked(String s);

    public abstract void onLocationClicked();

    public abstract void onPhotoClicked(String s, String s1, String s2);

    public abstract void onPhotoUpdateNeeded(String s, String s1, String s2);

    public abstract void onRsvpChanged(String s);

    public abstract void onUpdateCardClicked(EventUpdate eventupdate);

    public abstract void onViewAllInviteesClicked();
}
