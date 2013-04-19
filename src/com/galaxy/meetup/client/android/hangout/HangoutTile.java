/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog;
import com.galaxy.meetup.client.android.ui.fragments.BlockFragment;
import com.galaxy.meetup.client.android.ui.fragments.BlockPersonDialog;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.view.Tile;

/**
 * 
 * @author sihai
 *
 */
public abstract class HangoutTile extends RelativeLayout implements Tile {

	protected List greenRoomParticipants;
    protected Hangout.Info hangoutInfo;
    protected List listeners;
    protected EsAccount mAccount;
    protected boolean mHoaConsented;
    protected boolean skipGreenRoom;
	
    
    public HangoutTile(Context context)
    {
        super(context);
    }

    public HangoutTile(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public HangoutTile(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    private String getString(int i)
    {
        return getResources().getString(i);
    }

    public final void addParticipantPresenceListener(Tile.ParticipantPresenceListener participantpresencelistener)
    {
        if(listeners == null)
            listeners = new LinkedList();
        listeners.add(participantpresencelistener);
    }

    public final void blockPerson(Serializable serializable)
    {
        MeetingMember meetingmember = (MeetingMember)serializable;
        GCommNativeWrapper gcommnativewrapper = getGCommNativeWrapper();
        Iterator iterator = gcommnativewrapper.getMeetingMembersOrderedByEntry().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            MeetingMember meetingmember1 = (MeetingMember)iterator.next();
            if(!meetingmember1.getMucJid().equals(meetingmember.getMucJid()))
                continue;
            gcommnativewrapper.blockMedia(meetingmember1);
            break;
        } while(true);
        EsFragmentActivity esfragmentactivity = (EsFragmentActivity)getContext();
        BlockFragment.getInstance(esfragmentactivity, mAccount, meetingmember.getId(), meetingmember.getName(getContext()), false, true).show(esfragmentactivity);
    }

    public final EsAccount getAccount()
    {
        return mAccount;
    }

    public final HashSet getActiveParticipantIds()
    {
        HashSet hashset = new HashSet();
        if(GCommApp.isInstantiated())
        {
            for(Iterator iterator = getGCommNativeWrapper().getMeetingMembersOrderedByEntry().iterator(); iterator.hasNext(); hashset.add(((MeetingMember)iterator.next()).getId()));
        }
        return hashset;
    }

    protected final EsFragmentActivity getEsFragmentActivity()
    {
        return (EsFragmentActivity)getContext();
    }

    public final GCommNativeWrapper getGCommNativeWrapper()
    {
        return GCommApp.getInstance(getContext()).getGCommNativeWrapper();
    }

    protected final HangoutTileActivity getHangoutTileActivity()
    {
        return (HangoutTileActivity)getContext();
    }

    protected final String getWaitingMessage(boolean flag) {
        String s = getResources().getString(R.string.hangout_waiting_for_participants);
        Intent intent = ((Activity)getContext()).getIntent();
        if(!intent.hasExtra("audience") || flag) {
        	 return s; 
        }
        AudienceData audiencedata = (AudienceData)intent.getParcelableExtra("audience");
        if(audiencedata.getCircleCount() != 0) {
        	return s; 
        }
        if(audiencedata.getUserCount() != 1) {
        	if(audiencedata.getUserCount() == 2)
            {
                String s2 = getString(R.string.hangout_waiting_for_two_participants);
                Object aobj1[] = new Object[1];
                aobj1[0] = audiencedata.getUser(0).getName();
                s = String.format(s2, aobj1);
            } else
            if(audiencedata.getUserCount() > 2)
            {
                String s1 = getString(R.string.hangout_waiting_for_more_than_two_participants);
                Object aobj[] = new Object[2];
                aobj[0] = audiencedata.getUser(0).getName();
                aobj[1] = Integer.valueOf(-1 + audiencedata.getUserCount());
                s = String.format(s1, aobj);
            } 
        } else { 
        	 String s3 = getString(R.string.hangout_waiting_for_participant);
             Object aobj2[] = new Object[1];
             aobj2[0] = audiencedata.getUser(0).getName();
             s = String.format(s3, aobj2);
        }
        
        return s;
       
    }

    public void hideChild(View view)
    {
    }

    protected final void inviteMoreParticipants()
    {
        Activity activity = (Activity)getContext();
        String s = getResources().getString(R.string.realtimechat_conversation_invite_menu_item_text);
        List list = GCommApp.getInstance(activity).getGCommNativeWrapper().getMeetingMembersOrderedByEntry();
        List arraylist = new ArrayList();
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            MeetingMember meetingmember = (MeetingMember)iterator.next();
            if(!meetingmember.isSelf())
            {
                String s1 = "";
                if(meetingmember.getVCard() != null)
                    s1 = meetingmember.getVCard().getFullName();
                // TODO
                //arraylist.add(ParticipantUtils.makePersonFromParticipant(Data.Participant.newBuilder().setParticipantId(meetingmember.getId()).setFullName(s1).setFirstName(Hangout.getFirstNameFromFullName(s1)).build()));
            }
        } while(true);
        AudienceData audiencedata = new AudienceData(arraylist, null);
        activity.startActivityForResult(Intents.getEditAudienceActivityIntent(activity, mAccount, s, audiencedata, 5, false, false, true, true, true), 0);
    }

    public abstract boolean isTileStarted();

