/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * 
 * @author sihai
 *
 */
public class PhotoOneUpAnimationController {

	 private final boolean mAdjustMargins;
	 private float mCurrentOffset;
	 private final boolean mSlideFromTop;
	 private final android.view.animation.Animation.AnimationListener mSlideInListener = new android.view.animation.Animation.AnimationListener() {

        public final void onAnimationEnd(Animation animation) {
        	if(1 == mState) {
        		mState = 2;
                mCurrentOffset = 0.0F;
        	} else if(2 == mState) {
        		
        	} else if(3 == mState) {
        		mState = 0;
                mCurrentOffset = (float)getHideOffset(mSlideFromTop);
        	}
        	updateVisibility();
            return;
        }

        public final void onAnimationRepeat(Animation animation)
        {
        }

        public final void onAnimationStart(Animation animation) {
        	if(0 == mState) {
        		 mState = 1;
        	} else if(1 == mState) {
        		
        	} else if(2 == mState) {
        		mState = 3;
        	}
        	
        	updateVisibility();
            return;
        }
    };
    private int mState;
    private final View mView;
	    
	public PhotoOneUpAnimationController(View view, boolean flag, boolean flag1)
    {
        mState = 2;
        mView = view;
        mSlideFromTop = flag;
        mAdjustMargins = flag1;
    }

    private void startAnimation(float f, int i)
    {
        Animation animation = mView.getAnimation();
        if(animation != null)
            animation.cancel();
        TranslateAnimation translateanimation = new TranslateAnimation(0.0F, 0.0F, mCurrentOffset, f);
        translateanimation.setDuration(100L);
        translateanimation.setFillAfter(true);
        translateanimation.setAnimationListener(mSlideInListener);
        mView.startAnimation(translateanimation);
    }

    private void updateVisibility() {
            boolean flag;
            View view;
            int j;
            View view1;
            boolean flag1;
            if(mState == 0)
            {
                flag = false;
            } else
            {
                int i = getHideOffset(mSlideFromTop);
                if(!mSlideFromTop && mView.getHeight() > 0 && mCurrentOffset >= (float)i || mSlideFromTop && mView.getHeight() > 0 && mCurrentOffset <= (float)i)
                    flag = false;
                else
                    flag = true;
            }
            view = mView;
            if(flag || mAdjustMargins)
                j = 0;
            else
                j = 8;
            view.setVisibility(j);
            view1 = mView;
            if(!flag)
            {
                boolean flag2 = mAdjustMargins;
                if(flag2)
                	view1.setClickable(true);;
            }
    }

    public final void animate(boolean flag) {
        if(mState != 0 && mState != 3 || flag) {
        	if((mState == 2 || mState == 1) && flag)
            {
                int i = getHideOffset(mSlideFromTop);
                startAnimation(i, 100);
                mCurrentOffset = i;
            }
        } else { 
        	startAnimation(0.0F, 100);
        }
        
        updateVisibility();
        return;
    }

    protected int getHideOffset(boolean flag)
    {
        int i;
        if(mSlideFromTop)
            i = -(mView.getHeight() + mView.getPaddingTop());
        else
            i = mView.getHeight() + mView.getPaddingBottom();
        return i;
    }
}
