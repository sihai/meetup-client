/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.ui.fragments.EventActiveState;
import com.galaxy.meetup.server.client.domain.PlusEvent;

/**
 * 
 * @author sihai
 *
 */
public class EventDetailsSecondaryLayout extends ExactLayout implements android.view.View.OnClickListener {
	
	private static boolean sInitialized;
    private static int sPadding;
    private static int sSeeInviteesTextColor;
    private static int sSeeInvitesHeight;
    private static String sSeeInvitesText;
    private static float sSeeInvitesTextSize;
    private EventInviteeSummaryLayout mGuestSummary;
    private boolean mHideInvitees;
    private EventActionListener mListener;
    private TextView mViewInvitees;
    
    public EventDetailsSecondaryLayout(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public EventDetailsSecondaryLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(context, attributeset, 0);
    }

    public EventDetailsSecondaryLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(context, attributeset, i);
    }

    private void init(Context context, AttributeSet attributeset, int i)
    {
        if(!sInitialized)
        {
            Resources resources = context.getResources();
            sPadding = resources.getDimensionPixelSize(R.dimen.event_card_details_secondary_padding);
            sSeeInviteesTextColor = resources.getColor(R.color.event_card_details_see_invitees_color);
            sSeeInvitesText = resources.getString(R.string.event_button_view_all_invitees);
            sSeeInvitesTextSize = resources.getDimension(R.dimen.event_card_details_see_invitees_size);
            sSeeInvitesHeight = resources.getDimensionPixelSize(R.dimen.event_card_details_see_invitees_height);
            sInitialized = true;
        }
        mGuestSummary = new EventInviteeSummaryLayout(context, attributeset, i);
        addView(mGuestSummary);
        mViewInvitees = new TextView(context, attributeset, i);
        addView(mViewInvitees);
        mViewInvitees.setText(sSeeInvitesText);
        mViewInvitees.setTextSize(0, sSeeInvitesTextSize);
        mViewInvitees.setTextColor(sSeeInviteesTextColor);
        mViewInvitees.setGravity(17);
        mViewInvitees.setClickable(true);
        mViewInvitees.setOnClickListener(this);
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Theme);
        mViewInvitees.setBackgroundDrawable(typedarray.getDrawable(5));
        typedarray.recycle();
        addPadding(0, 0, 0, sPadding);
    }

    public final void bind(PlusEvent plusevent, EventActiveState eventactivestate, EventActionListener eventactionlistener)
    {
        boolean flag = true;
        boolean flag1;
        byte byte0;
        if(System.currentTimeMillis() > EsEventData.getEventEndTime(plusevent))
            flag1 = flag;
        else
            flag1 = false;
        if(plusevent.eventOptions == null || plusevent.eventOptions.hideGuestList == null || !plusevent.eventOptions.hideGuestList.booleanValue() || eventactivestate.isOwner)
            flag = false;
        mHideInvitees = flag;
        if(mHideInvitees)
            byte0 = 8;
        else
            byte0 = 0;
        mViewInvitees.setVisibility(byte0);
        mGuestSummary.setVisibility(byte0);
        mGuestSummary.bind(plusevent, eventactionlistener, flag1);
        mListener = eventactionlistener;
        invalidate();
    }

    public final void clear()
    {
        mGuestSummary.clear();
        mListener = null;
    }

    protected void measureChildren(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        if(!mHideInvitees)
        {
            EventInviteeSummaryLayout eventinviteesummarylayout = mGuestSummary;
            int l;
            int i1;
            if(eventinviteesummarylayout.size() > 0)
            {
                int j1 = 0 + sPadding;
                measure(eventinviteesummarylayout, k, 0x40000000, 0, 0);
                setCorner(eventinviteesummarylayout, 0, 0);
                l = j1 + eventinviteesummarylayout.getMeasuredHeight();
                eventinviteesummarylayout.setVisibility(0);
            } else
            {
                eventinviteesummarylayout.setVisibility(8);
                l = 0;
            }
            i1 = l + 0;
            measure(mViewInvitees, k, 0x40000000, sSeeInvitesHeight, 0x40000000);
            setCorner(mViewInvitees, 0, i1);
            mViewInvitees.getMeasuredHeight();
        }
    }

    public void onClick(View view)
    {
        if(view == mViewInvitees && mListener != null)
            mListener.onViewAllInviteesClicked();
    }
}
