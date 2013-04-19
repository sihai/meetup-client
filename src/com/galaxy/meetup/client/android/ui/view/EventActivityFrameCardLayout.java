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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.util.Dates;

/**
 * 
 * @author sihai
 *
 */
public class EventActivityFrameCardLayout extends CardViewLayout {

	private static int sAvatarLineupMarginBottom;
    private static int sAvatarLineupMarginLeft;
    private static int sAvatarLineupMarginRight;
    private static Drawable sCheckinIconDrawable;
    private static int sDateTextColor;
    private static int sDateTextSize;
    private static Drawable sGoingIconDrawable;
    private static boolean sInitialized;
    private static Drawable sInvitedIconDrawable;
    private static int sPaddingBottom;
    private static int sPaddingLeft;
    private static int sPaddingRight;
    private static int sPaddingTop;
    private AvatarLineupLayout mAvatarLineup;
    private TextView mDate;
    private TextView mDescription;
    private ImageView mIcon;
    
    public EventActivityFrameCardLayout(Context context)
    {
        super(context);
    }

    public EventActivityFrameCardLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public EventActivityFrameCardLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    private CharSequence getText(int i, ArrayList arraylist)
    {
        Resources resources = getContext().getResources();
        String as[] = null;
        switch(i) {
	        case 1:
	        	as = resources.getStringArray(R.array.event_activity_checked_in_strings);
	        	break;
	        case 2:
	        	as = resources.getStringArray(R.array.event_activity_invite_strings);
	        	break;
	        case 3:
	        	as = resources.getStringArray(R.array.event_activity_rsvp_no_strings);
	        	break;
	        case 4:
	        	as = resources.getStringArray(R.array.event_activity_rsvp_yes_strings);
	        	break;
        	default:
        		 break;
        }
        
        String s = null;
        if(as != null)
            if(arraylist.size() >= as.length)
            {
                String s2 = as[-1 + as.length];
                Object aobj1[] = new Object[1];
                aobj1[0] = Integer.valueOf(arraylist.size());
                s = String.format(s2, aobj1);
            } else
            {
                ArrayList arraylist1 = new ArrayList();
                Iterator iterator = arraylist.iterator();
                while(iterator.hasNext()) 
                {
                    EsEventData.EventPerson eventperson = (EsEventData.EventPerson)iterator.next();
                    String s1;
                    if(eventperson.numAdditionalGuests == 0)
                    {
                        s1 = eventperson.name;
                    } else
                    {
                        int k = R.plurals.event_invitee_with_guests;
                        int l = eventperson.numAdditionalGuests;
                        Object aobj[] = new Object[2];
                        aobj[0] = eventperson.name;
                        aobj[1] = Integer.valueOf(eventperson.numAdditionalGuests);
                        s1 = resources.getQuantityString(k, l, aobj);
                    }
                    arraylist1.add(s1);
                }
                int j = arraylist1.size();
                s = null;
                if(j > 0)
                    s = String.format(as[-1 + arraylist1.size()], arraylist1.toArray());
            }
        return s;
    }

    public final void bind(int i, long l, List list, EventActionListener eventactionlistener)
    {
        Drawable drawable = null;
        switch(i) {
	        case 1:
	        	drawable = sCheckinIconDrawable;
	        	break;
	        case 2:
	        	drawable = sInvitedIconDrawable;
	        	break;
	        case 3:
	        	break;
	        case 4:
	        	drawable = sGoingIconDrawable;
	        	break;
        	default:
        		break;
        }
        
        ArrayList arraylist;
        int j;
        if(drawable != null)
            mIcon.setImageDrawable(drawable);
        arraylist = new ArrayList();
        j = list.size();
        for(int k = 0; k < j; k++)
        {
            EsEventData.EventPerson eventperson = (EsEventData.EventPerson)list.get(k);
            if(eventperson.name != null)
                arraylist.add(eventperson);
        }
        
        mAvatarLineup.bind(arraylist, eventactionlistener, j);
        mDate.setText(Dates.getRelativeTimeSpanString(getContext(), l));
        mDescription.setText(getText(i, arraylist));
    }

