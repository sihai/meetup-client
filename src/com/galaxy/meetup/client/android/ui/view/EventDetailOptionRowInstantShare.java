/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.fragments.EventActiveState;

/**
 * 
 * @author sihai
 *
 */
public class EventDetailOptionRowInstantShare extends
		EventDetailsOptionRowLayout implements OnClickListener {

	private static String sAfterInstantDescription;
    private static String sBeforeInstantDescription;
    private static boolean sInitialized;
    private static Drawable sInstantShareDrawable;
    private static String sInstantTitle;
    private CheckBox mCheckBox;
    private EventActiveState mEventState;
    private ImageView mInstantIcon;
    private EventActionListener mListener;
    
    public EventDetailOptionRowInstantShare(Context context)
    {
        super(context);
    }

    public EventDetailOptionRowInstantShare(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public EventDetailOptionRowInstantShare(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public final void bind(EventActiveState eventactivestate)
    {
        mEventState = eventactivestate;
        String s;
        String s1;
        if(!eventactivestate.isInstantShareExpired)
        {
            mCheckBox.setVisibility(0);
            mCheckBox.setChecked(eventactivestate.isInstantShareEnabled);
            setClickable(true);
        } else
        {
            mCheckBox.setVisibility(8);
            setClickable(false);
        }
        s = sInstantTitle;
        if(eventactivestate.isInstantShareExpired)
            s1 = sAfterInstantDescription;
        else
            s1 = sBeforeInstantDescription;
        super.bind(s, s1, mInstantIcon, mCheckBox);
    }

    protected final void init(Context context, AttributeSet attributeset, int i)
    {
        super.init(context, attributeset, i);
        if(!sInitialized)
        {
            Resources resources = context.getResources();
            sInstantShareDrawable = resources.getDrawable(R.drawable.icn_events_party_mode_1up);
            sBeforeInstantDescription = resources.getString(R.string.instant_share_description);
            sAfterInstantDescription = resources.getString(R.string.instant_share_after_description);
            sInstantTitle = resources.getString(R.string.event_detail_instantshare_title);
            sInitialized = true;
        }
        mInstantIcon = new ImageView(context, attributeset, i);
        mInstantIcon.setImageDrawable(sInstantShareDrawable);
        mCheckBox = new CheckBox(context);
        mCheckBox.setLayoutParams(new ExactLayout.LayoutParams(-2, -2));
        mCheckBox.setVisibility(0);
        mCheckBox.setClickable(false);
        setOnClickListener(this);
    }

    public void onClick(View view)
    {
        if(this == view && mListener != null)
        {
            EventActionListener eventactionlistener = mListener;
            boolean flag;
            if(!mEventState.isInstantShareEnabled)
                flag = true;
            else
                flag = false;
            eventactionlistener.onInstantShareToggle(flag);
        }
    }

    public void setListener(EventActionListener eventactionlistener)
    {
        mListener = eventactionlistener;
    }
}
