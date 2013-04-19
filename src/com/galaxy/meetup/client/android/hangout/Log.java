/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class Log {

	public static void debug(String s)
    {
        if(EsLog.isLoggable("GoogleMeeting", 3))
            android.util.Log.d("GoogleMeeting", s);
    }

    public static void debug(String s, Object aobj[])
    {
        if(EsLog.isLoggable("GoogleMeeting", 3))
            android.util.Log.d("GoogleMeeting", String.format(s, aobj));
    }

    public static void error(String s)
    {
        if(EsLog.isLoggable("GoogleMeeting", 6))
            android.util.Log.e("GoogleMeeting", s);
    }

    public static void error(String s, Object aobj[])
    {
        if(EsLog.isLoggable("GoogleMeeting", 6))
            android.util.Log.e("GoogleMeeting", String.format(s, aobj));
    }

    public static void info(String s)
    {
        if(EsLog.isLoggable("GoogleMeeting", 4))
            android.util.Log.i("GoogleMeeting", s);
    }

    public static void info(String s, Object aobj[])
    {
        if(EsLog.isLoggable("GoogleMeeting", 4))
            android.util.Log.i("GoogleMeeting", String.format(s, aobj));
    }

    public static void warn(String s)
    {
        if(EsLog.isLoggable("GoogleMeeting", 5))
            android.util.Log.w("GoogleMeeting", s);
    }
}
