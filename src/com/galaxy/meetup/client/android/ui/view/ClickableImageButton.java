/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ClickableImageButton implements ClickableItem {

	private static Paint sImageSelectedPaint;
    private boolean mClicked;
    private Bitmap mClickedBitmap;
    private CharSequence mContentDescription;
    private Bitmap mDefaultBitmap;
    private ClickableImageButtonListener mListener;
    private Rect mRect;

    public ClickableImageButton(Context context, Bitmap bitmap, Bitmap bitmap1, ClickableImageButtonListener clickableimagebuttonlistener, CharSequence charsequence)
    {
        mDefaultBitmap = bitmap;
        if(mRect != null)
            mRect = new Rect(mRect.left, mRect.top, mRect.left + mDefaultBitmap.getWidth(), mRect.top + mDefaultBitmap.getHeight());
        mClickedBitmap = null;
        mListener = clickableimagebuttonlistener;
        mContentDescription = charsequence;
        if(sImageSelectedPaint == null)
        {
            Paint paint = new Paint();
            sImageSelectedPaint = paint;
            paint.setStrokeWidth(4F);
            sImageSelectedPaint.setColor(context.getApplicationContext().getResources().getColor(R.color.image_selected_stroke));
            sImageSelectedPaint.setStyle(android.graphics.Paint.Style.STROKE);
        }
    }

    public final int compare(ClickableItem obj, ClickableItem obj1)
    {
        ClickableItem clickableitem = (ClickableItem)obj;
        ClickableItem clickableitem1 = (ClickableItem)obj1;
        return sComparator.compare(clickableitem, clickableitem1);
    }

    public final void draw(Canvas canvas)
    {
        boolean flag;
        Bitmap bitmap;
        if(mClicked && mClickedBitmap == null && mListener != null)
            flag = true;
        else
            flag = false;
        if(mClicked && mClickedBitmap != null && mListener != null)
            bitmap = mClickedBitmap;
        else
            bitmap = mDefaultBitmap;
        canvas.drawBitmap(bitmap, null, mRect, null);
        if(flag)
            canvas.drawRect(2 + mRect.left, 2 + mRect.top, -2 + mRect.right, -2 + mRect.bottom, sImageSelectedPaint);
    }

    public final CharSequence getContentDescription()
    {
        return mContentDescription;
    }

    public final Rect getRect()
    {
        return mRect;
    }

    public final boolean handleEvent(int i, int j, int k) {
        boolean flag = true;
        if(3 == k) {
        	return false;
        }
        if(!mRect.contains(i, j)) {
        	if(k == 1)
                mClicked = false;
        	return false;
        }
        switch(k)
        {
        case 0: // '\0'
            mClicked = flag;
            break;

        case 1: // '\001'
            if(mClicked && mListener != null)
                mListener.onClickableImageButtonClick(this);
            mClicked = false;
            break;
        }
        return flag;
        
    }

    public final void setPosition(int i, int j)
    {
        mRect = new Rect(i, j, i + mDefaultBitmap.getWidth(), j + mDefaultBitmap.getHeight());
    }
    
    public static interface ClickableImageButtonListener
    {

        public abstract void onClickableImageButtonClick(ClickableImageButton clickableimagebutton);
    }

}
