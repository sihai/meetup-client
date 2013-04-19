/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.ProgressFragmentDialog;
import com.galaxy.meetup.client.android.ui.view.HangoutInviteesView;
import com.galaxy.meetup.client.android.ui.view.ParticipantsGalleryView;
import com.galaxy.meetup.client.android.ui.view.Tile;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.client.util.StringUtils;

/**
 * 
 * @author sihai
 *
 */
public class HangoutPhoneTile extends HangoutTile {

	static final boolean $assertionsDisabled;
    private TextView mEmptyHangoutMessageView;
    private final EventHandler mEventHandler;
    private FilmStripView mFilmStripView;
    private ParticipantsGalleryView mGreenRoomParticipantsGalleryView;
    private boolean mHadConnectedParticipant;
    private final Handler mHandler;
    private ViewGroup mHangoutLaunchJoinPanel;
    private HangoutParticipantsGalleryView mHangoutParticipantsGalleryView;
    private Hangout.SupportStatus mHangoutSupportStatus;
    private ImageButton mHangoutSwitchMenuButton;
    private boolean mInnerActionBarEnabled;
    private View mInstructionsView;
    private Runnable mInstructionsViewFadeOutRunnable;
    private ImageButton mInviteParticipantsMenuButton;
    private HangoutInviteesView mInviteesView;
    private boolean mIsHangoutLite;
    private boolean mIsTileStarted;
    private Button mJoinButton;
    private IncomingVideoView.MainVideoView mMainVideoView;
    private View mMessageContainer;
    private TextView mMessageView;
    private boolean mNeedToToastForInvite;
    private View mParticipantsView;
    private ViewGroup mRootView;
    private SelfVideoView mSelfVideoView;
    private FrameLayout mSelfVideoViewContainer;
    private boolean mShowOverlayMenu;
    private HangoutTile.State mState;
    private HangoutTile.State mStateBeforeStop;
    private ImageButton mSwitchCameraMenuItem;
    private View mTitleBarView;
    private ToastsView mToastsView;
    private ImageButton mToggleAudioMuteMenuButton;
    private ImageButton mToggleVideoMuteMenuButton;
    private View mTopMenuView;
    private View mTouchSensorView;
    private View mUpButton;

