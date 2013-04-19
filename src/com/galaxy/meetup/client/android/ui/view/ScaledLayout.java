/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ScaledLayout extends RelativeLayout {

	private final Rect mDispSize;
    private final Display mDisplay;
    private MarginMode mMarginMode;
    private float mScaleHeight;
    private float mScaleMarginBottom;
    private float mScaleMarginLeft;
    private float mScaleMarginRight;
    private float mScaleMarginTop;
    private float mScaleWidth;
    
    public ScaledLayout(Context context)
    {
        this(context, null);
    }

    public ScaledLayout(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public ScaledLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mScaleWidth = 1.0F;
        mScaleHeight = 1.0F;
        mScaleMarginLeft = 0.0F;
        mScaleMarginRight = 0.0F;
        mScaleMarginTop = 0.0F;
        mScaleMarginBottom = 0.0F;
        mMarginMode = MarginMode.SCALE_MARGIN_NONE;
        if(attributeset != null)
        {
            TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.ScaledLayout, i, 0);
            if(typedarray != null)
            {
                int j;
                if(typedarray.hasValue(0))
                {
                    float f1 = typedarray.getFloat(0, 1.0F);
                    mScaleWidth = f1;
                    mScaleHeight = f1;
                } else
                {
                    mScaleWidth = typedarray.getFloat(2, 1.0F);
                    mScaleHeight = typedarray.getFloat(1, 1.0F);
                }
                if(typedarray.hasValue(3))
                {
                    float f = typedarray.getFloat(3, 0.0F);
                    mScaleMarginLeft = f;
                    mScaleMarginRight = f;
                    mScaleMarginTop = f;
                    mScaleMarginBottom = f;
                } else
                {
                    mScaleMarginLeft = typedarray.getFloat(6, 0.0F);
                    mScaleMarginRight = typedarray.getFloat(7, 0.0F);
                    mScaleMarginTop = typedarray.getFloat(4, 0.0F);
                    mScaleMarginBottom = typedarray.getFloat(5, 0.0F);
                }
                j = typedarray.getInt(8, 0);
                if(j == 1)
                    mMarginMode = MarginMode.SCALE_MARGIN_INDEPENDENT;
                else
                if(j == 2)
                    mMarginMode = MarginMode.SCALE_MARGIN_LONG_EDGE;
                else
                if(j == 3)
                    mMarginMode = MarginMode.SCALE_MARGIN_SHORT_EDGE;
                else
                    mMarginMode = MarginMode.SCALE_MARGIN_NONE;
                typedarray.recycle();
            }
        }
        mDisplay = ((WindowManager)getContext().getSystemService("window")).getDefaultDisplay();
        mDispSize = new Rect();
    }

    protected void onMeasure(int i, int j)
    {
        android.view.ViewGroup.MarginLayoutParams marginlayoutparams;
        int k = android.view.View.MeasureSpec.getMode(i);
        int l = android.view.View.MeasureSpec.getSize(i);
        int i1 = android.view.View.MeasureSpec.getMode(j);
        int j1 = android.view.View.MeasureSpec.getSize(j);
        marginlayoutparams = (android.view.ViewGroup.MarginLayoutParams)getLayoutParams();
        boolean flag;
        int k1;
        int l1;
        int i2;
        int j2;
        if(mMarginMode == MarginMode.SCALE_MARGIN_INDEPENDENT || mMarginMode == MarginMode.SCALE_MARGIN_LONG_EDGE || mMarginMode == MarginMode.SCALE_MARGIN_SHORT_EDGE)
            flag = true;
        else
            flag = false;
        k1 = l;
        l1 = getMeasuredWidth();
        if(k == 0x80000000 && mScaleWidth > 0.0F)
        {
            int l3 = marginlayoutparams.leftMargin + marginlayoutparams.rightMargin;
            if(flag && l == l1 + l3)
                k1 = l1;
            else
                k1 = (int)((float)(l + l3) * mScaleWidth);
            k = 0x40000000;
        } else
        if(k == 0 && l <= 0)
            k1 = l1;
        i2 = j1;
        j2 = getMeasuredHeight();
        if(i1 == 0x80000000 && mScaleHeight > 0.0F)
        {
            int k3 = marginlayoutparams.topMargin + marginlayoutparams.bottomMargin;
            if(flag && j1 == j2 + k3)
                i2 = j2;
            else
                i2 = (int)((float)(j1 + k3) * mScaleHeight);
            i1 = 0x40000000;
        } else
        if(i1 == 0 && j1 <= 0)
            i2 = j2;
        setMeasuredDimension(k1, i2);
        super.onMeasure(android.view.View.MeasureSpec.makeMeasureSpec(k1, k), android.view.View.MeasureSpec.makeMeasureSpec(i2, i1));
        if(!flag) {
        	return;
        }
        
        int k2;
        int l2;
        int i3;
        int j3;
        android.view.ViewParent viewparent;
        do
            viewparent = getParent();
        while(viewparent != null && !(viewparent instanceof View));

        if(viewparent != null)
        {
            View view = (View)viewparent;
            k2 = view.getMeasuredWidth();
            l2 = view.getMeasuredHeight();
        } else
        {
            mDisplay.getRectSize(mDispSize);
            k2 = mDispSize.width();
            l2 = mDispSize.height();
        }
        if(k2 < l2)
        {
            i3 = k2;
            j3 = l2;
        } else
        {
            i3 = l2;
            j3 = k2;
        }
        
        if(MarginMode.SCALE_MARGIN_INDEPENDENT == mMarginMode) {
        	
        } else if(MarginMode.SCALE_MARGIN_LONG_EDGE == mMarginMode) {
        	marginlayoutparams.topMargin = (int)((float)j3 * mScaleMarginTop);
            marginlayoutparams.bottomMargin = (int)((float)j3 * mScaleMarginBottom);
            marginlayoutparams.leftMargin = (int)((float)j3 * mScaleMarginLeft);
            marginlayoutparams.rightMargin = (int)((float)j3 * mScaleMarginRight);
        } else if (MarginMode.SCALE_MARGIN_SHORT_EDGE == mMarginMode) {
        	 marginlayoutparams.topMargin = (int)((float)i3 * mScaleMarginTop);
             marginlayoutparams.bottomMargin = (int)((float)i3 * mScaleMarginBottom);
             marginlayoutparams.leftMargin = (int)((float)i3 * mScaleMarginLeft);
             marginlayoutparams.rightMargin = (int)((float)i3 * mScaleMarginRight);
        }
        setLayoutParams(marginlayoutparams);
    }

    public void setScale(float f)
    {
        mScaleWidth = f;
        mScaleHeight = f;
    }

    public void setScaleHeight(float f)
    {
        mScaleHeight = f;
    }

    public void setScaleMargin(float f)
    {
        mScaleMarginBottom = f;
        mScaleMarginRight = f;
        mScaleMarginLeft = f;
        mScaleMarginTop = f;
    }

    public void setScaleMarginBottom(float f)
    {
        mScaleMarginBottom = f;
    }

    public void setScaleMarginLeft(float f)
    {
        mScaleMarginLeft = f;
    }

    public void setScaleMarginMode(MarginMode marginmode)
    {
        mMarginMode = marginmode;
    }

    public void setScaleMarginRight(float f)
    {
        mScaleMarginRight = f;
    }

    public void setScaleMarginTop(float f)
    {
        mScaleMarginTop = f;
    }

    public void setScaleWidth(float f)
    {
        mScaleWidth = f;
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	public static enum MarginMode {
		SCALE_MARGIN_NONE,
		SCALE_MARGIN_INDEPENDENT,
		SCALE_MARGIN_LONG_EDGE,
		SCALE_MARGIN_SHORT_EDGE;
	}
}
