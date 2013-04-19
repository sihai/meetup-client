/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.fragments.BlockPersonDialog;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.client.util.QuickActions;

/**
 * 
 * @author sihai
 *
 */
public class TabletFilmStripView extends LinearLayout {

	private Dialog mContextMenuDialog;
    private int mCurrentOrientation;
    private CountDownTimer mDismissMenuTimer;
    private final EventHandler mEventHandler = new EventHandler();
    private final int mFilmStripMargin;
    private GCommApp mGCommAppInstance;
    private HangoutTile mHangoutTile;
    private HashMap mMeetingMembersByVideoView;
    private MeetingMember mPinnedVideoMember;
    private boolean mShouldShowStatusIcons;
    private Boolean mShouldShowStatusIconsOverride;
    private HashMap mTimersByMeetingMember;
    private HashMap mVideoViewsByMeetingMember;
    private boolean msResumed;
    
    
    public TabletFilmStripView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mVideoViewsByMeetingMember = new HashMap();
        mMeetingMembersByVideoView = new HashMap();
        mTimersByMeetingMember = new HashMap();
        mCurrentOrientation = 0;
        mFilmStripMargin = context.getResources().getDimensionPixelSize(R.dimen.hangout_filmstrip_margin);
        mShouldShowStatusIcons = Property.ENABLE_HANGOUT_FILMSTRIP_STATUS.getBoolean();
        mDismissMenuTimer = new CountDownTimer(5000L, 5000L) {

            public final void onFinish()
            {
                dismissParticipantMenuDialog();
            }

            public final void onTick(long l)
            {
            }

        };
    }

    private void addParticipantVideo(MeetingMember meetingmember)
    {
        if(!meetingmember.isSelf())
        {
            boolean flag;
            final RemoteVideoView.ParticipantVideoView participantvideoview;
            RemoteVideoView.ParticipantVideoView participantvideoview1;
            CountDownTimer countdowntimer;
            if(mPinnedVideoMember != null && meetingmember == mPinnedVideoMember)
                flag = true;
            else
                flag = false;
            participantvideoview = (RemoteVideoView.ParticipantVideoView)mVideoViewsByMeetingMember.get(meetingmember);
            if(participantvideoview != null)
                removeParticipantVideo(participantvideoview, flag);
            participantvideoview1 = new RemoteVideoView.ParticipantVideoView(getContext(), null, meetingmember);
            if(getChildCount() > 0)
            {
                android.widget.LinearLayout.LayoutParams layoutparams = new android.widget.LinearLayout.LayoutParams(-2, -2);
                layoutparams.leftMargin = mFilmStripMargin;
                participantvideoview1.setLayoutParams(layoutparams);
            }
            participantvideoview1.setOnTouchListener(new TouchListener(participantvideoview1));
            participantvideoview1.setTag(meetingmember);
            addView(participantvideoview1);
            participantvideoview1.setHangoutTile(mHangoutTile);
            participantvideoview1.onResume();
            countdowntimer = new CountDownTimer(2000L, 2000L) {

                public final void onFinish()
                {
                	participantvideoview.hideVolumeBar();
                }

                public final void onTick(long l)
                {
                }

            };
            mTimersByMeetingMember.put(meetingmember, countdowntimer);
            if(flag)
                pinVideo(meetingmember);
            mVideoViewsByMeetingMember.put(meetingmember, participantvideoview1);
            mMeetingMembersByVideoView.put(participantvideoview1, meetingmember);
            updateStatusOverlay(meetingmember);
        }
    }

    private GCommApp getGCommAppInstance()
    {
        GCommApp gcommapp;
        if(mGCommAppInstance != null)
            gcommapp = mGCommAppInstance;
        else
            gcommapp = GCommApp.getInstance(getContext());
        return gcommapp;
    }

    private void onVideoClicked(HangoutVideoView hangoutvideoview, MeetingMember meetingmember)
    {
        ParticipantContextMenuHelper participantcontextmenuhelper = new ParticipantContextMenuHelper(meetingmember);
        mContextMenuDialog = QuickActions.show(hangoutvideoview, null, null, participantcontextmenuhelper, participantcontextmenuhelper, true, true);
        mDismissMenuTimer.start();
    }

    private void pinVideo(MeetingMember meetingmember)
    {
        MeetingMember meetingmember1 = mPinnedVideoMember;
        getGCommAppInstance().setSelectedVideoSource(meetingmember);
        mHangoutTile.updateMainVideoStreaming();
        mPinnedVideoMember = meetingmember;
        updateStatusOverlay(meetingmember1);
        updateStatusOverlay(mPinnedVideoMember);
    }

    private void removeParticipantVideo(RemoteVideoView.ParticipantVideoView participantvideoview, boolean flag)
    {
        participantvideoview.onPause();
        MeetingMember meetingmember = (MeetingMember)mMeetingMembersByVideoView.remove(participantvideoview);
        if(meetingmember != null)
        {
            mVideoViewsByMeetingMember.remove(meetingmember);
            mTimersByMeetingMember.remove(meetingmember);
            if(flag && meetingmember == mPinnedVideoMember)
                unpinVideo();
        }
        removeView(participantvideoview);
    }

    private void unpinVideo()
    {
        getGCommAppInstance().setSelectedVideoSource(null);
        mHangoutTile.updateMainVideoStreaming();
        MeetingMember meetingmember = mPinnedVideoMember;
        mPinnedVideoMember = null;
        updateStatusOverlay(meetingmember);
    }

    private void updateStatusOverlay(MeetingMember meetingmember)
    {
        if(meetingmember != null && !meetingmember.isSelf())  {
        	HangoutVideoView hangoutvideoview = (HangoutVideoView)mVideoViewsByMeetingMember.get(meetingmember);
            if(hangoutvideoview != null)
                if(meetingmember == mPinnedVideoMember)
                    hangoutvideoview.showPinnedStatus();
                else
                    hangoutvideoview.hidePinnedStatus();
        }
    }

    public final void dismissParticipantMenuDialog()
    {
        mDismissMenuTimer.cancel();
        if(mContextMenuDialog != null)
        {
            mContextMenuDialog.dismiss();
            mContextMenuDialog = null;
        }
    }

    public final boolean isAudioMuted(MeetingMember meetingmember)
    {
        HangoutVideoView hangoutvideoview = (HangoutVideoView)mVideoViewsByMeetingMember.get(meetingmember);
        boolean flag;
        if(hangoutvideoview != null && hangoutvideoview.isAudioMuteStatusShowing())
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected void onConfigurationChanged(Configuration configuration)
    {
        super.onConfigurationChanged(configuration);
        if(configuration.orientation != mCurrentOrientation)
        {
            mCurrentOrientation = configuration.orientation;
            dismissParticipantMenuDialog();
        }
    }

    public final void onPause()
    {
        if(msResumed)
        {
            msResumed = false;
            getGCommAppInstance().unregisterForEvents(getContext(), mEventHandler, false);
            for(int i = -1 + getChildCount(); i >= 0; i--)
            {
                View view = getChildAt(i);
                if(view instanceof RemoteVideoView.ParticipantVideoView)
                    removeParticipantVideo((RemoteVideoView.ParticipantVideoView)view, false);
            }

            removeAllViews();
            dismissParticipantMenuDialog();
        }
    }

    public final void onResume()
    {
        if(!msResumed && mHangoutTile != null)
        {
            msResumed = true;
            mCurrentOrientation = 0;
            for(Iterator iterator = getGCommAppInstance().getGCommNativeWrapper().getMeetingMembersOrderedByEntry().iterator(); iterator.hasNext(); addParticipantVideo((MeetingMember)iterator.next()));
            getGCommAppInstance().registerForEvents(getContext(), mEventHandler, false);
        }
    }

    public final void requestPinVideo(RemoteVideoView.ParticipantVideoView participantvideoview)
    {
        if(msResumed)
        {
            MeetingMember meetingmember = participantvideoview.getMember();
            if(meetingmember != null && !meetingmember.isSelf())
                pinVideo(meetingmember);
        }
    }

    public void setGCommAppInstanceOverride(GCommApp gcommapp)
    {
        mGCommAppInstance = gcommapp;
    }

    public void setHangoutTile(HangoutTile hangouttile)
    {
        mHangoutTile = hangouttile;
    }

    public void setShouldShowStatusIconsOverride(boolean flag)
    {
        mShouldShowStatusIconsOverride = Boolean.valueOf(flag);
    }
    
    static void access$500(TabletFilmStripView tabletfilmstripview, MeetingMember meetingmember, boolean flag)
    {
        if(meetingmember != null && !meetingmember.isSelf()) {
        	HangoutVideoView hangoutvideoview = (HangoutVideoView)tabletfilmstripview.mVideoViewsByMeetingMember.get(meetingmember);
            if(hangoutvideoview != null)
                if(flag)
                {
                    boolean flag1;
                    if(tabletfilmstripview.mShouldShowStatusIconsOverride != null)
                        flag1 = tabletfilmstripview.mShouldShowStatusIconsOverride.booleanValue();
                    else
                        flag1 = tabletfilmstripview.mShouldShowStatusIcons;
                    if(flag1)
                        hangoutvideoview.showAudioMutedStatus();
                    hangoutvideoview.setVolume(0);
                    hangoutvideoview.hideVolumeBar();
                } else
                {
                    hangoutvideoview.hideAudioMutedStatus();
                }
        }
    }
    
    static void access$600(TabletFilmStripView tabletfilmstripview, MeetingMember meetingmember, int i)
    {
    	CountDownTimer countdowntimer;
        if(meetingmember != null && !meetingmember.isSelf())  {
        	HangoutVideoView hangoutvideoview = (HangoutVideoView)tabletfilmstripview.mVideoViewsByMeetingMember.get(meetingmember);
            if(hangoutvideoview != null)
            {
                hangoutvideoview.hideAudioMutedStatus();
                hangoutvideoview.setVolume(i);
                if(hangoutvideoview.isVideoShowing())
                    hangoutvideoview.showVolumeBar();
                else
                    hangoutvideoview.hideVolumeBar();
            }
            countdowntimer = (CountDownTimer)tabletfilmstripview.mTimersByMeetingMember.get(meetingmember);
            if(countdowntimer != null)
            {
                countdowntimer.cancel();
                countdowntimer.start();
            }
        }
    }
    
    static void access$800(TabletFilmStripView tabletfilmstripview, HangoutVideoView hangoutvideoview, MeetingMember meetingmember)
    {
        if(meetingmember != null)
        {
            if(meetingmember == tabletfilmstripview.mPinnedVideoMember)
                tabletfilmstripview.unpinVideo();
            else
                tabletfilmstripview.pinVideo(meetingmember);
        } else
        {
            tabletfilmstripview.onVideoClicked(hangoutvideoview, meetingmember);
        }
        return;
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private final class EventHandler extends GCommEventHandler
    {

        public final void onAudioMuteStateChanged(MeetingMember meetingmember, boolean flag)
        {
            super.onAudioMuteStateChanged(meetingmember, flag);
            TabletFilmStripView.access$500(TabletFilmStripView.this, meetingmember, flag);
        }

        public final void onMediaBlock(MeetingMember meetingmember, MeetingMember meetingmember1, boolean flag)
        {
            super.onMediaBlock(meetingmember, meetingmember1, flag);
            updateStatusOverlay(meetingmember);
            updateStatusOverlay(meetingmember1);
            mHangoutTile.updateMainVideoStreaming();
        }

        public final void onMeetingMemberEntered(MeetingMember meetingmember)
        {
            if(msResumed)
                addParticipantVideo(meetingmember);
        }

        public final void onMeetingMemberExited(MeetingMember meetingmember)
        {
            if(msResumed)
            {
                for(int i = -1 + getChildCount(); i >= 0; i--)
                {
                    View view = getChildAt(i);
                    if(view != null && (view instanceof RemoteVideoView.ParticipantVideoView) && ((RemoteVideoView.ParticipantVideoView)view).getMember() == meetingmember)
                        removeParticipantVideo((RemoteVideoView.ParticipantVideoView)view, true);
                }

            }
        }

        public final void onRemoteMute(MeetingMember meetingmember, MeetingMember meetingmember1)
        {
            super.onRemoteMute(meetingmember, meetingmember1);
            TabletFilmStripView.access$500(TabletFilmStripView.this, meetingmember, true);
        }

        public final void onVideoPauseStateChanged(MeetingMember meetingmember, boolean flag)
        {
            super.onVideoPauseStateChanged(meetingmember, flag);
            updateStatusOverlay(meetingmember);
        }

        public final void onVolumeChanged(MeetingMember meetingmember, int i)
        {
            super.onVolumeChanged(meetingmember, i);
            TabletFilmStripView.access$600(TabletFilmStripView.this, meetingmember, i);
        }

    }

    private final class ParticipantContextMenuHelper implements android.view.MenuItem.OnMenuItemClickListener, android.view.View.OnCreateContextMenuListener {

    	private final MeetingMember mMeetingMember;

        ParticipantContextMenuHelper(MeetingMember meetingmember)
        {
            super();
            mMeetingMember = meetingmember;
        }
        
        public final void onCreateContextMenu(ContextMenu contextmenu, View view, android.view.ContextMenu.ContextMenuInfo contextmenuinfo)
        {
            (new MenuInflater(getContext())).inflate(R.menu.hangout_participant_menu, contextmenu);
            MenuItem menuitem = contextmenu.findItem(R.id.menu_hangout_participant_profile);
            MenuItem menuitem1 = contextmenu.findItem(R.id.menu_hangout_participant_pin_video);
            MenuItem menuitem2 = contextmenu.findItem(R.id.menu_hangout_participant_unpin_video);
            MenuItem menuitem3 = contextmenu.findItem(R.id.menu_hangout_participant_remote_mute);
            MenuItem menuitem4 = contextmenu.findItem(R.id.menu_hangout_participant_remote_mute_disabled);
            MenuItem menuitem5 = contextmenu.findItem(R.id.menu_hangout_participant_block);
            MenuItem menuitem6 = contextmenu.findItem(R.id.menu_hangout_participant_block_disabled);
            menuitem.setTitle(mMeetingMember.getName(getContext()));
            if(mMeetingMember != mPinnedVideoMember)
                menuitem1.setVisible(true);
            else
                menuitem2.setVisible(true);
            if(!mMeetingMember.isMediaBlocked() && (view instanceof HangoutVideoView) && !((HangoutVideoView)view).isAudioMuteStatusShowing())
                menuitem3.setVisible(true);
            else
                menuitem4.setVisible(true);
            if(mMeetingMember.isSelfProfile() || mMeetingMember.isMediaBlocked())
                menuitem6.setVisible(true);
            else
                menuitem5.setVisible(true);
        }

        public final boolean onMenuItemClick(MenuItem menuitem)
        {
        	boolean flag;
        	int j;
        	
            int i = menuitem.getItemId();
            if(R.id.menu_hangout_participant_profile == i) {
            	android.content.Intent intent = Intents.getProfileActivityIntent(getContext(), mHangoutTile.getAccount(), mMeetingMember.getId(), null);
                getContext().startActivity(intent);
            } else if(R.id.menu_hangout_participant_pin_video == i) {
            	pinVideo(mMeetingMember);
            } else if(R.id.menu_hangout_participant_unpin_video == i) {
            	unpinVideo();
            } else if(R.id.menu_hangout_participant_remote_mute == i) {
            	getGCommAppInstance().getGCommNativeWrapper().remoteMute(mMeetingMember);
            } else {
            	j = R.id.menu_hangout_participant_block;
                flag = false;
                if(i != j) {
                	return flag;
                }
                (new BlockPersonDialog(false, mMeetingMember)).show(((EsFragmentActivity)getContext()).getSupportFragmentManager(), null);
            }
            
            mDismissMenuTimer.cancel();
            mContextMenuDialog = null;
            flag = true;
            return flag;
            
        }

    }

    private final class TouchListener extends android.view.GestureDetector.SimpleOnGestureListener implements android.view.View.OnTouchListener {

    	private final GestureDetector mGestureDetector;
        private final HangoutVideoView mVideoView;


        TouchListener(HangoutVideoView hangoutvideoview)
        {
            super();
            mGestureDetector = new GestureDetector(getContext(), this);
            mVideoView = hangoutvideoview;
            mGestureDetector.setOnDoubleTapListener(this);
        }
        
        public final boolean onDoubleTap(MotionEvent motionevent)
        {
            MeetingMember meetingmember = (MeetingMember)mVideoView.getTag();
            TabletFilmStripView.access$800(TabletFilmStripView.this, mVideoView, meetingmember);
            return true;
        }

        public final boolean onDown(MotionEvent motionevent)
        {
            return true;
        }

        public final boolean onFling(MotionEvent motionevent, MotionEvent motionevent1, float f, float f1)
        {
            boolean flag;
            if(mHangoutTile != null && f1 > 0.0F && f1 > Math.abs(f))
            {
                android.view.ViewParent viewparent = mVideoView.getParent();
                if(viewparent instanceof TabletFilmStripView)
                    mHangoutTile.hideChild((TabletFilmStripView)viewparent);
                flag = true;
            } else
            {
                flag = super.onFling(motionevent, motionevent1, f, f1);
            }
            return flag;
        }

        public final boolean onScroll(MotionEvent motionevent, MotionEvent motionevent1, float f, float f1)
        {
            boolean flag;
            if(getY() > (float)(getHeight() / 2))
            {
                if(f1 < 0.0F)
                    flag = true;
                else
                    flag = false;
            } else
            if(f1 > 0.0F)
                flag = true;
            else
                flag = false;
            if(flag)
            {
                android.view.View.DragShadowBuilder dragshadowbuilder = new android.view.View.DragShadowBuilder(mVideoView) {

                    public final void onDrawShadow(Canvas canvas)
                    {
                        android.graphics.Bitmap bitmap = mVideoView.getBitmap();
                        if(bitmap != null)
                            canvas.drawBitmap(bitmap, 0.0F, 0.0F, null);
                    }

                };
                mVideoView.startDrag(null, dragshadowbuilder, mVideoView, 0);
            }
            return true;
        }

        public final boolean onSingleTapConfirmed(MotionEvent motionevent)
        {
            if(getParent() != null && getVisibility() == 0 && mVideoView.getParent() != null)
            {
                MeetingMember meetingmember = (MeetingMember)mVideoView.getTag();
                onVideoClicked(mVideoView, meetingmember);
            }
            return true;
        }

        public final boolean onTouch(View view, MotionEvent motionevent)
        {
            return mGestureDetector.onTouchEvent(motionevent);
        }

    }
}
