/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Scroller;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class ColumnGridView extends ViewGroup {

	private int mActivePointerId;
    private ListAdapter mAdapter;
    private Bug6713624LinkedHashMap mBug6713624LinkedHashMap;
    private int mColCount;
    private int mColCountSetting;
    private final Point mCurrentTouchPoint;
    private boolean mDataChanged;
    private final EdgeEffectCompat mEndEdge;
    private int mFirstPosition;
    private final int mFlingVelocity;
    private boolean mHasStableIds;
    private boolean mHorizontalOrientation;
    private boolean mInLayout;
    private int mItemCount;
    private int mItemEnd[];
    private int mItemMargin;
    private int mItemStart[];
    private int mLastScrollState;
    private float mLastTouch;
    private final SparseArrayCompat mLayoutRecords;
    private int mLocation[];
    private final int mMaximumVelocity;
    private int mMinColWidth;
    private final AdapterDataSetObserver mObserver;
    private OnScrollListener mOnScrollListener;
    private boolean mPopulating;
    private boolean mPressed;
    private float mRatio;
    private final RecycleBin mRecycler;
    private int mRestoreOffset;
    private int mScrollState;
    private final Scroller mScroller;
    private final SparseBooleanArray mSelectedPositions;
    private ItemSelectionListener mSelectionListener;
    private boolean mSelectionMode;
    private final Point mSelectionStartPoint;
    private Drawable mSelector;
    private Runnable mSetPressedRunnable = new Runnable() {

        public final void run()
        {
            mPressed = true;
            invalidate();
        }
    };
    private final EdgeEffectCompat mStartEdge;
    private float mTouchRemainder;
    private final int mTouchSlop;
    private final VelocityTracker mVelocityTracker;
    private int mVisibleOffset;
    
    
	public ColumnGridView(Context context) {
		this(context, null);
	}

	public ColumnGridView(Context context, AttributeSet attributeset) {
		this(context, attributeset, 0);
	}

	public ColumnGridView(Context context, AttributeSet attributeset, int i) {
		super(context, attributeset, i);
		mColCountSetting = 2;
		mColCount = 2;
		mMinColWidth = 0;
		mLayoutRecords = new SparseArrayCompat();
		mRecycler = new RecycleBin();
		mObserver = new AdapterDataSetObserver();
		mVelocityTracker = VelocityTracker.obtain();
		mRatio = 1.0F;
		mBug6713624LinkedHashMap = new Bug6713624LinkedHashMap();
		mSelectedPositions = new SparseBooleanArray();
		mSelectionStartPoint = new Point();
		mCurrentTouchPoint = new Point(-1, -1);
		mLocation = new int[2];
		ViewConfiguration viewconfiguration = ViewConfiguration.get(context);
		mTouchSlop = viewconfiguration.getScaledTouchSlop();
		mMaximumVelocity = viewconfiguration.getScaledMaximumFlingVelocity();
		mFlingVelocity = viewconfiguration.getScaledMinimumFlingVelocity();
		mScroller = new Scroller(context);
		mStartEdge = new EdgeEffectCompat(context);
		mEndEdge = new EdgeEffectCompat(context);
		setWillNotDraw(false);
		setClipToPadding(false);
	}
    
	private void checkForSelection(int i, int j) {
		if (!mSelectionMode) {
			return;
		}
		int k = mSelectionStartPoint.x - i;
		int l = mSelectionStartPoint.y - j;
		if (k * k + l * l < mTouchSlop * mTouchSlop) {
			int i1 = mFirstPosition;
			boolean flag = false;
			int j1 = -1 + getChildCount();
			while (j1 >= 0) {
				View view = getChildAt(j1);
				view.getLocationOnScreen(mLocation);
				if (i >= mLocation[0] && i <= mLocation[0] + view.getWidth()
						&& j >= mLocation[1]
						&& j <= mLocation[1] + view.getHeight()) {
					int k1 = j1 + i1;
					if (isSelected(k1))
						deselect(k1);
					else
						select(k1);
					flag = true;
				}
				j1--;
			}
			if (flag)
				invalidate();
		}
	}
    
	private void clearAllState() {
        mBug6713624LinkedHashMap.put("clearallstate - clear", Integer.valueOf(0));
        mLayoutRecords.clear();
        removeAllViews();
        resetStateForGridTop();
        mRecycler.clear();
    }

    private void clearPressedState() {
        if(mPressed)
            invalidate();
        mPressed = false;
        removeCallbacks(mSetPressedRunnable);
    }

    private int fillDown(int i, int j)
    {
        int k;
        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int j7;
        int k7;
        int l7;
        if(mHorizontalOrientation)
            k = getPaddingTop();
        else
            k = getPaddingLeft();
        l = mItemMargin;
        i1 = getColumnSize();
        if(mHorizontalOrientation)
            j1 = getWidth() - getPaddingRight();
        else
            j1 = getHeight() - getPaddingBottom();
        k1 = j1 + j;
        l1 = getNextColumnDown(mItemEnd);
        for(int i2 = i; l1 >= 0 && mItemEnd[l1] < k1; i2++)
        {
            int i3 = mItemCount;
            if(i2 >= i3)
                break;
            View view = obtainView(i2, null);
            LayoutParams layoutparams = (LayoutParams)view.getLayoutParams();
            int j3;
            int k3;
            LayoutRecord layoutrecord;
            boolean flag;
            int i4;
            int j4;
            int k4;
            if(view.getParent() != this)
                if(mInLayout)
                    addViewInLayout(view, -1, layoutparams);
                else
                    addView(view);
            j3 = Math.min(mColCount, layoutparams.minorSpan);
            k3 = i1 * j3 + l * (j3 - 1);
            if(j3 > 1)
            {
                int ai1[] = mItemEnd;
                layoutrecord = getNextRecordDown(i2, j3, ai1);
                l1 = layoutrecord.column;
            } else
            {
                mBug6713624LinkedHashMap.put("filldown - get", Integer.valueOf(i2));
                layoutrecord = (LayoutRecord)mLayoutRecords.get(i2);
            }
            flag = false;
            if(layoutrecord == null)
            {
                layoutrecord = new LayoutRecord();
                mBug6713624LinkedHashMap.put("filldown - put", Integer.valueOf(i2));
                mLayoutRecords.put(i2, layoutrecord);
                int l3 = l1;
                layoutrecord.column = l3;
                layoutrecord.span = j3;
            } else
            if(j3 != layoutrecord.span)
            {
                layoutrecord.span = j3;
                int i8 = l1;
                layoutrecord.column = i8;
                flag = true;
            } else
            {
                l1 = layoutrecord.column;
                flag = false;
            }
            if(mHasStableIds)
            {
                long l8 = mAdapter.getItemId(i2);
                layoutrecord.id = l8;
                layoutparams.id = l8;
            }
            layoutparams.column = l1;
            if(mHorizontalOrientation)
            {
                j4 = android.view.View.MeasureSpec.makeMeasureSpec(k3, 0x40000000);
                
                if(layoutparams.width == -2)
                    i4 = android.view.View.MeasureSpec.makeMeasureSpec(0, 0);
                else
                if(layoutparams.width == -1)
                    i4 = android.view.View.MeasureSpec.makeMeasureSpec(l * (-1 + layoutparams.majorSpan) + (int)((float)(i1 * layoutparams.majorSpan) * mRatio), 0x40000000);
                else
                    i4 = android.view.View.MeasureSpec.makeMeasureSpec(layoutparams.width, 0x40000000);
            } else
            {
                i4 = android.view.View.MeasureSpec.makeMeasureSpec(k3, 0x40000000);
                if(layoutparams.height == -2)
                    j4 = android.view.View.MeasureSpec.makeMeasureSpec(0, 0);
                else
                if(layoutparams.height == -1)
                    j4 = android.view.View.MeasureSpec.makeMeasureSpec(l * (-1 + layoutparams.majorSpan) + (int)((float)(i1 * layoutparams.majorSpan) * mRatio), 0x40000000);
                else
                    j4 = android.view.View.MeasureSpec.makeMeasureSpec(layoutparams.height, 0x40000000);
            }
            view.measure(i4, j4);
            if(mHorizontalOrientation)
                k4 = view.getMeasuredWidth();
            else
                k4 = view.getMeasuredHeight();
            if(flag || k4 != layoutrecord.size && layoutrecord.size > 0)
                invalidateLayoutRecordsAfterPosition(i2);
            layoutrecord.size = k4;
            int l4;
            int i5;
            int j5;
            int k5;
            int l5;
            int i6;
            int j6;
            if(j3 > 1)
            {
                int i7 = mItemEnd[l1];
                j7 = l1 + 1;
                do
                {
                    k7 = l1 + j3;
                    if(j7 >= k7)
                        break;
                    l7 = mItemEnd[j7];
                    if(l7 > i7)
                        i7 = l7;
                    j7++;
                } while(true);
                l4 = i7;
            } else
            {
                l4 = mItemEnd[l1];
            }
            if(mHorizontalOrientation)
            {
                i5 = l4 + l;
                j5 = i5 + k4;
                k5 = k + l1 * (i1 + l);
                l5 = k5 + view.getMeasuredHeight();
                i6 = j5;
            } else
            {
                i5 = k + l1 * (i1 + l);
                j5 = i5 + view.getMeasuredWidth();
                k5 = l4 + l;
                l5 = k5 + k4;
                i6 = l5;
            }
            view.layout(i5, k5, j5, l5);
            j6 = l1;
            do
            {
                int k6 = l1 + j3;
                if(j6 >= k6)
                    break;
                int ai[] = mItemEnd;
                int l6 = j6 - l1;
                ai[j6] = i6 + layoutrecord.getMarginAfter(l6);
                j6++;
            } while(true);
            l1 = getNextColumnDown(mItemEnd);
        }

        int j2 = 0;
        int k2 = 0;
        do
        {
            int l2 = mColCount;
            if(k2 < l2)
            {
                if(mItemEnd[k2] > j2)
                    j2 = mItemEnd[k2];
                k2++;
            } else
            {
                return j2 - j1;
            }
        } while(true);
    }
    
    private int fillUp(int i, int j) {
    	int k;
        int l;
        int i1;
        int j1;
        int l1;
        boolean flag;
        int i2;
        View view;
        LayoutParams layoutparams;
        int j3;
        int k3;
        LayoutRecord layoutrecord;
        LayoutRecord layoutrecord1;
        int j9;
        int k9;
        int i10;
        int i11;
        int k1;
        int i3;
        int j11;
        int k11;
        
        int j2;
        int k2;
        int l2;
        boolean flag1;
        int l3;
        int i4;
        int j4;
        int k4;
        int l4;
        int i5;
        int j5;
        int k5;
        int l5;
        int i6;
        int j6;
        int k6;
        int l6;
        int i7;
        int j7;
        int k7;
        int ai[];
        int l7;
        int i8;
        int j8;
        int k8;
        int l8;
        long l9;
        int i9;
        int j10;
        int k10;
        int l10;
        int l11;
        
        
        if(mHorizontalOrientation)
            k = getPaddingTop();
        else
            k = getPaddingLeft();
        l = mItemMargin;
        i1 = getColumnSize();
        if(mHorizontalOrientation)
            j1 = getPaddingLeft();
        else
            j1 = getPaddingTop();
        k1 = j1 - j;
        l1 = getNextColumnUp();
        flag = true;
        i2 = i;
        
        while(!(l1 < 0 || mItemStart[l1] <= k1 && flag || i2 < 0)) {
        	i3 = mItemCount;
            if(i2 < i3) {
            	view = obtainView(i2, null);
            	layoutparams = (LayoutParams)view.getLayoutParams();
	            if(view.getParent() != this)
	                if(mInLayout)
	                    addViewInLayout(view, 0, layoutparams);
	                else
	                    addView(view, 0);
	            j3 = Math.min(mColCount, layoutparams.minorSpan);
	            k3 = i1 * j3 + l * (j3 - 1);
	            
	            if(j3 <= 1) {
	            	mBug6713624LinkedHashMap.put("fillup - get", Integer.valueOf(i2));
	                layoutrecord = (LayoutRecord)mLayoutRecords.get(i2);
	                flag1 = false;
	                if(layoutrecord == null)
	                {
	                    layoutrecord = new LayoutRecord();
	                    mBug6713624LinkedHashMap.put("fillup - put", Integer.valueOf(i2));
	                    mLayoutRecords.put(i2, layoutrecord);
	                    l3 = l1;
	                    layoutrecord.column = l3;
	                    layoutrecord.span = j3;
	                } else
	                if(j3 != layoutrecord.span)
	                {
	                    layoutrecord.span = j3;
	                    i9 = l1;
	                    layoutrecord.column = i9;
	                    flag1 = true;
	                } else
	                {
	                    l1 = layoutrecord.column;
	                    flag1 = false;
	                }
	                if(mHasStableIds)
	                {
	                    l9 = mAdapter.getItemId(i2);
	                    layoutrecord.id = l9;
	                    layoutparams.id = l9;
	                }
	                layoutparams.column = l1;
	                if(mHorizontalOrientation)
	                {
	                    j4 = android.view.View.MeasureSpec.makeMeasureSpec(k3, 0x40000000);
	                    if(layoutparams.width == -2)
	                        i4 = android.view.View.MeasureSpec.makeMeasureSpec(0, 0);
	                    else
	                    if(layoutparams.width == -1)
	                        i4 = android.view.View.MeasureSpec.makeMeasureSpec(l * (-1 + layoutparams.majorSpan) + (int)((float)(i1 * layoutparams.majorSpan) * mRatio), 0x40000000);
	                    else
	                        i4 = android.view.View.MeasureSpec.makeMeasureSpec(layoutparams.width, 0x40000000);
	                } else
	                {
	                    i4 = android.view.View.MeasureSpec.makeMeasureSpec(k3, 0x40000000);
	                    if(layoutparams.height == -2)
	                        j4 = android.view.View.MeasureSpec.makeMeasureSpec(0, 0);
	                    else
	                    if(layoutparams.height == -1)
	                        j4 = android.view.View.MeasureSpec.makeMeasureSpec(l * (-1 + layoutparams.majorSpan) + (int)((float)(i1 * layoutparams.majorSpan) * mRatio), 0x40000000);
	                    else
	                        j4 = android.view.View.MeasureSpec.makeMeasureSpec(layoutparams.height, 0x40000000);
	                }
	                view.measure(i4, j4);
	                if(mHorizontalOrientation)
	                    k4 = view.getMeasuredWidth();
	                else
	                    k4 = view.getMeasuredHeight();
	                if(flag1 || k4 != layoutrecord.size && layoutrecord.size > 0)
	                    invalidateLayoutRecordsBeforePosition(i2);
	                layoutrecord.size = k4;
	                if(j3 > 1)
	                {
	                    i8 = mItemStart[l1];
	                    j8 = l1 + 1;
	                    do
	                    {
	                        k8 = l1 + j3;
	                        if(j8 >= k8)
	                            break;
	                        l8 = mItemStart[j8];
	                        if(l8 < i8)
	                            i8 = l8;
	                        j8++;
	                    } while(true);
	                    l4 = i8;
	                } else
	                {
	                    l4 = mItemStart[l1];
	                }
	                if(mHorizontalOrientation)
	                {
	                    l5 = l4;
	                    k5 = l4 - k4;
	                    j5 = k + l1 * (i1 + l);
	                    i5 = j5 + view.getMeasuredHeight();
	                    i6 = k5;
	                } else
	                {
	                    i5 = l4;
	                    j5 = l4 - k4;
	                    k5 = k + l1 * (i1 + l);
	                    l5 = k5 + view.getMeasuredWidth();
	                    i6 = j5;
	                }
	                view.layout(k5, j5, l5, i5);
	                j6 = l1;
	                do
	                {
	                    k6 = l1 + j3;
	                    if(j6 >= k6)
	                        break;
	                    ai = mItemStart;
	                    l7 = j6 - l1;
	                    ai[j6] = i6 - layoutrecord.getMarginBefore(l7) - l;
	                    j6++;
	                } while(true);
	                flag = layoutparams.isBoxStart;
	                l6 = mItemStart[0];
	                i7 = 1;
	                do
	                {
	                    j7 = mColCount;
	                    if(i7 >= j7 || !flag)
	                        break;
	                    if(mItemStart[i7] != l6)
	                        flag = false;
	                    i7++;
	                } while(true);
	                l1 = getNextColumnUp();
	                k7 = i2 - 1;
	                mFirstPosition = i2;
	                i2 = k7;
	            } else {
	            	mBug6713624LinkedHashMap.put("getnextrecordup - get", Integer.valueOf(i2));
	                layoutrecord1 = (LayoutRecord)mLayoutRecords.get(i2);
	                if(layoutrecord1 != null) {
	                	if(layoutrecord1.span != j3)
	                    {
	                        throw new IllegalStateException((new StringBuilder("Invalid LayoutRecord! Record had span=")).append(layoutrecord1.span).append(" but caller requested span=").append(j3).append(" for position=").append(i2).toString());
	                    }
	                } else { 
	                	layoutrecord = new LayoutRecord();
	                    layoutrecord.span = j3;
	                    mBug6713624LinkedHashMap.put("getnextrecordup - put", Integer.valueOf(i2));
	                    mLayoutRecords.put(i2, layoutrecord);
	                    
	                    j9 = -1;
	                    k9 = 0x80000000;
	                    i10 = mColCount - j3;
	                    
	                    if(i10 < 0) {
	                    	j10 = j9;
	                        layoutrecord.column = j10;
	                        for(k10 = 0; k10 < j3; k10++)
	                        {
	                            l10 = mItemStart[k10 + j9] - k9;
	                            layoutrecord.setMarginAfter(k10, l10);
	                        }

	                        l1 = layoutrecord.column;
	                    } else {
	                    	i11 = 0x7fffffff;
	                        j11 = i10;
	                        k11 = i10 + j3;
	                        
	                    }
	                }
	            }
            } else {
            	
            }
        }
        
        j2 = getHeight();
        k2 = 0;
        do
        {
            l2 = mColCount;
            if(k2 < l2)
            {
                if(mItemStart[k2] < j2)
                    j2 = mItemStart[k2];
                k2++;
            } else
            {
                return j1 - j2;
            }
        } while(true);
        
    }
    
    public LayoutParams generateDefaultLayoutParams() {
        int i;
        if(mHorizontalOrientation)
            i = 1;
        else
            i = 2;
        return new LayoutParams(i, -2, 1, 1);
    }

    public LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutparams) {
        LayoutParams layoutparams1 = new LayoutParams(layoutparams);
        int i;
        if(mHorizontalOrientation)
            i = 1;
        else
            i = 2;
        layoutparams1.orientation = i;
        return layoutparams1;
    }

    private int getNextColumnDown(int ai[]) {
        int i = -1;
        int j = 0x7fffffff;
        int k = mColCount;
        for(int l = 0; l < k; l++)
        {
            int i1 = ai[l];
            if(i1 < j)
            {
                j = i1;
                i = l;
            }
        }

        return i;
    }

    private int getNextColumnUp() {
        int i = -1;
        int j = 0x80000000;
        for(int k = -1 + mColCount; k >= 0; k--)
        {
            int l = mItemStart[k];
            if(l > j)
            {
                j = l;
                i = k;
            }
        }

        return i;
    }

    private LayoutRecord getNextRecordDown(int i, int j, int ai[]) {
        mBug6713624LinkedHashMap.put("getnextrecorddown - get", Integer.valueOf(i));
        LayoutRecord layoutrecord = (LayoutRecord)mLayoutRecords.get(i);
        int k;
        int l;
        int i1;
        if(layoutrecord == null)
        {
            layoutrecord = new LayoutRecord();
            layoutrecord.span = j;
            mBug6713624LinkedHashMap.put("getnextrecorddown - put", Integer.valueOf(i));
            mLayoutRecords.put(i, layoutrecord);
        } else
        if(layoutrecord.span != j)
            throw new IllegalStateException((new StringBuilder("Invalid LayoutRecord! Record had span=")).append(layoutrecord.span).append(" but caller requested span=").append(j).append(" for position=").append(i).toString());
        k = -1;
        l = 0x7fffffff;
        i1 = mColCount;
        for(int j1 = 0; j1 <= i1 - j; j1++)
        {
            int l1 = 0x80000000;
            for(int i2 = j1; i2 < j1 + j; i2++)
            {
                int j2 = ai[i2];
                if(j2 > l1)
                    l1 = j2;
            }

            if(l1 < l)
            {
                l = l1;
                k = j1;
            }
        }

        layoutrecord.column = k;
        for(int k1 = 0; k1 < j; k1++)
            layoutrecord.setMarginBefore(k1, l - ai[k1 + k]);

        return layoutrecord;
    }

    private void invalidateLayoutRecordsAfterPosition(int i)
    {
        int j;
        for(j = -1 + mLayoutRecords.size(); j >= 0 && mLayoutRecords.keyAt(j) > i; j--);
        int k = j + 1;
        mBug6713624LinkedHashMap.put("invalidateafter - removeatrange", Integer.valueOf(mLayoutRecords.size() - k));
        mLayoutRecords.removeAtRange(k + 1, mLayoutRecords.size() - k);
    }

    private void invalidateLayoutRecordsBeforePosition(int i)
    {
        int j;
        for(j = 0; j < mLayoutRecords.size() && mLayoutRecords.keyAt(j) < i; j++);
        mBug6713624LinkedHashMap.put("invalidatebefore - removeatrange", Integer.valueOf(j));
        mLayoutRecords.removeAtRange(0, j);
    }

    private void invokeOnItemScrollListener(int i)
    {
        if(mOnScrollListener != null)
            mOnScrollListener.onScroll(this, mFirstPosition, mVisibleOffset, getChildCount(), mItemCount, i);
        onScrollChanged(0, 0, 0, 0);
    }

    private boolean isSelected(int i)
    {
        return mSelectedPositions.get(i, false);
    }

    private View obtainView(int i, View view)
    {
        View view1 = mRecycler.getTransientStateView(i);
        View view4;
        if(view1 != null)
        {
            view4 = view1;
        } else
        {
            int j;
            int k;
            View view2;
            View view3;
            Object obj;
            if(view != null)
                j = ((LayoutParams)view.getLayoutParams()).viewType;
            else
                j = -1;
            k = mAdapter.getItemViewType(i);
            if(j == k)
                view2 = view;
            else
                view2 = mRecycler.getScrapView(k);
            view3 = mAdapter.getView(i, view2, this);
            if(view3 != view2 && view2 != null)
                mRecycler.addScrap(view2, getChildCount());
            LayoutParams layoutparams;
            obj = view3.getLayoutParams();
            if(view3.getParent() != this)
            {
                if(obj == null)
                {
                    Log.e("ColumnGridView", (new StringBuilder("view at position ")).append(i).append(" doesn't have layout parameters;using default layout paramters").toString());
                    obj = generateDefaultLayoutParams();
                } else
                if(!checkLayoutParams(((android.view.ViewGroup.LayoutParams) (obj))))
                {
                    Log.e("ColumnGridView", (new StringBuilder("view at position ")).append(i).append(" doesn't have layout parameters of type ColumnGridView.LayoutParams; wrapping parameters").toString());
                    obj = generateLayoutParams(((android.view.ViewGroup.LayoutParams) (obj)));
                }
                view3.setLayoutParams(((android.view.ViewGroup.LayoutParams) (obj)));
            }
            layoutparams = (LayoutParams)obj;
            layoutparams.position = i;
            layoutparams.viewType = k;
            view4 = view3;
        }
        return view4;
    }

    private void populate()
    {
    	int i;
    	int k;
        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        int l12;
        int i14;
        int j14;
        boolean flag;
        
        int k8;
        int k9;
        int l9;
        int i10;
        int j10;
        int k10;
        int l10;
        int i11;
        int j11;
        int k11;
        int l11;
        int i12;
        LayoutRecord layoutrecord3;
        int j12;
        int k12;
        int i13;
        int j13;
        int k13;
        int l13;
        
        int k2;
        int l2;
        int i3;
        int j3;
        int k3;
        int ai[];
        int l3;
        int i4;
        int j4;
        int k4;
        int l4;
        View view;
        LayoutParams layoutparams;
        int i5;
        int j5;
        LayoutRecord layoutrecord;
        int k5;
        LayoutRecord layoutrecord1;
        boolean flag1;
        int l5;
        int i6;
        int j6;
        int k6;
        int l6;
        int i7;
        int j7;
        int k7;
        int l7;
        int i8;
        long l8;
        int j8;
        View view1;
        LayoutParams layoutparams1;
        LayoutRecord layoutrecord2;
        
        
        
        if(getWidth() != 0 && getHeight() != 0)
        {
            if(mColCount == -1)
            {
                if(mHorizontalOrientation)
                    j14 = getHeight() / mMinColWidth;
                else
                    j14 = getWidth() / mMinColWidth;
                if(j14 != mColCount)
                    mColCount = j14;
            }
            i = mColCount;
            if(mItemStart == null || mItemStart.length != i)
            {
                mItemStart = new int[i];
                mItemEnd = new int[i];
                int j;
                if(mHorizontalOrientation)
                    j = getPaddingLeft();
                else
                    j = getPaddingTop();
                k = j + mRestoreOffset;
                Arrays.fill(mItemStart, k);
                Arrays.fill(mItemEnd, k);
                mBug6713624LinkedHashMap.put("populate - clear", Integer.valueOf(0));
                mLayoutRecords.clear();
                if(mInLayout)
                    removeAllViewsInLayout();
                else
                    removeAllViews();
                mRestoreOffset = 0;
            }
            mPopulating = true;
            flag = mDataChanged;
            if(mHorizontalOrientation)
                l = getPaddingTop();
            else
                l = getPaddingLeft();
            i1 = mItemMargin;
            j1 = getColumnSize();
            k1 = -1;
            l1 = -1;
            Arrays.fill(mItemEnd, 0x80000000);
            i2 = getChildCount();
            j2 = 0;
            while(j2 < i2) 
            {
                View view2 = getChildAt(j2);
                LayoutParams layoutparams2 = (LayoutParams)view2.getLayoutParams();
                int i9 = layoutparams2.column;
                int j9 = j2 + mFirstPosition;
                boolean flag2;
                View view3;
                if(flag || view2.isLayoutRequested())
                    flag2 = true;
                else
                    flag2 = false;
                
                if(flag)
                {
                    view3 = obtainView(j9, view2);
                    
                    if(view3 != view2)
                    {
                        removeViewAt(j2);
                        addView(view3, j2);
                    } else
                    {
                        view3 = view2;
                    }
                    i14 = layoutparams2.minorSpan;
                    layoutparams2 = (LayoutParams)view3.getLayoutParams();
                    if(layoutparams2.minorSpan != i14)
                        Log.e("ColumnGridView", "Span changed!");
                    layoutparams2.column = i9;
                } else
                {
                    view3 = view2;
                }
                k9 = Math.min(mColCount, layoutparams2.minorSpan);
                l9 = j1 * k9 + i1 * (k9 - 1);
                if(flag2)
                {
                    if(mHorizontalOrientation)
                    {
                        l13 = android.view.View.MeasureSpec.makeMeasureSpec(l9, 0x40000000);
                        if(layoutparams2.width == -2)
                        {
                            j13 = android.view.View.MeasureSpec.makeMeasureSpec(0, 0);
                            k13 = l13;
                        } else
                        if(layoutparams2.width == -1)
                        {
                            j13 = android.view.View.MeasureSpec.makeMeasureSpec(i1 * (-1 + layoutparams2.majorSpan) + (int)((float)(j1 * layoutparams2.majorSpan) * mRatio), 0x40000000);
                            k13 = l13;
                        } else
                        {
                            j13 = android.view.View.MeasureSpec.makeMeasureSpec(layoutparams2.width, 0x40000000);
                            k13 = l13;
                        }
                    } else
                    {
                        j13 = android.view.View.MeasureSpec.makeMeasureSpec(l9, 0x40000000);
                        if(layoutparams2.height == -2)
                            k13 = android.view.View.MeasureSpec.makeMeasureSpec(0, 0);
                        else
                        if(layoutparams2.height == -1)
                            k13 = android.view.View.MeasureSpec.makeMeasureSpec(i1 * (-1 + layoutparams2.majorSpan) + (int)((float)(j1 * layoutparams2.majorSpan) * mRatio), 0x40000000);
                        else
                            k13 = android.view.View.MeasureSpec.makeMeasureSpec(layoutparams2.height, 0x40000000);
                    }
                    view3.measure(j13, k13);
                }
                if(mItemEnd[i9] > 0x80000000)
                    i10 = i1 + mItemEnd[i9];
                else
                if(mHorizontalOrientation)
                    i10 = view3.getLeft();
                else
                    i10 = view3.getTop();
                if(k9 > 1)
                {
                    l12 = i9 + 1;
                    while(l12 < i9 + k9) 
                    {
                        i13 = i1 + mItemEnd[l12];
                        if(i13 <= i10)
                            i13 = i10;
                        l12++;
                        i10 = i13;
                    }
                }
                if(mHorizontalOrientation)
                    j10 = view3.getMeasuredWidth();
                else
                    j10 = view3.getMeasuredHeight();
                if(mHorizontalOrientation)
                {
                    l10 = i10 + j10;
                    l11 = l + i9 * (j1 + i1);
                    i11 = l11 + view3.getMeasuredHeight();
                    j11 = l10;
                } else
                {
                    k10 = l + i9 * (j1 + i1);
                    l10 = k10 + view3.getMeasuredWidth();
                    i11 = i10 + j10;
                    j11 = i11;
                    k11 = i10;
                    i10 = k10;
                    l11 = k11;
                }
                view3.layout(i10, l11, l10, i11);
                for(i12 = i9; i12 < i9 + k9; i12++)
                    mItemEnd[i12] = j11;

                mBug6713624LinkedHashMap.put("layoutchildren - get", Integer.valueOf(j9));
                layoutrecord3 = (LayoutRecord)mLayoutRecords.get(j9);
                if(layoutrecord3 != null && layoutrecord3.size != j10)
                {
                    layoutrecord3.size = j10;
                    j12 = j9;
                } else
                {
                    j12 = k1;
                }
                if(layoutrecord3 != null && layoutrecord3.span != k9)
                {
                    layoutrecord3.span = k9;
                    k12 = j9;
                } else
                {
                    k12 = l1;
                }
                j2++;
                l1 = k12;
                k1 = j12;
            }
            for(k2 = 0; k2 < mColCount; k2++)
                if(mItemEnd[k2] == 0x80000000)
                    mItemEnd[k2] = mItemStart[k2];

            if(k1 >= 0 || l1 >= 0)
            {
                if(k1 >= 0)
                    invalidateLayoutRecordsBeforePosition(k1);
                if(l1 >= 0)
                    invalidateLayoutRecordsAfterPosition(l1);
                l2 = 0;
                while(l2 < i2) 
                {
                    j8 = l2 + mFirstPosition;
                    view1 = getChildAt(l2);
                    layoutparams1 = (LayoutParams)view1.getLayoutParams();
                    mBug6713624LinkedHashMap.put("layoutchildren - get2", Integer.valueOf(j8));
                    layoutrecord2 = (LayoutRecord)mLayoutRecords.get(j8);
                    if(layoutrecord2 == null)
                    {
                        layoutrecord2 = new LayoutRecord();
                        mBug6713624LinkedHashMap.put("layoutchildren - put2", Integer.valueOf(j8));
                        mLayoutRecords.put(j8, layoutrecord2);
                    }
                    layoutrecord2.column = layoutparams1.column;
                    if(mHorizontalOrientation)
                        k8 = view1.getWidth();
                    else
                        k8 = view1.getHeight();
                    layoutrecord2.size = k8;
                    layoutrecord2.id = layoutparams1.id;
                    layoutrecord2.span = Math.min(mColCount, layoutparams1.minorSpan);
                    l2++;
                }
            }
            i3 = mFirstPosition;
            if(mInLayout)
            {
                ai = new int[mColCount];
                l3 = mItemMargin;
                i4 = getColumnSize();
                j4 = getNextColumnDown(ai);
                k4 = 0;
                l4 = j4;
                while(k4 < i3 && k4 < mItemCount) 
                {
                    view = obtainView(k4, null);
                    layoutparams = (LayoutParams)view.getLayoutParams();
                    i5 = Math.min(mColCount, layoutparams.minorSpan);
                    j5 = i4 * i5 + l3 * (i5 - 1);
                    if(i5 > 1)
                    {
                        layoutrecord1 = getNextRecordDown(k4, i5, ai);
                        k5 = layoutrecord1.column;
                    } else
                    {
                        mBug6713624LinkedHashMap.put("prefilldown - get", Integer.valueOf(k4));
                        layoutrecord = (LayoutRecord)mLayoutRecords.get(k4);
                        k5 = l4;
                        layoutrecord1 = layoutrecord;
                    }
                    flag1 = false;
                    if(layoutrecord1 == null)
                    {
                        layoutrecord1 = new LayoutRecord();
                        mBug6713624LinkedHashMap.put("prefilldown - put", Integer.valueOf(k4));
                        mLayoutRecords.put(k4, layoutrecord1);
                        layoutrecord1.column = k5;
                        layoutrecord1.span = i5;
                    } else
                    if(i5 != layoutrecord1.span)
                    {
                        layoutrecord1.span = i5;
                        layoutrecord1.column = k5;
                        flag1 = true;
                    } else
                    {
                        k5 = layoutrecord1.column;
                        flag1 = false;
                    }
                    if(mHasStableIds)
                    {
                        l8 = mAdapter.getItemId(k4);
                        layoutrecord1.id = l8;
                        layoutparams.id = l8;
                    }
                    layoutparams.column = k5;
                    if(mHorizontalOrientation)
                    {
                        i8 = android.view.View.MeasureSpec.makeMeasureSpec(j5, 0x40000000);
                        if(layoutparams.width == -2)
                        {
                            l5 = android.view.View.MeasureSpec.makeMeasureSpec(0, 0);
                            i6 = i8;
                        } else
                        if(layoutparams.width == -1)
                        {
                            l5 = android.view.View.MeasureSpec.makeMeasureSpec(l3 * (-1 + layoutparams.majorSpan) + (int)((float)(i4 * layoutparams.majorSpan) * mRatio), 0x40000000);
                            i6 = i8;
                        } else
                        {
                            l5 = android.view.View.MeasureSpec.makeMeasureSpec(layoutparams.width, 0x40000000);
                            i6 = i8;
                        }
                    } else
                    {
                        l5 = android.view.View.MeasureSpec.makeMeasureSpec(j5, 0x40000000);
                        if(layoutparams.height == -2)
                            i6 = android.view.View.MeasureSpec.makeMeasureSpec(0, 0);
                        else
                        if(layoutparams.height == -1)
                            i6 = android.view.View.MeasureSpec.makeMeasureSpec(l3 * (-1 + layoutparams.majorSpan) + (int)((float)(i4 * layoutparams.majorSpan) * mRatio), 0x40000000);
                        else
                            i6 = android.view.View.MeasureSpec.makeMeasureSpec(layoutparams.height, 0x40000000);
                    }
                    view.measure(l5, i6);
                    if(mHorizontalOrientation)
                        j6 = view.getMeasuredWidth();
                    else
                        j6 = view.getMeasuredHeight();
                    if(flag1 || j6 != layoutrecord1.size && layoutrecord1.size > 0)
                        invalidateLayoutRecordsAfterPosition(k4);
                    layoutrecord1.size = j6;
                    if(i5 > 1)
                    {
                        k6 = ai[k5];
                        k7 = k5 + 1;
                        while(k7 < k5 + i5) 
                        {
                            l7 = ai[k7];
                            if(l7 <= k6)
                                l7 = k6;
                            k7++;
                            k6 = l7;
                        }
                    } else
                    {
                        k6 = ai[k5];
                    }
                    l6 = l3 + (k6 + j6);
                    for(i7 = k5; i7 < k5 + i5; i7++)
                        ai[i7] = l6 + layoutrecord1.getMarginAfter(i7 - k5);

                    j7 = getNextColumnDown(ai);
                    k4++;
                    l4 = j7;
                }
            }
            fillDown(mFirstPosition + getChildCount(), 0);
            j3 = -1 + mFirstPosition;
            if(mRestoreOffset > 0)
                k3 = mRestoreOffset;
            else
                k3 = 0;
            fillUp(j3, k3);
            setVisibleOffset();
            mPopulating = false;
            mDataChanged = false;
        }
    }

    private void reportScrollStateChange(int i)
    {
        if(i != mLastScrollState)
        {
            mLastScrollState = i;
            if(mOnScrollListener != null)
                mOnScrollListener.onScrollStateChanged(this, i);
        }
    }

    private void resetStateForGridTop()
    {
        int i = mColCount;
        if(i != -1)
        {
            if(mItemStart == null || mItemStart.length != i)
            {
                mItemStart = new int[i];
                mItemEnd = new int[i];
            }
            int j = getPaddingTop();
            Arrays.fill(mItemStart, j);
            Arrays.fill(mItemEnd, j);
        }
        mFirstPosition = 0;
        mVisibleOffset = 0;
        mRestoreOffset = 0;
    }

    private void setVisibleOffset()
    {
        int i = -mItemMargin;
        mVisibleOffset = 0;
        int j = 0;
        int k = getChildCount();
        do
        {
            if(j >= k)
                break;
            View view = getChildAt(j);
            int l;
            if(mHorizontalOrientation)
                l = view.getRight();
            else
                l = view.getBottom();
            if(l >= i)
                break;
            mVisibleOffset = 1 + mVisibleOffset;
            j++;
        } while(true);
    }
    
    private boolean trackMotionScroll(int i, boolean flag)
    {
        // FIXME
    	return true;
    }

    public final void deselect(int i)
    {
        if(mSelectionMode && mSelectedPositions.get(i))
        {
            mSelectedPositions.put(i, false);
            View view = getChildAt(i - mFirstPosition);
            if(mSelectionListener != null)
                mSelectionListener.onItemDeselected(view, i);
        }
    }

    public void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        if(mSelector != null)
        {
            int i = getPaddingLeft();
            int j = getRight() - getPaddingRight();
            int k = getPaddingTop();
            int l = getBottom() - getPaddingBottom();
            int i1 = -1 + getChildCount();
            for(; i1 >= 0; i1--) 
            {
                    View view = getChildAt(i1);
                    if(!isSelected(i1 + mFirstPosition))
                    {
                        if(!mPressed || mCurrentTouchPoint.x < 0 || mCurrentTouchPoint.y < 0 || (view instanceof PressedHighlightable) && !((PressedHighlightable)view).shouldHighlightOnPress())
                            continue;
                        view.getLocationOnScreen(mLocation);
                        if(mCurrentTouchPoint.x < mLocation[0] || mCurrentTouchPoint.x > mLocation[0] + view.getWidth() || mCurrentTouchPoint.y < mLocation[1] || mCurrentTouchPoint.y > mLocation[1] + view.getHeight())
                        	continue;
                    }
                    int j1 = view.getLeft();
                    int k1 = view.getRight();
                    int l1 = view.getTop();
                    int i2 = view.getBottom();
                    if(j1 <= j && k1 >= i && l1 <= l && i2 >= k)
                    {
                        mSelector.setBounds(j1, l1, k1, i2);
                        mSelector.draw(canvas);
                    }
            }
        }
        return;
    }

    public void draw(Canvas canvas)
    {
        super.draw(canvas);
        if(mStartEdge != null)
        {
            boolean flag = mStartEdge.isFinished();
            boolean flag1 = false;
            int k;
            if(!flag)
            {
                if(mHorizontalOrientation)
                {
                    int l = canvas.save();
                    canvas.rotate(270F);
                    canvas.translate(-getHeight(), 0.0F);
                    mStartEdge.draw(canvas);
                    canvas.restoreToCount(l);
                } else
                {
                    mStartEdge.draw(canvas);
                }
                flag1 = true;
            }
            if(!mEndEdge.isFinished())
            {
                if(mHorizontalOrientation)
                {
                    k = canvas.save();
                    canvas.rotate(90F);
                    canvas.translate(0.0F, -getWidth());
                    mEndEdge.draw(canvas);
                    canvas.restoreToCount(k);
                } else
                {
                    int i = canvas.save();
                    int j = getWidth();
                    canvas.translate(-j, getHeight());
                    canvas.rotate(180F, j, 0.0F);
                    mEndEdge.draw(canvas);
                    canvas.restoreToCount(i);
                }
                flag1 = true;
            }
            if(flag1)
                ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public final void endSelectionMode()
    {
        if(!mSelectionMode)
            throw new IllegalStateException("Not in selection mode!");
        mSelectionMode = false;
        if(mSelectedPositions.size() > 0)
            invalidate();
        mSelectedPositions.clear();
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeset)
    {
        return new LayoutParams(getContext(), attributeset);
    }

    public final ListAdapter getAdapter()
    {
        return mAdapter;
    }
    
    public final int getColumnCount()
    {
        return mColCount;
    }

    public final int getColumnSize()
    {
        int i;
        int j;
        int k;
        if(mHorizontalOrientation)
            i = getPaddingTop();
        else
            i = getPaddingLeft();
        if(mHorizontalOrientation)
            j = getPaddingBottom();
        else
            j = getPaddingRight();
        if(mHorizontalOrientation)
            k = getHeight();
        else
            k = getWidth();
        return (k - i - j - mItemMargin * (-1 + mColCount)) / mColCount;
    }

    public final int getFirstVisiblePosition()
    {
        return mFirstPosition;
    }

    public final int getLastVisiblePosition()
    {
        return -1 + (mFirstPosition + getChildCount());
    }

    public final void invalidateViews()
    {
        mDataChanged = true;
        requestLayout();
        invalidate();
    }

    public final boolean isInSelectionMode()
    {
        return mSelectionMode;
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        removeCallbacks(mSetPressedRunnable);
    }

	public boolean onInterceptTouchEvent(MotionEvent motionevent) {
		boolean flag = true;
		mVelocityTracker.addMovement(motionevent);
		switch (0xff & motionevent.getAction()) {
		case 0:
			mCurrentTouchPoint.set((int) motionevent.getRawX(),
					(int) motionevent.getRawY());
			postDelayed(mSetPressedRunnable, ViewConfiguration.getTapTimeout());
			mVelocityTracker.clear();
			mScroller.abortAnimation();
			if (mHorizontalOrientation)
				mLastTouch = motionevent.getX();
			else
				mLastTouch = motionevent.getY();
			mActivePointerId = MotionEventCompat.getPointerId(motionevent, 0);
			mTouchRemainder = 0.0F;
			if (mScrollState == 2)
				mScrollState = ((flag) ? 1 : 0);
			else if (!mSelectionMode) {
				// FIXME
			}
			break;
		case 1:

		case 3:
			mCurrentTouchPoint.set(-1, -1);
			clearPressedState();
			flag = false;
			break;
		case 2:
			int i = MotionEventCompat.findPointerIndex(motionevent,
					mActivePointerId);
			if (i < 0) {
				Log.e("ColumnGridView",
						(new StringBuilder(
								"onInterceptTouchEvent could not find pointer with id "))
								.append(mActivePointerId)
								.append(" - did we receive an inconsistent event stream?")
								.toString());
				flag = false;
			} else {
				float f;
				float f1;
				if (mHorizontalOrientation)
					f = MotionEventCompat.getX(motionevent, i);
				else
					f = MotionEventCompat.getY(motionevent, i);
				f1 = (f - mLastTouch) + mTouchRemainder;
				mTouchRemainder = f1 - (float) (int) f1;
				if (Math.abs(f1) > (float) mTouchSlop)
					mScrollState = ((flag) ? 1 : 0);
			}
			break;
		default:
			flag = false;
			break;
		}
		return flag;
	}

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        mInLayout = true;
        populate();
        mInLayout = false;
        int i1 = k - i;
        int j1 = l - j;
        if(mHorizontalOrientation)
        {
            mStartEdge.setSize(j1, i1);
            mEndEdge.setSize(j1, i1);
        } else
        {
            mStartEdge.setSize(i1, j1);
            mEndEdge.setSize(i1, j1);
        }
        invokeOnItemScrollListener(0);
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getMode(i);
        int l = android.view.View.MeasureSpec.getMode(j);
        int i1 = android.view.View.MeasureSpec.getSize(i);
        int j1 = android.view.View.MeasureSpec.getSize(j);
        if(k != 0x40000000)
            Log.e("ColumnGridView", (new StringBuilder("onMeasure: must have an exact width or match_parent! Using fallback spec of EXACTLY ")).append(i1).toString());
        if(l != 0x40000000)
            Log.e("ColumnGridView", (new StringBuilder("onMeasure: must have an exact height or match_parent! Using fallback spec of EXACTLY ")).append(j1).toString());
        setMeasuredDimension(i1, j1);
        if(mColCountSetting == -1 && j1 > 0 && i1 > 0)
        {
            int k1;
            if(mHorizontalOrientation)
                k1 = j1 / mMinColWidth;
            else
                k1 = i1 / mMinColWidth;
            mColCount = k1;
        }
    }

    public final void onPause()
    {
        clearPressedState();
    }

    public void onRestoreInstanceState(Parcelable parcelable)
    {
        SavedState savedstate = (SavedState)parcelable;
        super.onRestoreInstanceState(savedstate.getSuperState());
        mDataChanged = true;
        mFirstPosition = savedstate.position;
        mVisibleOffset = savedstate.visibleOffset;
        mRestoreOffset = savedstate.topOffset;
        mSelectedPositions.clear();
        for(int i = -1 + savedstate.selectedPositions.size(); i >= 0; i--)
            mSelectedPositions.put(savedstate.selectedPositions.keyAt(i), savedstate.selectedPositions.valueAt(i));

        mSelectionMode = savedstate.selectionMode;
        requestLayout();
    }

    public final void onResume()
    {
        clearPressedState();
    }

    public Parcelable onSaveInstanceState()
    {
        SavedState savedstate = new SavedState(super.onSaveInstanceState());
        int i = mFirstPosition;
        int j = mVisibleOffset;
        savedstate.position = i;
        savedstate.visibleOffset = j;
        if(i >= 0 && mAdapter != null && i < mAdapter.getCount())
            savedstate.firstId = mAdapter.getItemId(i);
        int k = mSelectedPositions.size();
        SparseBooleanArray sparsebooleanarray = new SparseBooleanArray(k);
        for(int l = k - 1; l >= 0; l--)
            sparsebooleanarray.put(mSelectedPositions.keyAt(l), mSelectedPositions.valueAt(l));

        savedstate.selectedPositions = sparsebooleanarray;
        savedstate.selectionMode = mSelectionMode;
        int i1 = getChildCount();
        int j1 = 0;
        
        // FIXME
        return savedstate;
    }

    public boolean onTouchEvent(MotionEvent motionevent) {
    	boolean flag;
        mVelocityTracker.addMovement(motionevent);
        switch(0xff & motionevent.getAction()) {
	        case 0:
	        	 mCurrentTouchPoint.set((int)motionevent.getRawX(), (int)motionevent.getRawY());
	             mSelectionStartPoint.set((int)motionevent.getRawX(), (int)motionevent.getRawY());
	             postDelayed(mSetPressedRunnable, ViewConfiguration.getTapTimeout());
	             mVelocityTracker.clear();
	             mScroller.abortAnimation();
	             if(mHorizontalOrientation)
	                 mLastTouch = motionevent.getX();
	             else
	                 mLastTouch = motionevent.getY();
	             mActivePointerId = MotionEventCompat.getPointerId(motionevent, 0);
	             mTouchRemainder = 0.0F;
	             reportScrollStateChange(mScrollState);
	             flag = true;
	        	break;
	        case 1:
	        	mCurrentTouchPoint.set(-1, -1);
	            clearPressedState();
	            mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
	            float f;
	            if(mHorizontalOrientation)
	                f = VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId);
	            else
	                f = VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId);
	            if(Math.abs(f) > (float)mFlingVelocity)
	            {
	                mScrollState = 2;
	                int i;
	                int j;
	                if(mHorizontalOrientation)
	                    i = (int)f;
	                else
	                    i = 0;
	                if(mHorizontalOrientation)
	                    j = 0;
	                else
	                    j = (int)f;
	                mScroller.fling(0, 0, i, j, 0x80000000, 0x7fffffff, 0x80000000, 0x7fffffff);
	                mLastTouch = 0.0F;
	                ViewCompat.postInvalidateOnAnimation(this);
	            } else
	            {
	                mScrollState = 0;
	            }
	            checkForSelection((int)motionevent.getRawX(), (int)motionevent.getRawY());
	            reportScrollStateChange(mScrollState);
	            flag = true;
	        	break;
	        case 2:
	        	int k;
	            mCurrentTouchPoint.set((int)motionevent.getRawX(), (int)motionevent.getRawY());
	            clearPressedState();
	            k = MotionEventCompat.findPointerIndex(motionevent, mActivePointerId);
	            if(k < 0)
	            	Log.e("ColumnGridView", (new StringBuilder("onInterceptTouchEvent could not find pointer with id ")).append(mActivePointerId).append(" - did we receive an inconsistent event stream?").toString());
	            flag = false;
	        	break;
	        case 3:
	            mCurrentTouchPoint.set(-1, -1);
	            clearPressedState();
	            mScrollState = 0;
	            reportScrollStateChange(mScrollState);
	            flag = true;
	            break;
        	default:
        		 reportScrollStateChange(mScrollState);
                 flag = true;
        		break;
        }
        return false;
    }

    public final void registerSelectionListener(ItemSelectionListener itemselectionlistener)
    {
        mSelectionListener = itemselectionlistener;
    }

    public void requestDisallowInterceptTouchEvent(boolean flag)
    {
        if(flag)
        {
            mCurrentTouchPoint.set(-1, -1);
            clearPressedState();
        }
        super.requestDisallowInterceptTouchEvent(flag);
    }

    public void requestLayout()
    {
        if(!mPopulating)
            super.requestLayout();
    }

    public final void select(int i)
    {
        if(mSelectionMode && !mSelectedPositions.get(i))
        {
            mSelectedPositions.put(i, true);
            View view = getChildAt(i - mFirstPosition);
            if(mSelectionListener != null)
                mSelectionListener.onItemSelected(view, i);
        }
    }

    public void setAdapter(ListAdapter listadapter)
    {
        if(mAdapter != null)
        {
            mAdapter.unregisterDataSetObserver(mObserver);
            clearAllState();
        }
        mAdapter = listadapter;
        mDataChanged = true;
        int i;
        if(listadapter != null)
            i = listadapter.getCount();
        else
            i = 0;
        mItemCount = i;
        if(listadapter != null)
        {
            listadapter.registerDataSetObserver(mObserver);
            mRecycler.setViewTypeCount(listadapter.getViewTypeCount());
            mHasStableIds = listadapter.hasStableIds();
        } else
        {
            mHasStableIds = false;
        }
        if(mSelectionMode)
            endSelectionMode();
        populate();
    }

    public void setColumnCount(int i)
    {
        if(i <= 0 && i != -1)
            throw new IllegalArgumentException((new StringBuilder("colCount must be at least 1 - received ")).append(i).toString());
        boolean flag;
        if(i != mColCount)
            flag = true;
        else
            flag = false;
        mColCountSetting = i;
        mColCount = i;
        if(flag)
            populate();
    }

    public void setItemMargin(int i)
    {
        boolean flag;
        if(i != mItemMargin)
            flag = true;
        else
            flag = false;
        mItemMargin = i;
        if(flag)
            populate();
    }

    public void setMinColumnWidth(int i)
    {
        mMinColWidth = i;
        setColumnCount(-1);
    }

    public void setOnScrollListener(OnScrollListener onscrolllistener)
    {
        mOnScrollListener = onscrolllistener;
        invokeOnItemScrollListener(0);
    }

    public void setOrientation(int i)
    {
        boolean flag = true;
        if(i != 1)
            flag = false;
        mHorizontalOrientation = flag;
    }

    public void setRatio(float f)
    {
        mRatio = f;
    }

    public void setRecyclerListener(RecyclerListener recyclerlistener)
    {
        mRecycler.mRecyclerListener = recyclerlistener;
    }

    public void setSelection(int i)
    {
        setSelectionFromTop(i, 0);
    }

    public void setSelectionFromTop(int i, int j)
    {
        if(mAdapter != null)
        {
            mFirstPosition = Math.max(0, i);
            mVisibleOffset = 0;
            mRestoreOffset = j;
            requestLayout();
        }
    }

    public void setSelectionToTop()
    {
        removeAllViews();
        resetStateForGridTop();
        populate();
    }

    public void setSelector(int i)
    {
        if(i == 0)
            mSelector = null;
        else
            mSelector = getResources().getDrawable(i);
    }

    public void setSelector(Drawable drawable)
    {
        mSelector = drawable;
    }

    public final void startSelectionMode()
    {
        if(mSelectionMode)
        {
            throw new IllegalStateException("Already in selection mode!");
        } else
        {
            mSelectionMode = true;
            return;
        }
    }

    public final void unregisterSelectionListener()
    {
        mSelectionListener = null;
    }
    
    

	public static class LayoutParams extends ViewGroup.LayoutParams {
        int column;
        long id;
        public boolean isBoxStart;
        public int majorSpan;
        public int minorSpan;
        int orientation;
        int position;
        int viewType;

        public LayoutParams(int i, int j) {
        	super(0, 0);
            int k;
            if(i == 1 && j != -3)
                k = j;
            else
                k = -1;
            
            if(i != 1) {
            	if(j == -3)
                    j = -1;
            } else {
            	j = -1;
            }
            this.width = k;
            this.height = j;
            majorSpan = 1;
            minorSpan = 1;
            id = -1L;
            isBoxStart = true;
            orientation = 2;
            orientation = i;
        }

        public LayoutParams(int i, int j, int k, int l)
        {
            this(i, j);
            minorSpan = k;
            majorSpan = l;
        }

        public LayoutParams(Context context, AttributeSet attributeset)
        {
            super(context, attributeset);
            majorSpan = 1;
            minorSpan = 1;
            id = -1L;
            isBoxStart = true;
            orientation = 2;
            TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.ColumnGridView_Layout);
            minorSpan = typedarray.getInteger(1, 1);
            majorSpan = typedarray.getInteger(2, 1);
            orientation = typedarray.getInteger(0, 2);
            typedarray.recycle();
            if(orientation != 1) {
            	if(width != -1)
                {
                    Log.w("ColumnGridView", (new StringBuilder("Inflation setting LayoutParams width to ")).append(width).append(" - must be MATCH_PARENT").toString());
                    width = -1;
                }
            } else {
            	if(height != -1)
                {
                    Log.w("ColumnGridView", (new StringBuilder("Inflation setting LayoutParams height to ")).append(height).append(" - must be MATCH_PARENT").toString());
                    height = -1;
                }
            }
        }

        public LayoutParams(ViewGroup.LayoutParams layoutparams)
        {
            super(layoutparams);
            majorSpan = 1;
            minorSpan = 1;
            id = -1L;
            isBoxStart = true;
            orientation = 2;
            if(orientation != 1) {
            	if(width != -1)
                {
                    Log.w("ColumnGridView", (new StringBuilder("Constructing LayoutParams with width ")).append(width).append(" - must be MATCH_PARENT").toString());
                    width = -1;
                }
            } else {
            	 if(height != -1)
                 {
                     Log.w("ColumnGridView", (new StringBuilder("Constructing LayoutParams with height ")).append(height).append(" - must be MATCH_PARENT").toString());
                     height = -1;
                 }
            }
        }
        
        public String toString()
        {
            return (new StringBuilder("ColumnGridView.LayoutParams: id=")).append(id).append(" major=").append(majorSpan).append(" minor=").append(minorSpan).append(" pos=").append(position).append(" type=").append(viewType).append(" col=").append(column).append(" boxstart=").append(isBoxStart).append(" orient=").append(orientation).toString();
        }
    }
	
	private static final class LayoutRecord
    {

		public int column;
        public long id;
        private int mMargins[];
        public int size;
        public int span;

        public LayoutRecord()
        {
            id = -1L;
        }
        
        private final void ensureMargins()
        {
            if(mMargins == null)
                mMargins = new int[2 * span];
        }

        public final int getMarginAfter(int i)
        {
            int j;
            if(mMargins == null)
                j = 0;
            else
                j = mMargins[1 + i * 2];
            return j;
        }

        public final int getMarginBefore(int i)
        {
            int j;
            if(mMargins == null)
                j = 0;
            else
                j = mMargins[i * 2];
            return j;
        }

        public final void setMarginAfter(int i, int j)
        {
            if(mMargins != null || j != 0)
            {
                ensureMargins();
                mMargins[1 + i * 2] = j;
            }
        }

        public final void setMarginBefore(int i, int j)
        {
            if(mMargins != null || j != 0)
            {
                ensureMargins();
                mMargins[i * 2] = j;
            }
        }

        public final String toString()
        {
            String s = (new StringBuilder("LayoutRecord{c=")).append(column).append(", id=").append(id).append(" sz=").append(size).append(" sp=").append(span).toString();
            if(mMargins != null)
            {
                String s1 = (new StringBuilder()).append(s).append(" margins[before, after](").toString();
                for(int i = 0; i < mMargins.length; i += 2)
                    s1 = (new StringBuilder()).append(s1).append("[").append(mMargins[i]).append(", ").append(mMargins[i + 1]).append("]").toString();

                s = (new StringBuilder()).append(s1).append(")").toString();
            }
            return (new StringBuilder()).append(s).append("}").toString();
        }

    }
	
	public static interface OnScrollListener {

		void onScroll(ColumnGridView columngridview, int i, int j, int k,
				int l, int i1);

		void onScrollStateChanged(ColumnGridView columngridview, int i);
	}
	
	public static interface ItemSelectionListener {

        void onItemDeselected(View view, int i);

        void onItemSelected(View view, int i);
    }
	
	public static interface PressedHighlightable {

		boolean shouldHighlightOnPress();
	}
	
	
	private static final class Bug6713624LinkedHashMap extends LinkedHashMap {

		protected final boolean removeEldestEntry(Map.Entry entry) {
			boolean flag;
			if (size() > 32)
				flag = true;
			else
				flag = false;
			return flag;
		}

		private static final long serialVersionUID = 0xe954f0cbe61dabdbL;

		Bug6713624LinkedHashMap() {
		}
    }
	
	private final class AdapterDataSetObserver extends DataSetObserver {

		public final void onChanged() {
			int i;
			mDataChanged = true;
			i = mItemCount;
			mItemCount = mAdapter.getCount();
			if (mItemCount < i) {
				for (int l = -1 + mSelectedPositions.size(); l >= 0; l--) {
					int i1 = mSelectedPositions.keyAt(l);
					if (i1 >= mItemCount && mSelectedPositions.valueAt(l))
						deselect(i1);
				}

			}
			mRecycler.clearTransientViews();
			if (0 == mItemCount) {
				clearAllState();
			} else {
				if (!mHasStableIds || mItemCount < i) {
					mBug6713624LinkedHashMap.put("onchanged - clear", Integer.valueOf(0));
					mLayoutRecords.clear();
					
					for(i = 0; i < getChildCount(); i++)
			            mRecycler.addScrap(getChildAt(i), getChildCount());

			        if(mInLayout)
			            removeAllViewsInLayout();
			        else
			            removeAllViews();
			        
					if (mItemStart != null) {
						int j = mColCount;
						int k = 0;
						while (k < j) {
							mItemEnd[k] = mItemStart[k];
							k++;
						}
					}
				}
			}
			requestLayout();
		}

		public final void onInvalidated() {
		}
    }
	
	public static interface RecyclerListener {

		void onMovedToScrapHeap(View view);
	}
	
	private class RecycleBin {
		
		private int mMaxScrap;
        private RecyclerListener mRecyclerListener;
        private List mScrapViews[];
        private SparseArray mTransientStateViews;
        private int mViewTypeCount;


/*
        static RecyclerListener access$402(RecycleBin recyclebin, RecyclerListener recyclerlistener)
        {
            recyclebin.mRecyclerListener = recyclerlistener;
            return recyclerlistener;
        }

*/

        private RecycleBin()
        {
        }

        public final void addScrap(View view, int i) {
            LayoutParams layoutparams = (LayoutParams)view.getLayoutParams();
            if(!ViewCompat.hasTransientState(view)) {
            	if(i > mMaxScrap)
                    mMaxScrap = i;
                List arraylist = mScrapViews[layoutparams.viewType];
                if(arraylist.size() < mMaxScrap)
                    arraylist.add(view);
                if(mRecyclerListener != null)
                    mRecyclerListener.onMovedToScrapHeap(view);
            } else {
            	 if(mTransientStateViews == null)
                     mTransientStateViews = new SparseArray();
                 mTransientStateViews.put(layoutparams.position, view);
            }
        }

        public final void clear()
        {
            int i = mViewTypeCount;
            for(int j = 0; j < i; j++)
                mScrapViews[j].clear();

            if(mTransientStateViews != null)
                mTransientStateViews.clear();
        }

        public final void clearTransientViews()
        {
            if(mTransientStateViews != null)
                mTransientStateViews.clear();
        }

        public final View getScrapView(int i)
        {
            List arraylist = mScrapViews[i];
            View view;
            if(arraylist.isEmpty())
            {
                view = null;
            } else
            {
                int j = -1 + arraylist.size();
                view = (View)arraylist.get(j);
                arraylist.remove(j);
            }
            return view;
        }

        public final View getTransientStateView(int i) {
        	if(null == mTransientStateViews) {
        		return null;
        	}
        	View view = (View)mTransientStateViews.get(i);
            if(view != null)
                mTransientStateViews.remove(i);
            return view;
        }

        public final void setViewTypeCount(int i) {
            if(i <= 0)
                throw new IllegalArgumentException((new StringBuilder("Must have at least one view type (")).append(i).append(" types reported)").toString());
            if(i != mViewTypeCount) {
                List aarraylist[] = new ArrayList[i];
                for(int j = 0; j < i; j++)
                    aarraylist[j] = new ArrayList();

                mViewTypeCount = i;
                mScrapViews = aarraylist;
            }
        }

    }
	
	static class SavedState extends View.BaseSavedState
    {
		
		long firstId;
        int position;
        SparseBooleanArray selectedPositions;
        boolean selectionMode;
        int topOffset;
        int visibleOffset;
        
        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

            public final Object createFromParcel(Parcel parcel)
            {
                return new SavedState(parcel);
            }

            public final Object[] newArray(int i)
            {
                return new SavedState[i];
            }

        };

        private SavedState(Parcel parcel)
        {
        	super(parcel);
            boolean flag = true;
            firstId = -1L;
            firstId = parcel.readLong();
            position = parcel.readInt();
            visibleOffset = parcel.readInt();
            topOffset = parcel.readInt();
            selectedPositions = parcel.readSparseBooleanArray();
            if(parcel.readInt() != 1)
                flag = false;
            selectionMode = flag;
        }

        SavedState(Parcel parcel, byte byte0)
        {
            this(parcel);
        }

        SavedState(Parcelable parcelable)
        {
            super(parcelable);
            firstId = -1L;
        }
        
        public String toString()
        {
            return (new StringBuilder("StaggereGridView.SavedState{")).append(Integer.toHexString(System.identityHashCode(this))).append(" firstId=").append(firstId).append(" position=").append(position).append(" selected=").append(selectedPositions).append(" selectionMode=").append(selectionMode).append("}").toString();
        }

        public void writeToParcel(Parcel parcel, int i)
        {
            super.writeToParcel(parcel, i);
            parcel.writeLong(firstId);
            parcel.writeInt(position);
            parcel.writeInt(visibleOffset);
            parcel.writeInt(topOffset);
            parcel.writeSparseBooleanArray(selectedPositions);
            int j;
            if(selectionMode)
                j = 1;
            else
                j = 0;
            parcel.writeInt(j);
        }

    }
}
