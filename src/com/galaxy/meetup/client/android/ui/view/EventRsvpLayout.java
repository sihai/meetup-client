/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AlphaAnimation;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.ui.fragments.EventActiveState;
import com.galaxy.meetup.server.client.v2.domain.Event;

/**
 * 
 * @author sihai
 *
 */
public class EventRsvpLayout extends ExactLayout implements EventRsvpListener {

	private static int sBackgroundColor;
    private static Paint sDividerPaint;
    private static boolean sInitialized;
    private static int sRsvpSectionHeight;
    private boolean mEventOver;
    private EventActionListener mListener;
    private int mMeasuredWidth;
    private EventRsvpButtonLayout mRsvpButtonLayout;
    private EventRsvpSpinnerLayout mRsvpSpinnerLayout;
    
    public EventRsvpLayout(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public EventRsvpLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(context, attributeset, 0);
    }

    public EventRsvpLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(context, attributeset, i);
    }

    private void init(Context context, AttributeSet attributeset, int i)
    {
        if(!sInitialized)
        {
            Resources resources = context.getResources();
            sBackgroundColor = resources.getColor(R.color.event_card_details_rsvp_background);
            Paint paint = new Paint();
            sDividerPaint = paint;
            paint.setColor(resources.getColor(R.color.card_event_divider));
            sDividerPaint.setStrokeWidth(resources.getDimension(R.dimen.event_card_divider_stroke_width));
            Paint paint1 = new Paint();
            sDividerPaint = paint1;
            paint1.setColor(resources.getColor(R.color.card_event_divider));
            sDividerPaint.setStrokeWidth(resources.getDimension(R.dimen.event_card_divider_stroke_width));
            sRsvpSectionHeight = resources.getDimensionPixelSize(R.dimen.event_card_details_rsvp_height);
            sInitialized = true;
        }
        setBackgroundColor(sBackgroundColor);
        mRsvpButtonLayout = (EventRsvpButtonLayout)((LayoutInflater)context.getSystemService("layout_inflater")).inflate(R.layout.event_rsvp_button_layout, null);
        addView(mRsvpButtonLayout);
        mRsvpSpinnerLayout = new EventRsvpSpinnerLayout(context, attributeset, i);
        mRsvpSpinnerLayout.setVisibility(4);
        addView(mRsvpSpinnerLayout);
        setWillNotDraw(false);
    }

    private void setRsvpView(String s, boolean flag)
    {
        boolean flag1;
        if(TextUtils.equals("MAYBE", s) || TextUtils.equals("NOT_RESPONDED", s))
            flag1 = true;
        else
            flag1 = false;
        if(!TextUtils.equals("NOT_RESPONDED", s) && (!mEventOver || !flag1))
        {
            int i = mRsvpButtonLayout.getVisibility();
            mRsvpButtonLayout.setVisibility(4);
            mRsvpSpinnerLayout.setVisibility(0);
            if(flag && i == 0)
            {
                AlphaAnimation alphaanimation = new AlphaAnimation(1.0F, 0.0F);
                alphaanimation.setDuration(500L);
                alphaanimation.setFillAfter(true);
                AlphaAnimation alphaanimation1 = new AlphaAnimation(0.0F, 1.0F);
                alphaanimation1.setDuration(500L);
                alphaanimation1.setFillAfter(true);
                mRsvpButtonLayout.startAnimation(alphaanimation);
                mRsvpSpinnerLayout.startAnimation(alphaanimation1);
            }
        } else
        {
            mRsvpButtonLayout.setVisibility(0);
            mRsvpSpinnerLayout.setVisibility(4);
        }
    }

    public final void bind(Event event, EventActiveState eventactivestate, EventActionListener eventactionlistener)
    {
        mListener = eventactionlistener;
        mEventOver = EsEventData.isEventOver(event, System.currentTimeMillis());
        mRsvpSpinnerLayout.bind(event, eventactivestate, this, eventactionlistener);
        mRsvpButtonLayout.bind(this, mEventOver);
        setRsvpView(EsEventData.getRsvpType(event), false);
    }

    protected void measureChildren(int i, int j)
    {
        mMeasuredWidth = android.view.View.MeasureSpec.getSize(i);
        int k = sRsvpSectionHeight;
        measure(mRsvpButtonLayout, mMeasuredWidth, 0x40000000, 0, 0);
        int l = Math.max(k, mRsvpButtonLayout.getMeasuredHeight());
        measure(mRsvpSpinnerLayout, mMeasuredWidth, 0x40000000, 0, 0);
        int i1 = Math.max(l, mRsvpSpinnerLayout.getMeasuredHeight());
        measure(mRsvpButtonLayout, mMeasuredWidth, 0x40000000, i1, 0x40000000);
        setCorner(mRsvpButtonLayout, 0, 0 + Math.max(0, (i1 - mRsvpButtonLayout.getMeasuredHeight()) / 2));
        measure(mRsvpSpinnerLayout, mMeasuredWidth, 0x40000000, i1, 0x40000000);
        setCorner(mRsvpSpinnerLayout, 0, 0 + Math.max(0, (i1 - mRsvpSpinnerLayout.getMeasuredHeight()) / 2));
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawLine(0.0F, 0.0F, mMeasuredWidth, 0.0F, sDividerPaint);
    }

    public final void onRsvpChanged(String s)
    {
        if(mListener != null)
        {
            setRsvpView(s, true);
            mListener.onRsvpChanged(s);
        }
    }
}
