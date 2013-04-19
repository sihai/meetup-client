/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 
 * @author sihai
 *
 */
public class LinearLayoutWithLayoutNotifications extends LinearLayout {

	private LayoutListener mLayoutListener;
    private int mMaxWidth;
    
	public LinearLayoutWithLayoutNotifications(Context context)
    {
        super(context);
        mMaxWidth = -1;
    }

    public LinearLayoutWithLayoutNotifications(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mMaxWidth = -1;
    }

    public LinearLayoutWithLayoutNotifications(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mMaxWidth = -1;
    }

    public void onLayout(boolean flag, int i, int j, int k, int l)
    {
        super.onLayout(flag, i, j, k, l);
        if(mLayoutListener != null)
        {
            LayoutListener _tmp = mLayoutListener;
        }
    }

    public void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        if(mMaxWidth > 0 && getMeasuredWidth() > mMaxWidth)
            super.onMeasure(android.view.View.MeasureSpec.makeMeasureSpec(mMaxWidth, 0x40000000), j);
        if(mLayoutListener != null)
            mLayoutListener.onMeasured(this);
    }

    protected void onSizeChanged(int i, int j, int k, int l)
    {
        super.onSizeChanged(i, j, k, l);
        if(mLayoutListener != null)
        {
            LayoutListener _tmp = mLayoutListener;
        }
    }

    public void setLayoutListener(LayoutListener layoutlistener)
    {
        mLayoutListener = layoutlistener;
    }

    public void setMaxWidth(int i)
    {
        mMaxWidth = i;
    }


    
    public static interface LayoutListener {

        public abstract void onMeasured(View view);
    }

}
