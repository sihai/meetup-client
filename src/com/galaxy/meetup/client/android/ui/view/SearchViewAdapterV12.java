/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.view.View;

import com.galaxy.meetup.client.util.SoftInput;

/**
 * 
 * @author sihai
 * 
 */
public final class SearchViewAdapterV12 extends SearchViewAdapterV11 implements
		View.OnAttachStateChangeListener {

	public SearchViewAdapterV12(View view) {
		super(view);
		mSearchView.addOnAttachStateChangeListener(this);
	}

	public final void onViewAttachedToWindow(View view) {
		if (mSearchView.hasFocus() && mRequestFocus)
			mSearchView.postDelayed(new Runnable() {

				public final void run() {
					SoftInput.show(mSearchView);
				}

			}, 100L);
	}

	public final void onViewDetachedFromWindow(View view) {
	}
}
