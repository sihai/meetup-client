/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.galaxy.meetup.client.android.ui.view.EventActionListener;
import com.galaxy.meetup.client.android.ui.view.EventActivityUpdateCardLayout;
import com.galaxy.meetup.client.android.ui.view.EventUpdate;

/**
 * 
 * @author sihai
 *
 */
public class EventUpdateDialog extends DialogFragment {

	private EventUpdate mEventUpdate;
	
	public EventUpdateDialog()
    {
    }

    static EventUpdateDialog newInstance()
    {
        return new EventUpdateDialog();
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setStyle(1, getTheme());
        if(bundle != null)
        {
            mEventUpdate = new EventUpdate();
            EventUpdate eventupdate = mEventUpdate;
            if(bundle != null)
            {
                eventupdate.timestamp = bundle.getLong((new StringBuilder()).append("eventupdate").append(".timestampe").toString());
                eventupdate.ownerName = bundle.getString((new StringBuilder()).append("eventupdate").append(".ownername").toString());
                eventupdate.gaiaId = bundle.getString((new StringBuilder()).append("eventupdate").append(".gaiaid").toString());
                eventupdate.comment = bundle.getString((new StringBuilder()).append("eventupdate").append(".comment").toString());
            }
        }
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        Object obj;
        LinearLayout linearlayout;
        EventActivityUpdateCardLayout eventactivityupdatecardlayout;
        android.support.v4.app.Fragment fragment;
        ScrollView scrollview;
        if(android.os.Build.VERSION.SDK_INT >= 11)
            obj = getActivity();
        else
            obj = new ContextThemeWrapper(getActivity(), 0x103000b);
        linearlayout = new LinearLayout(((android.content.Context) (obj)));
        eventactivityupdatecardlayout = new EventActivityUpdateCardLayout(((android.content.Context) (obj)));
        eventactivityupdatecardlayout.setLayoutParams(new android.view.ViewGroup.LayoutParams(-1, -2));
        eventactivityupdatecardlayout.toggleCardBorderStyle(false);
        fragment = getTargetFragment();
        eventactivityupdatecardlayout.bind(mEventUpdate, (EventActionListener)fragment, false);
        scrollview = new ScrollView(((android.content.Context) (obj)));
        scrollview.setLayoutParams(new android.view.ViewGroup.LayoutParams(-1, -1));
        scrollview.addView(eventactivityupdatecardlayout);
        linearlayout.addView(scrollview);
        linearlayout.setBackgroundResource(0x106000b);
        return linearlayout;
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        EventUpdate eventupdate = mEventUpdate;
        if(bundle != null)
        {
            bundle.putLong((new StringBuilder()).append("eventupdate").append(".timestampe").toString(), eventupdate.timestamp);
            bundle.putString((new StringBuilder()).append("eventupdate").append(".ownername").toString(), eventupdate.ownerName);
            bundle.putString((new StringBuilder()).append("eventupdate").append(".gaiaid").toString(), eventupdate.gaiaId);
            bundle.putString((new StringBuilder()).append("eventupdate").append(".comment").toString(), eventupdate.comment);
        }
        super.onSaveInstanceState(bundle);
    }

    public final void setUpdate(EventUpdate eventupdate)
    {
        mEventUpdate = eventupdate;
    }

}
