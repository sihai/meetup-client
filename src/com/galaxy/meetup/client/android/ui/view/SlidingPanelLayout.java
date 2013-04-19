/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.galaxy.meetup.client.android.R;


public class SlidingPanelLayout extends ScrollableViewGroup {

	private OnSlidingPanelStateChange mListener;
    private int mMaxNavigationBarWidth;
    private int mMinNavigationBarWidth;
    private int mNavigationBarWidth;
    private int mNavigationBarWidthPercent;
    private boolean mOpen;
    private View mPanel;
    private Drawable mShadow;
    private int mShadowWidth;
    
    //===========================================================================
    //						Constructor
    //===========================================================================
    public SlidingPanelLayout(Context context) {
        super(context);
        setBackgroundColor(0);
        setScrollEnabled(true);
        setVertical(false);
    }

    public SlidingPanelLayout(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        setBackgroundColor(0);
        setScrollEnabled(true);
        setVertical(false);
    }

    public SlidingPanelLayout(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        setBackgroundColor(0);
        setScrollEnabled(true);
        setVertical(false);
    }
    
    //===========================================================================
    //						Private function
    //===========================================================================
    private boolean isScrolling() {
        int i = getScroll();
        boolean flag;
        if(i != 0 && i != -mNavigationBarWidth)
            flag = true;
        else
            flag = false;
        return flag;
    }

    //===========================================================================
    //						Protected function
    //===========================================================================
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(getScrollX() < 0) {
            mShadow.setBounds(-mShadowWidth, 0, 0, getHeight());
            mShadow.draw(canvas);
        }
    }

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getChildCount() != 1) {
            throw new IllegalStateException((new StringBuilder()).append(getClass().getName()).append(" should have exactly one child").toString());
        } else {
            mPanel = getChildAt(0);
            Resources resources = getResources();
            mShadowWidth = resources.getDimensionPixelSize(R.dimen.host_shadow_width);
            mMinNavigationBarWidth = resources.getDimensionPixelSize(R.dimen.host_min_navigation_bar_width);
            mMaxNavigationBarWidth = resources.getDimensionPixelSize(R.dimen.host_max_navigation_bar_width);
            mNavigationBarWidthPercent = resources.getInteger(R.integer.host_navigation_bar_width_percent);
            mShadow = resources.getDrawable(R.drawable.navigation_shadow);
            return;
        }
    }
    
    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        mPanel.layout(0, 0, k - i, l - j);
        if(!isScrolling())
            if(!mOpen)
                scrollTo(0, 0);
            else
                scrollTo(-mNavigationBarWidth, 0);
    }

    protected void onMeasure(int i, int j) {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        super.onMeasure(i, j);
        mPanel.measure(android.view.View.MeasureSpec.makeMeasureSpec(k, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(l, 0x40000000));
        mNavigationBarWidth = Math.min(Math.max((k * mNavigationBarWidthPercent) / 100, mMinNavigationBarWidth), mMaxNavigationBarWidth);
        setScrollLimits(-mNavigationBarWidth, 0);
    }
    
    protected void onScrollChanged(int i, int j, int k, int l) {
    	super.onScrollChanged(i, j, k, l);
    	if(i == 0) {
    		 mOpen = false;
    	     if(mListener != null)
    	    	 mListener.onPanelClosed();
    	     return; 
    	} else {
    		if(i == -mNavigationBarWidth && mListener != null) {
    			// TODO
            }
    	}
    	
    }

	protected final void onScrollFinished(int i) {
		if (i < 0) {
			smoothScrollTo(-mNavigationBarWidth);
		} else {
			mOpen = false;
			smoothScrollTo(0);
		}
	}


    //===========================================================================
    //						Public function
    //===========================================================================
    public final void close() {
        if(mOpen) {
            setScrollEnabled(false);
            smoothScrollTo(0);
        }
    }

    public final int getNavigationBarWidth() {
        return mNavigationBarWidth;
    }

    public final boolean isOpen() {
        return mOpen;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
    	if(!mOpen) {
    		return false;
    	} else {
    		double i = motionevent.getX() - (float)mNavigationBarWidth;
    		if(i >= 0) {
                super.onInterceptTouchEvent(motionevent);
                return true;
            }
    		return false;
    	}
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedstate = (SavedState)parcelable;
        super.onRestoreInstanceState(savedstate.getSuperState());
        mOpen = savedstate.open;
        setScrollEnabled(mOpen);
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedstate = new SavedState(super.onSaveInstanceState());
        savedstate.open = mOpen;
        return savedstate;
    }

    public boolean onTouchEvent(MotionEvent motionevent) {
    	
    	if(!mOpen) {
    		return false;
    	} else {
    		if(motionevent.getX() < (float)mNavigationBarWidth) {
                boolean flag2 = isScrolling();
                if(!flag2)
                    return false;
            }
    		return super.onTouchEvent(motionevent);
    	}
    }

    public final void open() {
        if(!mOpen) {
            mOpen = true;
            setScrollEnabled(true);
            smoothScrollTo(-mNavigationBarWidth);
        }
    }

    public boolean performClick() {
        super.performClick();
        close();
        return true;
    }

    public void setOnSlidingPanelStateChange(OnSlidingPanelStateChange onslidingpanelstatechange) {
        mListener = onslidingpanelstatechange;
    }
    
    //===========================================================================
    //						Inner class
    //===========================================================================
	public static interface OnSlidingPanelStateChange {

		void onPanelClosed();
	}

    static class SavedState extends View.BaseSavedState {

    	boolean open;
    	
        public String toString() {
            String s = Integer.toHexString(System.identityHashCode(this));
            return (new StringBuilder("HostLayout.SavedState{")).append(s).append(" open=").append(open).append("}").toString();
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            if(open)
            	 parcel.writeInt(1);
            else
            	 parcel.writeInt(0);
        }

        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public final Object createFromParcel(Parcel parcel) {
                return new SavedState(parcel, (byte)0);
            }

            public final Object[] newArray(int i) {
                return new SavedState[i];
            }

        };

        private SavedState(Parcel parcel) {
            super(parcel);
            boolean flag;
            if(parcel.readInt() != 0)
                open = true;
            else
                open = false;
        }

        SavedState(Parcel parcel, byte byte0) {
            this(parcel);
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }
    }
}
