/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.common.Recyclable;

/**
 * 
 * @author sihai
 *
 */
public class ExactLayout extends ViewGroup implements Recyclable {

	private Drawable mBackground;
	
	public ExactLayout(Context context)
    {
        super(context);
    }

    public ExactLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public ExactLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public static int getMaxHeight(View aview[])
    {
        int i = 0;
        for(int j = Math.max(-1 + aview.length, 0); j >= 0; j--)
        {
            View view = aview[j];
            if(view != null)
                i = Math.max(i, view.getMeasuredHeight());
        }

        return i;
    }

    public static void measure(View view, int i, int j, int k, int l)
    {
        int i1 = Math.max(i, 0);
        int j1 = Math.max(k, 0);
        view.measure(android.view.View.MeasureSpec.makeMeasureSpec(i1, j), android.view.View.MeasureSpec.makeMeasureSpec(j1, l));
    }

    public static void setCenterBounds(View view, int i, int j)
    {
        LayoutParams layoutparams = (LayoutParams)view.getLayoutParams();
        layoutparams.verticalBound = j;
        layoutparams.horizontalBound = i;
        view.setLayoutParams(layoutparams);
    }

    public static void setCorner(View view, int i, int j)
    {
        LayoutParams layoutparams = (LayoutParams)view.getLayoutParams();
        if(layoutparams == null)
        {
            layoutparams = new LayoutParams(i, j);
        } else
        {
            layoutparams.x = i;
            layoutparams.y = j;
        }
        view.setLayoutParams(layoutparams);
    }

    public static void verticallyCenter(int i, View aview[])
    {
        int j = Math.max(-1 + aview.length, 0);
        int k = 0x7fffffff;
        for(int l = j; l >= 0; l--)
        {
            View view1 = aview[l];
            if(view1 != null)
                k = Math.min(k, ((LayoutParams)view1.getLayoutParams()).y);
        }

        for(int i1 = j; i1 >= 0; i1--)
        {
            View view = aview[i1];
            if(view != null)
            {
                LayoutParams layoutparams = (LayoutParams)view.getLayoutParams();
                setCorner(view, layoutparams.x, k);
                setCenterBounds(view, layoutparams.horizontalBound, i);
            }
        }

    }

    public final void addPadding(int i, int j, int k, int l)
    {
        setPadding(i + getPaddingLeft(), j + getPaddingTop(), k + getPaddingRight(), l + getPaddingBottom());
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutparams)
    {
        return layoutparams instanceof LayoutParams;
    }

    protected void dispatchDraw(Canvas canvas)
    {
        if(mBackground != null)
        {
            mBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            mBackground.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeset)
    {
        return new LayoutParams(getContext(), attributeset);
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutparams)
    {
        return new LayoutParams(layoutparams);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = getChildCount();
        for(int j1 = 0; j1 < i1; j1++)
        {
            View view = getChildAt(j1);
            if(view.getVisibility() != 8)
            {
                LayoutParams layoutparams = (LayoutParams)view.getLayoutParams();
                int k1 = view.getMeasuredWidth();
                int l1 = view.getMeasuredHeight();
                int i2 = getPaddingLeft() + Math.max(0, (layoutparams.horizontalBound - k1) / 2);
                int j2 = getPaddingTop() + Math.max(0, (layoutparams.verticalBound - l1) / 2);
                view.layout(i2 + layoutparams.x, j2 + layoutparams.y, k1 + (i2 + layoutparams.x), j2 + (l1 + layoutparams.y));
            }
        }

    }

    protected void onMeasure(int i, int j)
    {
        int k = 0;
        int l = 0;
        int i1 = getPaddingLeft() + getPaddingRight();
        int j1 = getPaddingTop() + getPaddingBottom();
        int k1 = android.view.View.MeasureSpec.getMode(i);
        int l1 = android.view.View.MeasureSpec.getMode(j);
        int i2 = android.view.View.MeasureSpec.getSize(i);
        int j2 = android.view.View.MeasureSpec.getSize(j);
        measureChildren(android.view.View.MeasureSpec.makeMeasureSpec(i2 - i1, k1), android.view.View.MeasureSpec.makeMeasureSpec(j2 - j1, l1));
        int k2 = getChildCount();
        for(int l2 = 0; l2 < k2; l2++)
        {
            View view = getChildAt(l2);
            if(view.getVisibility() != 8)
            {
                LayoutParams layoutparams = (LayoutParams)view.getLayoutParams();
                int i3 = layoutparams.x + view.getMeasuredWidth();
                int j3 = layoutparams.y + view.getMeasuredHeight();
                l = Math.max(l, i3);
                k = Math.max(k, j3);
            }
        }

        setMeasuredDimension(resolveSize(l + i1, i), resolveSize(k + j1, j));
    }

    public void onRecycle()
    {
        int i = getChildCount();
        for(int j = 0; j < i; j++)
        {
            View view = getChildAt(j);
            if(view instanceof Recyclable)
                ((Recyclable)view).onRecycle();
        }

    }

    public void setBackground(Drawable drawable)
    {
        mBackground = drawable;
    }
    
    public static class LayoutParams extends android.view.ViewGroup.LayoutParams
    {

        public int horizontalBound;
        public int verticalBound;
        public int x;
        public int y;

        public LayoutParams(int i, int j)
        {
            super(i, j);
        }

        public LayoutParams(Context context, AttributeSet attributeset)
        {
            super(context, attributeset);
            TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.ExactLayout_Layout);
            x = typedarray.getDimensionPixelOffset(0, 0);
            y = typedarray.getDimensionPixelOffset(1, 0);
            horizontalBound = typedarray.getDimensionPixelOffset(2, 0);
            verticalBound = typedarray.getDimensionPixelOffset(3, 0);
            typedarray.recycle();
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutparams)
        {
            super(layoutparams);
        }
    }

}
