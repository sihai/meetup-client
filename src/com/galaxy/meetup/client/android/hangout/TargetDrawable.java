/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * 
 * @author sihai
 *
 */
public class TargetDrawable {

	public static final int STATE_ACTIVE[] = {
        0x101009e, 0x10100a2
    };
    public static final int STATE_FOCUSED[] = {
        0x101009e, 0x101009c
    };
    public static final int STATE_INACTIVE[] = {
        0x101009e
    };
    private float mAlpha;
    private Drawable mDrawable;
    private float mScaleX;
    private float mScaleY;
    private float mTranslationX;
    private float mTranslationY;
    
	public TargetDrawable(Drawable drawable)
    {
        mTranslationX = 0.0F;
        mTranslationY = 0.0F;
        mScaleX = 1.0F;
        mScaleY = 1.0F;
        mAlpha = 1.0F;
        mScaleX = getScaleX();
        mScaleY = getScaleY();
        mTranslationX = getX();
        mTranslationY = getY();
        mAlpha = getAlpha();
        Drawable drawable1;
        if(drawable != null)
            drawable1 = drawable.mutate();
        else
            drawable1 = null;
        mDrawable = drawable1;
        if(mDrawable != null)
            mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        setState(STATE_INACTIVE);
    }

    public final void draw(Canvas canvas)
    {
        if(mDrawable != null)
        {
            canvas.save(1);
            canvas.translate(mTranslationX, mTranslationY);
            canvas.scale(mScaleX, mScaleY);
            canvas.translate(-0.5F * (float)getWidth(), -0.5F * (float)getHeight());
            mDrawable.setAlpha(Math.round(255F * mAlpha));
            mDrawable.draw(canvas);
            canvas.restore();
        }
    }

    public float getAlpha()
    {
        return mAlpha;
    }

    public int getHeight()
    {
        int i;
        if(mDrawable != null)
            i = mDrawable.getIntrinsicHeight();
        else
            i = 0;
        return i;
    }

    public float getScaleX()
    {
        return mScaleX;
    }

    public float getScaleY()
    {
        return mScaleY;
    }

    public int getWidth()
    {
        int i;
        if(mDrawable != null)
            i = mDrawable.getIntrinsicWidth();
        else
            i = 0;
        return i;
    }

    public float getX()
    {
        return mTranslationX;
    }

    public float getY()
    {
        return mTranslationY;
    }

    public final boolean hasState()
    {
        if(!(mDrawable instanceof StateListDrawable));
        return false;
    }

    public final boolean isValid()
    {
        boolean flag;
        if(mDrawable != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void setAlpha(float f)
    {
        mAlpha = f;
    }

    public final void setState(int ai[])
    {
        if(mDrawable instanceof StateListDrawable)
        {
            StateListDrawable statelistdrawable = (StateListDrawable)mDrawable;
            statelistdrawable.setState(ai);
            statelistdrawable.invalidateSelf();
        }
    }

    public final void setX(float f)
    {
        mTranslationX = f;
    }

    public final void setY(float f)
    {
        mTranslationY = f;
    }

}
