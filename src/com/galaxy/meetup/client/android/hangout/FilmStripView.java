/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.util.Iterator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class FilmStripView extends LinearLayout {

	private final EventHandler eventHandler = new EventHandler();
    private HangoutTile hangoutTile;
    private boolean isResumed;
    private final int size;
    
    public FilmStripView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        size = context.getResources().getDimensionPixelSize(R.dimen.micro_kind_max_dimension);
    }

    private void addParticipantVideo(MeetingMember meetingmember)
    {
        IncomingVideoView.ParticipantVideoView participantvideoview = new IncomingVideoView.ParticipantVideoView(getContext(), null, meetingmember);
        participantvideoview.setLayoutParams(new android.widget.LinearLayout.LayoutParams(size, size));
        addView(participantvideoview);
        participantvideoview.setHangoutTile(hangoutTile);
        participantvideoview.onResume();
    }

    public final void onPause()
    {
        isResumed = false;
        for(int i = 0; i < getChildCount(); i++)
        {
            android.view.View view = getChildAt(i);
            if(view instanceof IncomingVideoView.ParticipantVideoView)
                ((IncomingVideoView.ParticipantVideoView)view).onPause();
        }

        removeAllViews();
        GCommApp.getInstance(getContext()).unregisterForEvents(getContext(), eventHandler, false);
    }

    public final void onResume(SelfVideoView selfvideoview)
    {
        isResumed = true;
        if(selfvideoview.getParent() != null)
            ((ViewGroup)selfvideoview.getParent()).removeView(selfvideoview);
        for(Iterator iterator = hangoutTile.getGCommNativeWrapper().getMeetingMembersOrderedByEntry().iterator(); iterator.hasNext();)
        {
            MeetingMember meetingmember = (MeetingMember)iterator.next();
            if(meetingmember.isSelf())
            {
                selfvideoview.setLayoutMode(SelfVideoView.LayoutMode.FIT);
                selfvideoview.setLayoutParams(new android.widget.LinearLayout.LayoutParams(size, size));
                addView(selfvideoview);
            } else
            {
                addParticipantVideo(meetingmember);
            }
        }

        GCommApp.getInstance(getContext()).registerForEvents(getContext(), eventHandler, false);
    }

    public void setHangoutTile(HangoutTile hangouttile)
    {
        hangoutTile = hangouttile;
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	final class EventHandler extends GCommEventHandler
    {

        public final void onMeetingMemberEntered(MeetingMember meetingmember)
        {
            if(isResumed)
                addParticipantVideo(meetingmember);
        }

        public final void onMeetingMemberExited(MeetingMember meetingmember)
        {
            if(isResumed)
            {
                for(int i = 0; i < getChildCount(); i++)
                {
                    android.view.View view = getChildAt(i);
                    if(view != null && (view instanceof IncomingVideoView.ParticipantVideoView) && ((IncomingVideoView.ParticipantVideoView)view).getMember() == meetingmember)
                    {
                        ((IncomingVideoView.ParticipantVideoView)view).onPause();
                        removeView(view);
                    }
                }

            }
        }

    }
}
