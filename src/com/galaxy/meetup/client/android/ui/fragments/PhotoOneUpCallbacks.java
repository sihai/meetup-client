/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.support.v4.app.Fragment;

import com.galaxy.meetup.client.android.ui.activity.PhotoOneUpActivity;

/**
 * 
 * @author sihai
 *
 */
public interface PhotoOneUpCallbacks {

	public abstract void addMenuItemListener(PhotoOneUpActivity.OnMenuItemListener onmenuitemlistener);

    public abstract void addScreenListener(PhotoOneUpActivity.OnScreenListener onscreenlistener);

    public abstract boolean isFragmentActive(Fragment fragment);

    public abstract void onFragmentVisible(Fragment fragment);

    public abstract void onPhotoRemoved();

    public abstract void removeMenuItemListener(PhotoOneUpActivity.OnMenuItemListener onmenuitemlistener);

    public abstract void removeScreenListener(PhotoOneUpActivity.OnScreenListener onscreenlistener);

    public abstract void toggleFullScreen();
}
