/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public abstract class ScrollableViewGroup extends ViewGroup {

	private static final Interpolator sInterpolator = new Interpolator() {

		public final float getInterpolation(float f) {
			float f1 = f - 1.0F;
			return 1.0F + f1 * (f1 * (f1 * (f1 * f1)));
		}

	};

	private float mFlingVelocity;
    private boolean mFlingable;
    private boolean mIsBeingDragged;
    private float mLastPosition[] = {
        0.0F, 0.0F
    };
    private final int mLimits[] = {
        0x80000001, 0x7fffffff
    };
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private boolean mReceivedDown;
    private int mScrollDirection;
    private boolean mScrollEnabled;
    protected Scroller mScroller;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private boolean mVertical;
    
    //===========================================================================
    //						Constructor
    //===========================================================================
    public ScrollableViewGroup(Context context) {
        super(context);
        mFlingVelocity = 0.0F;
        mScrollDirection = 0;
        mVertical = true;
        mFlingable = true;
        mIsBeingDragged = false;
        mScrollEnabled = true;
        mReceivedDown = false;
        Context context1 = getContext();
        setFocusable(false);
        ViewConfiguration viewconfiguration = ViewConfiguration.get(context1);
        mTouchSlop = viewconfiguration.getScaledTouchSlop();
        mMinimumVelocity = viewconfiguration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = viewconfiguration.getScaledMaximumFlingVelocity();
        mScroller = new Scroller(context1, sInterpolator);
    }

    public ScrollableViewGroup(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        mFlingVelocity = 0.0F;
        mScrollDirection = 0;
        mVertical = true;
        mFlingable = true;
        mIsBeingDragged = false;
        mScrollEnabled = true;
        mReceivedDown = false;
        Context context1 = getContext();
        setFocusable(false);
        ViewConfiguration viewconfiguration = ViewConfiguration.get(context1);
        mTouchSlop = viewconfiguration.getScaledTouchSlop();
        mMinimumVelocity = viewconfiguration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = viewconfiguration.getScaledMaximumFlingVelocity();
        mScroller = new Scroller(context1, sInterpolator);
    }

    public ScrollableViewGroup(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mFlingVelocity = 0.0F;
        mScrollDirection = 0;
        mVertical = true;
        mFlingable = true;
        mIsBeingDragged = false;
        mScrollEnabled = true;
        mReceivedDown = false;
        Context context1 = getContext();
        setFocusable(false);
        ViewConfiguration viewconfiguration = ViewConfiguration.get(context1);
        mTouchSlop = viewconfiguration.getScaledTouchSlop();
        mMinimumVelocity = viewconfiguration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = viewconfiguration.getScaledMaximumFlingVelocity();
        mScroller = new Scroller(context1, sInterpolator);
    }
    
    //===========================================================================
    //						Private function
    //===========================================================================
    private int clampToScrollLimits(int i) {
    	
    	if(i >= mLimits[0]) {
    		 if(i > mLimits[1])
    	            i = mLimits[1];
    	} else {
    		i = mLimits[0];
    	}
    	return i;
    }
    
	private boolean shouldStartDrag(MotionEvent motionevent) {
		if (!mScrollEnabled) {
			return false;
		} else {
			if (mIsBeingDragged) {
				mIsBeingDragged = false;
				return false;
			} else {
				boolean flag1 = false;
				switch (motionevent.getAction()) {
				case 1: // '\001'
				default:
					flag1 = false;
					break;

				case 0: // '\0'
					updatePosition(motionevent);
					if (!mScroller.isFinished()) {
						startDrag();
						flag1 = true;
					} else {
						mReceivedDown = true;
						flag1 = false;
					}
					break;

				case 2: // '\002'
					float f = motionevent.getX() - mLastPosition[0];
					float f1 = motionevent.getY() - mLastPosition[1];
					boolean flag2;
					boolean flag3;
					boolean flag4;
					if (f > (float) mTouchSlop || f < (float) (-mTouchSlop))
						flag2 = true;
					else
						flag2 = false;

					if (f1 > (float) mTouchSlop || f1 < (float) (-mTouchSlop))
						flag3 = true;
					else
						flag3 = false;

					if (mVertical) {
						if (flag3 && !flag2)
							flag4 = true;
						else
							flag4 = false;
					} else {
						if (flag2 && !flag3)
							flag4 = true;
						else
							flag4 = false;
					}

					flag1 = false;
					if (flag4) {
						updatePosition(motionevent);
						startDrag();
						flag1 = true;
					}
					break;
				}

				return flag1;
			}
		}
	}
    
    private void startDrag() {
        mIsBeingDragged = true;
        mFlingVelocity = 0.0F;
        mScrollDirection = 0;
        mScroller.abortAnimation();
    }
    
    //===========================================================================
    //						Protected function
    //===========================================================================
    protected final void updatePosition(MotionEvent motionevent) {
        mLastPosition[0] = motionevent.getX();
        mLastPosition[1] = motionevent.getY();
    }
    
    protected final void scrollTo(int i) {
        if(mVertical)
            scrollTo(0, clampToScrollLimits(i));
        else
            scrollTo(clampToScrollLimits(i), 0);
    }

    //===========================================================================
    //						Public function
    //===========================================================================
    public void addView(View view)
    {
    	View view1 = view.findViewById(R.id.list_layout_parent);
    	if(null != view1) {
    		int i = ((Integer)view1.getTag()).intValue();
    	    int j = getChildCount();
    	    int k = 0;
    	    boolean flag = false;
    	    while(k < j) {
    	    	if(((Integer)getChildAt(k).findViewById(R.id.list_layout_parent).getTag()).intValue() <= i) {
    	    		k++; 
    	     	} else {
    	    		 addView(view, k);
    	    		 flag = true;
    	    		 if(!flag)
    	    			 super.addView(view);
    	    		 return;
    	    	 }
    	    }
    	    if(!flag)
                super.addView(view);
    	}
    }
    
    public void computeScroll() {
        if(mScroller.computeScrollOffset()) {
            int i;
            int j;
            if(mVertical)
                i = mScroller.getCurrY();
            else
                i = mScroller.getCurrX();
            scrollTo(i);
            invalidate();
            if(mVertical)
                j = mScroller.getFinalY();
            else
                j = mScroller.getFinalX();
            if(i == j)
                mScroller.abortAnimation();
            if(mFlingVelocity != 0.0F) {
                int k;
                if(mFlingVelocity > 0.0F)
                    k = 1;
                else
                    k = -1;
                mFlingVelocity = 0.0F;
                onScrollFinished(k);
            }
        }
    }
    
    public final int getScroll() {
        if(mVertical)
            return getScrollY();
        else
            return getScrollX();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent) {
        return shouldStartDrag(motionevent);
    }

	protected void onScrollFinished(int i) {
	}
	
	public boolean onTouchEvent(MotionEvent motionevent) {
        int i =  motionevent.getAction();
        if(mFlingable) {
            if(mVelocityTracker == null)
                mVelocityTracker = VelocityTracker.obtain();
            mVelocityTracker.addMovement(motionevent);
        }
        if(!mIsBeingDragged) {
        	boolean flag1;
            if(shouldStartDrag(motionevent)) {
                flag1 = true;
        	} else {
	            if(i == 1 && mReceivedDown) {
	                mReceivedDown = false;
	                flag1 = performClick();
	            } else {
	                flag1 = true;
	            }
            }
            return flag1;
        } else {
        	switch(i) {
        		default:
        			return true;
        		case 1:
        		case 3:
        			boolean flag;
        	        if(i == 3)
        	            flag = true;
        	        else
        	            flag = false;
        	        mIsBeingDragged = false;
        	        if(!flag && mFlingable && getChildCount() > 0) {
        	            mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        	            float f;
        	            if(mVertical)
        	                f = mVelocityTracker.getYVelocity();
        	            else
        	                f = mVelocityTracker.getXVelocity();
        	            if(f > (float)mMinimumVelocity || f < (float)(-mMinimumVelocity)) {
        	                float f1 = -f;
        	                mFlingVelocity = f1;
        	                int j = getScrollX();
        	                int k = getScrollY();
        	                if(mVertical)
        	                    mScroller.fling(j, k, 0, (int)f1, 0, 0, mLimits[0], mLimits[1]);
        	                else
        	                    mScroller.fling(j, k, (int)f1, 0, mLimits[0], mLimits[1], 0, 0);
        	                invalidate();
        	            } else {
        	                onScrollFinished(mScrollDirection);
        	            }
        	        } else {
        	            onScrollFinished(mScrollDirection);
        	        }
        	        if(mFlingable && mVelocityTracker != null) {
        	            mVelocityTracker.recycle();
        	            mVelocityTracker = null;
        	        }
        	        mReceivedDown = false;
        	        return true;
        		case 2:
        			int l;
        	        float f2;
        	        float f3;
        	        if(mVertical)
        	            l = 1;
        	        else
        	            l = 0;
        	        f2 = mLastPosition[l];
        	        updatePosition(motionevent);
        	        f3 = f2 - mLastPosition[l];
        	        if(f3 < -1F)
        	            mScrollDirection = -1;
        	        else
        	        if(f3 > 1.0F)
        	            mScrollDirection = 1;
        	        scrollTo(getScroll() + (int)f3);
        	        mReceivedDown = false;
        	        return true;
        	}
        }
    }
	
	public void setFlingable(boolean flag) {
		mFlingable = flag;
	}

	public void setScrollEnabled(boolean flag) {
		mScrollEnabled = flag;
	}

    public void setScrollLimits(int i, int j) {
        mLimits[0] = i;
        mLimits[1] = j;
    }

    public void setVertical(boolean flag) {
        mVertical = flag;
    }

    public boolean showContextMenuForChild(View view) {
        requestDisallowInterceptTouchEvent(true);
        return super.showContextMenuForChild(view);
    }

    public final void smoothScrollTo(int i) {
        int j = clampToScrollLimits(i) - getScroll();
        if(mVertical)
            mScroller.startScroll(0, getScrollY(), 0, j, 500);
        else
            mScroller.startScroll(getScrollX(), 0, j, 0, 500);
        invalidate();
    }
}
