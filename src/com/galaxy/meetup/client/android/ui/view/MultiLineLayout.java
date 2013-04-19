/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author sihai
 *
 */
public class MultiLineLayout extends ViewGroup {

	private int mChipHeight;
    private int mNumLines;
    
    public MultiLineLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mNumLines = 0;
        mChipHeight = 0;
    }

    public final int getHeightForNumLines(int i)
    {
        return getPaddingTop() * (i + 1) + i * mChipHeight;
    }

    public final int getNumLines()
    {
        return mNumLines;
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        (new Rules() {

            protected final void layout(View view, int i1, int j1, int k1, int l1)
            {
                view.layout(i1, j1, i1 + k1, j1 + l1);
            }

        }).apply(k - i);
    }

    protected void onMeasure(final int widthConstraint, final int heightConstraint)
    {
        (new Rules() {

        	private int mRequestedHeight = 0;
            private int mRequestedWidth = 0;
            
            public final void apply(int i)
            {
                super.apply(i);
                mRequestedWidth = mRequestedWidth + getPaddingRight();
                mRequestedHeight = mRequestedHeight + getPaddingBottom();
                setMeasuredDimension(View.resolveSize(mRequestedWidth, widthConstraint), View.resolveSize(mRequestedHeight, heightConstraint));
            }

            protected final void layout(View view, int i, int j, int k, int l)
            {
                mRequestedWidth = Math.max(mRequestedWidth, i + k);
                mRequestedHeight = Math.max(mRequestedHeight, j + l);
            }

            protected final void measure(View view)
            {
                measureChild(view, widthConstraint, heightConstraint);
            }
            
        }).apply(resolveSize(0x7fffffff, widthConstraint));
    }
    
    //==============================================================================
    //							Inner class
    //==============================================================================
    private class Rules
    {

        public void apply(int i)
        {
            int j = getPaddingLeft();
            int k = getPaddingTop();
            int l = 0;
            int i1 = getPaddingLeft();
            int j1 = getPaddingTop();
            int k1 = i - getPaddingLeft() - getPaddingRight();
            int l1 = getChildCount();
            mNumLines = 1;
            mChipHeight = 0;
            for(int i2 = 0; i2 < l1; i2++)
            {
                View view = getChildAt(i2);
                if(view.getVisibility() == 8)
                    continue;
                measure(view);
                int j2 = view.getMeasuredWidth();
                int k2 = view.getMeasuredHeight();
                if(mChipHeight < k2)
                    mChipHeight = k2;
                if(i1 + j2 > k1)
                {
                    i1 = getPaddingLeft();
                    j1 += l + k;
                    l = 0;
                }
                layout(view, i1, j1, j2, k2);
                i1 += j2 + j;
                l = Math.max(l, k2);
            }

        }

        protected void layout(View view, int i, int j, int k, int l)
        {
        }

        protected void measure(View view)
        {
        }
    }

}
