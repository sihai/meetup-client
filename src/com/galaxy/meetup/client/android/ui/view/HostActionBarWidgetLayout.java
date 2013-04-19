/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author sihai
 *
 */
public class HostActionBarWidgetLayout extends ViewGroup {

	private int mMaxWidth;
	
	public HostActionBarWidgetLayout(Context context)
    {
        super(context);
    }

    public HostActionBarWidgetLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        parseAttr(attributeset);
    }

    public HostActionBarWidgetLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        parseAttr(attributeset);
    }

    private void parseAttr(AttributeSet attributeset)
    {
        TypedArray typedarray = getContext().obtainStyledAttributes(attributeset, new int[] {
            0x101011f
        });
        mMaxWidth = typedarray.getDimensionPixelSize(0, 0);
        typedarray.recycle();
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        if(getChildCount() == 1)
        {
            View view = getChildAt(0);
            int i1 = view.getMeasuredHeight();
            int j1 = (l - j - i1) / 2;
            view.layout(0, j1, view.getMeasuredWidth(), j1 + i1);
        }
    }

    protected void onMeasure(int i, int j)
    {
        if(getChildCount() != 1)
        {
            setMeasuredDimension(0, 0);
        } else
        {
            View view = getChildAt(0);
            int k;
            int l;
            int i1;
            if(view.getLayoutParams().height == -2)
                k = android.view.View.MeasureSpec.makeMeasureSpec(android.view.View.MeasureSpec.getSize(j), 0x80000000);
            else
                k = android.view.View.MeasureSpec.makeMeasureSpec(android.view.View.MeasureSpec.getSize(j), 0x40000000);
            l = getLayoutParams().width;
            if(l == -2)
                view.measure(android.view.View.MeasureSpec.makeMeasureSpec(0, 0), k);
            else
                view.measure(android.view.View.MeasureSpec.makeMeasureSpec(l, 0x40000000), k);
            i1 = resolveSize(mMaxWidth, i);
            if(view.getMeasuredWidth() > i1)
                view.measure(android.view.View.MeasureSpec.makeMeasureSpec(i1, 0x40000000), k);
            setMeasuredDimension(view.getMeasuredWidth(), resolveSize(view.getMeasuredHeight(), j));
        }
    }
}
