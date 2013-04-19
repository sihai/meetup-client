/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.ui.fragments.BlockPersonDialog;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.view.OverlayedAvatarView;
import com.galaxy.meetup.client.android.ui.view.ParticipantsGalleryView;
import com.galaxy.meetup.client.android.ui.view.ParticipantsGalleryView.CommandListener;
import com.galaxy.meetup.client.util.QuickActions;

/**
 * 
 * @author sihai
 *
 */
public class HangoutParticipantsGalleryView extends ParticipantsGalleryView
		implements CommandListener {

	static final boolean $assertionsDisabled;
    private HashMap avatarViewsByMeetingMember;
    private MeetingMember currentSpeaker;
    private final EventHandler eventHandler = new EventHandler();
    private HangoutTile mHangoutTile;
    private int mainVideoRequestId;
    private final List meetingMembers = new ArrayList();
    private HashMap meetingMembersByAvatarView;
    private MeetingMember pinnedVideoMember;

    static 
    {
        boolean flag;
        if(!HangoutParticipantsGalleryView.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
    public HangoutParticipantsGalleryView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mainVideoRequestId = 0;
        setCommandListener(this);
    }

    private void pinVideo(MeetingMember meetingmember)
    {
        if(!$assertionsDisabled && meetingmember == null)
        {
            throw new AssertionError();
        } else
        {
            MeetingMember meetingmember1 = pinnedVideoMember;
            GCommApp.getInstance(getContext()).setSelectedVideoSource(meetingmember);
            mHangoutTile.updateMainVideoStreaming();
            pinnedVideoMember = meetingmember;
            setOverlay(meetingmember1);
            setOverlay(pinnedVideoMember);
            return;
        }
    }

    private void setCurrentSpeaker(MeetingMember meetingmember)
    {
        if(currentSpeaker != null)
        {
            OverlayedAvatarView overlayedavatarview1 = (OverlayedAvatarView)avatarViewsByMeetingMember.get(currentSpeaker);
            if(overlayedavatarview1 != null)
                setParticipantActive(overlayedavatarview1, true);
        }
        if(meetingmember != null && avatarViewsByMeetingMember != null)
        {
            OverlayedAvatarView overlayedavatarview = (OverlayedAvatarView)avatarViewsByMeetingMember.get(meetingmember);
            if(overlayedavatarview != null)
                setParticipantLoudestSpeaker(overlayedavatarview, true);
        }
        currentSpeaker = meetingmember;
    }

    private void setOverlay(MeetingMember meetingmember)
    {
        if(meetingmember != null && !meetingmember.isSelf())
        {
            OverlayedAvatarView overlayedavatarview = (OverlayedAvatarView)avatarViewsByMeetingMember.get(meetingmember);
            if(meetingmember == pinnedVideoMember)
                overlayedavatarview.setOverlayResource(R.drawable.hangout_pin);
            else
            if(meetingmember.isMediaBlocked())
                overlayedavatarview.setOverlayResource(R.drawable.list_circle_blocked);
            else
            if(meetingmember.isVideoPaused())
            {
                overlayedavatarview.setOverlayResource(R.drawable.hangout_video_pause);
            } else
            {
                overlayedavatarview.setOverlayResource(0);
                if(meetingmember.getCurrentStatus() == MeetingMember.Status.CONNECTING)
                    overlayedavatarview.setParticipantType(1);
                else
                    overlayedavatarview.setParticipantType(3);
            }
        }
    }

    private void unpinVideo()
    {
        GCommApp.getInstance(getContext()).setSelectedVideoSource(null);
        mHangoutTile.updateMainVideoStreaming();
        MeetingMember meetingmember = pinnedVideoMember;
        pinnedVideoMember = null;
        setOverlay(meetingmember);
    }

    public final void onAvatarClicked(OverlayedAvatarView overlayedavatarview, Data.Participant participant)
    {
        AvatarContextMenuHelper avatarcontextmenuhelper = new AvatarContextMenuHelper((MeetingMember)meetingMembersByAvatarView.get(overlayedavatarview), participant);
        avatarContextMenuDialog = QuickActions.show(overlayedavatarview, overlayedavatarview, null, avatarcontextmenuhelper, avatarcontextmenuhelper, true, false);
    }

    public final void onAvatarDoubleClicked(OverlayedAvatarView overlayedavatarview, Data.Participant participant)
    {
        MeetingMember meetingmember = (MeetingMember)meetingMembersByAvatarView.get(overlayedavatarview);
        if(meetingmember != null)
        {
            if(meetingmember == pinnedVideoMember)
                unpinVideo();
            else
                pinVideo(meetingmember);
        } else
        {
            onAvatarClicked(overlayedavatarview, participant);
        }
    }

    public final void onPause()
    {
        GCommApp.getInstance(getContext()).unregisterForEvents(getContext(), eventHandler, false);
        dismissAvatarMenuDialog();
    }

    public final void onResume()
    {
        GCommApp.getInstance(getContext()).registerForEvents(getContext(), eventHandler, false);
    }

    public final void onShowParticipantList()
    {
        android.content.Intent intent = ((HangoutTile.HangoutTileActivity)getContext()).getParticipantListActivityIntent();
        getContext().startActivity(intent);
    }

    public void setHangoutTile(HangoutTile hangouttile)
    {
        mHangoutTile = hangouttile;
    }

    final void setMainVideoRequestId(int i)
    {
        if(!$assertionsDisabled && i == 0)
        {
            throw new AssertionError();
        } else
        {
            mainVideoRequestId = i;
            return;
        }
    }

    public void setParticipants(HashMap hashmap, HashSet hashset)
    {
        removeAllParticipants();
        pinnedVideoMember = null;
        avatarViewsByMeetingMember = new HashMap();
        meetingMembersByAvatarView = new HashMap();
        HashSet hashset1;
        LayoutInflater layoutinflater;
        Iterator iterator;
        if(hashmap != null)
            hashset1 = new HashSet(hashmap.keySet());
        else
            hashset1 = null;
        layoutinflater = LayoutInflater.from(getContext());
        dismissAvatarMenuDialog();
        meetingMembers.clear();
        meetingMembers.addAll(GCommApp.getInstance(getContext()).getGCommNativeWrapper().getMeetingMembersOrderedByEntry());
        iterator = meetingMembers.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            MeetingMember meetingmember1 = (MeetingMember)iterator.next();
            String s1 = meetingmember1.getId();
            if(!meetingmember1.isSelf())
            {
                Data.Participant participant;
                OverlayedAvatarView overlayedavatarview;
                if(hashset1 != null && hashset1.remove(s1))
                {
                    participant = (Data.Participant)hashmap.get(s1);
                } else
                {
                    Data.Participant.Builder builder = Data.Participant.newBuilder();
                    builder.setParticipantId(meetingmember1.getId());
                    participant = builder.build();
                }
                overlayedavatarview = addParticipant(layoutinflater, participant);
                if(currentSpeaker != null && meetingmember1 == currentSpeaker)
                    setParticipantLoudestSpeaker(overlayedavatarview, true);
                else
                    setParticipantActive(overlayedavatarview, true);
                avatarViewsByMeetingMember.put(meetingmember1, overlayedavatarview);
                meetingMembersByAvatarView.put(overlayedavatarview, meetingmember1);
                setOverlay(meetingmember1);
            }
        } while(true);
        if(hashset != null)
        {
            Iterator iterator2 = hashset.iterator();
            do
            {
                if(!iterator2.hasNext())
                    break;
                String s = (String)iterator2.next();
                if(hashset1.remove(s))
                    setParticipantActive(addParticipant(layoutinflater, (Data.Participant)hashmap.get(s)), false);
            } while(true);
        }
        if(hashset1 != null)
        {
            for(Iterator iterator1 = hashset1.iterator(); iterator1.hasNext(); setParticipantActive(addParticipant(layoutinflater, (Data.Participant)hashmap.get((String)iterator1.next())), false));
        }
        MeetingMember meetingmember;
        if(currentSpeaker != null)
            if(meetingMembers.contains(currentSpeaker))
                setCurrentSpeaker(currentSpeaker);
            else
                setCurrentSpeaker(null);
        meetingmember = GCommApp.getInstance(getContext()).getSelectedVideoSource();
        if(meetingmember != null && meetingMembers.contains(meetingmember))
            pinVideo(meetingmember);
        else
            unpinVideo();
    }
	
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private final class AvatarContextMenuHelper implements android.view.MenuItem.OnMenuItemClickListener, android.view.View.OnCreateContextMenuListener {
		
		private final MeetingMember meetingMember;
	    private final Data.Participant participant;

	    AvatarContextMenuHelper(MeetingMember meetingmember, Data.Participant participant1)
	    {
	        super();
	        meetingMember = meetingmember;
	        participant = participant1;
	    }

	    public final void onCreateContextMenu(ContextMenu contextmenu, View view, android.view.ContextMenu.ContextMenuInfo contextmenuinfo)
	    {
	        boolean flag = true;
	        (new MenuInflater(getContext())).inflate(R.menu.hangout_avatar_tray, contextmenu);
	        MenuItem menuitem = contextmenu.findItem(R.id.menu_hangout_avatar_profile);
	        MenuItem menuitem1 = contextmenu.findItem(R.id.menu_hangout_avatar_pin_video);
	        MenuItem menuitem2 = contextmenu.findItem(R.id.menu_hangout_avatar_unpin_video);
	        MenuItem menuitem3 = contextmenu.findItem(R.id.menu_hangout_avatar_remote_mute);
	        MenuItem menuitem4 = contextmenu.findItem(R.id.menu_hangout_avatar_block);
	        if(meetingMember == null)
	        {
	            menuitem.setTitle(participant.getFullName());
	            menuitem1.setVisible(false);
	            menuitem2.setVisible(false);
	            menuitem3.setVisible(false);
	            menuitem4.setVisible(false);
	        } else
	        {
	            menuitem.setTitle(meetingMember.getName(getContext()));
	            boolean flag1;
	            if(meetingMember != pinnedVideoMember)
	                menuitem1.setVisible(flag);
	            else
	                menuitem2.setVisible(flag);
	            if(!meetingMember.isMediaBlocked())
	                flag1 = flag;
	            else
	                flag1 = false;
	            menuitem3.setVisible(flag1);
	            if(meetingMember.isSelfProfile())
	            {
	                menuitem4.setVisible(false);
	            } else
	            {
	                if(meetingMember.isMediaBlocked())
	                    flag = false;
	                menuitem4.setVisible(flag);
	            }
	        }
	    }

	    public final boolean onMenuItemClick(MenuItem menuitem)
	    {
	        int i = menuitem.getItemId();
	        if(R.id.menu_hangout_avatar_profile == i) {
	        	android.content.Intent intent = Intents.getProfileActivityIntent(getContext(), mHangoutTile.getAccount(), participant.getParticipantId(), null);
		        getContext().startActivity(intent);
	        } else if(R.id.menu_hangout_avatar_pin_video == i) {
	        	pinVideo(meetingMember);
	        } else if(R.id.menu_hangout_avatar_unpin_video == i) {
	        	unpinVideo();
	        } else if(R.id.menu_hangout_avatar_remote_mute == i) {
	        	GCommApp.getInstance(getContext()).getGCommNativeWrapper().remoteMute(meetingMember);
	        } else {
	        	int j = R.id.menu_hangout_avatar_block;
		        if(i != j) 
		        	return false; 
		        else 
		        	(new BlockPersonDialog(false, meetingMember)).show(((EsFragmentActivity)getContext()).getSupportFragmentManager(), null);
	        }
	        
	        return true;
	    }

	}

	private final class EventHandler extends GCommEventHandler
	{
	
	    public final void onCurrentSpeakerChanged(MeetingMember meetingmember)
	    {
	        if(pinnedVideoMember != null)
	            setCurrentSpeaker(meetingmember);
	    }
	
	    public final void onMediaBlock(MeetingMember meetingmember, MeetingMember meetingmember1, boolean flag)
	    {
	        super.onMediaBlock(meetingmember, meetingmember1, flag);
	        setOverlay(meetingmember);
	        setOverlay(meetingmember1);
	    }
	
	    public final void onVideoPauseStateChanged(MeetingMember meetingmember, boolean flag)
	    {
	        super.onVideoPauseStateChanged(meetingmember, flag);
	        setOverlay(meetingmember);
	    }
	
	    public final void onVideoSourceChanged(int i, MeetingMember meetingmember, boolean flag)
	    {
	        super.onVideoSourceChanged(i, meetingmember, flag);
	        if(i == mainVideoRequestId && pinnedVideoMember == null)
	            setCurrentSpeaker(meetingmember);
	    }
	
	}
}
