/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.server.client.v2.domain.EventTime;

/**
 * 
 * @author sihai
 *
 */
public class EventDateUtils {

	private static String sAbsoluteDateFormat;
    private static String sDateTimeZoneFormat;
    private static String sEndDateFormat;
    private static DateFormat sLongDateFormatter;
    private static DateFormat sMediumDateFormatter;
    private static String sRelativeBeginDateFormat;
    private static String sRelativeEndDateFormat;
    private static String sStartDateFormat;
    private static DateFormat sTimeFormatter;
    private static String sToday;
    private static long sTodayMsec;
    private static String sTomorrow;
    private static long sTomorrowMsec;
    private static String sYesterday;
    private static long sYesterdayMsec;
    
	private static String format(DateFormat dateformat, Date date, TimeZone timezone)
    {
        TimeZone timezone1 = dateformat.getTimeZone();
        if(timezone != null)
            dateformat.setTimeZone(timezone);
        String s = dateformat.format(date);
        dateformat.setTimeZone(timezone1);
        return s;
    }

    public static String getDateRange(Context context, EventTime eventtime, EventTime eventtime1, boolean flag)
    {
        initializeStrings(context);
        long l = eventtime.getTimeMs().longValue();
        return String.format("%s %s", new Object[] {
            getSingleDateDisplayLine(context, l), getDisplayTime(context, l)
        });
    }

    private static String getDisplayTime(Context context, long l)
    {
        initializeFormats(context);
        String s;
        synchronized(sTimeFormatter)
        {
            s = sTimeFormatter.format(Long.valueOf(l));
        }
        return s;
    }

    public static String getDisplayTime(Context context, long l, TimeZone timezone)
    {
        initializeFormats(context);
        String s;
        synchronized(sTimeFormatter)
        {
            TimeZone timezone1 = sTimeFormatter.getTimeZone();
            sTimeFormatter.setTimeZone(timezone);
            s = sTimeFormatter.format(Long.valueOf(l));
            sTimeFormatter.setTimeZone(timezone1);
        }
        return s;
    }

    private static String getSingleDateDisplayLine(Context context, long l)
    {
        initializeFormats(context);
        Date date = new Date(l);
        String s;
        synchronized(sLongDateFormatter)
        {
            s = sLongDateFormatter.format(date);
        }
        return s;
    }

    public static String getSingleDateDisplayLine(Context context, long l, TimeZone timezone)
    {
        initializeFormats(context);
        Date date = new Date(l);
        String s;
        synchronized(sLongDateFormatter)
        {
            TimeZone timezone1 = sLongDateFormatter.getTimeZone();
            sLongDateFormatter.setTimeZone(timezone);
            s = sLongDateFormatter.format(date);
            sLongDateFormatter.setTimeZone(timezone1);
        }
        return s;
    }

    public static String getSingleDisplayLine(Context context, EventTime eventtime, String s, boolean flag, TimeZone timezone)
    {
        long l = eventtime.getTimeMs().longValue();
        Date date = new Date(l);
        initializeFormats(context);
        initializeStrings(context);
        String s1;
        String s5;
        if(l > sTodayMsec && l < 0x5265c00L + sTodayMsec)
            s1 = sToday;
        else
        if(l > sTomorrowMsec && l < 0x5265c00L + sTomorrowMsec)
        {
            s1 = sTomorrow;
        } else
        {
            long i = l - sYesterdayMsec;
            s1 = null;
            if(i > 0)
            {
                long j = l - (0x5265c00L + sYesterdayMsec);
                s1 = null;
                if(j < 0)
                    s1 = sYesterday;
            }
        }
        if(timezone == null && eventtime != null && !TextUtils.isEmpty(eventtime.getTimezone()))
        {
            timezone = TimeZoneHelper.getSystemTimeZone(eventtime.getTimezone());
            if(!TimeZoneHelper.areTimeZoneIdsEquivalent(eventtime.getTimezone(), timezone.getID()))
                timezone = null;
        }
        if(s1 != null)
        {
            String s6;
            Object aobj1[];
            if(flag)
                s6 = sRelativeEndDateFormat;
            else
                s6 = sRelativeBeginDateFormat;
            aobj1 = new Object[2];
            aobj1[0] = s1;
            aobj1[1] = format(sTimeFormatter, date, timezone);
            s5 = String.format(s6, aobj1);
        } else
        {
            String s2 = sAbsoluteDateFormat;
            Object aobj[] = new Object[2];
            aobj[0] = format(sMediumDateFormatter, date, timezone);
            aobj[1] = format(sTimeFormatter, date, timezone);
            String s3 = String.format(s2, aobj);
            String s4;
            if(flag)
                s4 = sEndDateFormat;
            else
                s4 = sStartDateFormat;
            s5 = String.format(s4, new Object[] {
                s3
            });
        }
        if(!TextUtils.isEmpty(null))
            s5 = String.format(sDateTimeZoneFormat, new Object[] {
                s5, null
            });
        return s5;
    }

    public static void initializeFormats(Context context)
    {
        if(sMediumDateFormatter == null)
        {
            sMediumDateFormatter = android.text.format.DateFormat.getMediumDateFormat(context);
            sLongDateFormatter = android.text.format.DateFormat.getLongDateFormat(context);
            sTimeFormatter = android.text.format.DateFormat.getTimeFormat(context);
        }
    }

    private static void initializeStrings(Context context)
    {
        if(sToday == null)
        {
            sToday = context.getString(R.string.today);
            sTomorrow = context.getString(R.string.tomorrow);
            sYesterday = context.getString(R.string.yesterday);
            sRelativeBeginDateFormat = context.getString(R.string.event_relative_start_date_format);
            sRelativeEndDateFormat = context.getString(R.string.event_relative_end_date_format);
            sAbsoluteDateFormat = context.getString(R.string.event_absolute_date_format);
            sEndDateFormat = context.getString(R.string.event_end_date_format);
            sStartDateFormat = context.getString(R.string.event_start_date_format);
            sDateTimeZoneFormat = context.getString(R.string.event_card_start_time);
        }
        Calendar calendar = Calendar.getInstance();
        if(calendar.getTimeInMillis() > sTomorrowMsec)
        {
            calendar.set(11, 0);
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            long l = calendar.getTimeInMillis();
            sTodayMsec = l;
            sTomorrowMsec = l + 0x5265c00L;
            sYesterdayMsec = sTodayMsec - 0x5265c00L;
        }
    }

}