    protected final void init(Context context, AttributeSet attributeset, int i)
    {
        super.init(context, attributeset, i);
        if(!sInitialized)
        {
            Resources resources = context.getResources();
            sInvitedIconDrawable = resources.getDrawable(R.drawable.icn_events_activity_invited);
            sGoingIconDrawable = resources.getDrawable(R.drawable.icn_events_activity_going);
            sCheckinIconDrawable = resources.getDrawable(R.drawable.icn_events_activity_checkin);
            sDateTextColor = resources.getColor(R.color.event_card_activity_time_color);
            sDateTextSize = resources.getDimensionPixelSize(R.dimen.event_card_activity_time_size);
            sPaddingLeft = resources.getDimensionPixelSize(R.dimen.event_card_activity_padding_left);
            sPaddingRight = resources.getDimensionPixelSize(R.dimen.event_card_activity_padding_right);
            sPaddingTop = resources.getDimensionPixelSize(R.dimen.event_card_activity_padding_top);
            sPaddingBottom = resources.getDimensionPixelSize(R.dimen.event_card_activity_padding_bottom);
            sAvatarLineupMarginLeft = resources.getDimensionPixelSize(R.dimen.event_card_activity_avatar_lineup_margin_left);
            sAvatarLineupMarginRight = resources.getDimensionPixelSize(R.dimen.event_card_activity_avatar_lineup_margin_right);
            sAvatarLineupMarginBottom = resources.getDimensionPixelSize(R.dimen.event_card_activity_avatar_lineup_margin_bottom);
            sInitialized = true;
        }
        addPadding(sPaddingLeft, sPaddingTop, sPaddingRight, sPaddingBottom);
        mDate = new TextView(context, attributeset, i);
        mDate.setLayoutParams(new ExactLayout.LayoutParams(-2, -2));
        mDate.setTextColor(sDateTextColor);
        mDate.setTextSize(0, sDateTextSize);
        addView(mDate);
        mIcon = new ImageView(context, attributeset, i);
        addView(mIcon);
        mAvatarLineup = new AvatarLineupLayout(context, attributeset, i);
        addView(mAvatarLineup);
        mDescription = new TextView(context, attributeset, i);
        mDescription.setLayoutParams(new ExactLayout.LayoutParams(-2, -2));
        addView(mDescription);
    }

    protected void measureChildren(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        int i1 = k + 0;
        int j1 = l + 0;
        mIcon.measure(android.view.View.MeasureSpec.makeMeasureSpec(k, 0x80000000), android.view.View.MeasureSpec.makeMeasureSpec(l, 0x80000000));
        int k1 = mIcon.getMeasuredWidth();
        setCorner(mIcon, 0, 0);
        int l1 = k - k1;
        mDate.measure(android.view.View.MeasureSpec.makeMeasureSpec(l1, 0x80000000), android.view.View.MeasureSpec.makeMeasureSpec(l, 0x80000000));
        int i2 = mDate.getMeasuredWidth();
        int j2 = i1 - i2;
        setCorner(mDate, j2, 0);
        int k2 = l1 - i2 - (sAvatarLineupMarginLeft + sAvatarLineupMarginRight);
        mAvatarLineup.measure(android.view.View.MeasureSpec.makeMeasureSpec(k2, 0x80000000), android.view.View.MeasureSpec.makeMeasureSpec(l, 0x80000000));
        int l2 = 0 + mIcon.getMeasuredWidth() + sAvatarLineupMarginLeft;
        setCorner(mAvatarLineup, l2, 0);
        View aview[] = new View[3];
        aview[0] = mAvatarLineup;
        aview[1] = mDate;
        aview[2] = mIcon;
        int i3 = getMaxHeight(aview);
        View aview1[] = new View[3];
        aview1[0] = mAvatarLineup;
        aview1[1] = mDate;
        aview1[2] = mIcon;
        verticallyCenter(i3, aview1);
        int j3 = 0 + mAvatarLineup.getMeasuredHeight() + sAvatarLineupMarginBottom;
        setCorner(mDescription, 0, j3);
        mDescription.measure(android.view.View.MeasureSpec.makeMeasureSpec(k, 0x80000000), android.view.View.MeasureSpec.makeMeasureSpec(j1 - j3, android.view.View.MeasureSpec.getMode(j)));
    }

    public void onRecycle()
    {
        super.onRecycle();
        mIcon.setImageDrawable(null);
        mDate.setText(null);
        mDescription.setText(null);
        mAvatarLineup.clear();
    }
}
