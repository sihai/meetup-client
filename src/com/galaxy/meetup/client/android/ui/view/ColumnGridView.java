/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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

	private static final String TAG = "ColumnGridView";
	
	public static final int COLUMN_COUNT_AUTO = -1;
	
	private static final int TOUCH_MODE_IDLE = 0;
    private static final int TOUCH_MODE_DRAGGING = 1;
    private static final int TOUCH_MODE_FLINGING = 2;
    
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

    private int fillDown(int fromPosition, int overhang)
    {
    	final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int itemMargin = mItemMargin;
        final int colWidth =
                (getWidth() - paddingLeft - paddingRight - itemMargin * (mColCount - 1)) / mColCount;
        final int gridBottom = getHeight() - getPaddingBottom();
        final int fillTo = gridBottom + overhang;
        int nextCol = getNextColumnDown();
        int position = fromPosition;

        while (nextCol >= 0 && mItemEnd[nextCol] < fillTo && position < mItemCount) {
            final View child = obtainView(position, null);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (child.getParent() != this) {
                if (mInLayout) {
                    addViewInLayout(child, -1, lp);
                } else {
                    addView(child);
                }
            }

            final int span = Math.min(mColCount, lp.minorSpan);
            final int widthSize = colWidth * span + itemMargin * (span - 1);
            final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);

            LayoutRecord rec;
            if (span > 1) {
                rec = getNextRecordDown(position, span, mItemEnd);
                nextCol = rec.column;
            } else {
                rec = (LayoutRecord)mLayoutRecords.get(position);
            }

            boolean invalidateAfter = false;
            if (rec == null) {
                rec = new LayoutRecord();
                mLayoutRecords.put(position, rec);
                rec.column = nextCol;
                rec.span = span;
            } else if (span != rec.span) {
                rec.span = span;
                rec.column = nextCol;
                invalidateAfter = true;
            } else {
                nextCol = rec.column;
            }

            if (mHasStableIds) {
                final long id = mAdapter.getItemId(position);
                rec.id = id;
                lp.id = id;
            }

            lp.column = nextCol;

            final int heightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            } else {
                heightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            }
            child.measure(widthSpec, heightSpec);

            final int childHeight = child.getMeasuredHeight();
            if (invalidateAfter || (childHeight != rec.size && rec.size > 0)) {
                invalidateLayoutRecordsAfterPosition(position);
            }
            rec.size = childHeight;

            final int startFrom;
            if (span > 1) {
                int lowest = mItemEnd[nextCol];
                for (int i = nextCol + 1; i < nextCol + span; i++) {
                    final int bottom = mItemEnd[i];
                    if (bottom > lowest) {
                        lowest = bottom;
                    }
                }
                startFrom = lowest;
            } else {
                startFrom = mItemEnd[nextCol];
            }
            final int childTop = startFrom + itemMargin;
            final int childBottom = childTop + childHeight;
            final int childLeft = paddingLeft + nextCol * (colWidth + itemMargin);
            final int childRight = childLeft + child.getMeasuredWidth();
            child.layout(childLeft, childTop, childRight, childBottom);

            for (int i = nextCol; i < nextCol + span; i++) {
            	mItemEnd[i] = childBottom + rec.getMarginAfter(i - nextCol);
            }

            nextCol = getNextColumnDown();
            position++;
        }

        int lowestView = 0;
        for (int i = 0; i < mColCount; i++) {
            if (mItemEnd[i] > lowestView) {
                lowestView = mItemEnd[i];
            }
        }
        return lowestView - gridBottom;
    }
    
    private int fillUp(int fromPosition, int overhang) {
    	final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int itemMargin = mItemMargin;
        final int colWidth =
                (getWidth() - paddingLeft - paddingRight - itemMargin * (mColCount - 1)) / mColCount;
        final int gridTop = getPaddingTop();
        final int fillTo = gridTop - overhang;
        int nextCol = getNextColumnUp();
        int position = fromPosition;

        while (nextCol >= 0 && mItemStart[nextCol] > fillTo && position >= 0) {
            final View child = obtainView(position, null);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (child.getParent() != this) {
                if (mInLayout) {
                    addViewInLayout(child, 0, lp);
                } else {
                    addView(child, 0);
                }
            }

            final int span = Math.min(mColCount, lp.minorSpan);
            final int widthSize = colWidth * span + itemMargin * (span - 1);
            final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);

            LayoutRecord rec;
            if (span > 1) {
                rec = getNextRecordUp(position, span);
                nextCol = rec.column;
            } else {
                rec = (LayoutRecord)mLayoutRecords.get(position);
            }

            boolean invalidateBefore = false;
            if (rec == null) {
                rec = new LayoutRecord();
                mLayoutRecords.put(position, rec);
                rec.column = nextCol;
                rec.span = span;
            } else if (span != rec.span) {
                rec.span = span;
                rec.column = nextCol;
                invalidateBefore = true;
            } else {
                nextCol = rec.column;
            }

            if (mHasStableIds) {
                final long id = mAdapter.getItemId(position);
                rec.id = id;
                lp.id = id;
            }

            lp.column = nextCol;

            final int heightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            } else {
                heightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            }
            child.measure(widthSpec, heightSpec);

            final int childHeight = child.getMeasuredHeight();
            if (invalidateBefore || (childHeight != rec.size && rec.size > 0)) {
                invalidateLayoutRecordsBeforePosition(position);
            }
            rec.size = childHeight;

            final int startFrom;
            if (span > 1) {
                int highest = mItemStart[nextCol];
                for (int i = nextCol + 1; i < nextCol + span; i++) {
                    final int top = mItemStart[i];
                    if (top < highest) {
                        highest = top;
                    }
                }
                startFrom = highest;
            } else {
                startFrom = mItemStart[nextCol];
            }
            final int childBottom = startFrom;
            final int childTop = childBottom - childHeight;
            final int childLeft = paddingLeft + nextCol * (colWidth + itemMargin);
            final int childRight = childLeft + child.getMeasuredWidth();
            child.layout(childLeft, childTop, childRight, childBottom);

            for (int i = nextCol; i < nextCol + span; i++) {
                mItemStart[i] = childTop - rec.getMarginBefore(i - nextCol) - itemMargin;
            }

            nextCol = getNextColumnUp();
            mFirstPosition = position--;
        }

        int highestView = getHeight();
        for (int i = 0; i < mColCount; i++) {
            if (mItemStart[i] < highestView) {
                highestView = mItemStart[i];
            }
        }
        return gridTop - highestView;
    }
    
    /**
     * Return a LayoutRecord for the given position
     * @param position
     * @param span
     * @return
     */
    final LayoutRecord getNextRecordUp(int position, int span) {
        LayoutRecord rec = (LayoutRecord)mLayoutRecords.get(position);
        if (rec == null) {
            rec = new LayoutRecord();
            rec.span = span;
            mLayoutRecords.put(position, rec);
        } else if (rec.span != span) {
            throw new IllegalStateException("Invalid LayoutRecord! Record had span=" + rec.span +
                    " but caller requested span=" + span + " for position=" + position);
        }
        int targetCol = -1;
        int bottomMost = Integer.MIN_VALUE;

        final int colCount = mColCount;
        for (int i = colCount - span; i >= 0; i--) {
            int top = Integer.MAX_VALUE;
            for (int j = i; j < i + span; j++) {
                final int singleTop = mItemStart[j];
                if (singleTop < top) {
                    top = singleTop;
                }
            }
            if (top > bottomMost) {
                bottomMost = top;
                targetCol = i;
            }
        }

        rec.column = targetCol;

        for (int i = 0; i < span; i++) {
            rec.setMarginAfter(i, mItemStart[i + targetCol] - bottomMost);
        }

        return rec;
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

    final int getNextColumnDown() {
        int result = -1;
        int topMost = Integer.MAX_VALUE;

        final int colCount = mColCount;
        for (int i = 0; i < colCount; i++) {
            final int bottom = mItemEnd[i];
            if (bottom < topMost) {
                topMost = bottom;
                result = i;
            }
        }
        return result;
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
                mRecycler.addScrap(view2);
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

    private void populate() {
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        if (mColCount == COLUMN_COUNT_AUTO) {
            final int colCount = getWidth() / mMinColWidth;
            if (colCount != mColCount) {
                mColCount = colCount;
            }
        }

        final int colCount = mColCount;
        if (mItemStart == null || mItemStart.length != colCount) {
            mItemStart = new int[colCount];
            mItemEnd = new int[colCount];
            final int top = getPaddingTop();
            final int offset = top + Math.min(mRestoreOffset, 0);
            Arrays.fill(mItemStart, offset);
            Arrays.fill(mItemEnd, offset);
            mLayoutRecords.clear();
            if (mInLayout) {
                removeAllViewsInLayout();
            } else {
                removeAllViews();
            }
            mRestoreOffset = 0;
        }

        mPopulating = true;
        layoutChildren(mDataChanged);
        fillDown(mFirstPosition + getChildCount(), 0);
        fillUp(mFirstPosition - 1, 0);
        mPopulating = false;
        mDataChanged = false;
    }
    
    /**
     * Measure and layout all currently visible children.
     *
     * @param queryAdapter true to requery the adapter for view data
     */
    final void layoutChildren(boolean queryAdapter) {
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int itemMargin = mItemMargin;
        final int colWidth =
                (getWidth() - paddingLeft - paddingRight - itemMargin * (mColCount - 1)) / mColCount;
        int rebuildLayoutRecordsBefore = -1;
        int rebuildLayoutRecordsAfter = -1;

        Arrays.fill(mItemEnd, Integer.MIN_VALUE);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int col = lp.column;
            final int position = mFirstPosition + i;
            final boolean needsLayout = queryAdapter || child.isLayoutRequested();

            if (queryAdapter) {
                View newView = obtainView(position, child);
                if (newView != child) {
                    removeViewAt(i);
                    addView(newView, i);
                    child = newView;
                }
                lp = (LayoutParams) child.getLayoutParams(); // Might have changed
            }

            final int span = Math.min(mColCount, lp.minorSpan);
            final int widthSize = colWidth * span + itemMargin * (span - 1);

            if (needsLayout) {
                final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);

                final int heightSpec;
                if (lp.height == LayoutParams.WRAP_CONTENT) {
                    heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                } else {
                    heightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
                }

                child.measure(widthSpec, heightSpec);
            }

            int childTop = mItemEnd[col] > Integer.MIN_VALUE ?
                    mItemEnd[col] + mItemMargin : child.getTop();
            if (span > 1) {
                int lowest = childTop;
                for (int j = col + 1; j < col + span; j++) {
                    final int bottom = mItemEnd[j] + mItemMargin;
                    if (bottom > lowest) {
                        lowest = bottom;
                    }
                }
                childTop = lowest;
            }
            final int childHeight = child.getMeasuredHeight();
            final int childBottom = childTop + childHeight;
            final int childLeft = paddingLeft + col * (colWidth + itemMargin);
            final int childRight = childLeft + child.getMeasuredWidth();
            child.layout(childLeft, childTop, childRight, childBottom);

            for (int j = col; j < col + span; j++) {
                mItemEnd[j] = childBottom;
            }

            final LayoutRecord rec = (LayoutRecord)mLayoutRecords.get(position);
            if (rec != null && rec.size != childHeight) {
                // Invalidate our layout records for everything before this.
                rec.size = childHeight;
                rebuildLayoutRecordsBefore = position;
            }

            if (rec != null && rec.span != span) {
                // Invalidate our layout records for everything after this.
                rec.span = span;
                rebuildLayoutRecordsAfter = position;
            }
        }

        // Update mItemEnd for any empty columns
        for (int i = 0; i < mColCount; i++) {
            if (mItemEnd[i] == Integer.MIN_VALUE) {
                mItemEnd[i] = mItemStart[i];
            }
        }

        if (rebuildLayoutRecordsBefore >= 0 || rebuildLayoutRecordsAfter >= 0) {
            if (rebuildLayoutRecordsBefore >= 0) {
                invalidateLayoutRecordsBeforePosition(rebuildLayoutRecordsBefore);
            }
            if (rebuildLayoutRecordsAfter >= 0) {
                invalidateLayoutRecordsAfterPosition(rebuildLayoutRecordsAfter);
            }
            for (int i = 0; i < childCount; i++) {
                final int position = mFirstPosition + i;
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                LayoutRecord rec = (LayoutRecord)mLayoutRecords.get(position);
                if (rec == null) {
                    rec = new LayoutRecord();
                    mLayoutRecords.put(position, rec);
                }
                rec.column = lp.column;
                rec.size = child.getHeight();
                rec.id = lp.id;
                rec.span = Math.min(mColCount, lp.minorSpan);
            }
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
    
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            final int y = mScroller.getCurrY();
            final int dy = (int) (y - mLastTouch);
            mLastTouch = y;
            final boolean stopped = !trackMotionScroll(dy, false);

            if (!stopped && !mScroller.isFinished()) {
                ViewCompat.postInvalidateOnAnimation(this);
            } else {
                if (stopped) {
                    final int overScrollMode = ViewCompat.getOverScrollMode(this);
                    if (overScrollMode != ViewCompat.OVER_SCROLL_NEVER) {
                        final EdgeEffectCompat edge;
                        if (dy > 0) {
                            edge = mStartEdge;
                        } else {
                            edge = mEndEdge;
                        }
                        edge.onAbsorb(Math.abs((int) mScroller.getCurrVelocity()));
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                    mScroller.abortAnimation();
                }
                mScrollState = TOUCH_MODE_IDLE;
            }
        }
    }
    
    /**
    *
    * @param deltaY Pixels that content should move by
    * @return true if the movement completed, false if it was stopped prematurely.
    */
   private boolean trackMotionScroll(int deltaY, boolean allowOverScroll) {
       final boolean contentFits = contentFits();
       final int allowOverhang = Math.abs(deltaY);

       final int overScrolledBy;
       final int movedBy;
       if (!contentFits) {
           final int overhang;
           final boolean up;
           mPopulating = true;
           if (deltaY > 0) {
               overhang = fillUp(mFirstPosition - 1, allowOverhang);
               up = true;
           } else {
               overhang = fillDown(mFirstPosition + getChildCount(), allowOverhang) + mItemMargin;
               up = false;
           }
           movedBy = Math.min(overhang, allowOverhang);
           offsetChildren(up ? movedBy : -movedBy);
           recycleOffscreenViews();
           mPopulating = false;
           overScrolledBy = allowOverhang - overhang;
       } else {
           overScrolledBy = allowOverhang;
           movedBy = 0;
       }

       if (allowOverScroll) {
           final int overScrollMode = ViewCompat.getOverScrollMode(this);

           if (overScrollMode == ViewCompat.OVER_SCROLL_ALWAYS ||
                   (overScrollMode == ViewCompat.OVER_SCROLL_IF_CONTENT_SCROLLS && !contentFits)) {

               if (overScrolledBy > 0) {
                   EdgeEffectCompat edge = deltaY > 0 ? mStartEdge : mEndEdge;
                   edge.onPull((float) Math.abs(deltaY) / getHeight());
                   ViewCompat.postInvalidateOnAnimation(this);
               }
           }
       }

       return deltaY == 0 || movedBy != 0;
   }
   
   private void recycleOffscreenViews() {
       final int height = getHeight();
       final int clearAbove = -mItemMargin;
       final int clearBelow = height + mItemMargin;
       for (int i = getChildCount() - 1; i >= 0; i--) {
           final View child = getChildAt(i);
           if (child.getTop() <= clearBelow)  {
               // There may be other offscreen views, but we need to maintain
               // the invariant documented above.
               break;
           }

           if (mInLayout) {
               removeViewsInLayout(i, 1);
           } else {
               removeViewAt(i);
           }

           mRecycler.addScrap(child);
       }

       while (getChildCount() > 0) {
           final View child = getChildAt(0);
           if (child.getBottom() >= clearAbove) {
               // There may be other offscreen views, but we need to maintain
               // the invariant documented above.
               break;
           }

           if (mInLayout) {
               removeViewsInLayout(0, 1);
           } else {
               removeViewAt(0);
           }

           mRecycler.addScrap(child);
           mFirstPosition++;
       }

       final int childCount = getChildCount();
       if (childCount > 0) {
           // Repair the top and bottom column boundaries from the views we still have
           Arrays.fill(mItemStart, Integer.MAX_VALUE);
           Arrays.fill(mItemEnd, Integer.MIN_VALUE);

           for (int i = 0; i < childCount; i++){
               final View child = getChildAt(i);
               final LayoutParams lp = (LayoutParams) child.getLayoutParams();
               final int top = child.getTop() - mItemMargin;
               final int bottom = child.getBottom();
               final LayoutRecord rec = (LayoutRecord)mLayoutRecords.get(mFirstPosition + i);

               final int colEnd = lp.column + Math.min(mColCount, lp.minorSpan);
               for (int col = lp.column; col < colEnd; col++) {
                   final int colTop = top - rec.getMarginBefore(col - lp.column);
                   final int colBottom = bottom + rec.getMarginAfter(col - lp.column);
                   if (colTop < mItemStart[col]) {
                       mItemStart[col] = colTop;
                   }
                   if (colBottom > mItemEnd[col]) {
                       mItemEnd[col] = colBottom;
                   }
               }
           }

           for (int col = 0; col < mColCount; col++) {
               if (mItemStart[col] == Integer.MAX_VALUE) {
                   // If one was untouched, both were.
                   mItemStart[col] = 0;
                   mItemEnd[col] = 0;
               }
           }
       }
   }
   
   final void offsetChildren(int offset) {
       final int childCount = getChildCount();
       for (int i = 0; i < childCount; i++) {
           final View child = getChildAt(i);
           child.layout(child.getLeft(), child.getTop() + offset,
                   child.getRight(), child.getBottom() + offset);
       }

       final int colCount = mColCount;
       for (int i = 0; i < colCount; i++) {
           mItemStart[i] += offset;
           mItemEnd[i] += offset;
       }
   }
   
   private final boolean contentFits() {
       if (mFirstPosition != 0 || getChildCount() != mItemCount) {
           return false;
       }

       int topmost = Integer.MAX_VALUE;
       int bottommost = Integer.MIN_VALUE;
       for (int i = 0; i < mColCount; i++) {
           if (mItemStart[i] < topmost) {
               topmost = mItemStart[i];
           }
           if (mItemEnd[i] > bottommost) {
               bottommost = mItemEnd[i];
           }
       }

       return topmost >= getPaddingTop() && bottommost <= getHeight() - getPaddingBottom();
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

    @Override
	public boolean onInterceptTouchEvent(MotionEvent motionevent) {
		boolean flag = true;
		mVelocityTracker.addMovement(motionevent);
        final int action = motionevent.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
		case MotionEvent.ACTION_DOWN:
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
			if (mScrollState == TOUCH_MODE_FLINGING)
				mScrollState = ((flag) ? TOUCH_MODE_DRAGGING : TOUCH_MODE_IDLE);
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
					mScrollState = ((flag) ? TOUCH_MODE_DRAGGING : TOUCH_MODE_IDLE);
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
    	
    	mVelocityTracker.addMovement(motionevent);
        final int action = motionevent.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
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
                break;

            case MotionEvent.ACTION_MOVE: {
                mCurrentTouchPoint.set((int)motionevent.getRawX(), (int)motionevent.getRawY());
                clearPressedState();
                final int index = MotionEventCompat.findPointerIndex(motionevent, mActivePointerId);
                if (index < 0) {
                    Log.e(TAG, "onInterceptTouchEvent could not find pointer with id " +
                            mActivePointerId + " - did StaggeredGridView receive an inconsistent " +
                            "event stream?");
                    return false;
                }
                
                float y;
                float dy;
                if(mHorizontalOrientation)
                    y = MotionEventCompat.getX(motionevent, index);
                else
                    y = MotionEventCompat.getY(motionevent, index);
                dy = (y - mLastTouch) + mTouchRemainder;
                final int deltaY = (int) dy;
                mTouchRemainder = dy - (float)deltaY;
                if(Math.abs(dy) > (float)mTouchSlop)
                    mScrollState = TOUCH_MODE_DRAGGING;
                if(mScrollState == TOUCH_MODE_DRAGGING)
                {
                    mLastTouch = y;
                    if(!trackMotionScroll(deltaY, true))
                        mVelocityTracker.clear();
                }
                break;
            } 
            case MotionEvent.ACTION_CANCEL:
            	mCurrentTouchPoint.set(-1, -1);
 	            clearPressedState();
 	            mScrollState = TOUCH_MODE_IDLE;
 	            reportScrollStateChange(mScrollState);
                break;

            case MotionEvent.ACTION_UP: {
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
	                mScrollState = TOUCH_MODE_FLINGING;
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
	                mScrollState = TOUCH_MODE_IDLE;
	            }
	            checkForSelection((int)motionevent.getRawX(), (int)motionevent.getRawY());
	            reportScrollStateChange(mScrollState);
	        	break;
            }
            default:{
       		 	reportScrollStateChange(mScrollState);
       		 	break;
            }
        }
        return true;
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
			            mRecycler.addScrap(getChildAt(i));

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
        private ArrayList<View>[] mScrapViews;
        private int mViewTypeCount;
        private int mMaxScrap;

        private SparseArray<View> mTransientStateViews;
        
        private RecyclerListener mRecyclerListener;

        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Must have at least one view type (" +
                        viewTypeCount + " types reported)");
            }
            if (viewTypeCount == mViewTypeCount) {
                return;
            }

            ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
            for (int i = 0; i < viewTypeCount; i++) {
                scrapViews[i] = new ArrayList<View>();
            }
            mViewTypeCount = viewTypeCount;
            mScrapViews = scrapViews;
        }

        public void clear() {
            final int typeCount = mViewTypeCount;
            for (int i = 0; i < typeCount; i++) {
                mScrapViews[i].clear();
            }
            if (mTransientStateViews != null) {
                mTransientStateViews.clear();
            }
        }

        public void clearTransientViews() {
            if (mTransientStateViews != null) {
                mTransientStateViews.clear();
            }
        }

        public void addScrap(View v) {
            final LayoutParams lp = (LayoutParams) v.getLayoutParams();
            if (ViewCompat.hasTransientState(v)) {
                if (mTransientStateViews == null) {
                    mTransientStateViews = new SparseArray<View>();
                }
                mTransientStateViews.put(lp.position, v);
                return;
            }

            final int childCount = getChildCount();
            if (childCount > mMaxScrap) {
                mMaxScrap = childCount;
            }

            ArrayList<View> scrap = mScrapViews[lp.viewType];
            if (scrap.size() < mMaxScrap) {
                scrap.add(v);
                if(mRecyclerListener != null)
                    mRecyclerListener.onMovedToScrapHeap(v);
            }
        }

        public View getTransientStateView(int position) {
            if (mTransientStateViews == null) {
                return null;
            }

            final View result = mTransientStateViews.get(position);
            if (result != null) {
                mTransientStateViews.remove(position);
            }
            return result;
        }

        public View getScrapView(int type) {
            ArrayList<View> scrap = mScrapViews[type];
            if (scrap.isEmpty()) {
                return null;
            }

            final int index = scrap.size() - 1;
            final View result = scrap.get(index);
            scrap.remove(index);
            return result;
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