    public void onActivityResult(int i, int j, Intent intent)
    {
        if(i == 0 && j == -1 && intent != null)
        {
            AudienceData audiencedata = (AudienceData)intent.getParcelableExtra("audience");
            boolean flag;
            if(hangoutInfo == null || hangoutInfo.getLaunchSource() != Hangout.LaunchSource.Creation || hangoutInfo.getRingInvitees())
                flag = true;
            else
                flag = false;
            GCommApp.getInstance(getContext()).getGCommNativeWrapper().inviteToMeeting(audiencedata, "HANGOUT", flag, true);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        return false;
    }

    public void onPrepareOptionsMenu(Menu menu)
    {
    }

    public final void removeParticipantPresenceListener(Tile.ParticipantPresenceListener participantpresencelistener)
    {
        if(listeners != null)
            listeners.remove(participantpresencelistener);
    }

    protected final void sendInvites()
    {
        Intent intent = ((Activity)getContext()).getIntent();
        if(intent.hasExtra("audience"))
        {
            AudienceData audiencedata = (AudienceData)intent.getParcelableExtra("audience");
            if(audiencedata != null)
                GCommApp.getInstance(getContext()).getGCommNativeWrapper().inviteToMeeting(audiencedata, "HANGOUT", hangoutInfo.getRingInvitees(), true);
        }
    }

    public void setHangoutInfo(EsAccount esaccount, Hangout.Info info, List arraylist, boolean flag, boolean flag1)
    {
        mAccount = esaccount;
        hangoutInfo = info;
        greenRoomParticipants = arraylist;
        skipGreenRoom = flag1;
        Log.info("setHangoutInfo: %s", new Object[] {
            info
        });
        if((EsFragmentActivity)getContext() instanceof HangoutActivity)
            StressMode.initialize(getContext(), GCommApp.getInstance(getContext()), info);
    }

    public abstract void setParticipants(HashMap hashmap, HashSet hashset);

    public void showChild(View view)
    {
    }

    protected final void showError(int i, boolean flag)
    {
        showError(getResources().getString(i), flag);
    }

    protected final void showError(String s, final boolean finishOnOk)
    {
        Object aobj[] = new Object[2];
        aobj[0] = s;
        aobj[1] = Boolean.valueOf(finishOnOk);
        Log.debug("showError: message=%s finishOnOk=%s", aobj);
        if(StressMode.isEnabled())
        {
            if(finishOnOk)
                ((Activity)getContext()).finish();
        } else
        {
            AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(null, s, getContext().getResources().getString(R.string.ok), null, 0x1080027);
            alertfragmentdialog.setCancelable(false);
            alertfragmentdialog.setListener(new AlertFragmentDialog.AlertDialogListener() {

                public final void onDialogCanceled(String s1)
                {
                }

                public final void onDialogListClick(int i, Bundle bundle)
                {
                }

                public final void onDialogNegativeClick(String s1)
                {
                }

                public final void onDialogPositiveClick(Bundle bundle, String s1)
                {
                    if(finishOnOk)
                        getHangoutTileActivity().stopHangoutTile();
                }
            });
            alertfragmentdialog.show(((EsFragmentActivity)getContext()).getSupportFragmentManager(), "error");
        }
    }

    protected final void showHoaNotification(Button button)
    {
        (new HoaNotificationDialog(button)).show(((EsFragmentActivity)getContext()).getSupportFragmentManager(), "notification");
    }

    public abstract void transfer();

    public abstract void updateMainVideoStreaming();
    
    protected static enum State {
    	START,
    	SIGNING_IN,
    	SIGNIN_ERROR,
    	READY_TO_LAUNCH_MEETING,
    	ENTERING_MEETING,
    	IN_MEETING,
    	IN_MEETING_WITH_SELF_VIDEO_INSET,
    	IN_MEETING_WITH_FILM_STRIP;
    	
    	public final boolean isInMeeting()
        {
            boolean flag;
            if(this == IN_MEETING || this == IN_MEETING_WITH_SELF_VIDEO_INSET || this == IN_MEETING_WITH_FILM_STRIP)
                flag = true;
            else
                flag = false;
            return flag;
        }

    	public final boolean isSigningIn()
        {
            boolean flag;
            if(this == SIGNING_IN)
                flag = true;
            else
                flag = false;
            return flag;
        }
    }
    
    public static abstract interface HangoutTileActivity extends BlockFragment.Listener, BlockPersonDialog.PersonBlocker {

		public abstract Intent getGreenRoomParticipantListActivityIntent(List arraylist);

		public abstract Intent getHangoutNotificationIntent();

		public abstract Intent getParticipantListActivityIntent();

		public abstract void onMeetingMediaStarted();

		public abstract void stopHangoutTile();
	}

	private final class HoaNotificationDialog extends AlertFragmentDialog {
		Button mJoinButton;

		public HoaNotificationDialog(Button button) {
			super();
			mJoinButton = button;
		}

		public final Dialog onCreateDialog(Bundle bundle) {
			Context context = getDialogContext();
			View view = LayoutInflater.from(context).inflate(
					R.layout.hangout_onair_dialog, null);
			CheckBox checkbox = (CheckBox) view
					.findViewById(R.id.hangoutOnAirCheckbox);
			final AlertDialog alertdialog = (new android.app.AlertDialog.Builder(
					context))
					.setIcon(0x1080027)
					.setView(view)
					.setTitle(R.string.hangout_onair_warning_header)
					.setPositiveButton(
							R.string.hangout_onair_ok_button_text,
							new android.content.DialogInterface.OnClickListener() {

								public final void onClick(
										DialogInterface dialoginterface, int i) {
									mHoaConsented = true;
									if (mJoinButton.isShown()
											&& mJoinButton.isEnabled())
										mJoinButton.performClick();
								}
							})
					.setNegativeButton(
							R.string.hangout_onair_cancel_button_text, null)
					.show();
			alertdialog.getButton(-1).setEnabled(checkbox.isChecked());
			checkbox.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

				public final void onCheckedChanged(
						CompoundButton compoundbutton, boolean flag) {
					alertdialog.getButton(-1).setEnabled(flag);
				}
			});
			return alertdialog;
		}

	}
}
