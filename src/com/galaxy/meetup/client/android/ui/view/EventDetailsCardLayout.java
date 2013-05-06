/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.fragments.EventActiveState;
import com.galaxy.meetup.client.util.ScreenMetrics;
import com.galaxy.meetup.server.client.v2.domain.Event;

/**
 * 
 * @author sihai
 *
 */
public class EventDetailsCardLayout extends EsScrollView {

	private static NinePatchDrawable sBackground;
    private static boolean sInitialized;
    private static int sPadding;
    private static int sPaddingBottom;
    private static int sPaddingLeft;
    private static int sPaddingRight;
    private static int sPaddingTop;
    private static int sScrollingSecondaryPadding;
    private static int sSecondaryPadding;
    private static float sTwoSpanLayoutDividerPercentage;
    private boolean mCardLayout;
    private ContainerView mContainer;
    private boolean mExpanded;
    private EventDetailsHeaderView mHeaderView;
    private boolean mLandscape;
    private EventDetailsMainLayout mMainLayout;
    private EventDetailsSecondaryLayout mSecondaryLayout;
    private boolean mUserClick;
    
    public EventDetailsCardLayout(Context context)
    {
        this(context, null);
    }

    public EventDetailsCardLayout(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public EventDetailsCardLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        if(!sInitialized)
        {
            Resources resources = context.getResources();
            sSecondaryPadding = resources.getDimensionPixelSize(R.dimen.event_card_details_secondary_padding);
            sScrollingSecondaryPadding = resources.getDimensionPixelSize(R.dimen.event_card_details_secondary_scroll_padding);
            sTwoSpanLayoutDividerPercentage = resources.getDimension(R.dimen.event_card_devails_percent_divider);
            sBackground = (NinePatchDrawable)resources.getDrawable(R.drawable.bg_tacos);
            sPaddingLeft = (int)resources.getDimension(R.dimen.card_border_left_padding);
            sPaddingTop = (int)resources.getDimension(R.dimen.card_border_top_padding);
            sPaddingRight = (int)resources.getDimension(R.dimen.card_border_right_padding);
            sPaddingBottom = (int)resources.getDimension(R.dimen.card_border_bottom_padding);
            sPadding = (int)resources.getDimension(R.dimen.event_card_padding);
            sInitialized = true;
        }
        boolean flag;
        int j;
        boolean flag1;
        ContainerView containerview;
        byte byte0;
        byte byte1;
        if(context.getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        mLandscape = flag;
        j = ScreenMetrics.getInstance(context).screenDisplayType;
        flag1 = false;
        if(j == 1)
            flag1 = true;
        mCardLayout = flag1;
        mContainer = new ContainerView(context, attributeset, i);
        containerview = mContainer;
        if(mLandscape)
            byte0 = -2;
        else
            byte0 = -1;
        containerview.setLayoutParams(new android.widget.FrameLayout.LayoutParams(-1, byte0));
        addView(mContainer);
        mHeaderView = new EventDetailsHeaderView(context, attributeset, i);
        mHeaderView.setId(R.id.event_header_view);
        mContainer.addView(mHeaderView);
        mExpanded = mLandscape;
        mMainLayout = new EventDetailsMainLayout(context, attributeset, i);
        if(mExpanded)
            mContainer.addView(mMainLayout);
        mSecondaryLayout = new EventDetailsSecondaryLayout(context, attributeset, i);
        if(mExpanded)
            mContainer.addView(mSecondaryLayout);
        if(mCardLayout)
            byte1 = 2;
        else
            byte1 = 1;
        if(mLandscape)
            setLayoutParams(new ColumnGridView.LayoutParams(1, -2, byte1, byte1));
        else
            setLayoutParams(new ColumnGridView.LayoutParams(2, -2, byte1, byte1));
    }

    private void toggleExpansion()
    {
        boolean flag = true;
        if(!mExpanded)
        {
            mContainer.addView(mMainLayout);
            mContainer.addView(mSecondaryLayout);
            mHeaderView.setExpandState(flag);
        } else
        {
            mContainer.removeView(mMainLayout);
            mContainer.removeView(mSecondaryLayout);
            mHeaderView.setExpandState(false);
        }
        if(mExpanded)
            flag = false;
        mExpanded = flag;
    }

    public final void bind(Event event, EventActiveState eventactivestate, EventActionListener eventactionlistener)
    {
        Object obj;
        EventDetailsHeaderView eventdetailsheaderview;
        if(!mUserClick && !eventactivestate.hasUserInteracted)
        {
            boolean flag;
            if(!mLandscape && !eventactivestate.expanded)
                flag = true;
            else
                flag = false;
            if(flag && mExpanded)
                toggleExpansion();
            else
            if(!flag && !mExpanded)
                toggleExpansion();
        }
        eventdetailsheaderview = mHeaderView;
        if(mLandscape)
            obj = null;
        else
            obj = this;
        eventdetailsheaderview.bind(event, ((android.view.View.OnClickListener) (obj)), mCardLayout, eventactionlistener);
        mMainLayout.bind(event, eventactivestate, eventactionlistener);
        mSecondaryLayout.bind(event, eventactivestate, eventactionlistener);
    }

    public void onClick(View view)
    {
        toggleExpansion();
        mUserClick = true;
    }

    public void onDraw(Canvas canvas)
    {
        sBackground.setBounds(0, 0, getMeasuredWidth(), mContainer.getMeasuredHeight());
        sBackground.draw(canvas);
        super.onDraw(canvas);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        super.onLayout(flag, i, j, k, l);
        int i1 = getMeasuredWidth();
        mContainer.layout(0, 0, i1, mContainer.getMeasuredHeight());
        int j1 = mHeaderView.getMeasuredHeight();
        int k1 = j1 + sPadding;
        mHeaderView.layout(sPaddingLeft, 0, i1, j1);
        int _tmp = sPaddingLeft;
        int _tmp1 = sPaddingRight;
        if(mExpanded)
        {
            int l1 = sPaddingLeft + mMainLayout.getMeasuredWidth();
            int i2 = k1 + mMainLayout.getMeasuredHeight();
            mMainLayout.layout(sPaddingLeft, k1, l1, i2);
            mHeaderView.setLayoutType(mCardLayout);
            if(mCardLayout)
            {
                mContainer.setDivider(l1, k1);
                mSecondaryLayout.layout(l1 + sSecondaryPadding, k1, l1 + sSecondaryPadding + mSecondaryLayout.getMeasuredWidth(), k1 + mSecondaryLayout.getMeasuredHeight());
            } else
            {
                mContainer.clearDivider();
                int j2 = i2 + sScrollingSecondaryPadding;
                mSecondaryLayout.layout(sPaddingLeft + sSecondaryPadding, j2, sPaddingLeft + sSecondaryPadding + mSecondaryLayout.getMeasuredWidth(), j2 + mSecondaryLayout.getMeasuredHeight());
            }
        }
    }

    protected void onMeasure(int i, int j)
    {
    	int l1;
        int i2;
        int j2;
        super.onMeasure(i, j);
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        if(k == 0)
            k = l;
        int i1 = sPaddingTop;
        int j1 = k - (sPaddingLeft + sPaddingRight);
        mHeaderView.measure(android.view.View.MeasureSpec.makeMeasureSpec(j1, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(l, 0));
        int k1 = i1 + (mHeaderView.getMeasuredHeight() + sPadding);
        if(mExpanded)
        {
            if(mCardLayout)
            {
                i2 = (int)((float)j1 * sTwoSpanLayoutDividerPercentage);
                j2 = j1 - i2 - 2 * sSecondaryPadding;
            } else
            {
                i2 = j1;
                j2 = j1 - 2 * sSecondaryPadding;
            }
            mMainLayout.measure(android.view.View.MeasureSpec.makeMeasureSpec(i2, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(l, 0));
            mSecondaryLayout.measure(android.view.View.MeasureSpec.makeMeasureSpec(j2, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(l, 0));
            if(mCardLayout)
                k1 += Math.max(mMainLayout.getMeasuredHeight(), mSecondaryLayout.getMeasuredHeight());
            else
                k1 += mMainLayout.getMeasuredHeight() + mSecondaryLayout.getMeasuredHeight() + sScrollingSecondaryPadding;
        }
        l1 = k1 + sPaddingBottom;
        mContainer.measure(android.view.View.MeasureSpec.makeMeasureSpec(j1, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(Math.max(l1, l), 0x40000000));
        if(!mLandscape)
        {
            l = l1;
            mContainer.measure(android.view.View.MeasureSpec.makeMeasureSpec(j1, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(l, 0x40000000));
        }
        setMeasuredDimension(j1 + sPaddingLeft + sPaddingRight, l);
    }

    public void onRecycle()
    {
        mHeaderView.onRecycle();
        mMainLayout.clear();
        mSecondaryLayout.clear();
    }
    
    private static class ContainerView extends ViewGroup
    {

    	private int mDividerLeft;
        private Paint mDividerPaint;
        private int mDividerTop;
        private boolean mDrawDivider;

        public ContainerView(Context context, AttributeSet attributeset, int i)
        {
            super(context, attributeset, i);
            Resources resources = context.getResources();
            mDividerPaint = new Paint();
            mDividerPaint.setColor(resources.getColor(R.color.card_event_divider));
            mDividerPaint.setStrokeWidth(resources.getDimension(R.dimen.event_card_divider_stroke_width));
            setWillNotDraw(false);
        }
        
        public final void clearDivider()
        {
            mDrawDivider = false;
        }

        protected void onDraw(Canvas canvas)
        {
            if(mDrawDivider)
                canvas.drawLine(mDividerLeft, mDividerTop, mDividerLeft, getMeasuredHeight() - EventDetailsCardLayout.sPaddingBottom, mDividerPaint);
            super.onDraw(canvas);
        }

        protected void onLayout(boolean flag, int i, int j, int k, int l)
        {
        }

        public final void setDivider(int i, int j)
        {
            mDrawDivider = true;
            mDividerLeft = i;
            mDividerTop = j;
        }
    }
}
