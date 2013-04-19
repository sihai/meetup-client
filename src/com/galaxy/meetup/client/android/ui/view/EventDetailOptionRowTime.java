/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.util.EventDateUtils;
import com.galaxy.meetup.client.util.TimeZoneHelper;
import com.galaxy.meetup.server.client.domain.EventTime;
import com.galaxy.meetup.server.client.domain.PlusEvent;

/**
 * 
 * @author sihai
 *
 */
public class EventDetailOptionRowTime extends EventDetailsOptionRowLayout {

	private static Drawable sClockIconDrawabale;
    private ImageView mClockIcon;
    private boolean sInitialized;
    
    public EventDetailOptionRowTime(Context context)
    {
        super(context);
    }

    public EventDetailOptionRowTime(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public EventDetailOptionRowTime(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public final void bind(PlusEvent plusevent)
    {
        Context context = getContext();
        EventTime eventtime = plusevent.startTime;
        java.util.TimeZone timezone = null;
        if(eventtime != null)
        {
            String s3 = plusevent.startTime.timezone;
            timezone = null;
            if(s3 != null)
                timezone = TimeZoneHelper.getSystemTimeZone(plusevent.startTime.timezone);
        }
        EventTime eventtime1 = plusevent.startTime;
        String s = null;
        if(eventtime1 != null)
        {
            Long long2 = plusevent.startTime.timeMs;
            s = null;
            if(long2 != null)
                s = EventDateUtils.getSingleDisplayLine(context, plusevent.startTime, null, false, timezone);
        }
        EventTime eventtime2 = plusevent.endTime;
        String s1 = null;
        if(eventtime2 != null)
        {
            Long long1 = plusevent.endTime.timeMs;
            s1 = null;
            if(long1 != null)
                s1 = EventDateUtils.getSingleDisplayLine(context, plusevent.endTime, null, true, timezone);
        }
        ArrayList arraylist = new ArrayList();
        if(s1 != null)
            arraylist.add(s1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(plusevent.startTime.timeMs.longValue());
        String s2 = TimeZoneHelper.getDisplayString(plusevent.startTime.timezone, calendar, EsEventData.isEventHangout(plusevent));
        if(s2 != null)
            arraylist.add(s2);
        super.bind(s, arraylist, mClockIcon, null);
    }

    protected final void init(Context context, AttributeSet attributeset, int i)
    {
        super.init(context, attributeset, i);
        TimeZoneHelper.initialize(context);
        if(!sInitialized)
        {
            sClockIconDrawabale = context.getResources().getDrawable(R.drawable.icn_events_details_time);
            sInitialized = true;
        }
        mClockIcon = new ImageView(context, attributeset, i);
        mClockIcon.setImageDrawable(sClockIconDrawabale);
    }
}
