/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class GridView extends android.widget.GridView {

	private final int attrsArray[] = {
        0x1010114, 0x1010115
    };
    private int mHorizontalSpacing;
    private int mVerticalSpacing;
	    
    public GridView(Context context)
    {
        super(context);
        mHorizontalSpacing = 0;
        mVerticalSpacing = 0;
    }

    public GridView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mHorizontalSpacing = 0;
        mVerticalSpacing = 0;
        init(context, attributeset);
    }

    public GridView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mHorizontalSpacing = 0;
        mVerticalSpacing = 0;
        init(context, attributeset);
    }

    private void init(Context context, AttributeSet attributeset)
    {
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, attrsArray);
        mHorizontalSpacing = typedarray.getDimensionPixelSize(0, 0);
        mVerticalSpacing = typedarray.getDimensionPixelSize(1, 0);
        typedarray.recycle();
    }

    protected void onMeasure(int i, int j)
    {
        Resources resources = getContext().getResources();
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = resources.getDimensionPixelSize(R.dimen.medium_avatar_dimension) + 2 * resources.getDimensionPixelSize(R.dimen.medium_avatar_selected_padding);
        int i1 = (k + mHorizontalSpacing) / (l + mHorizontalSpacing);
        int j1 = i1 * (l + mHorizontalSpacing) - mHorizontalSpacing;
        if(i1 > 0 && getLayoutParams().height == -2)
        {
            int k1 = getCount();
            int l1 = resources.getDimensionPixelSize(R.dimen.medium_avatar_selected_dimension) + resources.getDimensionPixelSize(R.dimen.medium_avatar_name_height);
            int i2 = (-1 + (k1 + i1)) / i1;
            int j2 = l1 * i2 + getPaddingTop() + getPaddingBottom() + mVerticalSpacing * (i2 - 1);
            super.onMeasure(android.view.View.MeasureSpec.makeMeasureSpec(j1, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(j2, 0x40000000));
        } else
        {
            super.onMeasure(android.view.View.MeasureSpec.makeMeasureSpec(j1, 0x40000000), j);
        }
    } 
}
