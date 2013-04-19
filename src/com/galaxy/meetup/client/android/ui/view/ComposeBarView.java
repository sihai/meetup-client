/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 
 * @author sihai
 * 
 */
public class ComposeBarView extends FrameLayout implements
		ColumnGridView.PressedHighlightable {

	private OnComposeBarMeasuredListener mOnComposeBarMeasuredListener;

	public ComposeBarView(Context context) {
		this(context, null);
	}

	public ComposeBarView(Context context, AttributeSet attributeset) {
		this(context, attributeset, 0);
	}

	public ComposeBarView(Context context, AttributeSet attributeset, int i) {
		super(context, attributeset, i);
	}

	protected void onMeasure(int i, int j) {
		super.onMeasure(i, j);
		if (mOnComposeBarMeasuredListener != null)
			mOnComposeBarMeasuredListener.onComposeBarMeasured(
					getMeasuredWidth(), getMeasuredHeight());
	}

	public void setOnComposeBarMeasuredListener(
			OnComposeBarMeasuredListener oncomposebarmeasuredlistener) {
		mOnComposeBarMeasuredListener = oncomposebarmeasuredlistener;
	}

	public final boolean shouldHighlightOnPress() {
		return false;
	}

	public static interface OnComposeBarMeasuredListener {

		void onComposeBarMeasured(int i, int j);
	}
}
