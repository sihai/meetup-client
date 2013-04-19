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
public class CircleNameAndCountView extends ViewGroup {

	private View mCountTextView;
    private View mIconView;
    private View mNameTextView;
    
    public CircleNameAndCountView(Context context)
    {
        super(context);
    }

    public CircleNameAndCountView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public CircleNameAndCountView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        mNameTextView = findViewById(0x1020014);
        mCountTextView = findViewById(0x1020015);
        mIconView = findViewById(0x1020006);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = mNameTextView.getMeasuredWidth();
        int j1 = mNameTextView.getMeasuredHeight();
        int k1 = (l - j - j1) / 2;
        int l1 = getPaddingLeft();
        mNameTextView.layout(l1, k1, l1 + i1, k1 + j1);
        if(mCountTextView.getVisibility() == 0)
            mCountTextView.layout(l1 + i1, k1, l1 + i1 + mCountTextView.getMeasuredWidth(), k1 + mCountTextView.getMeasuredHeight());
        if(mIconView.getVisibility() == 0)
            mIconView.layout(k - mIconView.getLayoutParams().width, 0, k - i, l - j);
    }

    protected void onMeasure(int i, int j)
    {
        int k;
        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int k2;
        int l2;
        int i3;
        k = resolveSize(0, i);
        l = resolveSize(0, j);
        boolean flag;
        boolean flag1;
        int j2;
        if(mCountTextView.getVisibility() == 0)
            flag = true;
        else
            flag = false;
        if(mIconView.getVisibility() == 0)
            flag1 = true;
        else
            flag1 = false;
        mNameTextView.measure(0, 0);
        i1 = mNameTextView.getMeasuredWidth();
        j1 = mNameTextView.getMeasuredHeight();
        k1 = 0;
        l1 = 0;
        if(flag)
        {
            mCountTextView.measure(0, 0);
            l1 = mCountTextView.getMeasuredWidth();
            k1 = mCountTextView.getMeasuredHeight();
        }
        i2 = 0;
        if(flag1)
            i2 = mIconView.getLayoutParams().width;
        j2 = i1 + l1;
        k2 = getPaddingLeft();
        l2 = getPaddingRight();
        i3 = l2 + (j2 + k2);
        int mode = android.view.View.MeasureSpec.getMode(i);
        if(-2147483648 == mode) {
        	if(k == 0 || i3 + i2 < k)
                k = i3 + i2;
        } else if(0 == mode) {
        	k = i3 + i2;
        } else  if(1073741824 == mode) {
        	i1 = Math.min(i1, Math.max(k - k2 - l2 - l1 - i2, 0));
        }
        
        mNameTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(i1, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(j1, 0x40000000));
        if(flag)
            mCountTextView.measure(android.view.View.MeasureSpec.makeMeasureSpec(l1, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(k1, 0x40000000));
        
        mode = android.view.View.MeasureSpec.getMode(j);
        if(-2147483648 == mode || 0 == mode) {
        	l = Math.max(j1, k1) + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(k, l);
        return;
    }
}
