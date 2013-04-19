/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author sihai
 *
 */
public class OneUpTouchHandler extends ViewGroup {

	private View mActionBar;
    private View mBackground;
    private int mLocation[];
    private View mScrollView;
    private View mTagView;
    private View mTargetView;
    
    public OneUpTouchHandler(Context context)
    {
        super(context);
        mLocation = new int[2];
    }

    public OneUpTouchHandler(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mLocation = new int[2];
    }

    public OneUpTouchHandler(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mLocation = new int[2];
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent)
    {
        float f = motionevent.getRawX();
        float f1 = motionevent.getRawY();
        if(mTargetView == null && mScrollView != null && mScrollView.getVisibility() == 0)
        {
            View view1 = mScrollView.findViewById(0x102000a);
            view1.getLocationOnScreen(mLocation);
            if(f >= (float)mLocation[0] && f < (float)(mLocation[0] + view1.getWidth()) && f1 >= (float)mLocation[1] && f1 < (float)(mLocation[1] + view1.getHeight()))
                mTargetView = mScrollView;
        }
        if(mTargetView == null && mTagView != null && mTagView.getVisibility() == 0)
        {
            mTagView.getLocationOnScreen(mLocation);
            if(f >= (float)mLocation[0] && f < (float)(mLocation[0] + mTagView.getWidth()) && f1 >= (float)mLocation[1] && f1 < (float)(mLocation[1] + mTagView.getHeight()))
                mTargetView = mTagView;
        }
        if(mTargetView == null && mActionBar != null && mActionBar.getVisibility() == 0)
        {
            mActionBar.getLocationOnScreen(mLocation);
            if(f >= (float)mLocation[0] && f < (float)(mLocation[0] + mActionBar.getWidth()) && f1 >= (float)mLocation[1] && f1 < (float)(mLocation[1] + mActionBar.getHeight()))
                mTargetView = mActionBar;
        }
        if(mTargetView == null && mBackground != null)
            mTargetView = mBackground;
        if(mTargetView != null)
        {
            View view = mTargetView;
            int i = motionevent.getAction();
            int j;
            if(i == 3)
            {
                motionevent.setAction(3);
                view.dispatchTouchEvent(motionevent);
                motionevent.setAction(i);
            } else
            {
                MotionEvent motionevent1 = MotionEvent.obtain(motionevent);
                motionevent1.offsetLocation(getScrollX() - view.getLeft(), getScrollY() - view.getTop());
                view.dispatchTouchEvent(motionevent1);
                motionevent1.recycle();
            }
            j = motionevent.getAction();
            if(j == 3 || j == 1)
                mTargetView = null;
        }
        return true;
    }

    public final View getTargetView()
    {
        return mTargetView;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        return true;
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        return true;
    }

    public void setActionBar(View view)
    {
        mActionBar = view;
    }

    public void setBackground(View view)
    {
        mBackground = view;
    }

    public void setScrollView(View view)
    {
        mScrollView = view;
    }

    public void setTagLayout(View view)
    {
        mTagView = view;
    }

}
