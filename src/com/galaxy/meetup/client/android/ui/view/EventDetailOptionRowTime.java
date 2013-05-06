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
import com.galaxy.meetup.server.client.v2.domain.Event;
import com.galaxy.meetup.server.client.v2.domain.EventTime;

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

    public final void bind(Event plusevent)
    {
        Context context = getContext();
        EventTime startTime = plusevent.getStartTime();
        java.util.TimeZone timezone = null;
        if(startTime != null)
        {
            String s3 = startTime.getTimezone();
            timezone = null;
            if(s3 != null)
                timezone = TimeZoneHelper.getSystemTimeZone(s3);
        }
        String s = null;
        if(startTime != null)
        {
            Long long2 = startTime.getTimeMs();
            s = null;
            if(long2 != null)
                s = EventDateUtils.getSingleDisplayLine(context, startTime, null, false, timezone);
        }
        EventTime endTime = plusevent.getEndTime();
        String s1 = null;
        if(endTime != null)
        {
            Long long1 = endTime.getTimeMs();
            s1 = null;
            if(long1 != null)
                s1 = EventDateUtils.getSingleDisplayLine(context, endTime, null, true, timezone);
        }
        ArrayList arraylist = new ArrayList();
        if(s1 != null)
            arraylist.add(s1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime.getTimeMs().longValue());
        String s2 = TimeZoneHelper.getDisplayString(startTime.getTimezone(), calendar, EsEventData.isEventHangout(plusevent));
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
