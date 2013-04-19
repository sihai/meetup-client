/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 
 * @author sihai
 *
 */
public class PlusOneAnimatorView extends View implements AnimatorListener {

	private static final Interpolator sAccelerateInterpolator = new AccelerateInterpolator(1.2F);
    private static final Interpolator sDecelerateInterpolator = new DecelerateInterpolator(1.2F);
    private int mAnimStage;
    private ClickableButton mCurrentButton;
    private ClickableButton mNextButton;
    private int mOriginalTranslateY;
    private PlusOneAnimListener mPlusOneAnimListener;
    
    public PlusOneAnimatorView(Context context)
    {
        this(context, null);
    }

    public PlusOneAnimatorView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public PlusOneAnimatorView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }
    
    public void onAnimationCancel(Animator animator)
    {
        if(mAnimStage >= 2)
        {
            mCurrentButton = null;
            mNextButton = null;
            mPlusOneAnimListener.onPlusOneAnimFinished();
        }
    }
    
    public void onAnimationEnd(Animator animator) {
    	
    	if(0 == mAnimStage) {
    		 mCurrentButton = mNextButton;
    		 mNextButton = null;
    	     animate().setDuration(75L).scaleX(2.0F).setInterpolator(sDecelerateInterpolator).setListener(this);
    	     invalidate();
    	     mAnimStage = 1 + mAnimStage;
    	} else if (1 == mAnimStage) {
    		animate().setDuration(270L).translationY(mOriginalTranslateY).scaleX(1.0F).scaleY(1.0F).setInterpolator(sDecelerateInterpolator).setListener(this);
            mAnimStage = 1 + mAnimStage;
    	} else if(2 == mAnimStage) {
    		 mCurrentButton = null;
    	     mPlusOneAnimListener.onPlusOneAnimFinished();
    	     invalidate();
    	}
        
    }

    public void onAnimationRepeat(Animator animator)
    {
    }

    public void onAnimationStart(Animator animator)
    {
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(mCurrentButton != null)
            mCurrentButton.draw(canvas);
    }

    protected void onMeasure(int i, int j)
    {
        if(mCurrentButton != null)
        {
            Rect rect = mCurrentButton.getRect();
            setMeasuredDimension(rect.width(), rect.height());
        } else
        {
            setMeasuredDimension(0, 0);
        }
    }

    public final void startPlusOneAnim(PlusOneAnimListener plusoneanimlistener, ClickableButton clickablebutton, ClickableButton clickablebutton1)
    {
        mAnimStage = 0;
        mPlusOneAnimListener = plusoneanimlistener;
        mCurrentButton = clickablebutton;
        mNextButton = clickablebutton1;
        Rect rect = clickablebutton.getRect();
        setX(rect.left);
        setY(rect.top);
        rect.offsetTo(0, 0);
        clickablebutton1.getRect().offsetTo(0, 0);
        if(android.os.Build.VERSION.SDK_INT >= 12)
        {
            mOriginalTranslateY = (int)getTranslationY();
            int i = 2 * mCurrentButton.getRect().height();
            animate().setDuration(270L).translationY(mOriginalTranslateY - i).scaleX(2.0F).scaleY(2.0F).setInterpolator(sAccelerateInterpolator).setListener(this);
        }
        requestLayout();
    }
	
	public static interface PlusOneAnimListener {

        public abstract void onPlusOneAnimFinished();
    }

}
