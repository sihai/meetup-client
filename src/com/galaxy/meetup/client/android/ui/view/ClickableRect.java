/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.graphics.Rect;

/**
 * 
 * @author sihai
 *
 */
public class ClickableRect implements ClickableItem {

	private boolean mClicked;
    private CharSequence mContentDescription;
    private ClickableRectListener mListener;
    private Rect mRect;
    
    public ClickableRect(int i, int j, int k, int l, ClickableRectListener clickablerectlistener, CharSequence charsequence)
    {
        this(new Rect(i, j, i + k, j + l), clickablerectlistener, charsequence);
    }

    private ClickableRect(Rect rect, ClickableRectListener clickablerectlistener, CharSequence charsequence)
    {
        mRect = rect;
        mListener = clickablerectlistener;
        mContentDescription = charsequence;
    }

    public final int compare(ClickableItem obj, ClickableItem obj1)
    {
        ClickableItem clickableitem = (ClickableItem)obj;
        ClickableItem clickableitem1 = (ClickableItem)obj1;
        return sComparator.compare(clickableitem, clickableitem1);
    }

    public final CharSequence getContentDescription()
    {
        return mContentDescription;
    }

    public final Rect getRect()
    {
        return mRect;
    }

    public final boolean handleEvent(int i, int j, int k)
    {
        boolean flag = true;
        if(3 == k) {
        	mClicked = false;
        	return true;
        }
        if(!mRect.contains(i, j)) {
        	if(k == 1)
                mClicked = false;
            return false;
        }
        switch(k)
        {
        case 0: // '\0'
            mClicked = flag;
            break;

        case 1: // '\001'
            if(mClicked && mListener != null)
                mListener.onClickableRectClick();
            mClicked = false;
            break;
        }
        return flag;
    }
    
    public static interface ClickableRectListener {

        void onClickableRectClick();
    }

}