    static 
    {
        boolean flag;
        if(!HangoutPhoneTile.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
    public HangoutPhoneTile(Context context)
    {
        this(context, null);
    }

    public HangoutPhoneTile(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public HangoutPhoneTile(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mHandler = new Handler(Looper.getMainLooper());
        mIsHangoutLite = true;
        mEventHandler = new EventHandler();
        mInnerActionBarEnabled = true;
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = context;
        aobj[2] = mEventHandler;
        Log.debug("HangoutPhoneTile(): this=%s context=%s eventHandler=%s", aobj);
    }

    private void addSelfVideoViewToRootView()
    {
        ViewGroup viewgroup = (ViewGroup)mSelfVideoView.getParent();
        if(viewgroup != mSelfVideoViewContainer)
        {
            if(viewgroup != null)
                viewgroup.removeView(mSelfVideoView);
            android.widget.FrameLayout.LayoutParams layoutparams = new android.widget.FrameLayout.LayoutParams(-1, -1);
            mSelfVideoView.setLayoutParams(layoutparams);
            mSelfVideoViewContainer.addView(mSelfVideoView);
        }
    }

    private void checkAndDismissCallgrokLogUploadProgressDialog()
    {
        DialogFragment dialogfragment = (DialogFragment)((EsFragmentActivity)getContext()).getSupportFragmentManager().findFragmentByTag("log_upload");
        if(dialogfragment != null)
            dialogfragment.dismiss();
    }

    private void fadeOutInstructionsView()
    {
        if(mInstructionsView != null && mInstructionsView.getVisibility() != 8)
        {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
            animation.setAnimationListener(new HideViewAnimationListener(mInstructionsView));
            mInstructionsView.startAnimation(animation);
        }
    }

    private void setState(HangoutTile.State state)
    {
        boolean flag;
        HangoutTile.State state1;
        flag = true;
        Log.debug((new StringBuilder("Setting state to ")).append(state).toString());
        state1 = mState;
        mState = state;
        if(state.isInMeeting()) {
        	mGreenRoomParticipantsGalleryView.setVisibility(8);
            mInstructionsView.setVisibility(8);
            mHangoutLaunchJoinPanel.setVisibility(8);
            mJoinButton.setVisibility(8);
            mMainVideoView.setVisibility(0);
            if(!$assertionsDisabled && !mState.isInMeeting())
                throw new AssertionError();
            mIsHangoutLite = GCommApp.getInstance(getContext()).getGCommNativeWrapper().getIsHangoutLite();
            if(!mIsHangoutLite)
                mInviteParticipantsMenuButton.setVisibility(0);
            if(mState == HangoutTile.State.IN_MEETING_WITH_SELF_VIDEO_INSET)
            {
                if(!mIsHangoutLite)
                {
                    mTopMenuView.setVisibility(0);
                    mParticipantsView.setVisibility(0);
                    mHangoutParticipantsGalleryView.setVisibility(0);
                }
                if(!$assertionsDisabled && mState != HangoutTile.State.IN_MEETING_WITH_SELF_VIDEO_INSET)
                    throw new AssertionError();
                final Animation slideInDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_down_self);
                final Animation slideOutUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_up_self);
                final Animation slideInUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_up_self);
                final Animation slideOutDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_down_self);
                final View overlayMenu = findViewById(R.id.overlay_menu);
                int i;
                if(overlayMenu.getVisibility() != 0)
                    flag = false;
                mShowOverlayMenu = flag;
                mTouchSensorView.setOnTouchListener(new android.view.View.OnTouchListener() {

                    public final boolean onTouch(View view, MotionEvent motionevent)
                    {
                        if(motionevent.getAction() == 1)
                        {
                            HangoutPhoneTile hangoutphonetile;
                            boolean flag1;
                            boolean flag2;
                            if(mShowOverlayMenu)
                            {
                                if(!mIsHangoutLite && mHadConnectedParticipant)
                                {
                                    mTopMenuView.startAnimation(slideOutUp);
                                    mTopMenuView.setVisibility(8);
                                }
                                slideOutDown.setAnimationListener(new OverlayMenuSlideOutAnimationListener());
                                overlayMenu.startAnimation(slideOutDown);
                                overlayMenu.setVisibility(8);
                            } else
                            {
                                if(!mIsHangoutLite && mHadConnectedParticipant)
                                {
                                    mTopMenuView.startAnimation(slideInDown);
                                    mTopMenuView.setVisibility(0);
                                }
                                slideOutDown.setAnimationListener(null);
                                overlayMenu.startAnimation(slideInUp);
                                overlayMenu.setVisibility(0);
                                mSelfVideoView.setExtraBottomOffset(overlayMenu.getHeight());
                            }
                            hangoutphonetile = HangoutPhoneTile.this;
                            flag1 = mShowOverlayMenu;
                            flag2 = false;
                            if(!flag1)
                                flag2 = true;
                            hangoutphonetile.mShowOverlayMenu = flag2;
                        }
                        return true;
                    }

                });
                i = getResources().getDimensionPixelOffset(R.dimen.hangout_overlay_menu_height);
                mSelfVideoView.setExtraBottomOffset(i);
                mSelfVideoView.setVisibility(0);
                addSelfVideoViewToRootView();
                mFilmStripView.setVisibility(8);
            } else
            {
                mTopMenuView.setVisibility(8);
                mParticipantsView.setVisibility(8);
                mHangoutParticipantsGalleryView.setVisibility(8);
                mTouchSensorView.setOnTouchListener(null);
                mSelfVideoView.setVisibility(0);
                mFilmStripView.setVisibility(0);
            }
            updateOverlayMenuAndMessageViews();
            mSelfVideoView.setVisibleViewOnTouchListener(new android.view.View.OnTouchListener() {

                public final boolean onTouch(View view, MotionEvent motionevent)
                {
                    if(Property.NATIVE_HANGOUT_LOG.getBoolean() && motionevent.getAction() == 1)
                    {
                        HangoutPhoneTile hangoutphonetile = HangoutPhoneTile.this;
                        HangoutTile.State state2;
                        android.content.SharedPreferences.Editor editor;
                        boolean flag1;
                        if(mState == HangoutTile.State.IN_MEETING_WITH_SELF_VIDEO_INSET)
                            state2 = HangoutTile.State.IN_MEETING_WITH_FILM_STRIP;
                        else
                            state2 = HangoutTile.State.IN_MEETING_WITH_SELF_VIDEO_INSET;
                        hangoutphonetile.setState(state2);
                        editor = getContext().getSharedPreferences("com.google.android.apps.plus.hangout.HangoutTile", 0).edit();
                        if(mState == HangoutTile.State.IN_MEETING_WITH_FILM_STRIP)
                            flag1 = true;
                        else
                            flag1 = false;
                        editor.putBoolean("filmStrip_", flag1);
                        editor.commit();
                    }
                    return true;
                }

            });
            if(!mIsTileStarted) {
            	// TODO
            	return;
            }
            if(!state1.isInMeeting()) {
            	mMainVideoView.onResume();
                mHangoutParticipantsGalleryView.setMainVideoRequestId(mMainVideoView.getRequestId());
                mSelfVideoView.startCapturing();
                if(state != HangoutTile.State.IN_MEETING_WITH_SELF_VIDEO_INSET) {
                	mFilmStripView.onResume(mSelfVideoView);
                	return;
                } else { 
                	mToastsView.onResume();
                	return;
                }
            } else { 
            	 if(state == HangoutTile.State.IN_MEETING_WITH_SELF_VIDEO_INSET)
                 {
                     mToastsView.onResume();
                     mFilmStripView.onPause();
                     mFilmStripView.onResume(mSelfVideoView);
                     return;
                 }
                 mToastsView.onPause();
                 return;
            }
        } else {
        	mHangoutParticipantsGalleryView.setVisibility(8);
            mToastsView.setVisibility(8);
            mMainVideoView.setVisibility(8);
            mFilmStripView.setVisibility(8);
            addSelfVideoViewToRootView();
            mTopMenuView.setVisibility(8);
            mInviteesView.setVisibility(8);
            mEmptyHangoutMessageView.setVisibility(0);
            mSelfVideoView.setLayoutMode(SelfVideoView.LayoutMode.FIT);
            switch(state) {
            case START:
            	if(!skipGreenRoom)
                {
                    mGreenRoomParticipantsGalleryView.removeAllParticipants();
                    mGreenRoomParticipantsGalleryView.setVisibility(0);
                    if(greenRoomParticipants != null)
                        mGreenRoomParticipantsGalleryView.addParticipants(greenRoomParticipants);
                    mGreenRoomParticipantsGalleryView.setCommandListener(new ParticipantsGalleryView.SimpleCommandListener(mGreenRoomParticipantsGalleryView, getAccount()) {

                        public final void onShowParticipantList()
                        {
                            Intent intent = getHangoutTileActivity().getGreenRoomParticipantListActivityIntent(greenRoomParticipants);
                            getContext().startActivity(intent);
                        }

                    });
                    mInstructionsView.setVisibility(0);
                    mHandler.postDelayed(mInstructionsViewFadeOutRunnable, 5000L);
                }
            case SIGNING_IN:
            	mJoinButton.setVisibility(8);
                mMessageContainer.setVisibility(0);
                mMessageView.setText(R.string.hangout_launch_signing_in);
            	break;
            case SIGNIN_ERROR:
            	mJoinButton.setVisibility(8);
                mMessageContainer.setVisibility(0);
                mMessageView.setText(R.string.hangout_launch_signing_in);
            	break;
            case READY_TO_LAUNCH_MEETING:
            	mHangoutLaunchJoinPanel.setVisibility(0);
                mJoinButton.setVisibility(0);
                Button button = mJoinButton;
                if(StressMode.isEnabled())
                    flag = false;
                button.setEnabled(flag);
                mMessageContainer.setVisibility(8);
            	break;
            case ENTERING_MEETING:
            	fadeOutInstructionsView();
                mJoinButton.setVisibility(8);
                mMessageContainer.setVisibility(0);
                mMessageView.setText(R.string.hangout_launch_joining);
            	break;
            default:
            	break;
            }
        }
    }

    private void showError(GCommNativeWrapper.Error error, boolean flag) {
    	
    	switch(error) {
    	case FATAL:
    		 showError(R.string.hangout_fatal_error, flag);
    		break;
    	case INCONSISTENT_STATE:
    		 showError(R.string.hangout_fatal_error, flag);
    		break;
    	case NETWORK:
    		showError(R.string.hangout_network_error, flag);
    		break;
    	case AUTHENTICATION:
    		showError(R.string.hangout_authentication_error, flag);
    		break;
    	case AUDIO_VIDEO_SESSION:
    		showError(R.string.hangout_audio_video_error, flag);
    		break;
    	case UNKNOWN:
    		showError(R.string.hangout_unknown_error, flag);
    		break;
    	default: 
    		break;
    	}
    }

    private void updateAudioMuteMenuButton(boolean flag)
    {
        if(flag)
        {
            mToggleAudioMuteMenuButton.setImageResource(R.drawable.hangout_ic_menu_audio_unmute);
            mToggleAudioMuteMenuButton.setContentDescription(getResources().getString(R.string.hangout_menu_audio_unmute));
        } else
        {
            mToggleAudioMuteMenuButton.setImageResource(R.drawable.hangout_ic_menu_audio_mute);
            mToggleAudioMuteMenuButton.setContentDescription(getResources().getString(R.string.hangout_menu_audio_mute));
        }
    }

    private void updateAudioMuteMenuButtonState(Boolean boolean1)
    {
        boolean flag;
        if(boolean1 == null)
            flag = GCommApp.getInstance(getContext()).isAudioMute();
        else
            flag = boolean1.booleanValue();
        if(mToggleAudioMuteMenuButton != null)
        {
            updateAudioMuteMenuButton(flag);
            ImageButton imagebutton = mToggleAudioMuteMenuButton;
            boolean flag1;
            if(!GCommApp.getInstance(getContext()).isInAHangoutWithMedia() || GCommApp.getInstance(getContext()).hasAudioFocus())
                flag1 = true;
            else
                flag1 = false;
            imagebutton.setEnabled(flag1);
        }
    }

    private void updateOverlayMenuAndMessageViews() {
        GCommNativeWrapper gcommnativewrapper = GCommApp.getInstance(getContext()).getGCommNativeWrapper();
        if(null == gcommnativewrapper || null == hangoutInfo) {
        	return;
        }
        
        mHadConnectedParticipant = gcommnativewrapper.getHadSomeConnectedParticipantInPast();
        HangoutInviteesView hangoutinviteesview = mInviteesView;
        byte byte0;
        boolean flag;
        SelfVideoView selfvideoview;
        if(mHadConnectedParticipant)
            byte0 = 8;
        else
            byte0 = 0;
        hangoutinviteesview.setVisibility(byte0);
        if(!mHadConnectedParticipant) {
        	mTopMenuView.setVisibility(8);
        	Intent intent = ((Activity)getContext()).getIntent();
            if(intent.hasExtra("audience") || gcommnativewrapper.getHadSomeConnectedParticipantInPast()) {
            	 AudienceData audiencedata = (AudienceData)intent.getParcelableExtra("audience");
                 if(audiencedata.getUserCount() > 0) {
                 	mInviteesView.setInvitees(audiencedata, getAccount());
                 	mInviteesView.setVisibility(0);
                 	flag = true;
                 } else {
                 	flag = false;
                 }
            } else {
            	flag = false;
            }
            
            if(!flag)
                mInviteesView.setVisibility(8);
        } else { 
        	if(mShowOverlayMenu)
                mTopMenuView.setVisibility(0);
        }
        
        selfvideoview = mSelfVideoView;
        Intent intent;
        SelfVideoView.LayoutMode layoutmode;
        AudienceData audiencedata;
        if(gcommnativewrapper.getHasSomeConnectedParticipant())
            layoutmode = SelfVideoView.LayoutMode.INSET;
        else
            layoutmode = SelfVideoView.LayoutMode.FIT;
        selfvideoview.setLayoutMode(layoutmode);
        mSelfVideoView.requestLayout();
        if(gcommnativewrapper.getMeetingMemberCount() > 1)
            mEmptyHangoutMessageView.setVisibility(8);
        if(gcommnativewrapper.getHasSomeConnectedParticipant())
            mMessageContainer.setVisibility(8);
        else
        if(hangoutInfo.getLaunchSource() == Hangout.LaunchSource.Ring)
        {
            if(gcommnativewrapper.getMeetingMemberCount() == 1 && !gcommnativewrapper.getHadSomeConnectedParticipantInPast())
            {
                mMessageContainer.setVisibility(0);
                mMessageView.setText(getResources().getString(R.string.hangout_already_ended));
                mEmptyHangoutMessageView.setVisibility(8);
            }
        } else
        if(!gcommnativewrapper.getHadSomeConnectedParticipantInPast())
        {
            if(mState != null && mState.isInMeeting())
            {
                mMessageContainer.setVisibility(0);
                mMessageView.setText(getWaitingMessage(false));
            }
            mEmptyHangoutMessageView.setVisibility(8);
        } else
        if((hangoutInfo.getLaunchSource() == Hangout.LaunchSource.Ring || hangoutInfo.getRingInvitees()) && gcommnativewrapper.getMeetingMemberCount() == 1 && !gcommnativewrapper.getHadSomeConnectedParticipantInPast())
        {
            mMessageContainer.setVisibility(0);
            mMessageView.setText(getResources().getString(R.string.hangout_no_one_joined));
            mEmptyHangoutMessageView.setVisibility(8);
        }
        
    }

    private void updateVideoMuteMenuButton(boolean flag)
    {
        if(flag)
        {
            mToggleVideoMuteMenuButton.setImageResource(R.drawable.hangout_ic_menu_video_unmute);
            mToggleVideoMuteMenuButton.setContentDescription(getResources().getString(R.string.hangout_menu_video_unmute));
        } else
        {
            mToggleVideoMuteMenuButton.setImageResource(R.drawable.hangout_ic_menu_video_mute);
            mToggleVideoMuteMenuButton.setContentDescription(getResources().getString(R.string.hangout_menu_video_mute));
        }
    }

    private void updateVideoMuteMenuButtonState(Boolean boolean1)
    {
        boolean flag;
        if(boolean1 == null)
            flag = GCommApp.getInstance(getContext()).isOutgoingVideoMute();
        else
            flag = boolean1.booleanValue();
        if(mToggleVideoMuteMenuButton != null)
            updateVideoMuteMenuButton(flag);
    }

    public final boolean isTileStarted()
    {
        return mIsTileStarted;
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
        super.onActivityResult(i, j, intent);
        if(i == 0 && j == -1 && intent != null)
            mNeedToToastForInvite = true;
    }

    public final void onCreate(Bundle bundle)
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = mEventHandler;
        Log.debug("HangoutPhoneTile.onCreate: this=%s context=%s eventHandler=%s", aobj);
        if(bundle != null)
            mStateBeforeStop = HangoutTile.State.values()[bundle.getInt("HangoutTile_state")];
    }

    public final void onPause()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = mEventHandler;
        Log.debug("HangoutPhoneTile.onPause: this=%s context=%s eventHandler=%s", aobj);
        mStateBeforeStop = mState;
        mState = null;
    }

    public final void onResume()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = mEventHandler;
        Log.debug("HangoutPhoneTile.onResume: this=%s context=%s eventHandler=%s", aobj);
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        HangoutTile.State state;
        Object aobj[];
        if(mState == null)
            state = mStateBeforeStop;
        else
            state = mState;
        aobj = new Object[4];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = mEventHandler;
        aobj[3] = state;
        Log.debug("HangoutPhoneTile.onSaveInstanceState: this=%s context=%s eventHandler=%s stateToSave:%s", aobj);
        bundle.putInt("HangoutTile_state", state.ordinal());
    }

    public final void onStart()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = mEventHandler;
        Log.debug("HangoutPhoneTile.onStart: this=%s context=%s eventHandler=%s", aobj);
        GCommApp.getInstance(getContext()).startingHangoutActivity((EsFragmentActivity)getContext());
    }

    public final void onStop()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = mEventHandler;
        Log.debug("HangoutPhoneTile.onStop: this=%s context=%s eventHandler=%s", aobj);
        GCommApp.getInstance(getContext()).stoppingHangoutActivity();
    }

    public final void onTilePause()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = mEventHandler;
        Log.debug("HangoutPhoneTile.onTilePause: this=%s context=%s eventHandler=%s", aobj);
        if(mHangoutSupportStatus == Hangout.SupportStatus.SUPPORTED)
        {
            if(mState != null && mState.isInMeeting())
            {
                mMainVideoView.onPause();
                if(mState == HangoutTile.State.IN_MEETING_WITH_SELF_VIDEO_INSET)
                    mToastsView.onPause();
                else
                if(mState == HangoutTile.State.IN_MEETING_WITH_FILM_STRIP)
                    mFilmStripView.onPause();
            } else
            {
                mHandler.removeCallbacks(mInstructionsViewFadeOutRunnable);
            }
            mSelfVideoView.onPause();
            mGreenRoomParticipantsGalleryView.dismissAvatarMenuDialog();
            mHangoutParticipantsGalleryView.onPause();
            checkAndDismissCallgrokLogUploadProgressDialog();
            mIsTileStarted = false;
        }
    }

    public final void onTileResume()
    {
        Object aobj[] = new Object[4];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = mEventHandler;
        aobj[3] = hangoutInfo;
        Log.debug("HangoutPhoneTile.onTileResume: this=%s context=%s eventHandler=%s hangoutInfo=%s", aobj);
        if(!$assertionsDisabled && mAccount == null)
            throw new AssertionError();
        mHangoutSupportStatus = Hangout.getSupportedStatus(getContext(), mAccount);
        if(mHangoutSupportStatus != Hangout.SupportStatus.SUPPORTED) {
        	showError(mHangoutSupportStatus.getErrorMessage(getContext()), true);
        	return;
        }
        
        mIsTileStarted = true;
        setState(HangoutTile.State.START);
        mSelfVideoView.onResume();
        mHangoutParticipantsGalleryView.onResume();
        if(hangoutInfo == null || hangoutInfo.getRoomType() != Hangout.RoomType.UNKNOWN) {
        	if(mNeedToToastForInvite)
            {
                mToastsView.addToast(R.string.hangout_invites_sent);
                mNeedToToastForInvite = false;
            }
            if(!GCommApp.getInstance(getContext()).hasAudioFocus()) {
            	if(GCommApp.getInstance(getContext()).isInAHangoutWithMedia())
                    mToastsView.addToast(R.string.hangout_audiofocus_loss_warning); 
            } else { 
            	((Activity)getContext()).setVolumeControlStream(0);
            }
            
            if(GCommApp.getInstance(getContext()).isInHangout(hangoutInfo))
            {
                HangoutTile.State state;
                if(getContext().getSharedPreferences("com.google.android.apps.plus.hangout.HangoutTile", 0).getBoolean("filmStrip_", false))
                    state = HangoutTile.State.IN_MEETING_WITH_FILM_STRIP;
                else
                    state = HangoutTile.State.IN_MEETING_WITH_SELF_VIDEO_INSET;
                setState(state);
            } else
            if(mStateBeforeStop != null && mStateBeforeStop.isInMeeting())
            {
                mStateBeforeStop = null;
                if(ExitHistory.exitReported(getContext(), hangoutInfo))
                {
                    ((HangoutTile.HangoutTileActivity)getContext()).stopHangoutTile();
                    Log.debug("Stopping hangout tile. Exit from hangout already reported.");
                } else
                {
                    if(ExitHistory.exitedNormally(getContext(), hangoutInfo))
                    {
                        showError(R.string.hangout_exited, true);
                    } else
                    {
                        GCommNativeWrapper.Error error = ExitHistory.getError(getContext(), hangoutInfo);
                        if(error != null)
                            showError(error, true);
                        else
                            showError(R.string.hangout_exit_generic, true);
                    }
                    ExitHistory.recordExitReported(getContext(), hangoutInfo);
                }
            } else
            if(GCommApp.getInstance(getContext()).isInAHangout())
                showError(R.string.hangout_launch_already_in_hangout, true);
            else
            if(getGCommNativeWrapper().getCurrentState() == GCommNativeWrapper.GCommAppState.SIGNED_IN)
            {
                mEventHandler.onSignedIn(GCommApp.getInstance(getContext()).getGCommNativeWrapper().getUserJid());
            } else
            {
                GCommApp.getInstance(getContext()).disconnect();
                setState(HangoutTile.State.SIGNING_IN);
                GCommApp.getInstance(getContext()).signinUser(getAccount());
            }
        } else { 
        	showError(R.string.hangout_not_supported_type, true);
        	return;
        }
        
    }

    public final void onTileStart()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = mEventHandler;
        Log.debug("HangoutPhoneTile.onTileStart: this=%s context=%s eventHandler=%s", aobj);
        if(!$assertionsDisabled && mAccount == null)
            throw new AssertionError();
        addView(((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(R.layout.hangout_tile, null));
        mRootView = (ViewGroup)findViewById(R.id.hangout_tile_root_view);
        mTopMenuView = findViewById(R.id.hangout_top_menu);
        mTitleBarView = findViewById(R.id.title_bar);
        View view = mTitleBarView;
        int i;
        final GCommApp gCommApp;
        if(mInnerActionBarEnabled)
            i = 0;
        else
            i = 8;
        view.setVisibility(i);
        mParticipantsView = findViewById(R.id.hangout_participants_info);
        mTouchSensorView = findViewById(R.id.touch_sensor_view);
        mSelfVideoViewContainer = (FrameLayout)findViewById(R.id.self_video_container);
        mSelfVideoView = new SelfVideoView(getContext(), null);
        mSelfVideoView.setHangoutTile(this);
        mUpButton = findViewById(R.id.up);
        mUpButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                ((Activity)getContext()).onBackPressed();
            }

        });
        mInviteParticipantsMenuButton = (ImageButton)findViewById(R.id.invite_participants);
        mInviteParticipantsMenuButton.setVisibility(8);
        mInviteParticipantsMenuButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                inviteMoreParticipants();
            }

        });
        gCommApp = GCommApp.getInstance(getContext());
        mToggleAudioMuteMenuButton = (ImageButton)findViewById(R.id.hangout_menu_common_toggle_audio_mute);
        updateAudioMuteMenuButton(gCommApp.isAudioMute());
        mToggleAudioMuteMenuButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                GCommApp gcommapp = gCommApp;
                boolean flag;
                if(!gCommApp.isAudioMute())
                    flag = true;
                else
                    flag = false;
                gcommapp.setAudioMute(flag);
            }

        });
        mToggleVideoMuteMenuButton = (ImageButton)findViewById(R.id.hangout_menu_common_toggle_video_mute);
        updateVideoMuteMenuButton(gCommApp.isOutgoingVideoMute());
        mToggleVideoMuteMenuButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                GCommApp.sendEmptyMessage(getContext(), 202);
            }

        });
        mHangoutSwitchMenuButton = (ImageButton)findViewById(R.id.hangout_menu_common_hangout_switch);
        if(mHangoutSwitchMenuButton != null && Property.ENABLE_HANGOUT_SWITCH.getBoolean())
        {
            mHangoutSwitchMenuButton.setVisibility(0);
            mHangoutSwitchMenuButton.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view1)
                {
                    transfer();
                }

            });
        }
        mSwitchCameraMenuItem = (ImageButton)findViewById(R.id.hangout_menu_common_switch_camera);
        mSwitchCameraMenuItem.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                GCommApp.sendEmptyMessage(getContext(), 201);
            }

        });
        findViewById(R.id.hangout_menu_common_exit).setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                access$1400(HangoutPhoneTile.this, view1);
            }

        });
        updateAudioMuteMenuButtonState(null);
        if(Cameras.isAnyCameraAvailable())
        {
            mToggleVideoMuteMenuButton.setVisibility(0);
            updateVideoMuteMenuButtonState(null);
            if(Cameras.isFrontFacingCameraAvailable() && Cameras.isRearFacingCameraAvailable())
                mSwitchCameraMenuItem.setVisibility(0);
        }
        mInstructionsView = findViewById(R.id.hangout_green_room_instructions);
        mInstructionsViewFadeOutRunnable = new Runnable() {

            public final void run()
            {
                fadeOutInstructionsView();
            }
        };
        mHangoutLaunchJoinPanel = (ViewGroup)findViewById(R.id.hangout_launch_join_panel);
        mJoinButton = (Button)findViewById(R.id.hangout_launch_join_button);
        mJoinButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view1)
            {
                if(!$assertionsDisabled && mState != HangoutTile.State.READY_TO_LAUNCH_MEETING)
                    throw new AssertionError(mState);
                setState(HangoutTile.State.ENTERING_MEETING);
                if(hangoutInfo == null)
                {
                    Intent intent = ((Activity)getContext()).getIntent();
                    GCommApp.getInstance(getContext()).createHangout(intent.getBooleanExtra("hangout_ring_invitees", false));
                } else
                {
                    GCommApp gcommapp = GCommApp.getInstance(getContext());
                    Hangout.Info info = hangoutInfo;
                    boolean flag;
                    if(hangoutInfo.getLaunchSource() == Hangout.LaunchSource.MissedCall)
                        flag = true;
                    else
                        flag = false;
                    gcommapp.enterHangout(info, flag, greenRoomParticipants, mHoaConsented);
                }
            }

        });
        mGreenRoomParticipantsGalleryView = (ParticipantsGalleryView)findViewById(R.id.green_room_participants_view);
        mToastsView = (ToastsView)findViewById(R.id.toasts_view);
        mMainVideoView = (IncomingVideoView.MainVideoView)findViewById(R.id.main_video);
        mMainVideoView.setHangoutTile(this);
        mFilmStripView = (FilmStripView)findViewById(R.id.film_strip);
        mFilmStripView.setHangoutTile(this);
        mHangoutParticipantsGalleryView = (HangoutParticipantsGalleryView)findViewById(R.id.hangout_participants_view);
        mHangoutParticipantsGalleryView.setHangoutTile(this);
        mMessageView = (TextView)findViewById(R.id.message);
        mMessageContainer = findViewById(R.id.message_container);
        mInviteesView = (HangoutInviteesView)findViewById(R.id.invitees_view);
        mEmptyHangoutMessageView = (TextView)mHangoutParticipantsGalleryView.findViewById(R.id.empty_message);
        mHangoutParticipantsGalleryView.setAccount(mAccount);
        mGreenRoomParticipantsGalleryView.setAccount(mAccount);
        GCommApp.getInstance(getContext()).registerForEvents(getContext(), mEventHandler, false);
    }

    public final void onTileStop()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = mEventHandler;
        Log.debug("HangoutPhoneTile.onTileStop: this=%s context=%s eventHandler=%s", aobj);
        GCommApp.getInstance(getContext()).unregisterForEvents(getContext(), mEventHandler, false);
        mTouchSensorView.setOnTouchListener(null);
        removeAllViews();
        mRootView = null;
        mTopMenuView = null;
        mTitleBarView = null;
        mParticipantsView = null;
        mTouchSensorView = null;
        mToggleAudioMuteMenuButton = null;
        mToggleVideoMuteMenuButton = null;
        mSwitchCameraMenuItem = null;
        mSelfVideoViewContainer = null;
        mSelfVideoView = null;
        mGreenRoomParticipantsGalleryView = null;
        mInstructionsView = null;
        mHangoutLaunchJoinPanel = null;
        mJoinButton = null;
        mHangoutParticipantsGalleryView = null;
        mToastsView = null;
        mMainVideoView = null;
        mFilmStripView = null;
        mMessageView = null;
        mMessageContainer = null;
        mInviteesView = null;
        mEmptyHangoutMessageView = null;
    }

    public final HangoutPhoneTile setInnerActionBarEnabled(boolean flag)
    {
        mInnerActionBarEnabled = false;
        if(mTitleBarView != null)
            mTitleBarView.setVisibility(8);
        return this;
    }

    public void setParticipants(HashMap hashmap, HashSet hashset)
    {
        if(mHangoutParticipantsGalleryView != null)
            mHangoutParticipantsGalleryView.setParticipants(hashmap, hashset);
    }

    public final void transfer()
    {
        AudienceData audiencedata = new AudienceData(null, null);
        Log.debug("Transfer hangout");
        GCommApp.getInstance(getContext()).getGCommNativeWrapper().inviteToMeeting(audiencedata, "TRANSFER", true, false);
        mToastsView.addToast(R.string.hangout_transfer_sent);
    }

    public final void updateMainVideoStreaming()
    {
        mMainVideoView.updateVideoStreaming();
    }
    
    static void access$1400(HangoutPhoneTile hangoutphonetile, View view)
    {
        Log.debug((new StringBuilder("HangoutPhoneTile onExit with state:")).append(hangoutphonetile.mState).toString());
        if(hangoutphonetile.mState != null)
            if(hangoutphonetile.mState.isInMeeting())
            {
                Log.debug("Setting userRequestedMeetingExit to true");
                GCommApp.getInstance(hangoutphonetile.getContext()).exitMeeting();
            } else
            {
                ((HangoutTile.HangoutTileActivity)hangoutphonetile.getContext()).stopHangoutTile();
                Log.debug("Did not set userRequestedMeetingExit");
            }
        return;
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    private class AlertDialogListener implements AlertFragmentDialog.AlertDialogListener
    {

	    public final void onDialogCanceled(String s)
	    {
	    }
	
	    public final void onDialogListClick(int i, Bundle bundle)
	    {
	    }
	
	    public final void onDialogNegativeClick(String s)
	    {
	    }
	
	    public final void onDialogPositiveClick(Bundle bundle, String s)
	    {
	        getHangoutTileActivity().stopHangoutTile();
	        Intent intent = new Intent("android.intent.action.VIEW");
	        intent.addFlags(0x80000);
	        intent.setData(Uri.parse("market://details?id=com.google.android.apps.plus"));
	        intent.addFlags(0x14000000);
	        getContext().startActivity(intent);
	    }

    }

    final class EventHandler extends GCommEventHandler
    {

	    private void notifyListeners()
	    {
	        if(listeners != null)
	        {
	            Iterator iterator = listeners.iterator();
	            while(iterator.hasNext()) 
	                ((Tile.ParticipantPresenceListener)iterator.next()).onParticipantPresenceChanged();
	        }
	    }
	
	    public final void onAudioMuteStateChanged(MeetingMember meetingmember, boolean flag)
	    {
	        if(meetingmember == null || meetingmember.isSelf())
	            updateAudioMuteMenuButtonState(Boolean.valueOf(flag));
	    }
	
	    public final void onCallgrokLogUploadCompleted$4f708078()
	    {
	        checkAndDismissCallgrokLogUploadProgressDialog();
	        getHangoutTileActivity().stopHangoutTile();
	    }
	
	    public final void onError(GCommNativeWrapper.Error error)
	    {
	        super.onError(error);
	        Log.info("HangoutPhoneTile$EventHandler.onError(%s) %s", new Object[] {
	            error, this
	        });
	        if(error == GCommNativeWrapper.Error.AUTHENTICATION)
	        {
	            if(!$assertionsDisabled && !mState.isSigningIn())
	                throw new AssertionError(mState);
	            Object aobj[] = new Object[2];
	        } else
	        {
	            showError(error, true);
	        }
	        if(hangoutInfo != null)
	            ExitHistory.recordErrorExit(getContext(), hangoutInfo, error, true);
	    }
	
	    public final void onHangoutCreated(Hangout.Info info)
	    {
	        super.onHangoutCreated(info);
	        hangoutInfo = info;
	        Log.debug((new StringBuilder("HangoutPhoneTile.onHangoutCreated: ")).append(info).toString());
	        updateOverlayMenuAndMessageViews();
	        GCommApp.getInstance(getContext()).enterHangout(info, true, greenRoomParticipants, mHoaConsented);
	    }
	
	    public final void onHangoutWaitTimeout(Hangout.Info info)
	    {
	        super.onHangoutWaitTimeout(info);
	        mMessageContainer.setVisibility(0);
	        mMessageView.setText(getResources().getString(R.string.hangout_no_one_joined));
	    }

	    public final void onMeetingEnterError(GCommNativeWrapper.MeetingEnterError meetingentererror)
	    {
	        super.onMeetingEnterError(meetingentererror);
	        if(null == mRootView) {
	        	return;
	        }
	        
	        setState(HangoutTile.State.READY_TO_LAUNCH_MEETING);
	        if(meetingentererror == GCommNativeWrapper.MeetingEnterError.HANGOUT_ON_AIR)
	        {
	            showHoaNotification(mJoinButton);
	            return;
	        }
	        if(meetingentererror == GCommNativeWrapper.MeetingEnterError.OUTDATED_CLIENT)
	        {
	            AlertFragmentDialog alertfragmentdialog = AlertFragmentDialog.newInstance(null, HangoutPhoneTile.this.getContext().getResources().getString(R.string.hangout_enter_timeout_error), HangoutPhoneTile.this.getContext().getResources().getString(R.string.hangout_enter_max_users_error), HangoutPhoneTile.this.getContext().getResources().getString(R.string.hangout_enter_server_error), 0x1080027);
	            return;
	        }
	        
	        // TODO
	    }

	    public final void onMeetingExited(boolean flag)
	    {
	        Object aobj[] = new Object[3];
	        aobj[0] = this;
	        aobj[1] = HangoutPhoneTile.this;
	        aobj[2] = Boolean.valueOf(flag);
	        Log.debug("HangoutPhoneTile$EventHandler.onMeetingExited: this=%s, tile=%s clientInitiated=%s", aobj);
	        super.onMeetingExited(flag);
	        if(mRootView != null && mState != null)
	        {
	            if(flag)
	            {
	                if(StringUtils.getDomain(mAccount.getName()).equals("google.com"))
	                {
	                    getGCommNativeWrapper().uploadCallgrokLog();
	                    ProgressFragmentDialog.newInstance(getResources().getString(R.string.hangout_log_upload_title), getResources().getString(R.string.hangout_log_upload_message)).show(getEsFragmentActivity().getSupportFragmentManager(), "log_upload");
	                } else
	                {
	                    getHangoutTileActivity().stopHangoutTile();
	                }
	            } else
	            {
	                showError(R.string.hangout_exited, true);
	            }
	            ExitHistory.recordNormalExit(getContext(), hangoutInfo, true);
	        }
	    }

	    public final void onMeetingMediaStarted()
	    {
	        super.onMeetingMediaStarted();
	        if(mRootView != null)
	        {
	            boolean flag = getContext().getSharedPreferences("com.google.android.apps.plus.hangout.HangoutTile", 0).getBoolean("filmStrip_", false);
	            HangoutPhoneTile hangoutphonetile = HangoutPhoneTile.this;
	            HangoutTile.State state;
	            if(flag)
	                state = HangoutTile.State.IN_MEETING_WITH_FILM_STRIP;
	            else
	                state = HangoutTile.State.IN_MEETING_WITH_SELF_VIDEO_INSET;
	            hangoutphonetile.setState(state);
	            updateOverlayMenuAndMessageViews();
	            GCommApp.getInstance(getContext()).getGCommService().showHangoutNotification(getHangoutTileActivity().getHangoutNotificationIntent());
	            getHangoutTileActivity().onMeetingMediaStarted();
	        }
	    }

	    public final void onMeetingMemberEntered(MeetingMember meetingmember)
	    {
	        super.onMeetingMemberEntered(meetingmember);
	        updateOverlayMenuAndMessageViews();
	        notifyListeners();
	    }
	
	    public final void onMeetingMemberExited(MeetingMember meetingmember)
	    {
	        super.onMeetingMemberExited(meetingmember);
	        updateOverlayMenuAndMessageViews();
	        notifyListeners();
	    }
	
	    public final void onMeetingMemberPresenceConnectionStatusChanged(MeetingMember meetingmember)
	    {
	        super.onMeetingMemberPresenceConnectionStatusChanged(meetingmember);
	        updateOverlayMenuAndMessageViews();
	        notifyListeners();
	    }
	
	    public final void onMucEntered(MeetingMember meetingmember)
	    {
	        super.onMucEntered(meetingmember);
	        sendInvites();
	    }
	
	    public final void onRemoteMute(MeetingMember meetingmember, MeetingMember meetingmember1)
	    {
	        if(meetingmember.isSelf())
	            updateAudioMuteMenuButtonState(Boolean.valueOf(true));
	    }

	    public final void onSignedIn(String s)
	    {
	        boolean flag = true;
	        super.onSignedIn(s);
	        Object aobj[] = new Object[2];
	        aobj[0] = this;
	        aobj[1] = HangoutPhoneTile.this;
	        Log.debug("HangoutPhoneTile$EventHandler.onSignedIn: this=%s, tile=%s", aobj);
	        if(!$assertionsDisabled && !mState.isSigningIn())
	            throw new AssertionError(mState);
	        if(mRootView != null)
	            if(skipGreenRoom)
	            {
	                setState(HangoutTile.State.ENTERING_MEETING);
	                if(hangoutInfo == null)
	                {
	                    Intent intent = ((Activity)getContext()).getIntent();
	                    GCommApp.getInstance(getContext()).createHangout(intent.getBooleanExtra("hangout_ring_invitees", false));
	                } else
	                {
	                    GCommApp gcommapp = GCommApp.getInstance(getContext());
	                    Hangout.Info info = hangoutInfo;
	                    if(hangoutInfo.getLaunchSource() != Hangout.LaunchSource.MissedCall)
	                        flag = false;
	                    gcommapp.enterHangout(info, flag, greenRoomParticipants, mHoaConsented);
	                }
	            } else
	            {
	                setState(HangoutTile.State.READY_TO_LAUNCH_MEETING);
	            }
	    }

	    public final void onSignedOut()
	    {
	        super.onSignedOut();
	        Log.info("HangoutPhoneTile$EventHandler.onSignedOut");
	        if(mRootView != null)
	        {
	            showError(R.string.hangout_signin_timeout_error, true);
	            setState(HangoutTile.State.SIGNIN_ERROR);
	        }
	    }

	    public final void onSigninTimeOutError()
	    {
	        super.onSigninTimeOutError();
	        Log.info((new StringBuilder("HangoutPhoneTile$EventHandler.onSigninTimeOutError: this=")).append(this).toString());
	        if(mRootView != null)
	        {
	            showError(R.string.hangout_signin_timeout_error, true);
	            setState(HangoutTile.State.SIGNIN_ERROR);
	        }
	    }

	    public final void onVideoMuteChanged(boolean flag)
	    {
	        updateVideoMuteMenuButtonState(Boolean.valueOf(flag));
	    }
	
	}

	private final class OverlayMenuSlideOutAnimationListener implements android.view.animation.Animation.AnimationListener
	{
	
	    public final void onAnimationEnd(Animation animation)
	    {
	        mSelfVideoView.setExtraBottomOffset(0);
	    }
	
	    public final void onAnimationRepeat(Animation animation)
	    {
	    }
	
	    public final void onAnimationStart(Animation animation)
	    {
	    }
	    
	}
}
