/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class Dates {

	private static Time sThenTime;
	
	public static CharSequence getAbbreviatedRelativeTimeSpanString(Context context, long l)
    {
        long l1 = System.currentTimeMillis();
        if(l1 - l < 60000L) {
        	return context.getResources().getText(R.string.posted_just_now);
        } else {
        	try {
        		return DateUtils.getRelativeTimeSpanString(l, l1, 60000L, 0x50000).toString();
        	} catch (Exception e) {
        		 if(EsLog.isLoggable("Dates", 3))
        	            Log.d("Dates", (new StringBuilder("DateUtils.getRelativeTimeSpanString threw an exception! time=")).append(l).append(", now=").append(l1).append("\n").append(e.getMessage()).toString());
        		 return getShortRelativeTimeSpanString(context, l);
        	}
        }
    }

    private static synchronized long getNumberOfDaysPassed(long l, long l1) {
        int j;
        if(sThenTime == null)
            sThenTime = new Time();
        sThenTime.set(l);
        int i = Time.getJulianDay(l, sThenTime.gmtoff);
        sThenTime.set(l1);
        j = Math.abs(Time.getJulianDay(l1, sThenTime.gmtoff) - i);
        long l2 = j;
        return l2;
    }

    public static CharSequence getRelativeTimeSpanString(Context context, long l) {
        long l1 = System.currentTimeMillis();
        if(l1 - l < 60000L) {
        	return context.getResources().getText(R.string.posted_just_now);
        } else {
        	try {
        		return DateUtils.getRelativeTimeSpanString(l, l1, 60000L, 0x40000).toString();
        	} catch (Exception e) {
        		if(EsLog.isLoggable("Dates", 3))
                    Log.d("Dates", (new StringBuilder("DateUtils.getRelativeTimeSpanString threw an exception! time=")).append(l).append(", now=").append(l1).append("\n").append(e.getMessage()).toString());
        		return getShortRelativeTimeSpanString(context, l);
        	}
        }
    }

    public static CharSequence getShortRelativeTimeSpanString(Context context, long l) {
        // TODO
    	return "";
    }
}
