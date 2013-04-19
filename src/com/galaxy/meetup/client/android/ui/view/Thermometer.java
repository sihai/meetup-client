/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class Thermometer extends View {

	private static Rect sBounds = new Rect();
    private Drawable mBackground;
    private double mFillLevel;
    private Drawable mForeground;
    private Orientation mOrientation;
    
	public Thermometer(Context context)
    {
        this(context, null);
    }

    public Thermometer(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public Thermometer(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mOrientation = Orientation.HORIZONTAL;
        if(null == attributeset) {
        	return;
        }
        
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Thermometer, i, 0);
        if(typedarray != null)
        {
            if(typedarray.hasValue(0))
                mBackground = typedarray.getDrawable(0);
            if(typedarray.hasValue(1))
                mForeground = typedarray.getDrawable(1);
            int j = typedarray.getInt(2, 0);
            if(j != 0 && j == 1)
                mOrientation = Orientation.VERTICAL;
            else
                mOrientation = Orientation.HORIZONTAL;
            typedarray.recycle();
        }
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(null == mBackground && null == mForeground) {
        	return;
        }
        sBounds.set(0, 0, getWidth(), getHeight());
        canvas.clipRect(sBounds, android.graphics.Region.Op.REPLACE);
        if(mForeground != null)
            mForeground.setBounds(sBounds);
        if(mBackground != null)
        {
            mBackground.setBounds(sBounds);
            mBackground.draw(canvas);
        }
        canvas.save();
        if(mOrientation == Orientation.HORIZONTAL) {
        	int j = (int)((double)sBounds.width() * mFillLevel);
            sBounds.right = j + sBounds.left;
        } else if(mOrientation == Orientation.VERTICAL) {
        	int i = (int)((double)sBounds.height() * mFillLevel);
            sBounds.bottom = i + sBounds.top;
        }
        
        canvas.clipRect(sBounds, android.graphics.Region.Op.REPLACE);
        if(mForeground != null)
            mForeground.draw(canvas);
        canvas.restore();
    }

    protected void onMeasure(int i, int j)
    {
        Drawable drawable = mBackground;
        int k = 0;
        int l = 0;
        if(drawable != null)
        {
            int k1 = mBackground.getIntrinsicWidth();
            l = 0;
            if(k1 > 0)
                l = k1;
            int l1 = mBackground.getIntrinsicHeight();
            k = 0;
            if(l1 > 0)
                k = l1;
        }
        if(mForeground != null)
        {
            int i1 = mForeground.getIntrinsicWidth();
            if(i1 > l)
                l = i1;
            int j1 = mForeground.getIntrinsicHeight();
            if(j1 > k)
                k = j1;
        }
        if(l > 0 && k > 0)
            setMeasuredDimension(resolveSize(l, i), resolveSize(k, j));
        else
            super.onMeasure(i, j);
    }

    public void setBackgroundImage(Drawable drawable)
    {
        mBackground = drawable;
        invalidate();
    }

    public void setFillLevel(double d)
    {
        if(d < 0.0D)
            d = 0.0D;
        if(d > 1.0D)
            d = 1.0D;
        mFillLevel = d;
        invalidate();
    }

    public void setForegroundImage(Drawable drawable)
    {
        mForeground = drawable;
        invalidate();
    }

    public void setOrientation(Orientation orientation)
    {
        mOrientation = orientation;
        invalidate();
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	public static enum Orientation {
		HORIZONTAL,
		VERTICAL;
	}
	
}
