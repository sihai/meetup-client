/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 
 * @author sihai
 *
 */
public class TwoPointerGestureDetector extends SimpleOnGestureListener
		implements OnTouchListener {

	private long mEndTime;
    private final GestureDetector mGestureDetector;
    private android.view.MotionEvent.PointerCoords mPointerEnd[];
    private android.view.MotionEvent.PointerCoords mPointerStart[];
    private long mStartTime;
    private TwoPointerSwipeState mTwoSwipeState;
    
    public TwoPointerGestureDetector(Context context)
    {
        mTwoSwipeState = TwoPointerSwipeState.INITIAL;
        android.view.MotionEvent.PointerCoords apointercoords[] = new android.view.MotionEvent.PointerCoords[2];
        apointercoords[0] = new android.view.MotionEvent.PointerCoords();
        apointercoords[1] = new android.view.MotionEvent.PointerCoords();
        mPointerStart = apointercoords;
        android.view.MotionEvent.PointerCoords apointercoords1[] = new android.view.MotionEvent.PointerCoords[2];
        apointercoords1[0] = new android.view.MotionEvent.PointerCoords();
        apointercoords1[1] = new android.view.MotionEvent.PointerCoords();
        mPointerEnd = apointercoords1;
        mGestureDetector = new GestureDetector(context, this);
    }

    private static boolean verifyPointerDistance(android.view.MotionEvent.PointerCoords apointercoords[])
    {
        boolean flag = true;
        float f = Math.abs(apointercoords[0].x - apointercoords[1].x);
        float f1 = Math.abs(apointercoords[0].y - apointercoords[1].y);
        if(f > 100F || f1 > 250F)
            flag = false;
        return flag;
    }

    public boolean onDown(MotionEvent motionevent)
    {
        return true;
    }

    public boolean onTouch(View view, MotionEvent motionevent)
    {
        int i = motionevent.getActionMasked();
        int j = motionevent.getPointerCount();
        switch(mTwoSwipeState) {
        case INITIAL:
        	if(i == 0 && j == 1)
                mTwoSwipeState = TwoPointerSwipeState.ONE_DOWN;
        	break;
        case ONE_DOWN:
        	if(i == 5)
            {
                if(j == 2)
                {
                    mTwoSwipeState = TwoPointerSwipeState.TWO_DOWN;
                    motionevent.getPointerCoords(0, mPointerStart[0]);
                    motionevent.getPointerCoords(1, mPointerStart[1]);
                    mStartTime = motionevent.getEventTime();
                    if(verifyPointerDistance(mPointerStart))
                        mTwoSwipeState = TwoPointerSwipeState.TWO_DOWN;
                    else
                        mTwoSwipeState = TwoPointerSwipeState.INITIAL;
                } else
                {
                    mTwoSwipeState = TwoPointerSwipeState.INITIAL;
                }
            } else
            if(i == 1 || i == 6)
                mTwoSwipeState = TwoPointerSwipeState.INITIAL;
        	break;
        case TWO_DOWN:
        	if(j != 2)
                mTwoSwipeState = TwoPointerSwipeState.INITIAL;
            else
            if(i == 6)
            {
                motionevent.getPointerCoords(0, mPointerEnd[0]);
                motionevent.getPointerCoords(1, mPointerEnd[1]);
                mEndTime = motionevent.getEventTime();
                if(verifyPointerDistance(mPointerEnd))
                    mTwoSwipeState = TwoPointerSwipeState.ONE_UP;
                else
                    mTwoSwipeState = TwoPointerSwipeState.INITIAL;
            }
        	break;
        case ONE_UP:
        	if(j != 1)
                mTwoSwipeState = TwoPointerSwipeState.INITIAL;
            else
            if(i == 5 || i == 0)
                mTwoSwipeState = TwoPointerSwipeState.INITIAL;
            else
            if(i == 1)
                if(motionevent.getEventTime() - mEndTime > 100L)
                {
                    mTwoSwipeState = TwoPointerSwipeState.INITIAL;
                } else
                {
                    mTwoSwipeState = TwoPointerSwipeState.INITIAL;
                    long l = mEndTime - mStartTime;
                    if(l > 0L && l <= 500L)
                    {
                        float f = (1000F * (mPointerEnd[0].x - mPointerStart[0].x)) / (float)l;
                        float f1 = (1000F * (mPointerEnd[0].y - mPointerStart[0].y)) / (float)l;
                        onTwoPointerSwipe(mPointerStart[0], mPointerEnd[0], f, f1);
                    }
                }
        	break;
        default:
        	break;
        }
        
        return mGestureDetector.onTouchEvent(motionevent);

    }

    public boolean onTwoPointerSwipe(android.view.MotionEvent.PointerCoords pointercoords, android.view.MotionEvent.PointerCoords pointercoords1, float f, float f1)
    {
        return true;
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private static enum TwoPointerSwipeState {
		INITIAL,
		ONE_DOWN,
		TWO_DOWN,
		ONE_UP;
	}
}
