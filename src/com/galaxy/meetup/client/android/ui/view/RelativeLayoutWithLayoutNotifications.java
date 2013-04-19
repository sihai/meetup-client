/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 
 * @author sihai
 *
 */
public class RelativeLayoutWithLayoutNotifications extends RelativeLayout {

	private LayoutListener layoutListener;
	
	public static interface LayoutListener
    {
    }


    public RelativeLayoutWithLayoutNotifications(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public void onLayout(boolean flag, int i, int j, int k, int l)
    {
        if(layoutListener != null)
        {
            LayoutListener _tmp = layoutListener;
        }
        super.onLayout(flag, i, j, k, l);
    }

    public void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i) - (getPaddingLeft() + getPaddingRight());
        int l = android.view.View.MeasureSpec.getSize(j) - (getPaddingTop() + getPaddingBottom());
        if(layoutListener != null)
        {
            LayoutListener _tmp = layoutListener;
        }
        onMeasure(k, l);
        super.onMeasure(i, j);
    }

    protected void onSizeChanged(int i, int j, int k, int l)
    {
        if(layoutListener != null)
        {
            LayoutListener _tmp = layoutListener;
        }
        super.onSizeChanged(i, j, k, l);
    }

    public void setLayoutListener(LayoutListener layoutlistener)
    {
        layoutListener = layoutlistener;
    }
}
