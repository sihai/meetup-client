/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.util.AccessibilityUtils;
import com.galaxy.meetup.client.util.EventDateUtils;
import com.galaxy.meetup.server.client.v2.domain.Event;

/**
 * 
 * @author sihai
 *
 */
public class EventDestinationCardView extends CardView {

	private EventCardDrawer mDrawer;
    private Event mEvent;
    private boolean mIgnoreHeight;
    
    public EventDestinationCardView(Context context)
    {
        this(context, null);
    }

    public EventDestinationCardView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public EventDestinationCardView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mDrawer = new EventCardDrawer(this);
        setPaddingEnabled(false);
        setFocusable(true);
    }

    public final void bindData(EsAccount esaccount, Event plusevent)
    {
        mEvent = plusevent;
        mDrawer.bind(esaccount, this, plusevent, mItemClickListener);
    }

    protected final int draw(Canvas canvas, int i, int j, int k, int l)
    {
        return mDrawer.draw(j, j + l, canvas);
    }

    public CharSequence getContentDescription() {
        Object obj = null;
        Resources resources = getResources();
        StringBuilder stringbuilder = new StringBuilder();
        AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, mEvent.getName());
        AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, EventDateUtils.getDateRange(getContext(), mEvent.getStartTime(), null, true));
        if(mEvent.getLocation() != null)
        {
            int i1 = R.string.event_location_accessibility_description;
            Object aobj3[] = new Object[]{mEvent.getLocation().buildAddress()};
            AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, resources.getString(i1, aobj3));
        }
        
        switch(EsEventData.getRsvpStatus(mEvent)) {
	        case 0:
	        	 obj = resources.getString(R.string.card_event_invited_prompt);
	        	break;
	        case 1:
	        	int l = R.string.event_rsvp_accessibility_description;
	            Object aobj2[] = new Object[1];
	            aobj2[0] = resources.getString(R.string.card_event_going_prompt);
	            obj = resources.getString(l, aobj2);
	        	break;
	        case 2:
	        	int j = R.string.event_rsvp_accessibility_description;
	            Object aobj[] = new Object[1];
	            aobj[0] = resources.getString(R.string.card_event_maybe_prompt);
	            obj = resources.getString(j, aobj);
	        	break;
	        case 3:
	        	int k = R.string.event_rsvp_accessibility_description;
	            Object aobj1[] = new Object[1];
	            aobj1[0] = resources.getString(R.string.card_event_declined_prompt);
	            obj = resources.getString(k, aobj1);
	        	break;
	        default:
	        	break;
        }
        
        AccessibilityUtils.appendAndSeparateIfNotEmpty(stringbuilder, ((CharSequence) (obj)));
        return stringbuilder.toString();
        
    }

    public final Event getEvent()
    {
        return mEvent;
    }

    protected final int layoutElements(int i, int j, int k, int l)
    {
        return mDrawer.layout(i, j, mIgnoreHeight, k, l);
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        mDrawer.attach();
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mDrawer.detach();
    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        boolean flag;
        int i1;
        int j1;
        if(android.view.View.MeasureSpec.getMode(j) == 0)
            flag = true;
        else
            flag = false;
        mIgnoreHeight = flag;
        if(mIgnoreHeight)
            i1 = k;
        else
            i1 = l;
        j1 = layoutElements(sLeftBorderPadding, sTopBorderPadding, k - (sLeftBorderPadding + sRightBorderPadding), i1 - (sTopBorderPadding + sBottomBorderPadding));
        if(mIgnoreHeight)
            i1 = j1 + sTopBorderPadding + sBottomBorderPadding + sYPadding;
        setMeasuredDimension(k, i1);
        mBackgroundRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    public void onRecycle()
    {
        super.onRecycle();
        mDrawer.clear();
        mEvent = null;
    }

}
