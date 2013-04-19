/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class TimeZoneHelper {

	private static String sGenericTimeZonePrefix;
    private static String sTimeZoneFormat;
    private Calendar mCalendar;
    private LongSparseArray mOffsetToTimeZonesMapping;
    private List mOrderedTimeZoneInfoList;
    
    public static final class TimeZoneGroup
    {

        List mExcludedTimeZoneInfoList;
        HashSet mSeenDisplayNames;
        List mTimeZoneInfoList;

        public TimeZoneGroup()
        {
            mTimeZoneInfoList = new ArrayList();
            mExcludedTimeZoneInfoList = new ArrayList();
            mSeenDisplayNames = new HashSet();
        }
    }

    public static final class TimeZoneInfo implements Comparable
    {

        public final int compareTo(Object obj)
        {
            TimeZoneInfo timezoneinfo = (TimeZoneInfo)obj;
            return mTimeZone.getDisplayName().compareToIgnoreCase(timezoneinfo.mTimeZone.getDisplayName());
        }

        public final long getOffset()
        {
            return mOffset;
        }

        public final int getPosition()
        {
            return mPosition;
        }

        public final TimeZone getTimeZone()
        {
            return mTimeZone;
        }

        public final void setOffset(long l)
        {
            mOffset = l;
        }

        public final void setPosition(int i)
        {
            mPosition = i;
        }

        private long mOffset;
        private int mPosition;
        private TimeZone mTimeZone;

        public TimeZoneInfo(TimeZone timezone)
        {
            mTimeZone = timezone;
            mPosition = -1;
        }
    }


    public TimeZoneHelper(Context context)
    {
        initialize(context);
    }

    public static boolean areTimeZoneIdsEquivalent(String s, String s1)
    {
        boolean flag;
        if(s != null && s1 != null)
            flag = TextUtils.equals(s.toLowerCase(), s1.toLowerCase());
        else
            flag = false;
        return flag;
    }

    private static LongSparseArray buildMapping(String as[], String s, Calendar calendar)
    {
        LongSparseArray longsparsearray = new LongSparseArray();
        int i = -1 + as.length;
        while(i >= 0) 
        {
            TimeZone timezone = getSystemTimeZone(as[i]);
            long l = getOffset(timezone, calendar);
            TimeZoneGroup timezonegroup1 = (TimeZoneGroup)longsparsearray.get(l);
            if(timezonegroup1 == null)
            {
                timezonegroup1 = new TimeZoneGroup();
                longsparsearray.put(l, timezonegroup1);
            }
            TimeZoneInfo timezoneinfo = new TimeZoneInfo(timezone);
            timezoneinfo.setOffset(l);
            String s1 = timezone.getDisplayName();
            if(!timezonegroup1.mSeenDisplayNames.contains(s1))
            {
                if(s != null && s1.startsWith(s))
                    timezonegroup1.mExcludedTimeZoneInfoList.add(timezoneinfo);
                else
                    timezonegroup1.mTimeZoneInfoList.add(timezoneinfo);
                timezonegroup1.mSeenDisplayNames.add(s1);
            }
            i--;
        }
        for(int j = -1 + longsparsearray.size(); j >= 0; j--)
        {
            TimeZoneGroup timezonegroup = (TimeZoneGroup)longsparsearray.get(longsparsearray.keyAt(j));
            if(timezonegroup.mTimeZoneInfoList.isEmpty() && !timezonegroup.mExcludedTimeZoneInfoList.isEmpty())
            {
                Collections.sort(timezonegroup.mExcludedTimeZoneInfoList);
                timezonegroup.mTimeZoneInfoList.add(timezonegroup.mExcludedTimeZoneInfoList.get(0));
            }
            timezonegroup.mExcludedTimeZoneInfoList.clear();
            Collections.sort(timezonegroup.mTimeZoneInfoList);
        }

        return longsparsearray;
    }

    public static String getDisplayString(String s, Calendar calendar, boolean flag) {
        TimeZone timezone;
        TimeZone timezone1;
        timezone = getSystemTimeZone(s);
        timezone1 = calendar.getTimeZone();
        if(timezone == null) 
        	return null; 
        long l = getOffset(timezone1, calendar);
        long l1 = getOffset(timezone, calendar);
        if(!areTimeZoneIdsEquivalent(timezone.getID(), s) || !flag && l == l1) {
        	return null;
        }
        return timezone.getDisplayName();
    }

    private static long getOffset(TimeZone timezone, Calendar calendar) {
        int i = calendar.get(0);
        int j = calendar.get(1);
        int k = calendar.get(5);
        int l = calendar.get(2);
        int i1 = calendar.get(11);
        int j1 = calendar.get(12);
        return (long)timezone.getOffset(i, j, l, k, calendar.get(7), 60000 * (j1 + i1 * 60));
    }

    public static TimeZone getSystemTimeZone(String s) {
        if(!TextUtils.isEmpty(s)) {
        	TimeZone timezone = TimeZone.getTimeZone(s);
        	if(timezone == null)
        		timezone = TimeZone.getDefault();
        	return timezone;
        } else {
        	return TimeZone.getDefault();
        }
    }

    private TimeZoneInfo getTimeZoneInfo(String s, Long long1)
    {
    	int i = mOffsetToTimeZonesMapping.size();
        if(TextUtils.isEmpty(s) && null == long1) {
        	return getCurrentTimeZoneInfo();
        }
        
        TimeZone timezone = getSystemTimeZone(s);
        String s1 = null;
        if(timezone != null) {
            if(areTimeZoneIdsEquivalent(timezone.getID(), s))
            {
                s1 = timezone.getDisplayName();
                long1 = Long.valueOf(getOffset(timezone, mCalendar));
            } else
            {
                s1 = null;
                if(long1 == null)
                {
                    return getCurrentTimeZoneInfo();
                }
            }
        }
        
        TimeZoneInfo timezoneinfo = null;
        TimeZoneInfo timezoneinfo1 = null;
        if(i > 0)
        {
            int j = 0;
            timezoneinfo = null;
            if(long1 != null)
            {
                j = mOffsetToTimeZonesMapping.indexOfKey(long1.longValue());
                timezoneinfo = null;
                if(j < 0)
                    j = 0;
            }
            for(; j < i; j++)
            {
                long l = mOffsetToTimeZonesMapping.keyAt(j);
                List arraylist = ((TimeZoneGroup)mOffsetToTimeZonesMapping.get(l)).mTimeZoneInfoList;
                int k = arraylist.size();
                for(int i1 = 0; i1 < k; i1++)
                {
                    timezoneinfo1 = (TimeZoneInfo)arraylist.get(i1);
                    boolean flag = TextUtils.equals(timezoneinfo1.getTimeZone().getDisplayName(), s1);
                    if(l == long1.longValue() && (flag || TextUtils.isEmpty(s1)))
                        return timezoneinfo1;
                    if(flag || i1 == 0)
                        timezoneinfo = timezoneinfo1;
                }

            }

        }
        return timezoneinfo;
    }

    public static void initialize(Context context)
    {
        if(sTimeZoneFormat == null)
        {
            sTimeZoneFormat = context.getResources().getString(R.string.time_zone_utc_format);
            sGenericTimeZonePrefix = context.getResources().getString(R.string.time_zone_generic_system_prefix);
        }
    }

    public final void configure(Calendar calendar)
    {
        mCalendar = calendar;
        mOrderedTimeZoneInfoList = new ArrayList();
        mOffsetToTimeZonesMapping = buildMapping(TimeZone.getAvailableIDs(), sGenericTimeZonePrefix, calendar);
        int i = mOffsetToTimeZonesMapping.size();
        int j = 0;
        for(int k = 0; k < i;)
        {
            long l = mOffsetToTimeZonesMapping.keyAt(k);
            List arraylist = ((TimeZoneGroup)mOffsetToTimeZonesMapping.get(l)).mTimeZoneInfoList;
            int i1 = arraylist.size();
            int j1 = 0;
            int k1;
            int l1;
            for(k1 = j; j1 < i1; k1 = l1)
            {
                TimeZoneInfo timezoneinfo = (TimeZoneInfo)arraylist.get(j1);
                l1 = k1 + 1;
                timezoneinfo.setPosition(k1);
                mOrderedTimeZoneInfoList.add(timezoneinfo);
                j1++;
            }

            k++;
            j = k1;
        }

    }

    public final TimeZoneInfo getCurrentTimeZoneInfo()
    {
        TimeZone timezone = mCalendar.getTimeZone();
        return getTimeZoneInfo(timezone.getID(), Long.valueOf(getOffset(timezone, mCalendar)));
    }

    public final long getOffset(TimeZone timezone)
    {
        return getOffset(timezone, mCalendar);
    }

    public final TimeZone getTimeZone(String s, Long long1)
    {
        TimeZoneInfo timezoneinfo = getTimeZoneInfo(s, null);
        TimeZone timezone = null;
        if(timezoneinfo != null)
            timezone = timezoneinfo.getTimeZone();
        return timezone;
    }

    public final List getTimeZoneInfos()
    {
        return mOrderedTimeZoneInfoList;
    }

    public final int getTimeZonePos(String s, Long long1)
    {
        TimeZoneInfo timezoneinfo = getTimeZoneInfo(s, null);
        int i;
        if(timezoneinfo != null)
            i = timezoneinfo.getPosition();
        else
            i = -1;
        return i;
    }
}
