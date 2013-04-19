/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class EventRsvpButtonLayout extends LinearLayout implements
		OnClickListener {

	private EventRsvpListener mListener;
    private View mMaybeDivider;
    private View mMaybeView;
    private View mNoView;
    private View mYesView;
    
    public EventRsvpButtonLayout(Context context)
    {
        super(context);
    }

    public EventRsvpButtonLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public EventRsvpButtonLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public final void bind(EventRsvpListener eventrsvplistener, boolean flag)
    {
        mListener = eventrsvplistener;
        byte byte0;
        if(flag)
            byte0 = 8;
        else
            byte0 = 0;
        mMaybeDivider.setVisibility(byte0);
        mMaybeView.setVisibility(byte0);
    }

    public void onClick(View view)
    {
        if(mListener == null) {
        	return;
        }
        
        if(view == mYesView) {
        	mListener.onRsvpChanged("ATTENDING");
        } else if(view == mMaybeView) {
        	mListener.onRsvpChanged("MAYBE");
        } else if(view == mNoView) {
        	mListener.onRsvpChanged("NOT_ATTENDING");
        }
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        mMaybeView = findViewById(R.id.maybeButton);
        mMaybeView.setOnClickListener(this);
        mYesView = findViewById(R.id.yesButton);
        mYesView.setOnClickListener(this);
        mNoView = findViewById(R.id.noButton);
        mNoView.setOnClickListener(this);
        mMaybeDivider = findViewById(R.id.maybeDivider);
    }

}
