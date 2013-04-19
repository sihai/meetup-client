/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceListener;
import com.galaxy.meetup.client.android.ui.activity.ConversationActivity;

/**
 * 
 * @author sihai
 *
 */
public class ConversationTile extends RelativeLayout implements Tile {

	List listeners;
    private HashSet mActiveParticipantIds;
    private Long mConversationRowId;
    private EditText mEditText;
    private RealTimeChatServiceListener rtcListener;
    
    public ConversationTile(Context context)
    {
        this(context, null);
    }

    public ConversationTile(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mConversationRowId = null;
        rtcListener = new RTCServiceListener();
        mActiveParticipantIds = new HashSet();
        addView(((LayoutInflater)context.getSystemService("layout_inflater")).inflate(R.layout.conversation_tile, null));
        mEditText = (EditText)findViewById(R.id.message_text);
    }

    public final void addParticipantPresenceListener(Tile.ParticipantPresenceListener participantpresencelistener)
    {
        if(listeners == null)
            listeners = new LinkedList();
        listeners.add(participantpresencelistener);
    }

    public final HashSet getActiveParticipantIds()
    {
        return mActiveParticipantIds;
    }

    public final void onCreate(Bundle bundle)
    {
    }

    public final void onPause()
    {
        RealTimeChatService.unregisterListener(rtcListener);
    }

    public final void onResume()
    {
        RealTimeChatService.registerListener(rtcListener);
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
    }

    public final void onStart()
    {
    }

    public final void onStop()
    {
    }

    public final void onTilePause()
    {
        if(mConversationRowId != null)
            RealTimeChatService.sendPresenceRequest(getContext(), ((ConversationActivity)getContext()).getAccount(), mConversationRowId.longValue(), false, false);
        ((InputMethodManager)getContext().getSystemService("input_method")).hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public final void onTileResume()
    {
        if(mConversationRowId != null)
            RealTimeChatService.sendPresenceRequest(getContext(), ((ConversationActivity)getContext()).getAccount(), mConversationRowId.longValue(), true, true);
    }

    public final void onTileStart()
    {
    }

    public final void onTileStop()
    {
    }

    public void setConversationRowId(Long long1)
    {
        mConversationRowId = long1;
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private final class RTCServiceListener extends RealTimeChatServiceListener
    {

        public final void onUserPresenceChanged(long l, String s, boolean flag)
        {
            if(flag)
                mActiveParticipantIds.add(s);
            else
                mActiveParticipantIds.remove(s);
            if(mConversationRowId != null && l == mConversationRowId.longValue())
            {
                for(Iterator iterator = listeners.iterator(); iterator.hasNext(); ((Tile.ParticipantPresenceListener)iterator.next()).onParticipantPresenceChanged());
            }
        }

    }
}
