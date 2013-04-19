/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.graphics.Rect;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * 
 * @author sihai
 *
 */
public class PositionedStaticLayout extends StaticLayout {

	protected Rect mContentArea;
	
	public PositionedStaticLayout(CharSequence charsequence,
			TextPaint textpaint, int i,
			android.text.Layout.Alignment alignment, float f, float f1,
			boolean flag) {
		super(charsequence, textpaint, i, alignment, f, f1, flag);
		mContentArea = new Rect();
	}

	public final int getBottom() {
		return mContentArea.bottom;
	}

	public final int getLeft() {
		return mContentArea.left;
	}

	public final int getRight() {
		return mContentArea.right;
	}

	public final int getTop() {
		return mContentArea.top;
	}

	public final void setPosition(int i, int j) {
		mContentArea.set(i, j, i + getWidth(), j + getHeight());
	}

}
