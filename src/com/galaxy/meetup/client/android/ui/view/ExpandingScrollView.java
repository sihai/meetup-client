/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ExpandingScrollView extends ScrollableViewGroup {

	private static final Interpolator sBigBounceInterpolator = new OvershootInterpolator(15F);
    private static final Interpolator sBounceInterpolator = new OvershootInterpolator();
    private static boolean sInitialized;
    private static int sMinExposureLand;
    private static int sMinExposurePort;
    private boolean mAlwaysExpanded;
    private Runnable mAnimateInRunnable;
    private boolean mBigBounce;
    private boolean mCanAnimate;
    private boolean mHasPlayedAnimation;
    private MotionEvent mLastTouchEvent;
    private int mLastTouchY;
    private int mMaxScroll;
    private int mMinExposure;
    private int mMinExposureLand;
    private int mMinExposurePort;
    private int mOriginalTranslationY;
    private Boolean mRestoreExpandedScrollPosition;
    private int mTouchSlop;
    
    public ExpandingScrollView(Context context)
    {
        super(context);
        mAlwaysExpanded = true;
        Context context1 = getContext();
        if(!sInitialized)
        {
            Resources resources = context1.getResources();
            sMinExposureLand = resources.getDimensionPixelOffset(R.dimen.stream_one_up_list_min_height_land);
            sMinExposurePort = resources.getDimensionPixelOffset(R.dimen.stream_one_up_list_min_height_port);
            sInitialized = true;
        }
        mTouchSlop = ViewConfiguration.get(context1).getScaledTouchSlop();
    }

    public ExpandingScrollView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mAlwaysExpanded = true;
        Context context1 = getContext();
        if(!sInitialized)
        {
            Resources resources = context1.getResources();
            sMinExposureLand = resources.getDimensionPixelOffset(R.dimen.stream_one_up_list_min_height_land);
            sMinExposurePort = resources.getDimensionPixelOffset(R.dimen.stream_one_up_list_min_height_port);
            sInitialized = true;
        }
        mTouchSlop = ViewConfiguration.get(context1).getScaledTouchSlop();
        setAttributeValues(context, attributeset);
    }

    public ExpandingScrollView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mAlwaysExpanded = true;
        Context context1 = getContext();
        if(!sInitialized)
        {
            Resources resources = context1.getResources();
            sMinExposureLand = resources.getDimensionPixelOffset(R.dimen.stream_one_up_list_min_height_land);
            sMinExposurePort = resources.getDimensionPixelOffset(R.dimen.stream_one_up_list_min_height_port);
            sInitialized = true;
        }
        mTouchSlop = ViewConfiguration.get(context1).getScaledTouchSlop();
        setAttributeValues(context, attributeset);
    }

    private void setAttributeValues(Context context, AttributeSet attributeset)
    {
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.ExpandingScrollView);
        mMinExposureLand = typedarray.getDimensionPixelOffset(0, sMinExposureLand);
        mMinExposurePort = typedarray.getDimensionPixelOffset(1, sMinExposurePort);
        mBigBounce = typedarray.getBoolean(2, false);
    }

    public final MotionEvent getLastTouchEvent()
    {
        return mLastTouchEvent;
    }

    protected void onDetachedFromWindow()
    {
        if(mAnimateInRunnable != null)
        {
            removeCallbacks(mAnimateInRunnable);
            mAnimateInRunnable = null;
        }
        clearAnimation();
        super.onDetachedFromWindow();
    }

    public boolean onHoverEvent(MotionEvent motionevent)
    {
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent) {
        boolean flag;
        boolean flag1;
        flag = mAlwaysExpanded;
        flag1 = false;
        if(flag) {
        	return false;
        }
        
        if(!mScroller.isFinished())
        {
            flag1 = true;
        } else
        {
            int i = (int)motionevent.getY();
            switch(motionevent.getAction())
            {
            case 1: // '\001'
            default:
                flag1 = false;
                break;

            case 0: // '\0'
                updatePosition(motionevent);
                mLastTouchY = i;
                flag1 = false;
                break;

            case 2: // '\002'
                int j = getScrollY();
                boolean flag2;
                int k;
                boolean flag3;
                AbsListView abslistview;
                boolean flag4;
                int i1;
                if(j == mMaxScroll)
                    flag2 = true;
                else
                    flag2 = false;
                mRestoreExpandedScrollPosition = Boolean.valueOf(flag2);
                k = i - mLastTouchY;
                if(k < 0)
                    flag3 = true;
                else
                    flag3 = false;
                abslistview = (AbsListView)getChildAt(0);
                if(abslistview.getChildCount() == 0 || abslistview.getFirstVisiblePosition() == 0 && abslistview.getChildAt(0).getTop() == 0 && abslistview.getScrollY() == 0)
                    flag4 = true;
                else
                    flag4 = false;
                if(j != 0)
                {
                    flag1 = false;
                    if(flag3)
                        break;
                    flag1 = false;
                    if(!flag4)
                        break;
                    int l = mMaxScroll;
                    flag1 = false;
                    if(j != l)
                        break;
                }
                super.onInterceptTouchEvent(motionevent);
                if(k <= mTouchSlop)
                {
                    int k1 = -mTouchSlop;
                    flag1 = false;
                    if(k >= k1)
                        break;
                }
                i1 = 0;
                for(int j1 = abslistview.getChildCount(); i1 < j1; i1++)
                {
                    View view = abslistview.getChildAt(i1);
                    if(view instanceof StreamOneUpCommentView)
                        ((StreamOneUpCommentView)view).cancelPressedState();
                }

                flag1 = true;
                break;
            }
        }
        return flag1;
        
    }

    public void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1;
        int j1;
        if(mAlwaysExpanded)
            i1 = 0;
        else
            i1 = mMaxScroll;
        for(j1 = 0; j1 < getChildCount(); j1++)
        {
            View view = getChildAt(j1);
            int k2 = i1 + view.getMeasuredHeight();
            view.layout(i, i1, k, k2);
            i1 = k2;
        }

        int k1;
        int l1;
        if(mAlwaysExpanded)
            k1 = 0;
        else
            k1 = mMaxScroll;
        setScrollLimits(0, k1);
        if(mRestoreExpandedScrollPosition != null && mRestoreExpandedScrollPosition.booleanValue())
        {
            boolean flag1 = mAlwaysExpanded;
            int j2 = 0;
            
            if(!flag1)
                j2 = mMaxScroll;
            scrollTo(j2);
        }
        if(android.os.Build.VERSION.SDK_INT >= 12 && !mHasPlayedAnimation && mCanAnimate && mRestoreExpandedScrollPosition == null)
        {
            if(mAnimateInRunnable == null)
            {
                mOriginalTranslationY = (int)getTranslationY();
                l1 = mOriginalTranslationY;
                int i2;
                if(mAlwaysExpanded)
                    i2 = mMaxScroll;
                else
                    i2 = mMinExposure;
                setTranslationY(i2 + l1);
                mAnimateInRunnable = new Runnable() {

                    public final void run()
                    {
                        int l2;
                        Interpolator interpolator;
                        int i3;
                        if(mBigBounce)
                            l2 = 1000;
                        else
                            l2 = 750;
                        if(mBigBounce)
                            interpolator = ExpandingScrollView.sBigBounceInterpolator;
                        else
                            interpolator = ExpandingScrollView.sBounceInterpolator;
                        for(i3 = 0; i3 < getChildCount(); i3++)
                            getChildAt(i3).setVerticalScrollBarEnabled(false);

                        animate().translationY(mOriginalTranslationY).setInterpolator(interpolator).setDuration(l2);
                        mAnimateInRunnable = null;
                        mHasPlayedAnimation = true;
                        postDelayed(new Runnable() {

                            public final void run()
                            {
                                for(int i = 0; i < getChildCount(); i++)
                                    getChildAt(i).setVerticalScrollBarEnabled(true);

                            }

                        }, l2 + 200);
                    }

                };
            }
            removeCallbacks(mAnimateInRunnable);
            postDelayed(mAnimateInRunnable, 250L);
        }
    }

    public void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(j);
        int l;
        int i1;
        if(getContext().getResources().getConfiguration().orientation == 2)
            mMinExposure = mMinExposureLand;
        else
            mMinExposure = mMinExposurePort;
        mMaxScroll = k - mMinExposure;
        l = 0;
        i1 = android.view.View.MeasureSpec.makeMeasureSpec(k, 0x40000000);
        for(int j1 = 0; j1 < getChildCount(); j1++)
        {
            getChildAt(j1).measure(i, i1);
            l = Math.max(l, getChildAt(j1).getMeasuredWidth());
        }

        setMeasuredDimension(l, k + mMaxScroll);
    }

    public void onRestoreInstanceState(Parcelable parcelable)
    {
        SavedState savedstate = (SavedState)parcelable;
        super.onRestoreInstanceState(savedstate.getSuperState());
        mRestoreExpandedScrollPosition = Boolean.valueOf(savedstate.mExpanded);
        mMinExposureLand = savedstate.mExposureLand;
        mMinExposurePort = savedstate.mExposurePort;
        mBigBounce = savedstate.mBigBounce;
    }

    protected Parcelable onSaveInstanceState()
    {
        Parcelable parcelable = super.onSaveInstanceState();
        boolean flag;
        if(mMaxScroll != 0 && getScrollY() == mMaxScroll)
            flag = true;
        else
            flag = false;
        return new SavedState(parcelable, flag, mMinExposureLand, mMinExposurePort, mBigBounce);
    }

    protected final void onScrollFinished(int i)
    {
        int j;
        boolean flag;
        if(i < 0)
            j = 0;
        else
            j = mMaxScroll;
        smoothScrollTo(j);
        flag = false;
        if(i >= 0)
            flag = true;
        mRestoreExpandedScrollPosition = Boolean.valueOf(flag);
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        mLastTouchEvent = motionevent;
        return super.onTouchEvent(motionevent);
    }

    public void setAlwaysExpanded(boolean flag)
    {
        mAlwaysExpanded = flag;
    }

    public void setBigBounce(boolean flag)
    {
        mBigBounce = flag;
    }

    public void setCanAnimate(boolean flag)
    {
        mCanAnimate = flag;
        if(mCanAnimate && !mHasPlayedAnimation)
            requestLayout();
    }

    public void setMinimumExposure(int i, int j)
    {
        mMinExposureLand = i;
        mMinExposurePort = j;
    }
    
    private static class SavedState extends android.view.View.BaseSavedState
    {
    	final boolean mBigBounce;
        final boolean mExpanded;
        final int mExposureLand;
        final int mExposurePort;

        SavedState(Parcel parcel)
        {
        	super(parcel);
            boolean flag = true;
            boolean flag1;
            if(parcel.readInt() == 1)
                flag1 = flag;
            else
                flag1 = false;
            mExpanded = flag1;
            mExposureLand = parcel.readInt();
            mExposurePort = parcel.readInt();
            if(parcel.readInt() != 1)
                flag = false;
            mBigBounce = flag;
        }

        public SavedState(Parcelable parcelable, boolean flag, int i, int j, boolean flag1)
        {
            super(parcelable);
            mExpanded = flag;
            mExposureLand = i;
            mExposurePort = j;
            mBigBounce = flag1;
        }
        
        public void writeToParcel(Parcel parcel, int i)
        {
            int j = 1;
            super.writeToParcel(parcel, i);
            int k;
            if(mExpanded)
                k = j;
            else
                k = 0;
            parcel.writeInt(k);
            parcel.writeInt(mExposureLand);
            parcel.writeInt(mExposurePort);
            if(!mBigBounce)
                j = 0;
            parcel.writeInt(j);
        }

        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public final Object createFromParcel(Parcel parcel)
            {
                return new SavedState(parcel);
            }

            public final Object[] newArray(int i)
            {
                return new SavedState[i];
            }

        };
    }
}
