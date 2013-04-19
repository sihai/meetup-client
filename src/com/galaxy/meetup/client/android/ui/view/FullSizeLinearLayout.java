/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 
 * @author sihai
 *
 */
public class FullSizeLinearLayout extends LinearLayout {

	private int mMaxHeight;
	
	public FullSizeLinearLayout(Context context)
    {
        this(context, null);
        init(context, null);
    }

    public FullSizeLinearLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(context, attributeset);
    }

    public FullSizeLinearLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(context, attributeset);
    }

    private void init(Context context, AttributeSet attributeset)
    {
        mMaxHeight = 0x7fffffff;
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, new int[] {
            0x1010120
        });
        mMaxHeight = typedarray.getDimensionPixelSize(0, 0);
        typedarray.recycle();
    }

    protected final void onMeasure(int i, int j)
    {
        if(mMaxHeight > 0)
        {
            if(android.view.View.MeasureSpec.getMode(i) == 0x80000000)
                i = android.view.View.MeasureSpec.makeMeasureSpec(android.view.View.MeasureSpec.getSize(i), 0x40000000);
            if(android.view.View.MeasureSpec.getMode(j) == 0x80000000)
                j = android.view.View.MeasureSpec.makeMeasureSpec(Math.min(android.view.View.MeasureSpec.getSize(j), mMaxHeight), 0x40000000);
        } else
        {
            i = android.view.View.MeasureSpec.makeMeasureSpec(android.view.View.MeasureSpec.getSize(i), 0x40000000);
            j = android.view.View.MeasureSpec.makeMeasureSpec(android.view.View.MeasureSpec.getSize(j), 0x40000000);
        }
        super.onMeasure(i, j);
    }
}
