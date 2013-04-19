/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.util.SoftInput;

/**
 * 
 * @author sihai
 * 
 */
public class SearchViewAdapter implements TextWatcher,
		TextView.OnEditorActionListener {

	protected final ArrayList mListeners = new ArrayList();
	protected boolean mRequestFocus;
	private TextView mSearchView;
	
	protected SearchViewAdapter(View view) {
		mRequestFocus = true;
		mSearchView = (TextView) view;
		if (mSearchView != null) {
			mSearchView.addTextChangedListener(this);
			mSearchView.setOnEditorActionListener(this);
			View view1 = (View) mSearchView.getParent();
			if (view1 != null) {
				View view2 = view1.findViewById(R.id.search_go_btn);
				if (view2 != null)
					view2.setOnClickListener(new android.view.View.OnClickListener() {

						public final void onClick(View view3) {
							onQueryTextSubmit(mSearchView.getText().toString());
							SoftInput.hide(mSearchView);
						}
					});
			}
		}
	}

	public static SearchViewAdapter createInstance(View view) {
		SearchViewAdapter obj;
		if (android.os.Build.VERSION.SDK_INT >= 12)
			obj = new SearchViewAdapterV12(view);
		else if (android.os.Build.VERSION.SDK_INT >= 11)
			obj = new SearchViewAdapterV11(view);
		else
			obj = new SearchViewAdapter(view);
		return obj;
	}

	public void addOnChangeListener(OnQueryChangeListener onquerychangelistener) {
		mListeners.add(onquerychangelistener);
	}

	public void afterTextChanged(Editable editable) {
	}

	public void beforeTextChanged(CharSequence charsequence, int i, int j, int k) {
	}

	public void hideSoftInput() {
		SoftInput.hide(mSearchView);
	}

	public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent) {
		boolean flag;
		if ((i == 6 || i == 3) && mSearchView == textview) {
			onQueryTextSubmit(mSearchView.getText().toString());
			SoftInput.hide(mSearchView);
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	public boolean onQueryTextChange(String s) {
		for (Iterator iterator = mListeners.iterator(); iterator.hasNext(); ((OnQueryChangeListener) iterator
				.next()).onQueryTextChanged(s))
			;
		return false;
	}

	public boolean onQueryTextSubmit(String s) {
		for (Iterator iterator = mListeners.iterator(); iterator.hasNext(); ((OnQueryChangeListener) iterator
				.next()).onQueryTextSubmitted(s))
			;
		return false;
	}

	public void onTextChanged(CharSequence charsequence, int i, int j, int k) {
		onQueryTextChange(charsequence.toString());
	}

	public void requestFocus(boolean flag) {
		mRequestFocus = flag;
	}

	public void setQueryHint(int i) {
		mSearchView.setHint(i);
	}

	public void setQueryText(String s) {
		mSearchView.setText(s);
		if (mRequestFocus)
			mSearchView.requestFocus();
	}

	public void setVisible(boolean flag) {
		setVisible(flag, ((View) (mSearchView)));
	}

    protected final void setVisible(boolean flag, View view)
    {
    	if(null == view) {
    		return;
    	} else {
    		int i = view.getVisibility();
            int j;
            if(flag)
                j = View.VISIBLE;
            else
                j = View.GONE;
            if(i == j) {
            	return;
            } else {
            	view.setVisibility(j);
            	if(j == View.VISIBLE) {
            		SoftInput.hide(view);
        	        if(view.hasFocus())
        	            view.getRootView().requestFocus();
            	} else {
            		showSoftInput();
            	}
            }
    	}
    }

	protected void showSoftInput() {
		mSearchView.requestFocus();
		mSearchView.postDelayed(new Runnable() {

			public final void run() {
				SoftInput.show(mSearchView);
			}
		}, 50L);
	}
	    
	public static interface OnQueryChangeListener {

		void onQueryClose();

		void onQueryTextChanged(CharSequence charsequence);

		void onQueryTextSubmitted(CharSequence charsequence);
	}
}
