/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.Iterator;

import android.view.View;
import android.widget.SearchView;

import com.galaxy.meetup.client.util.SoftInput;

/**
 * 
 * @author sihai
 * 
 */
public class SearchViewAdapterV11 extends SearchViewAdapter implements
		SearchView.OnCloseListener, SearchView.OnQueryTextListener {

	protected final SearchView mSearchView;

	public SearchViewAdapterV11(View view) {
		super(null);
		mSearchView = (SearchView) view;
		mSearchView.setSubmitButtonEnabled(false);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setOnCloseListener(this);
	}

	public void hideSoftInput() {
		SoftInput.hide(mSearchView);
	}

	public boolean onClose() {
		for (Iterator iterator = mListeners.iterator(); iterator.hasNext(); ((SearchViewAdapter.OnQueryChangeListener) iterator
				.next()).onQueryClose())
			;
		return true;
	}

	public boolean onQueryTextSubmit(String s) {
		super.onQueryTextSubmit(s);
		SoftInput.hide(mSearchView);
		return false;
	}

	public void setQueryHint(int i) {
		mSearchView.setQueryHint(mSearchView.getContext().getString(i));
	}

	public void setQueryText(String s) {
		mSearchView.setQuery(s, false);
		if (mRequestFocus)
			mSearchView.requestFocus();
	}

	public void setVisible(boolean flag) {
		setVisible(flag, ((View) (mSearchView)));
	}

	protected final void showSoftInput() {
		mSearchView.postDelayed(new Runnable() {

			public final void run() {
				mSearchView.setIconified(false);
			}
			
		}, 50L);
	}
}