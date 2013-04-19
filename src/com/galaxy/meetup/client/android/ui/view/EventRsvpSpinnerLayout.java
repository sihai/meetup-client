/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.ui.fragments.EventActiveState;
import com.galaxy.meetup.server.client.domain.PlusEvent;

/**
 * 
 * @author sihai
 *
 */
public class EventRsvpSpinnerLayout extends ExactLayout implements
		OnClickListener, OnItemSelectedListener {

	private static Drawable sAddPhotosDrawable;
    private static String sAddPhotosText;
    private static boolean sInitialized;
    private static Drawable sInviteMoreDrawable;
    private static String sInviteMoreText;
    private static int sPadding;
    private EventActionButtonLayout mActionButton;
    private int mCurrentSelectionIndex;
    private PlusEvent mEvent;
    private EventActionListener mEventActionListener;
    private boolean mEventOver;
    private EventRsvpListener mListener;
    private Spinner mRsvpSpinner;
    private boolean mShowActionButton;
    private RsvpSpinnerAdapter mSpinnerAdapter;
    
    public EventRsvpSpinnerLayout(Context context)
    {
        super(context);
        mCurrentSelectionIndex = -1;
        init(context, null, 0);
    }

    public EventRsvpSpinnerLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mCurrentSelectionIndex = -1;
        init(context, attributeset, 0);
    }

    public EventRsvpSpinnerLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mCurrentSelectionIndex = -1;
        init(context, attributeset, i);
    }

    private void init(Context context, AttributeSet attributeset, int i)
    {
        if(!sInitialized)
        {
            Resources resources = context.getResources();
            sPadding = resources.getDimensionPixelSize(R.dimen.event_card_details_padding);
            sInviteMoreDrawable = resources.getDrawable(R.drawable.icn_events_rsvp_invite_more);
            sInviteMoreText = resources.getString(R.string.event_button_invite_more_label);
            sAddPhotosDrawable = resources.getDrawable(R.drawable.icn_events_rsvp_add_photo);
            sAddPhotosText = resources.getString(R.string.event_button_add_photos_label);
            sInitialized = true;
        }
        mRsvpSpinner = new Spinner(context);
        mRsvpSpinner.setLayoutParams(new ExactLayout.LayoutParams(-1, -2));
        addView(mRsvpSpinner);
        mActionButton = new EventActionButtonLayout(context, attributeset, i);
        mActionButton.setOnClickListener(this);
        addView(mActionButton);
        setPadding(sPadding, sPadding, sPadding, sPadding);
    }

    public final void bind(PlusEvent plusevent, EventActiveState eventactivestate, EventRsvpListener eventrsvplistener, EventActionListener eventactionlistener)
    {
        mListener = eventrsvplistener;
        mEventActionListener = eventactionlistener;
        mEvent = plusevent;
        long l = System.currentTimeMillis();
        mEventOver = EsEventData.isEventOver(mEvent, l);
        mSpinnerAdapter = new RsvpSpinnerAdapter(getContext(), mEventOver);
        mRsvpSpinner.setAdapter(mSpinnerAdapter);
        String s;
        boolean flag;
        if(eventactivestate.temporalRsvpValue != null)
            s = eventactivestate.temporalRsvpValue;
        else
            s = EsEventData.getRsvpType(plusevent);
        mCurrentSelectionIndex = mSpinnerAdapter.access$000(s);
        mRsvpSpinner.setSelection(mCurrentSelectionIndex);
        mSpinnerAdapter.notifyDataSetChanged();
        mRsvpSpinner.setOnItemSelectedListener(this);
        mRsvpSpinner.setEnabled(eventactivestate.isRsvpEnabled);
        if(mCurrentSelectionIndex == mSpinnerAdapter.access$000(s) && (mEventOver && EsEventData.canViewerAddPhotos(mEvent) || !mEventOver && eventactivestate.canInviteOthers))
            flag = true;
        else
            flag = false;
        mShowActionButton = flag;
    }

    protected void measureChildren(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getSize(i);
        int l;
        if(!mShowActionButton)
            l = k;
        else
            l = Math.max(0, (k - sPadding) / 2);
        measure(mRsvpSpinner, l, 0x40000000, 0, 0);
        setCorner(mRsvpSpinner, 0, 0);
        if(mShowActionButton)
        {
            measure(mActionButton, l, 0x40000000, mRsvpSpinner.getMeasuredHeight(), 0x40000000);
            setCorner(mActionButton, l + 0 + sPadding, 0);
            mActionButton.setVisibility(0);
            if(mEvent != null && mEventOver)
                mActionButton.bind(sAddPhotosText, sAddPhotosDrawable);
            else
                mActionButton.bind(sInviteMoreText, sInviteMoreDrawable);
        } else
        {
            mActionButton.setVisibility(8);
        }
    }

    public void onClick(View view)
    {
        if(mEventActionListener != null && view == mActionButton)
            if(mEventOver)
                mEventActionListener.onAddPhotosClicked();
            else
                mEventActionListener.onInviteMoreClicked();
    }

    public void onItemSelected(AdapterView adapterview, View view, int i, long l)
    {
        if(mCurrentSelectionIndex == i) 
        	return; 
        
        boolean flag;
        if(mCurrentSelectionIndex == -1)
            flag = true;
        else
            flag = false;
        if(!flag) {
        	if(0 == i) {
        		mListener.onRsvpChanged("ATTENDING");
        	} else  if(1 == i) {
        		String s;
                if(mEventOver)
                    s = "NOT_ATTENDING";
                else
                    s = "MAYBE";
                mListener.onRsvpChanged(s);
        	} else if(2 == i) {
        		mListener.onRsvpChanged("NOT_ATTENDING");
        	}
        }
        
        mCurrentSelectionIndex = i;
        boolean flag1;
        EventRsvpListener eventrsvplistener;
        String s;
        if(mCurrentSelectionIndex == mSpinnerAdapter.access$000("ATTENDING"))
            flag1 = true;
        else
            flag1 = false;
        mShowActionButton = flag1;
        requestLayout(); 
    }

    public void onNothingSelected(AdapterView adapterview)
    {
    }
    
    private final class RsvpSpinnerAdapter extends BaseAdapter
    {

    	private Context mContext;
        private boolean mPast;

        public RsvpSpinnerAdapter(Context context, boolean flag)
        {
            super();
            mPast = flag;
            mContext = context;
        }
        
        public final int getCount()
        {
            byte byte0;
            if(mPast)
                byte0 = 2;
            else
                byte0 = 3;
            return byte0;
        }

        public final Object getItem(int i)
        {
            return Integer.valueOf(i);
        }

        public final long getItemId(int i)
        {
            return (long)i;
        }

        public final View getView(int i, View view, ViewGroup viewgroup)
        {
            String s = null;
            View view1 = null;
            if(0 == i) {
            	 view1 = LayoutInflater.from(mContext).inflate(R.layout.event_rsvp_attending, viewgroup, false);
                 s = "ATTENDING";
            } else if(1 == i) {
            	if(!mPast) {
            		view1 = LayoutInflater.from(mContext).inflate(R.layout.event_rsvp_maybe, viewgroup, false);
                	s = "MAYBE";
            	} else {
            		view1 = LayoutInflater.from(mContext).inflate(R.layout.event_rsvp_not_attending, viewgroup, false);
                    s = "NOT_ATTENDING";
            	}
            } else if(2 == i) {
            	view1 = LayoutInflater.from(mContext).inflate(R.layout.event_rsvp_not_attending, viewgroup, false);
                s = "NOT_ATTENDING";
            }
            if(view1 != null)
            {
                TextView textview = (TextView)((TextView)view1.findViewById(R.id.text)).findViewById(R.id.text);
                if(textview != null)
                {
                    Resources resources = getContext().getResources();
                    int j;
                    if("MAYBE".equals(s))
                        j = R.string.event_rsvp_maybe;
                    else
                    if("NOT_ATTENDING".equals(s))
                    {
                        j = R.string.event_rsvp_not_attending;
                    } else
                    {
                        boolean flag = "ATTENDING".equals(s);
                        j = 0;
                        if(flag)
                            j = R.string.event_rsvp_attending;
                    }
                    textview.setText(resources.getString(j));
                }
            }
            return view1;
        }
        
        public int access$000(String s)
        {
            byte byte0 = -1;
            if(!TextUtils.equals(s, "ATTENDING") && !TextUtils.equals(s, "CHECKIN")) {
            	if(TextUtils.equals(s, "MAYBE") || mPast && TextUtils.equals(s, "NOT_ATTENDING"))
                    byte0 = 1;
                else
                if(TextUtils.equals(s, "NOT_ATTENDING"))
                    byte0 = 2;
            } else { 
            	byte0 = 0;
            }
            return byte0;
        }

    }

}
