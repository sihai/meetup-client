/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.server.client.domain.Invitee;
import com.galaxy.meetup.server.client.domain.InviteeSummary;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.v2.domain.Event;
import com.galaxy.meetup.server.client.v2.domain.EventMember;

/**
 * 
 * @author sihai
 *
 */
public class EventInviteeSummaryLayout extends ExactLayout {

	private static int sFontColor;
    private static float sFontSize;
    private static String sGuestsFormat;
    private static boolean sInitialized;
    private static String sRsvpInvitedFormat;
    private static String sRsvpInvitedPastFormat;
    private static String sRsvpMaybeFormat;
    private static String sRsvpYesFormat;
    private static String sRsvpYesPastFormat;
    private AvatarLineupLayout mLineupLayout;
    private int mSize;
    private TextView mStatus;
    private int mVisibleSize;
    
    public EventInviteeSummaryLayout(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public EventInviteeSummaryLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(context, attributeset, 0);
    }

    public EventInviteeSummaryLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(context, attributeset, i);
    }

    private static int getGaiaIds(Event event, String s, List<String> userNameList) {
        List<EventMember> emList = event.getMemberList();
        for(EventMember em : emList) {
        	userNameList.add(em.getUserName());
        }
        return userNameList.size();
    }
    
    private static int getGaiaIds(PlusEvent plusevent, String s, ArrayList arraylist)
    {
        InviteeSummary inviteesummary = EsEventData.getInviteeSummary(plusevent, s);
        int i = 0;
        if(inviteesummary != null)
        {
            if(inviteesummary.invitee != null)
            {
                Iterator iterator = inviteesummary.invitee.iterator();
                do
                {
                    if(!iterator.hasNext())
                        break;
                    Invitee invitee = (Invitee)iterator.next();
                    if(invitee.invitee != null)
                    {
                        String s1 = invitee.invitee.ownerObfuscatedId;
                        if(s1 != null)
                            arraylist.add(s1);
                    }
                } while(true);
            }
            i = inviteesummary.count.intValue();
        }
        return i;
    }

    private void init(Context context, AttributeSet attributeset, int i)
    {
        if(!sInitialized)
        {
            Resources resources = context.getResources();
            sRsvpYesFormat = resources.getString(R.string.event_detail_rsvp_yes_count);
            sRsvpYesPastFormat = resources.getString(R.string.event_detail_rsvp_yes_count_past);
            sGuestsFormat = resources.getString(R.string.event_details_rsvp_guests_count);
            sRsvpMaybeFormat = resources.getString(R.string.event_detail_rsvp_maybe_count);
            sRsvpInvitedFormat = resources.getString(R.string.event_detail_rsvp_invited_count);
            sRsvpInvitedPastFormat = resources.getString(R.string.event_detail_rsvp_invited_count_past);
            sFontSize = resources.getDimension(R.dimen.event_card_details_rsvp_count_size);
            sFontColor = resources.getColor(R.color.event_card_details_rsvp_count_color);
            sInitialized = true;
        }
        mStatus = TextViewUtils.createText(context, attributeset, i, sFontSize, sFontColor, false, true);
        addView(mStatus);
        mLineupLayout = new AvatarLineupLayout(context, attributeset, i);
        addView(mLineupLayout);
        mVisibleSize = 0;
        mSize = 0;
    }

    public final void bind(Event plusevent, EventActionListener eventactionlistener, boolean flag)
    {
        ArrayList arraylist = new ArrayList();
        mSize = 0 + getGaiaIds(plusevent, "ATTENDING", arraylist) + getGaiaIds(plusevent, "MAYBE", arraylist) + getGaiaIds(plusevent, "NOT_RESPONDED", arraylist);
        mLineupLayout.bindIds(arraylist, eventactionlistener, mSize);
        mVisibleSize = arraylist.size();
        String s = sGuestsFormat;
        TextView textview = mStatus;
        Object aobj[] = new Object[1];
        aobj[0] = Integer.valueOf(mSize);
        textview.setText(String.format(s, aobj));
        requestLayout();
    }

    public final void clear()
    {
        mVisibleSize = 0;
        mSize = 0;
    }

    protected void measureChildren(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        measure(mStatus, k, 0x80000000, 0, 0);
        setCorner(mStatus, 0, 0);
        int l = 0 + mStatus.getMeasuredHeight();
        if(mVisibleSize > 0)
        {
            measure(mLineupLayout, k, 0x80000000, 0, 0);
            setCorner(mLineupLayout, 0, l);
            mLineupLayout.getMeasuredHeight();
        }
    }

    public final int size()
    {
        return mSize;
    }
}
