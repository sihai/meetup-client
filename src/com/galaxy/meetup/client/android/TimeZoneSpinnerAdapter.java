/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.galaxy.meetup.client.util.TimeZoneHelper;

/**
 * 
 * @author sihai
 *
 */
public class TimeZoneSpinnerAdapter extends BaseAdapter {

	private static String stimeZoneFormat;
    private Context mContext;
    private TimeZoneHelper mTimeZoneHelper;
    private List mTimeZones;
    
    public TimeZoneSpinnerAdapter(Context context)
    {
        mContext = context;
        if(stimeZoneFormat == null)
            stimeZoneFormat = context.getResources().getString(R.string.time_zone_format);
    }

    private View prepareRow(int i, View view, ViewGroup viewgroup, boolean flag)
    {
        if(view == null)
            if(flag)
            {
                view = LayoutInflater.from(mContext).inflate(R.layout.timezone_spinner_dropdown_item, viewgroup, false);
            } else
            {
                Context context = mContext;
                view = new TextView(context);
            }
        if(view instanceof TextView)
        {
            TimeZoneHelper.TimeZoneInfo timezoneinfo = (TimeZoneHelper.TimeZoneInfo)mTimeZones.get(i);
            TimeZone timezone = timezoneinfo.getTimeZone();
            long l = timezoneinfo.getOffset();
            TextView textview = (TextView)view;
            String s = stimeZoneFormat;
            Object aobj[] = new Object[3];
            aobj[0] = timezone.getDisplayName();
            aobj[1] = Long.valueOf(l / 0x36ee80L);
            aobj[2] = Long.valueOf(Math.abs((l - 0x36ee80L * (l / 0x36ee80L)) / 60000L));
            textview.setText(String.format(s, aobj));
        }
        return view;
    }

    public final int getCount()
    {
        return mTimeZones.size();
    }

    public final View getDropDownView(int i, View view, ViewGroup viewgroup)
    {
        return prepareRow(i, view, viewgroup, true);
    }

    public final Object getItem(int i)
    {
        return mTimeZones.get(i);
    }

    public final long getItemId(int i)
    {
        return (long)i;
    }

    public final View getView(int i, View view, ViewGroup viewgroup)
    {
        return prepareRow(i, view, viewgroup, false);
    }

    public final void setTimeZoneHelper(TimeZoneHelper timezonehelper)
    {
        mTimeZoneHelper = timezonehelper;
        mTimeZones = mTimeZoneHelper.getTimeZoneInfos();
        notifyDataSetChanged();
    }
}
