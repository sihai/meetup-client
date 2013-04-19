/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.ui.fragments.EventActiveState;
import com.galaxy.meetup.client.util.TextPaintUtils;
import com.galaxy.meetup.server.client.domain.PlusEvent;

/**
 * 
 * @author sihai
 *
 */
public class EventDetailsMainLayout extends ExactLayout implements
		ItemClickListener {

	private static TextPaint sDescriptionTextPaint;
    private static Paint sDividerPaint;
    private static int sGoingLabelColor;
    private static int sGoingLabelSize;
    private static String sGoingLabelText;
    private static boolean sInitialized;
    private static int sPadding;
    private static String sWentLabelText;
    private ConstrainedTextView mDescriptionTextView;
    private EventDetailOptionRowInstantShare mInstantShareRow;
    private EventActionListener mListener;
    private EventDetailOptionRowLocation mLocationRow;
    private TextView mRsvpLabel;
    private EventRsvpLayout mRsvpLayout;
    private EventDetailOptionRowTime mTimeRow;
    
    public EventDetailsMainLayout(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public EventDetailsMainLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        init(context, attributeset, 0);
    }

    public EventDetailsMainLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init(context, attributeset, i);
    }

    private void init(Context context, AttributeSet attributeset, int i)
    {
        if(!sInitialized)
        {
            Resources resources = context.getResources();
            sPadding = resources.getDimensionPixelSize(R.dimen.event_card_padding);
            Paint paint = new Paint();
            sDividerPaint = paint;
            paint.setColor(resources.getColor(R.color.card_event_divider));
            sDividerPaint.setStrokeWidth(resources.getDimension(R.dimen.event_card_divider_stroke_width));
            TextPaint textpaint = new TextPaint();
            sDescriptionTextPaint = textpaint;
            textpaint.setAntiAlias(true);
            sDescriptionTextPaint.setColor(resources.getColor(R.color.event_card_details_description_color));
            sDescriptionTextPaint.setTextSize(resources.getDimension(R.dimen.event_card_details_description_size));
            sDescriptionTextPaint.linkColor = resources.getColor(R.color.comment_link);
            TextPaintUtils.registerTextPaint(sDescriptionTextPaint, R.dimen.event_card_details_description_size);
            sGoingLabelColor = resources.getColor(R.color.event_card_details_going_label);
            sGoingLabelSize = resources.getDimensionPixelSize(R.dimen.event_card_details_going_label_text_size);
            sGoingLabelText = resources.getString(R.string.event_rsvp_are_you_going);
            sWentLabelText = resources.getString(R.string.event_rsvp_are_you_going_past);
            sInitialized = true;
        }
        mDescriptionTextView = new ConstrainedTextView(context, attributeset, i);
        mDescriptionTextView.setTextPaint(sDescriptionTextPaint);
        mDescriptionTextView.setClickListener(this);
        addView(mDescriptionTextView);
        mTimeRow = new EventDetailOptionRowTime(context, attributeset, i);
        addView(mTimeRow);
        mLocationRow = new EventDetailOptionRowLocation(context, attributeset, i);
        addView(mLocationRow);
        mInstantShareRow = new EventDetailOptionRowInstantShare(context, attributeset, i);
        mInstantShareRow.setId(R.id.event_instant_share_selection);
        addView(mInstantShareRow);
        mRsvpLayout = new EventRsvpLayout(context, attributeset, i);
        addView(mRsvpLayout);
        mRsvpLayout.setLayoutParams(new ExactLayout.LayoutParams(-1, -2));
        mRsvpLayout.setId(R.id.event_rsvp_section);
        mRsvpLabel = TextViewUtils.createText(context, attributeset, i, sGoingLabelSize, sGoingLabelColor, false, true);
        mRsvpLabel.setLayoutParams(new ExactLayout.LayoutParams(-1, -2));
        addView(mRsvpLabel);
    }

    public final void bind(PlusEvent plusevent, EventActiveState eventactivestate, EventActionListener eventactionlistener)
    {
        mListener = eventactionlistener;
        long l;
        TextView textview;
        String s;
        if(plusevent.displayContent != null && plusevent.displayContent.descriptionHtml != null && !TextUtils.isEmpty(plusevent.displayContent.descriptionHtml))
            mDescriptionTextView.setHtmlText(plusevent.displayContent.descriptionHtml, false);
        else
        if(plusevent.description != null && !TextUtils.isEmpty(plusevent.description))
            mDescriptionTextView.setText(plusevent.description, false);
        else
            mDescriptionTextView.setText(null, false);
        l = System.currentTimeMillis();
        textview = mRsvpLabel;
        if(EsEventData.isEventOver(plusevent, l))
            s = sWentLabelText;
        else
            s = sGoingLabelText;
        textview.setText(s);
        mTimeRow.bind(plusevent);
        if(plusevent.location != null || plusevent.hangoutInfo != null)
        {
            mLocationRow.bind(plusevent, eventactionlistener);
            mLocationRow.setVisibility(0);
        } else
        {
            mLocationRow.setVisibility(8);
        }
        if(eventactivestate.isInstantShareAvailable || eventactivestate.isInstantShareExpired)
        {
            mInstantShareRow.setListener(eventactionlistener);
            mInstantShareRow.bind(eventactivestate);
            mInstantShareRow.setVisibility(0);
        } else
        {
            mInstantShareRow.setVisibility(8);
        }
        if(EsEventData.canRsvp(plusevent) && eventactivestate.eventSource == 1)
        {
            mRsvpLayout.bind(plusevent, eventactivestate, eventactionlistener);
            mRsvpLabel.setVisibility(0);
            mRsvpLayout.setVisibility(0);
        } else
        {
            mRsvpLabel.setVisibility(8);
            mRsvpLayout.setVisibility(8);
        }
    }

    public final void clear()
    {
        mTimeRow.clear();
        mLocationRow.clear();
        mInstantShareRow.clear();
    }

    protected void measureChildren(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        int i1 = 0 + sPadding;
        int j1 = k - 2 * sPadding;
        int k1;
        EventDetailsOptionRowLayout aeventdetailsoptionrowlayout[];
        int l1;
        boolean flag;
        int i2;
        if(mDescriptionTextView.getLength() > 0)
        {
            mDescriptionTextView.setVisibility(0);
            int i3 = l + 0;
            measure(mDescriptionTextView, j1 - sPadding, 0x80000000, i3, 0);
            setCorner(mDescriptionTextView, i1 + sPadding, 0);
            k1 = 0 + (mDescriptionTextView.getMeasuredHeight() + sPadding);
        } else
        {
            mDescriptionTextView.setVisibility(8);
            k1 = 0;
        }
        if(mRsvpLabel.getVisibility() != 8)
        {
            measure(mRsvpLabel, j1, 0x40000000, 0, 0);
            setCorner(mRsvpLabel, i1, k1);
            k1 += mRsvpLabel.getMeasuredHeight() + sPadding;
        }
        if(mRsvpLayout.getVisibility() != 8)
        {
            measure(mRsvpLayout, k, 0x40000000, 0, 0);
            setCorner(mRsvpLayout, 0, k1);
            k1 += mRsvpLayout.getMeasuredHeight();
        }
        aeventdetailsoptionrowlayout = new EventDetailsOptionRowLayout[3];
        aeventdetailsoptionrowlayout[0] = mInstantShareRow;
        aeventdetailsoptionrowlayout[1] = mTimeRow;
        aeventdetailsoptionrowlayout[2] = mLocationRow;
        l1 = aeventdetailsoptionrowlayout.length;
        flag = false;
        i2 = 0;
        while(i2 < l1) 
        {
            if(flag || aeventdetailsoptionrowlayout[i2].getVisibility() == 0)
                flag = true;
            else
                flag = false;
            i2++;
        }
        int j2 = 0;
        boolean flag1 = true;
        int k2 = 0;
        while(k2 < l1) 
        {
            EventDetailsOptionRowLayout eventdetailsoptionrowlayout = aeventdetailsoptionrowlayout[k2];
            int l2;
            boolean flag2;
            if(eventdetailsoptionrowlayout.getVisibility() != 8)
            {
                eventdetailsoptionrowlayout.setFirst(flag1);
                measure(eventdetailsoptionrowlayout, k, 0x80000000, 0, 0);
                setCorner(eventdetailsoptionrowlayout, 0, k1);
                l2 = eventdetailsoptionrowlayout.getMeasuredHeight();
            } else
            {
                l2 = 0;
            }
            j2 += l2;
            k1 += l2;
            if(flag1 && l2 == 0)
                flag2 = true;
            else
                flag2 = false;
            k2++;
            flag1 = flag2;
        }
    }

    public final void onSpanClick(URLSpan urlspan)
    {
        if(mListener != null)
            mListener.onLinkClicked(urlspan.getURL());
    }

    public final void onUserImageClick(String s, String s1)
    {
    }
}
