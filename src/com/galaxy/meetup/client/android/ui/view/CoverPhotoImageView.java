/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;

/**
 * 
 * @author sihai
 *
 */
public class CoverPhotoImageView extends ImageResourceView {

	private Matrix mCoverPhotoMatrix;
    private int mLayoutWidth;
    private int mOffset;
    private int mRequiredWidth;
    
    public CoverPhotoImageView(Context context)
    {
        super(context);
        setSizeCategory(0);
        setScaleMode(2);
    }

    public CoverPhotoImageView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        setSizeCategory(0);
        setScaleMode(2);
    }

    public CoverPhotoImageView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        setSizeCategory(0);
        setScaleMode(2);
    }

    protected void onDraw(Canvas canvas) {
    	if(!hasBitmap() || mCoverPhotoMatrix != null) {
    	} else {
    		int j;
	        int k;
	        int i = getBitmap().getWidth();
	        mCoverPhotoMatrix = new Matrix();
	        mCoverPhotoMatrix.reset();
	        float f = 1.0F;
	        if(mLayoutWidth > mRequiredWidth)
	        {
	            f = (float)mLayoutWidth / (float)i;
	            j = mLayoutWidth;
	        } else
	        {
	            if(mRequiredWidth > i)
	                f = (float)mRequiredWidth / (float)i;
	            j = mRequiredWidth;
	        }
	        mCoverPhotoMatrix.postScale(f, f);
	        k = mOffset;
	        if(j >= 940) {
	        	if(j > 940)
	                k = Math.round(((float)j / 940F) * (float)mOffset);
	        } else {
	        	k = Math.round(((float)mRequiredWidth / 940F) * (float)mOffset);
	        }
	        mCoverPhotoMatrix.postTranslate(0.0F, k);
	        setImageMatrix(mCoverPhotoMatrix);
    	}
    	
    	super.onDraw(canvas);
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        mLayoutWidth = getMeasuredWidth();
        int k = getMeasuredHeight();
        if(mLayoutWidth != 0 && k != 0)
        {
            int l = mLayoutWidth;
            mRequiredWidth = Math.round(5.222222F * (float)k);
            if(mRequiredWidth > l)
                l = mRequiredWidth;
            if(l > 940)
                l = 940;
            setCustomImageSize(l, 0);
            mCoverPhotoMatrix = null;
        }
    }

    protected final void onUnbindResources()
    {
        super.onUnbindResources();
        mCoverPhotoMatrix = null;
    }

    public void setTopOffset(int i)
    {
        if(mOffset != i)
        {
            mOffset = i;
            mCoverPhotoMatrix = null;
            invalidate();
        }
    }
}
