/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @author sihai
 *
 */
public class TabContainer extends ScrollableViewGroup {

	private int mFirstVisiblePanel;
    private int mLastVisiblePanel;
    private OnTabChangeListener mListener;
    private int mPanelWidth;
    private int mSelectedPanel;
    
    public TabContainer(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mFirstVisiblePanel = -1;
        mLastVisiblePanel = -1;
        setVertical(false);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        boolean flag1;
        int i1;
        if(mPanelWidth == 0)
            flag1 = true;
        else
            flag1 = false;
        mPanelWidth = k - i;
        i1 = l - j;
        for(int j1 = 0; j1 < getChildCount(); j1++)
        {
            View view = getChildAt(j1);
            int k1 = j1 * mPanelWidth;
            view.layout(k1, 0, k1 + mPanelWidth, i1);
        }

        setScrollLimits(0, mPanelWidth * (-1 + getChildCount()));
        if(flag1)
            scrollTo(mSelectedPanel * mPanelWidth);
    }

    public void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        setMeasuredDimension(k, l);
        int i1 = android.view.View.MeasureSpec.makeMeasureSpec(k, 0x40000000);
        int j1 = android.view.View.MeasureSpec.makeMeasureSpec(l, 0x40000000);
        for(int k1 = 0; k1 < getChildCount(); k1++)
            getChildAt(k1).measure(i1, j1);

    }

    public void onScrollChanged(int i, int j, int k, int l)
    {
        int i1 = i / mPanelWidth;
        int j1;
        int k1;
        if(i % mPanelWidth == 0)
            j1 = 0;
        else
            j1 = 1;
        k1 = i1 + j1;
        if(i1 != mFirstVisiblePanel || k1 != mLastVisiblePanel)
        {
            mFirstVisiblePanel = i1;
            mLastVisiblePanel = k1;
            int l1 = 0;
            while(l1 < getChildCount()) 
            {
                OnTabChangeListener ontabchangelistener = mListener;
                boolean flag;
                if(l1 >= mFirstVisiblePanel && l1 <= mLastVisiblePanel)
                    flag = true;
                else
                    flag = false;
                ontabchangelistener.onTabVisibilityChange(l1, flag);
                l1++;
            }
        }
    }

    protected final void onScrollFinished(int i)
    {
        if(mPanelWidth != 0)
        {
            int j = getScrollX();
            if(i < 0)
                mSelectedPanel = j / mPanelWidth;
            else
                mSelectedPanel = 1 + j / mPanelWidth;
            if(mSelectedPanel >= getChildCount())
                mSelectedPanel = -1 + getChildCount();
            smoothScrollTo(mSelectedPanel * mPanelWidth);
            mListener.onTabSelected(mSelectedPanel);
        }
    }

    public void setOnTabChangeListener(OnTabChangeListener ontabchangelistener)
    {
        mListener = ontabchangelistener;
    }

    public void setSelectedPanel(int i)
    {
        if(mSelectedPanel != i)
        {
            mSelectedPanel = i;
            if(mPanelWidth != 0)
                smoothScrollTo(mPanelWidth * mSelectedPanel);
        }
    }
    
    //==================================================================================================================
    //										Inner class
    //==================================================================================================================
    public static interface OnTabChangeListener {

        public abstract void onTabSelected(int i);

        public abstract void onTabVisibilityChange(int i, boolean flag);
    }

}
