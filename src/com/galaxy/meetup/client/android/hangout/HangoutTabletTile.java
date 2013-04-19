/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.ui.fragments.AlertFragmentDialog;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.ProgressFragmentDialog;
import com.galaxy.meetup.client.android.ui.view.HangoutInviteesView;
import com.galaxy.meetup.client.android.ui.view.ScaledLayout;
import com.galaxy.meetup.client.android.ui.view.Tile;
import com.galaxy.meetup.client.android.ui.view.TwoPointerGestureDetector;
import com.galaxy.meetup.client.util.AccessibilityUtils;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.client.util.StringUtils;

/**
 * 
 * @author sihai
 *
 */
public class HangoutTabletTile extends HangoutTile implements
		OnHierarchyChangeListener {

	static final boolean $assertionsDisabled;
    private RelativeLayout bradyLayoutContainer;
    private final EventHandler eventHandler;
    private ViewGroup hangoutLaunchJoinPanel;
    private View instructionsView;
    private Runnable instructionsViewFadeOutRunnable;
    private boolean isRegistered;
    private boolean isTileStarted;
    private ActionBar mActionBar;
    private CountDownTimer mActionBarDismissalTimer;
    private EsFragmentActivity mActivity;
    private RelativeLayout mCenterStageContainer;
    private RemoteVideoView.CenterStageVideoView mCenterStageVideo;
    private StageViewMode mCurrentStageMode;
    private boolean mEnableStageIcons;
    private Animation mFilmStripAnimOut;
    private View mFilmStripContainer;
    private boolean mFilmStripIsPaused;
    private CountDownTimer mFilmStripPauseTimer;
    private TabletFilmStripView mFilmStripView;
    private final Handler mHandler;
    private Hangout.SupportStatus mHangoutSupportStatus;
    private ScaledLayout mInset;
    private FrameLayout mInsetVideo;
    private View mInviteesContainer;
    private TextView mInviteesMessageView;
    private HangoutInviteesView mInviteesView;
    private boolean mIsAudioEnabled;
    private boolean mIsAudioMuted;
    private boolean mIsHangoutLite;
    private boolean mIsVideoMuted;
    private Button mJoinButton;
    private LocalVideoView mLocalVideoView;
    private View mMessageContainer;
    private TextView mMessageView;
    private boolean mNeedToToastForInvite;
    private Animation mSlideInUp;
    private ToastsView mToastsView;
    private ViewMode mViewMode;
    private ProgressBar progressBar;
    private TextView progressText;
    private RelativeLayout stageLayoutContainer;
    private HangoutTile.State state;
    private HangoutTile.State stateBeforeStop;

    static 
    {
        boolean flag;
        if(!HangoutTabletTile.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
    public HangoutTabletTile(Context context)
    {
        this(context, null);
    }

    public HangoutTabletTile(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public HangoutTabletTile(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mIsAudioEnabled = true;
        mHandler = new Handler(Looper.getMainLooper());
        mFilmStripIsPaused = true;
        mIsHangoutLite = true;
        eventHandler = new EventHandler();
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = context;
        aobj[2] = eventHandler;
        Log.debug("HangoutTabletTile(): this=%s context=%s eventHandler=%s", aobj);
    }
    
    private void addVideoToCenterStage(HangoutVideoView hangoutvideoview)
    {
        ViewGroup viewgroup = (ViewGroup)hangoutvideoview.getParent();
        if(viewgroup != mCenterStageContainer)
        {
            if(viewgroup != null)
                viewgroup.removeView(hangoutvideoview);
            mCenterStageContainer.addView(hangoutvideoview);
            hangoutvideoview.setLayoutMode(HangoutVideoView.LayoutMode.FIT);
            mCenterStageContainer.invalidate();
            mCenterStageContainer.requestLayout();
        }
    }

    private void checkAndDismissCallgrokLogUploadProgressDialog()
    {
        DialogFragment dialogfragment = (DialogFragment)mActivity.getSupportFragmentManager().findFragmentByTag("log_upload");
        if(dialogfragment != null)
            dialogfragment.dismiss();
    }

    private void fadeOutInstructionsView()
    {
        if(isRegistered && instructionsView.getVisibility() != 8)
        {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
            animation.setAnimationListener(new HideViewAnimationListener(instructionsView));
            instructionsView.startAnimation(animation);
        }
    }
    
    private void hideActionBar() {
    	if(null == mActionBar) {
    		return;
    	}
    	
    	mActionBarDismissalTimer.cancel();
        if(!AccessibilityUtils.isAccessibilityEnabled(getContext()) && GCommApp.getInstance(getContext()).getGCommNativeWrapper().getHadSomeConnectedParticipantInPast())
            mActionBar.hide();
    }

    private void hideFilmStrip()
    {
        if(mFilmStripContainer.getVisibility() == 0)
            mFilmStripContainer.startAnimation(mFilmStripAnimOut);
    }

    private void pauseFilmStrip()
    {
        mFilmStripPauseTimer.cancel();
        if(!mFilmStripIsPaused)
        {
            mFilmStripIsPaused = true;
            mFilmStripView.onPause();
        }
    }

    private void resumeFilmStrip()
    {
        mFilmStripPauseTimer.cancel();
        if(mFilmStripIsPaused)
        {
            mFilmStripIsPaused = false;
            mFilmStripView.onResume();
        }
    }

    private void setStageViewMode(StageViewMode stageviewmode) {
    	
        if(mViewMode == ViewMode.MODE_STAGE_VIEW && stageviewmode != mCurrentStageMode) {
        	if(StageViewMode.STAGE_MODE_LOCAL_ONLY == stageviewmode) {
        		mCenterStageContainer.removeAllViews();
                addVideoToCenterStage(mLocalVideoView);
                mInset.setVisibility(8);
                mCenterStageContainer.setVisibility(0);
                mCurrentStageMode = stageviewmode;
        	} else if(StageViewMode.STAGE_MODE_LOCAL_AND_REMOTE == stageviewmode) {
        		mCenterStageContainer.removeAllViews();
                addVideoToCenterStage(mCenterStageVideo);
                LocalVideoView localvideoview = mLocalVideoView;
                ViewGroup viewgroup = (ViewGroup)localvideoview.getParent();
                if(viewgroup != mInsetVideo)
                {
                    if(viewgroup != null)
                        viewgroup.removeView(localvideoview);
                    mInsetVideo.removeAllViews();
                    mInsetVideo.addView(localvideoview);
                    localvideoview.setLayoutMode(HangoutVideoView.LayoutMode.FIT);
                    mInsetVideo.invalidate();
                    mInsetVideo.requestLayout();
                }
                mInset.setVisibility(0);
                mCenterStageContainer.setVisibility(0);
                mCurrentStageMode = stageviewmode;
        	} else {
        		Log.error((new StringBuilder("Unknown stage layout mode: ")).append(stageviewmode).toString());
        	}
        }
    }
    
    private void setState(HangoutTile.State state1)
    {
        HangoutTile.State state2;
        Log.debug((new StringBuilder("Setting state to ")).append(state1).toString());
        state2 = state;
        state = state1;
        if(state1.isInMeeting()) {
        	instructionsView.setVisibility(8);
            hangoutLaunchJoinPanel.setVisibility(8);
            mJoinButton.setVisibility(8);
            progressBar.setVisibility(8);
            progressText.setVisibility(8);
            if(!$assertionsDisabled && !state.isInMeeting())
                throw new AssertionError();
            if(mViewMode != ViewMode.MODE_STAGE_VIEW) {
            	if(mViewMode == ViewMode.MODE_BRADY_VIEW)
                    stageLayoutContainer.setVisibility(8);
            } else { 
            	stageLayoutContainer.setVisibility(0);
                bradyLayoutContainer.setVisibility(8);
                mLocalVideoView.setVisibility(0);
                mCenterStageVideo.setVisibility(0);
                showFilmStrip();
            }
            updateHangoutViews();
            if(isTileStarted)
                if(state2.isInMeeting())
                {
                    if(mViewMode == ViewMode.MODE_STAGE_VIEW)
                    {
                        mToastsView.onResume();
                        resumeFilmStrip();
                    } else
                    if(mViewMode != ViewMode.MODE_BRADY_VIEW);
                } else
                if(mViewMode == ViewMode.MODE_STAGE_VIEW)
                {
                    mCenterStageVideo.onResume();
                    mLocalVideoView.onResume();
                    resumeFilmStrip();
                    mToastsView.onResume();
                } else
                {
                	mViewMode = ViewMode.MODE_BRADY_VIEW;
                }
        } else { 
        	mToastsView.setVisibility(8);
            mInset.setVisibility(8);
            bradyLayoutContainer.setVisibility(8);
            if(mViewMode == ViewMode.MODE_STAGE_VIEW)
            {
                setStageViewMode(StageViewMode.STAGE_MODE_LOCAL_ONLY);
            } else
            {
            	mViewMode = ViewMode.MODE_BRADY_VIEW;
            }
            mMessageContainer.setVisibility(8);
            mInviteesContainer.setVisibility(8);
            hangoutLaunchJoinPanel.setVisibility(0);
            switch(state1) {
            case SIGNING_IN:
            	if(!skipGreenRoom)
                {
                    instructionsView.setVisibility(0);
                    mHandler.postDelayed(instructionsViewFadeOutRunnable, 5000L);
                }
            case SIGNIN_ERROR:
            case READY_TO_LAUNCH_MEETING:
            	mJoinButton.setVisibility(8);
                progressBar.setVisibility(0);
                progressText.setVisibility(0);
                progressText.setText(R.string.hangout_launch_signing_in);
            	break;
            case ENTERING_MEETING:
            	mJoinButton.setVisibility(0);
                Button button = mJoinButton;
                boolean flag = StressMode.isEnabled();
                boolean flag1 = false;
                if(!flag)
                    flag1 = true;
                button.setEnabled(flag1);
                progressBar.setVisibility(8);
                progressText.setVisibility(8);
            	break;
            case IN_MEETING:
            	fadeOutInstructionsView();
                mJoinButton.setVisibility(8);
                progressBar.setVisibility(0);
                progressText.setVisibility(0);
                progressText.setText(R.string.hangout_launch_joining);
            	break;
            default:
            	break;
            }
        }
    }

    private void showActionBar()
    {
        if(mActionBar != null)
        {
            mActionBar.show();
            mActionBarDismissalTimer.start();
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

    private void showFilmStrip()
    {
        int i = GCommApp.getInstance(getContext()).getGCommNativeWrapper().getMeetingMemberCount();
        if(!mIsHangoutLite && i > 1 && mFilmStripContainer.getVisibility() != 0)
            mFilmStripContainer.startAnimation(mSlideInUp);
    }

    private void updateAudioMuteMenuButtonState(Boolean boolean1)
    {
        boolean flag;
        if(boolean1 == null)
            flag = GCommApp.getInstance(getContext()).isAudioMute();
        else
            flag = boolean1.booleanValue();
        if(isRegistered)
        {
            boolean flag1;
            boolean flag2;
            boolean flag3;
            if(!GCommApp.getInstance(getContext()).isInAHangoutWithMedia() || GCommApp.getInstance(getContext()).hasAudioFocus())
                flag1 = true;
            else
                flag1 = false;
            flag2 = mIsAudioMuted;
            flag3 = false;
            if(flag2 != flag)
            {
                flag3 = true;
                mIsAudioMuted = flag;
            }
            if(mIsAudioEnabled != flag1)
            {
                flag3 = true;
                mIsAudioEnabled = flag1;
            }
            if(flag3)
                mActivity.invalidateOptionsMenu();
            if(mViewMode == ViewMode.MODE_STAGE_VIEW)
            {
                if(mIsAudioMuted)
                    mLocalVideoView.showAudioMutedStatus();
                else
                    mLocalVideoView.hideAudioMutedStatus();
            } else
            {
                ViewMode _tmp = mViewMode;
                ViewMode _tmp1 = ViewMode.MODE_BRADY_VIEW;
            }
        }
    }

    private void updateHangoutViews()
    {
        byte byte0 = 8;
        GCommNativeWrapper gcommnativewrapper = GCommApp.getInstance(getContext()).getGCommNativeWrapper();
        mIsHangoutLite = GCommApp.getInstance(getContext()).getGCommNativeWrapper().getIsHangoutLite();
        mActivity.invalidateOptionsMenu();
        if(null == gcommnativewrapper || null == hangoutInfo) {
        	setStageViewMode(StageViewMode.STAGE_MODE_LOCAL_ONLY);
        	return;
        }
        
        int i;
        boolean flag;
        boolean flag1;
        boolean flag2;
        i = gcommnativewrapper.getMeetingMemberCount();
        flag = gcommnativewrapper.getHadSomeConnectedParticipantInPast();
        if(hangoutInfo.getLaunchSource() == Hangout.LaunchSource.Creation && hangoutInfo.getRingInvitees())
            flag1 = true;
        else
            flag1 = false;
        if(state != null && state.isInMeeting())
            flag2 = true;
        else
            flag2 = false;
        if(!flag) {
        	if(flag1 && flag2) {
        		Intent intent = ((Activity)getContext()).getIntent();
                if(intent.hasExtra("audience") || gcommnativewrapper.getHadSomeConnectedParticipantInPast()) {
                	AudienceData audiencedata = (AudienceData)intent.getParcelableExtra("audience");
                	mInviteesView.setInvitees(audiencedata, getAccount());
	                if(mInviteesView.getAvatarCount() > 0) {
	                	mInviteesView.setVisibility(0);
	                	flag1 = true;
	                } else {
	                	flag1 = false;
	                }
                } else {
                	flag1 = false;
                }
                
                if(flag1)
                    mInviteesContainer.setVisibility(0);
        	}
        	if(flag1)
                mInviteesContainer.setVisibility(0);
        } else { 
        	mInviteesContainer.setVisibility(byte0);
        }
        
        mActionBarDismissalTimer.start();
        Intent intent;
        AudienceData audiencedata;
        if(gcommnativewrapper.getHasSomeConnectedParticipant())
        {
            setStageViewMode(StageViewMode.STAGE_MODE_LOCAL_AND_REMOTE);
            mMessageContainer.setVisibility(byte0);
            mInviteesContainer.setVisibility(byte0);
        } else
        {
            setStageViewMode(StageViewMode.STAGE_MODE_LOCAL_ONLY);
            if(hangoutInfo.getLaunchSource() == Hangout.LaunchSource.Ring)
            {
                if(i == 1 && !flag)
                {
                    mMessageView.setText(getResources().getString(R.string.hangout_already_ended));
                    mMessageContainer.setVisibility(0);
                }
            } else
            if(flag && (hangoutInfo.getLaunchSource() == Hangout.LaunchSource.Ring || hangoutInfo.getRingInvitees()))
            {
                if(i == 1 && !flag)
                {
                    mMessageView.setText(getResources().getString(R.string.hangout_no_one_joined));
                    mMessageContainer.setVisibility(0);
                }
            } else
            {
                String s = getWaitingMessage(flag);
                if(state != null && state.isInMeeting())
                    byte0 = 0;
                if(flag2)
                    if(flag1)
                    {
                        mInviteesMessageView.setText(s);
                    } else
                    {
                        mMessageView.setText(s);
                        mMessageContainer.setVisibility(byte0);
                    }
            }
        }
        
    }

    private void updateVideoMuteMenuButtonState(Boolean boolean1)
    {
        boolean flag;
        if(boolean1 == null)
            flag = GCommApp.getInstance(getContext()).isOutgoingVideoMute();
        else
            flag = boolean1.booleanValue();
        if(isRegistered && mIsVideoMuted != flag)
        {
            mIsVideoMuted = flag;
            mActivity.invalidateOptionsMenu();
        }
        return;
    }

    public final void hideChild(View view)
    {
        if(view == mFilmStripView)
            hideFilmStrip();
    }

    public final boolean isTileStarted()
    {
        return isTileStarted;
    }

    public final void onActivityResult(int i, int j, Intent intent)
    {
        super.onActivityResult(i, j, intent);
        if(i == 0 && j == -1 && intent != null)
            mNeedToToastForInvite = true;
    }

    public void onChildViewAdded(View view, View view1)
    {
        if(view == mFilmStripView)
            showFilmStrip();
    }

    public void onChildViewRemoved(View view, View view1)
    {
        if(view == mFilmStripView && mFilmStripView.getChildCount() <= 0)
            hideFilmStrip();
    }

    public final void onCreate(Bundle bundle)
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = eventHandler;
        Log.debug("HangoutTabletTile.onCreate: this=%s context=%s eventHandler=%s", aobj);
        if(bundle != null)
            stateBeforeStop = HangoutTile.State.values()[bundle.getInt("HangoutTile_state")];
        mEnableStageIcons = Property.ENABLE_HANGOUT_STAGE_STATUS.getBoolean();
        mActivity = (EsFragmentActivity)getContext();
        mActionBar = mActivity.getActionBar();
        ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(R.layout.hangout_tablet_tile, this, true);
        mViewMode = ViewMode.MODE_STAGE_VIEW;
        mCurrentStageMode = StageViewMode.STAGE_MODE_INVALID;
        stageLayoutContainer = (RelativeLayout)findViewById(R.id.stage_container);
        bradyLayoutContainer = (RelativeLayout)findViewById(R.id.brady_container);
        mInset = (ScaledLayout)findViewById(R.id.inset);
        mInsetVideo = (FrameLayout)findViewById(R.id.inset_video_container);
        mLocalVideoView = new LocalVideoView(getContext(), null);
        mLocalVideoView.setHangoutTile(this);
        mFilmStripContainer = findViewById(R.id.filmstrip_container);
        updateAudioMuteMenuButtonState(null);
        if(Cameras.isAnyCameraAvailable())
            updateVideoMuteMenuButtonState(null);
        instructionsView = findViewById(R.id.hangout_green_room_instructions);
        instructionsViewFadeOutRunnable = new Runnable() {

            public final void run()
            {
                fadeOutInstructionsView();
            }

        };
        hangoutLaunchJoinPanel = (ViewGroup)findViewById(R.id.hangout_launch_join_panel);
        mJoinButton = (Button)findViewById(R.id.hangout_launch_join_button);
        mJoinButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                if(!$assertionsDisabled && state != HangoutTile.State.READY_TO_LAUNCH_MEETING)
                    throw new AssertionError(state);
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
        progressBar = (ProgressBar)findViewById(R.id.hangout_launch_progress_bar);
        progressText = (TextView)findViewById(R.id.hangout_launch_progress_text);
        mToastsView = (ToastsView)findViewById(R.id.toasts_view);
        mCenterStageContainer = (RelativeLayout)findViewById(R.id.center_stage_video_container);
        mCenterStageVideo = new RemoteVideoView.CenterStageVideoView(getContext(), null);
        mCenterStageVideo.setHangoutTile(this);
        mFilmStripView = (TabletFilmStripView)findViewById(R.id.film_strip);
        mFilmStripView.setHangoutTile(this);
        mFilmStripView.setOnHierarchyChangeListener(this);
        mMessageContainer = findViewById(R.id.message_container);
        mMessageView = (TextView)findViewById(R.id.message);
        mInviteesContainer = findViewById(R.id.invitees_container);
        mInviteesMessageView = (TextView)findViewById(R.id.invitees_message);
        mInviteesView = (HangoutInviteesView)findViewById(R.id.invitees_view);
        mSlideInUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_up_self);
        mFilmStripAnimOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_down_self);
        mActionBarDismissalTimer = new CountDownTimer(5000L, 5000L) {

            public final void onFinish()
            {
                hideActionBar();
            }

            public final void onTick(long l)
            {
            }

        };
        mFilmStripPauseTimer = new CountDownTimer(30000L, 30000L) {

            public final void onFinish()
            {
                pauseFilmStrip();
            }

            public final void onTick(long l)
            {
            }

        };
        if(mActionBar != null)
            mActionBar.addOnMenuVisibilityListener(new android.app.ActionBar.OnMenuVisibilityListener() {

                public final void onMenuVisibilityChanged(boolean flag)
                {
                    if(flag)
                        mActionBarDismissalTimer.cancel();
                    else
                        mActionBarDismissalTimer.start();
                }

            });
        mFilmStripAnimOut.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            public final void onAnimationEnd(Animation animation)
            {
                if(mFilmStripContainer != null)
                {
                    mFilmStripContainer.setVisibility(8);
                    mFilmStripPauseTimer.start();
                }
            }

            public final void onAnimationRepeat(Animation animation)
            {
            }

            public final void onAnimationStart(Animation animation)
            {
            }

        });
        mSlideInUp.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            public final void onAnimationEnd(Animation animation)
            {
            }

            public final void onAnimationRepeat(Animation animation)
            {
            }

            public final void onAnimationStart(Animation animation)
            {
                resumeFilmStrip();
                if(mFilmStripContainer != null)
                    mFilmStripContainer.setVisibility(0);
            }

        });
        mInsetVideo.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                if(mViewMode == ViewMode.MODE_STAGE_VIEW)
                    HangoutTabletTile.access$1600(HangoutTabletTile.this);
            }

        });
        mCenterStageContainer.setOnTouchListener(new CenterStageTouchListener(getContext()));
        mCenterStageVideo.setOnDragListener(new android.view.View.OnDragListener() {

            private boolean onTarget(int i, int j)
            {
                int k = mCenterStageVideo.getWidth();
                int l = mCenterStageVideo.getHeight();
                int i1 = l / 4;
                boolean flag;
                if(i >= 0 && i + 0 <= k && j >= 0 && j + i1 <= l)
                    flag = true;
                else
                    flag = false;
                return flag;
            }

            public final boolean onDrag(View view, DragEvent dragevent)
            {
                float f = 1.0F;
                RemoteVideoView.ParticipantVideoView participantvideoview = null;
                Object obj = dragevent.getLocalState();
                boolean flag = obj instanceof RemoteVideoView.ParticipantVideoView;
                if(flag)
                    participantvideoview = (RemoteVideoView.ParticipantVideoView)obj;
                boolean flag1 = false;
                switch(dragevent.getAction()) {
                case 1:
                case 6:
                	if(participantvideoview == null)
                    {
                        flag1 = false;
                    } else
                    {
                        int j = getResources().getColor(R.color.hangout_drag_drop_off_target);
                        mCenterStageVideo.setBackgroundColor(j);
                        RemoteVideoView.CenterStageVideoView centerstagevideoview1 = mCenterStageVideo;
                        if(j != 0)
                            f = 0.85F;
                        centerstagevideoview1.setAlpha(f);
                        mCenterStageVideo.invalidate();
                        flag1 = true;
                    }
                	break;
                case 2:
                case 5:
                	int i;
                    RemoteVideoView.CenterStageVideoView centerstagevideoview;
                    if(onTarget((int)dragevent.getX(), (int)dragevent.getY()))
                        i = getResources().getColor(R.color.hangout_drag_drop_on_target);
                    else
                        i = getResources().getColor(R.color.hangout_drag_drop_off_target);
                    mCenterStageVideo.setBackgroundColor(i);
                    centerstagevideoview = mCenterStageVideo;
                    if(i != 0)
                        f = 0.85F;
                    centerstagevideoview.setAlpha(f);
                    mCenterStageVideo.invalidate();
                    flag1 = true;
                	break;
                case 3:
                	flag1 = onTarget((int)dragevent.getX(), (int)dragevent.getY());
                	break;
                case 4:
                	mCenterStageVideo.setBackgroundColor(0);
                    mCenterStageVideo.setAlpha(f);
                    mCenterStageVideo.invalidate();
                    if(dragevent.getResult())
                        mFilmStripView.requestPinVideo(participantvideoview);
                    flag1 = true;
                	break;
                default:
                	flag1 = false;
                	break;
                }
                
                return flag1;
            }

        });
        final Animation animOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        final Animation animIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        animOut.setDuration(500L);
        animIn.setDuration(500L);
        mCenterStageVideo.setVideoChangeListener(new RemoteVideoView.VideoChangeListener() {

            public final void onVideoSourceAboutToChange() {
            	if(mSkipFirst) {
            		mSkipFirst = false;
            		return;
            	}
            	
            	if(!mIsActive)
                {
                    mIsActive = true;
                    final ImageView imageview = mCenterStageVideo.getSnapshotView();
                    imageview.setImageBitmap(mCenterStageVideo.getBitmap());
                    imageview.setVisibility(0);
                    mCenterStageVideo.hideVideoSurface();
                    mCenterStageVideo.hidePinnedStatus();
                    mCenterStageVideo.hideAudioMutedStatus();
                    animOut.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

                        public final void onAnimationEnd(Animation animation)
                        {
                        	imageview.setVisibility(8);
                            mCenterStageVideo.startAnimation(animIn);
                            mCenterStageVideo.showVideoSurface();
                            mIsActive = false;
                            HangoutTabletTile.access$3000(HangoutTabletTile.this, mCenterStageVideo.getCurrentVideoSource());
                        }

                        public final void onAnimationRepeat(Animation animation)
                        {
                        }

                        public final void onAnimationStart(Animation animation)
                        {
                        }
                    });
                    imageview.startAnimation(animOut);
                }
            }

            private volatile boolean mIsActive;
            private boolean mSkipFirst = true;

        });
    }

    public final void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(R.menu.hangout_exit_menu, menu);
        menuinflater.inflate(R.menu.hangout_camera_switch_menu, menu);
        menuinflater.inflate(R.menu.hangout_audio_toggle_menu, menu);
        menuinflater.inflate(R.menu.hangout_video_toggle_menu, menu);
        menuinflater.inflate(R.menu.hangout_invite_people_menu, menu);
        menu.findItem(R.id.hangout_video_toggle_menu_item).setVisible(Cameras.isAnyCameraAvailable());
        MenuItem menuitem = menu.findItem(R.id.hangout_menu_switch_camera);
        boolean flag;
        if(Cameras.isFrontFacingCameraAvailable() && Cameras.isRearFacingCameraAvailable())
            flag = true;
        else
            flag = false;
        menuitem.setVisible(flag);
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag = true;
        int i = menuitem.getItemId();
        if(i == R.id.hangout_exit_menu_item)
        {
            hideActionBar();
            Log.debug((new StringBuilder("HangoutTabletTile onExit with state:")).append(state).toString());
            if(state != null)
                if(state.isInMeeting())
                {
                    Log.debug("Setting userRequestedMeetingExit to true");
                    GCommApp.getInstance(getContext()).exitMeeting();
                } else
                {
                    ((HangoutTile.HangoutTileActivity)getContext()).stopHangoutTile();
                    Log.debug("Did not set userRequestedMeetingExit");
                }
        } else
        if(i == R.id.hangout_menu_switch_camera)
        {
            GCommApp.sendEmptyMessage(getContext(), 201);
            hideActionBar();
        } else
        if(i == R.id.hangout_audio_toggle_menu_item)
        {
            GCommApp gcommapp = GCommApp.getInstance(getContext());
            boolean flag1;
            if(!gcommapp.isAudioMute())
                flag1 = flag;
            else
                flag1 = false;
            gcommapp.setAudioMute(flag1);
            hideActionBar();
        } else
        if(i == R.id.hangout_video_toggle_menu_item)
        {
            GCommApp.sendEmptyMessage(getContext(), 202);
            hideActionBar();
        } else
        if(i == R.id.hangout_invite_menu_item)
        {
            hideActionBar();
            inviteMoreParticipants();
        } else
        {
            flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    public final void onPause()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = eventHandler;
        Log.debug("HangoutTabletTile.onPause: this=%s context=%s eventHandler=%s", aobj);
        stateBeforeStop = state;
        state = null;
    }

    public final void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuitem = menu.findItem(R.id.hangout_video_toggle_menu_item);
        boolean flag;
        MenuItem menuitem1;
        MenuItem menuitem2;
        if(menuitem.isVisible())
        {
            mIsVideoMuted = GCommApp.getInstance(getContext()).isOutgoingVideoMute();
            int k;
            int l;
            if(mIsVideoMuted)
            {
                k = R.drawable.hangout_ic_menu_video_unmute;
                l = R.string.hangout_menu_video_unmute;
            } else
            {
                k = R.drawable.hangout_ic_menu_video_mute;
                l = R.string.hangout_menu_video_mute;
            }
            menuitem.setIcon(k);
            menuitem.setTitle(l);
        }
        menuitem1 = menu.findItem(R.id.hangout_audio_toggle_menu_item);
        if(menuitem1.isVisible())
        {
            mIsAudioMuted = GCommApp.getInstance(getContext()).isAudioMute();
            int i;
            int j;
            if(mIsAudioMuted)
            {
                i = R.drawable.hangout_ic_menu_audio_unmute;
                j = R.string.hangout_menu_audio_unmute;
            } else
            {
                i = R.drawable.hangout_ic_menu_audio_mute;
                j = R.string.hangout_menu_audio_mute;
            }
            menuitem1.setIcon(i);
            menuitem1.setTitle(j);
            menuitem1.setEnabled(mIsAudioEnabled);
        }
        menuitem2 = menu.findItem(R.id.hangout_invite_menu_item);
        if(!mIsHangoutLite)
            flag = true;
        else
            flag = false;
        menuitem2.setVisible(flag);
    }

    public final void onResume()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = eventHandler;
        Log.debug("HangoutTabletTile.onResume: this=%s context=%s eventHandler=%s", aobj);
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        HangoutTile.State state1;
        Object aobj[];
        if(state == null)
            state1 = stateBeforeStop;
        else
            state1 = state;
        aobj = new Object[4];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = eventHandler;
        aobj[3] = state1;
        Log.debug("HangoutTabletTile.onSaveInstanceState: this=%s context=%s eventHandler=%s stateToSave:%s", aobj);
        bundle.putInt("HangoutTile_state", state1.ordinal());
    }

    public final void onStart()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = eventHandler;
        Log.debug("HangoutTabletTile.onStart: this=%s context=%s eventHandler=%s", aobj);
        GCommApp.getInstance(getContext()).startingHangoutActivity(mActivity);
    }

    public final void onStop()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = eventHandler;
        Log.debug("HangoutTabletTile.onStop: this=%s context=%s eventHandler=%s", aobj);
        GCommApp.getInstance(getContext()).stoppingHangoutActivity();
    }

    public final void onTilePause()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = eventHandler;
        Log.debug("HangoutTabletTile.onTilePause: this=%s context=%s eventHandler=%s", aobj);
        if(mHangoutSupportStatus == Hangout.SupportStatus.SUPPORTED)
        {
            if(state != null && state.isInMeeting())
            {
                if(mViewMode == ViewMode.MODE_STAGE_VIEW)
                {
                    mCenterStageVideo.onPause();
                    mToastsView.onPause();
                    pauseFilmStrip();
                } else
                if(mViewMode != ViewMode.MODE_BRADY_VIEW);
            } else
            {
                mHandler.removeCallbacks(instructionsViewFadeOutRunnable);
            }
            if(mViewMode == ViewMode.MODE_STAGE_VIEW)
            {
                mLocalVideoView.onPause();
                mFilmStripView.dismissParticipantMenuDialog();
                mCurrentStageMode = StageViewMode.STAGE_MODE_INVALID;
            } else
            {
                ViewMode _tmp = mViewMode;
                ViewMode _tmp1 = ViewMode.MODE_BRADY_VIEW;
            }
            checkAndDismissCallgrokLogUploadProgressDialog();
            isTileStarted = false;
        }
    }

    public final void onTileResume()
    {
        Object aobj[] = new Object[4];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = eventHandler;
        aobj[3] = hangoutInfo;
        Log.debug("HangoutTabletTile.onTileResume: this=%s context=%s eventHandler=%s hangoutInfo=%s", aobj);
        if(!$assertionsDisabled && mAccount == null)
            throw new AssertionError();
        showActionBar();
        mHangoutSupportStatus = Hangout.getSupportedStatus(getContext(), mAccount);
        
        if(Hangout.SupportStatus.SUPPORTED != mHangoutSupportStatus) {
        	showError(mHangoutSupportStatus.getErrorMessage(getContext()), true);
        	return;
        }
        
        isTileStarted = true;
        setState(HangoutTile.State.START);
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
                setState(HangoutTile.State.IN_MEETING);
            else
            if(stateBeforeStop != null && stateBeforeStop.isInMeeting())
            {
                stateBeforeStop = null;
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
                eventHandler.onSignedIn(GCommApp.getInstance(getContext()).getGCommNativeWrapper().getUserJid());
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
        aobj[2] = eventHandler;
        Log.debug("HangoutTabletTile.onTileStart: this=%s context=%s eventHandler=%s", aobj);
        if(mAccount == null)
        {
            throw new RuntimeException("setHangoutInfo must be called before switching to HangoutTabletTile");
        } else
        {
            GCommApp.getInstance(getContext()).registerForEvents(getContext(), eventHandler, false);
            isRegistered = true;
            return;
        }
    }

    public final void onTileStop()
    {
        Object aobj[] = new Object[3];
        aobj[0] = this;
        aobj[1] = getContext();
        aobj[2] = eventHandler;
        Log.debug("HangoutTabletTile.onTileStop: this=%s context=%s eventHandler=%s", aobj);
        mActionBarDismissalTimer.cancel();
        isRegistered = false;
        GCommApp.getInstance(getContext()).unregisterForEvents(getContext(), eventHandler, false);
    }

    public void setParticipants(HashMap hashmap, HashSet hashset)
    {
    }

    public final void showChild(View view)
    {
        if(view == mFilmStripView)
            showFilmStrip();
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
        if(mViewMode == ViewMode.MODE_STAGE_VIEW && state != null && state.isInMeeting())
            mCenterStageVideo.updateVideoStreaming();
    }
	
    static void access$1600(HangoutTabletTile hangouttablettile)
    {
        if(hangouttablettile.mActionBar != null)
            if(hangouttablettile.mActionBar.isShowing())
                hangouttablettile.hideActionBar();
            else
                hangouttablettile.showActionBar();
        return;
    }
    
    static void access$1700(HangoutTabletTile hangouttablettile, boolean flag)
    {
    	// TODO
    }
    
    static void access$3000(HangoutTabletTile hangouttablettile, MeetingMember meetingmember)
    {
        if(hangouttablettile.mViewMode == ViewMode.MODE_STAGE_VIEW && hangouttablettile.mEnableStageIcons)
        {
            if(GCommApp.getInstance(hangouttablettile.getContext()).getSelectedVideoSource() == null)
                hangouttablettile.mCenterStageVideo.hidePinnedStatus();
            else
                hangouttablettile.mCenterStageVideo.showPinnedStatus();
            if(hangouttablettile.mFilmStripView.isAudioMuted(meetingmember))
                hangouttablettile.mCenterStageVideo.showAudioMutedStatus();
            else
                hangouttablettile.mCenterStageVideo.hideAudioMutedStatus();
        }
        return;
    }
    
    static void access$100(HangoutTabletTile hangouttablettile, MeetingMember meetingmember)
    {
        if(hangouttablettile.mViewMode == ViewMode.MODE_STAGE_VIEW)
        {
            if(hangouttablettile.mFilmStripIsPaused)
            {
                GCommApp gcommapp = GCommApp.getInstance(hangouttablettile.getContext());
                if(gcommapp.getSelectedVideoSource() == meetingmember)
                {
                    gcommapp.setSelectedVideoSource(null);
                    hangouttablettile.updateMainVideoStreaming();
                }
            }
        } else
        {
            ViewMode _tmp = hangouttablettile.mViewMode;
            ViewMode _tmp1 = ViewMode.MODE_BRADY_VIEW;
        }
        return;
    }
    
    static void access$1400(HangoutTabletTile hangouttablettile, MeetingMember meetingmember, boolean flag)
    {
        if(meetingmember != null && !meetingmember.isSelf()) {
        	if(hangouttablettile.mViewMode == ViewMode.MODE_STAGE_VIEW)
            {
                if(hangouttablettile.mEnableStageIcons && hangouttablettile.mCenterStageVideo.getCurrentVideoSource() == meetingmember)
                    if(flag)
                        hangouttablettile.mCenterStageVideo.showAudioMutedStatus();
                    else
                        hangouttablettile.mCenterStageVideo.hideAudioMutedStatus();
            } else
            {
                hangouttablettile.mViewMode = ViewMode.MODE_BRADY_VIEW;
            }
        }
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private enum ViewMode {
		MODE_STAGE_VIEW,
		MODE_BRADY_VIEW;
	}
	
	private static enum StageViewMode {
		STAGE_MODE_INVALID,
		STAGE_MODE_LOCAL_ONLY,
		STAGE_MODE_LOCAL_AND_REMOTE;
	}
	
	private class AlertDialogListener implements AlertFragmentDialog.AlertDialogListener {

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
	
	private final class CenterStageTouchListener extends TwoPointerGestureDetector {

		CenterStageTouchListener(Context context) {
            super(context);
        }
		
        public final boolean onFling(MotionEvent motionevent, MotionEvent motionevent1, float f, float f1) {
            int i = getHeight() / 2;
            int j = (int)motionevent.getY();
            if(f1 <= 0.0F || f1 <= Math.abs(f)) {
            	if(f1 < 0.0F && f1 < -Math.abs(f))
                {
                    if(j >= i)
                    {
                        showChild(mFilmStripView);
                        return true;
                    }
                    if(j <= i)
                    {
                        hideActionBar();
                        return true;
                    }
                }
                return super.onFling(motionevent, motionevent1, f, f1); 
            } else { 
            	if(j < i) {
            		if(j <= i)
                    {
                        showActionBar();
                        return true;
                    } 
            	} else { 
            		hideChild(mFilmStripView);
            		return true;
            	}
            	return false;
            }
        }

        public final boolean onSingleTapConfirmed(MotionEvent motionevent)
        {
            HangoutTabletTile.access$1600(HangoutTabletTile.this);
            return true;
        }

        public final boolean onTwoPointerSwipe(android.view.MotionEvent.PointerCoords pointercoords, android.view.MotionEvent.PointerCoords pointercoords1, float f, float f1)
        {
            if(Math.abs(pointercoords.y - pointercoords1.y) >= 250F) {
            	return super.onTwoPointerSwipe(pointercoords, pointercoords1, f, f1);
            } else { 
                int i = getWidth();
                int j = (int)pointercoords.x;
                int k = (int)pointercoords1.x;
                if(j >= k) {
                	if(j > i / 2 && k < i / 8)
                    {
                        HangoutTabletTile.access$1700(HangoutTabletTile.this, false);
                        return true;
                    }
                	return false;
                } else { 
                	if(j >= i / 2 || k <= (i * 7) / 8) {
                		return super.onTwoPointerSwipe(pointercoords, pointercoords1, f, f1); 
                	} else { 
                		HangoutTabletTile.access$1700(HangoutTabletTile.this, true);
                		return true;
                	}
                }
            }
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
            super.onAudioMuteStateChanged(meetingmember, flag);
            if(meetingmember == null || meetingmember.isSelf())
                updateAudioMuteMenuButtonState(Boolean.valueOf(flag));
            else
                HangoutTabletTile.access$1400(HangoutTabletTile.this, meetingmember, flag);
        }

        public final void onCallgrokLogUploadCompleted()
        {
            checkAndDismissCallgrokLogUploadProgressDialog();
            getHangoutTileActivity().stopHangoutTile();
        }

        public final void onError(GCommNativeWrapper.Error error)
        {
            super.onError(error);
            Log.info("HangoutTabletTile$EventHandler.onError(%s) %s", new Object[] {
                error, this
            });
            if(error == GCommNativeWrapper.Error.AUTHENTICATION)
            {
                if(!$assertionsDisabled && !state.isSigningIn())
                    throw new AssertionError(state);
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
            Log.debug((new StringBuilder("HangoutTabletTile.onHangoutCreated: ")).append(info).toString());
            updateHangoutViews();
            GCommApp.getInstance(getContext()).enterHangout(info, true, greenRoomParticipants, mHoaConsented);
        }

        public final void onHangoutWaitTimeout(Hangout.Info info)
        {
            super.onHangoutWaitTimeout(info);
            mMessageView.setText(getResources().getString(R.string.hangout_no_one_joined));
        }

        public final void onMeetingEnterError(GCommNativeWrapper.MeetingEnterError meetingentererror)
        {
            super.onMeetingEnterError(meetingentererror);
            // TODO
        }

        public final void onMeetingExited(boolean flag)
        {
            Object aobj[] = new Object[3];
            aobj[0] = this;
            aobj[1] = HangoutTabletTile.this;
            aobj[2] = Boolean.valueOf(flag);
            Log.debug("HangoutTabletTile$EventHandler.onMeetingExited: this=%s, tile=%s clientInitiated=%s", aobj);
            onMeetingExited(flag);
            if(isRegistered && state != null)
            {
                if(flag)
                {
                    if(StringUtils.getDomain(mAccount.getName()).equals("google.com"))
                    {
                        getGCommNativeWrapper().uploadCallgrokLog();
                        ProgressFragmentDialog.newInstance(getResources().getString(R.string.hangout_log_upload_title), getResources().getString(R.string.hangout_log_upload_message)).show(mActivity.getSupportFragmentManager(), "log_upload");
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
            onMeetingMediaStarted();
            if(isRegistered)
            {
                setState(HangoutTile.State.IN_MEETING);
                updateHangoutViews();
                updateAudioMuteMenuButtonState(null);
                showFilmStrip();
                GCommApp.getInstance(getContext()).getGCommService().showHangoutNotification(getHangoutTileActivity().getHangoutNotificationIntent());
                getHangoutTileActivity().onMeetingMediaStarted();
            }
        }

        public final void onMeetingMemberEntered(MeetingMember meetingmember)
        {
            super.onMeetingMemberEntered(meetingmember);
            updateHangoutViews();
            notifyListeners();
        }

        public final void onMeetingMemberExited(MeetingMember meetingmember)
        {
            super.onMeetingMemberExited(meetingmember);
            updateHangoutViews();
            notifyListeners();
            HangoutTabletTile.access$100(HangoutTabletTile.this, meetingmember);
        }

        public final void onMeetingMemberPresenceConnectionStatusChanged(MeetingMember meetingmember)
        {
            super.onMeetingMemberPresenceConnectionStatusChanged(meetingmember);
            updateHangoutViews();
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
            else
                HangoutTabletTile.access$1400(HangoutTabletTile.this, meetingmember, true);
        }

        public final void onSignedIn(String s)
        {
            boolean flag = true;
            super.onSignedIn(s);
            Object aobj[] = new Object[2];
            aobj[0] = this;
            aobj[1] = HangoutTabletTile.this;
            Log.debug("HangoutTabletTile$EventHandler.onSignedIn: this=%s, tile=%s", aobj);
            if(!$assertionsDisabled && !state.isSigningIn())
                throw new AssertionError(state);
            if(isRegistered)
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
            Log.info("HangoutTabletTile$EventHandler.onSignedOut");
            if(isRegistered)
            {
                showError(R.string.hangout_signin_timeout_error, true);
                setState(HangoutTile.State.SIGNIN_ERROR);
            }
        }

        public final void onSigninTimeOutError()
        {
            super.onSigninTimeOutError();
            Log.info((new StringBuilder("HangoutTabletTile$EventHandler.onSigninTimeOutError: this=")).append(this).toString());
            if(isRegistered)
            {
                showError(R.string.hangout_signin_timeout_error, true);
                setState(HangoutTile.State.SIGNIN_ERROR);
            }
        }

        public final void onVideoMuteChanged(boolean flag)
        {
            updateVideoMuteMenuButtonState(Boolean.valueOf(flag));
        }

        public final void onVolumeChanged(MeetingMember meetingmember, int i)
        {
            if(meetingmember == null || meetingmember.isSelf())
                updateAudioMuteMenuButtonState(Boolean.valueOf(false));
            else
                HangoutTabletTile.access$1400(HangoutTabletTile.this, meetingmember, false);
        }

    }

	
}
