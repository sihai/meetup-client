/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 
 * @author sihai
 *
 */
public class PhotoViewPager extends ViewPager {

	private float mActivatedX;
    private float mActivatedY;
    private int mActivePointerId;
    private float mLastMotionX;
    private OnInterceptTouchListener mListener;
    
	public PhotoViewPager(Context context)
    {
        super(context);
    }

    public PhotoViewPager(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        boolean flag;
        boolean flag1;
        boolean flag2 = false;
        InterceptType intercepttype;
        int i;
        if(mListener != null)
            intercepttype = mListener.onTouchIntercept(mActivatedX, mActivatedY);
        else
            intercepttype = InterceptType.NONE;
        if(intercepttype == InterceptType.BOTH || intercepttype == InterceptType.LEFT)
            flag = true;
        else
            flag = false;
        if(intercepttype == InterceptType.BOTH || intercepttype == InterceptType.RIGHT)
            flag1 = true;
        else
            flag1 = false;
        i = 0xff & motionevent.getAction();
        if(i == 3 || i == 1)
            mActivePointerId = -1;
        if(0 == i) {
        	mLastMotionX = motionevent.getX();
            mActivatedX = motionevent.getRawX();
            mActivatedY = motionevent.getRawY();
            mActivePointerId = MotionEventCompat.getPointerId(motionevent, 0);
        } else if(2 == i) {
        	if(!flag && !flag1)
        		return flag2;
            int l = mActivePointerId;
            if(l == -1)
            	return flag2;
            float f = MotionEventCompat.getX(motionevent, MotionEventCompat.findPointerIndex(motionevent, l));
            if(flag && flag1)
            {
                mLastMotionX = f;
                flag2 = false;
            } else
            if(flag && f > mLastMotionX)
            {
                mLastMotionX = f;
                flag2 = false;
            } else
            {
                if(!flag1 || f >= mLastMotionX)
                	return flag2;
                mLastMotionX = f;
                flag2 = false;
            }
        } else if(6 == i) {
        	int j = MotionEventCompat.getActionIndex(motionevent);
            if(MotionEventCompat.getPointerId(motionevent, j) == mActivePointerId)
            {
                int k;
                if(j == 0)
                    k = 1;
                else
                    k = 0;
                mLastMotionX = MotionEventCompat.getX(motionevent, k);
                mActivePointerId = MotionEventCompat.getPointerId(motionevent, k);
            }
            flag2 = super.onInterceptTouchEvent(motionevent);
        } else {
        	flag2 = super.onInterceptTouchEvent(motionevent);
        }
        
        return flag2;
        
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener onintercepttouchlistener)
    {
        mListener = onintercepttouchlistener;
    }
	
	public static enum InterceptType {
		NONE,
		LEFT,
		RIGHT,
		BOTH;
	}
	
	public static interface OnInterceptTouchListener {

        public abstract InterceptType onTouchIntercept(float f, float f1);
    }
}
