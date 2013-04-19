/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.ui.fragments.CircleNameResolver;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.view.AvatarView;

/**
 * 
 * @author sihai
 *
 */
public class HangoutRingingActivity extends EsFragmentActivity {

	private static final String INVITER_PROJECTION[] = {
        "packed_circle_ids"
    };
    private static boolean isCurrentlyRinging = false;
    private static Ringtone mRingtone;
    private static HangoutRingingActivity sRingingActivity = null;
    private EsAccount mAccount;
    private MultiWaveView mAnswerWidget;
    private final Runnable mAnswerWidgetPingRunnable = new Runnable() {

        public final void run()
        {
            if(mAnswerWidget != null)
                mAnswerWidget.ping();
            if(mHandler != null && !mHasActed)
                mHandler.postDelayed(this, 2000L);
        }
    };

	private Runnable mCallTimeoutRunnable;
    private CircleNameResolver mCircleNameResolver;
    volatile boolean mContinueVibrating;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Hangout.Info mHangoutInfo;
    private final HangoutRingingActivityEventHandler mHangoutRingingEventHandler = new HangoutRingingActivityEventHandler();
    private boolean mHasActed;
    private String mInviteId;
    private AvatarView mInviterAvatar;
    private TextView mInviterCircleNamesTextView;
    private String mInviterId;
    private String mInviterName;
    private boolean mIsHangoutLite;
    private NotificationManager mNotificationManager;
    private String mPackedCircleIds;
    private RingStatus mPendingFinishStatus;
    private final PersonLoaderCallbacks mPersonLoaderCallbacks = new PersonLoaderCallbacks();
    private PhoneStateChangeListener mPhoneStateChangeListener;
    private float mSelfVideoVerticalGravity;
    private SelfVideoView mSelfVideoView;
    private FrameLayout mSelfVideoViewContainer;
    Vibrator mVibrator;
    VibratorThread mVibratorThread;
    private ImageButton toggleAudioMuteMenuButton;
    private ImageButton toggleVideoMuteMenuButton;
    
    
    public HangoutRingingActivity()
    {
        mHasActed = false;
        mPendingFinishStatus = null;
        mSelfVideoVerticalGravity = -0.35F;
        mCallTimeoutRunnable = null;
    }

    private static String buildNotificationTag(Context context, EsAccount esaccount)
    {
        return (new StringBuilder()).append(context.getPackageName()).append(":notifications:").append(esaccount.getName()).toString();
    }

    private void createMissedHangoutNotification()
    {
        String s = getResources().getString(R.string.hangout_missed_notification_title);
        Object aobj[] = new Object[1];
        aobj[0] = mInviterName;
        String s1 = String.format(s, aobj);
        PersonData persondata = new PersonData(EsPeopleData.extractGaiaId(mInviterId), mInviterName, null);
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, Intents.getMissedHangoutCallbackIntent(this, mAccount, mHangoutInfo, new AudienceData(persondata)), 0x8000000);
        Notification notification = new Notification(R.drawable.ic_stat_gplus, s1, System.currentTimeMillis());
        notification.flags = 0x10 | notification.flags;
        notification.setLatestEventInfo(this, s1, getResources().getString(R.string.hangout_missed_notification_content), pendingintent);
        ((NotificationManager)getSystemService("notification")).notify(buildNotificationTag(this, mAccount), 2, notification);
    }

    public static void deactivateAccount(Context context, EsAccount esaccount)
    {
        HangoutRingingActivity hangoutringingactivity = sRingingActivity;
        if(hangoutringingactivity != null)
            hangoutringingactivity.exit(RingStatus.IGNORED);
        String s = buildNotificationTag(context, esaccount);
        NotificationManager notificationmanager = (NotificationManager)context.getSystemService("notification");
        notificationmanager.cancel(s, 2);
        notificationmanager.cancel(s, 3);
    }

    private void exit(RingStatus ringstatus)
    {
        sRingingActivity = null;
        isCurrentlyRinging = false;
        if(mCallTimeoutRunnable != null)
        {
            mHandler.removeCallbacks(mCallTimeoutRunnable);
            mCallTimeoutRunnable = null;
        }
        stopRingTone();
        GCommApp.getInstance(this).unregisterForEvents(this, mHangoutRingingEventHandler, false);
        if(mPhoneStateChangeListener != null)
            GCommApp.getInstance(this).getApp().unregisterReceiver(mPhoneStateChangeListener);
        if(ringstatus != RingStatus.ACCEPTED)
            GCommApp.getInstance(this).disconnect();
        removeStatusBarNotification();
        finish();
    }

    public static void onC2DMReceive(Context context, EsAccount esaccount, Intent intent)
    {
        // TODO
    }

    private void removeStatusBarNotification()
    {
        mNotificationManager.cancel(buildNotificationTag(this, mAccount), 3);
    }

    private void sendHangoutRingStatus(RingStatus ringstatus)
    {
        Log.debug((new StringBuilder("Sending hangout finish request. Status: ")).append(ringstatus).toString());
        GCommApp.getInstance(this).getGCommNativeWrapper().sendRingStatus(mInviteId, mHangoutInfo.getId(), ringstatus.toString());
    }

    public static void stopRingActivity()
    {
        HangoutRingingActivity hangoutringingactivity = sRingingActivity;
        if(hangoutringingactivity != null)
        {
            hangoutringingactivity.exit(RingStatus.IGNORED);
            hangoutringingactivity.createMissedHangoutNotification();
        }
    }

    private void stopRingTone()
    {
        if(mRingtone != null)
        {
            mRingtone.stop();
            mRingtone = null;
        }
        if(mVibratorThread != null)
        {
            mContinueVibrating = false;
            mVibratorThread = null;
        }
        mVibrator.cancel();
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return null;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Intent intent;
        Resources resources;
        String s;
        boolean flag;
        Resources resources1;
        String s1;
        String s2;
        boolean flag1;
        float f;
        int i;
        if((0xf & getResources().getConfiguration().screenLayout) == 4)
        {
            if(getResources().getConfiguration().orientation == 1)
                flag1 = true;
            else
                flag1 = false;
            if(flag1)
                f = -0.19F;
            else
                f = -0.1F;
            mSelfVideoVerticalGravity = f;
            if(flag1)
                i = 1;
            else
                i = 0;
            setRequestedOrientation(i);
        } else
        {
            setRequestedOrientation(1);
        }
        setContentView(R.layout.hangout_ringing_activity);
        getWindow().addFlags(0x680080);
        intent = getIntent();
        mAccount = (EsAccount)intent.getParcelableExtra("account");
        mHangoutInfo = (Hangout.Info)intent.getSerializableExtra("hangout_info");
        mInviteId = intent.getStringExtra("hangout_invite_id");
        mInviterId = intent.getStringExtra("hangout_inviter_id");
        mInviterName = intent.getStringExtra("hangout_inviter_name");
        if(TextUtils.isEmpty(mInviterName))
            mInviterName = getResources().getString(R.string.hangout_anonymous_person);
        mIsHangoutLite = intent.getBooleanExtra("hangout_is_lite", false);
        ((TextView)findViewById(R.id.inviter_name)).setText(mInviterName.toUpperCase());
        mInviterCircleNamesTextView = (TextView)findViewById(R.id.circle_names);
        mInviterAvatar = (AvatarView)findViewById(R.id.inviter_avatar);
        if(mIsHangoutLite)
        {
            mInviterAvatar.setVisibility(8);
        } else
        {
            mInviterAvatar.setVisibility(0);
            mInviterAvatar.setGaiaId(EsPeopleData.extractGaiaId(mInviterId));
        }
        mSelfVideoViewContainer = (FrameLayout)findViewById(R.id.self_video_container);
        mCircleNameResolver = new CircleNameResolver(this, getSupportLoaderManager(), mAccount);
        mCircleNameResolver.initLoader();
        mCircleNameResolver.registerObserver(new DataSetObserver() {

            public final void onChanged()
            {
                HangoutRingingActivity.access$1000(HangoutRingingActivity.this);
            }

        });
        mAnswerWidget = (MultiWaveView)findViewById(R.id.incomingCallWidget);
        mAnswerWidget.setOnTriggerListener(new MultiWaveView.OnTriggerListener() {

            public final void onTrigger(int j)
            {
            	if(mHasActed) {
            		return;
            	}
            	
            	switch(j)
                {
                case 1: // '\001'
                default:
                    Log.debug((new StringBuilder("Unexpected trigger for MultiwaveView widget value: ")).append(j).toString());
                    break;

                case 0: // '\0'
                    HangoutRingingActivity.access$1100(HangoutRingingActivity.this);
                    break;

                case 2: // '\002'
                    HangoutRingingActivity.access$1200(HangoutRingingActivity.this);
                    break;
                }
            }

        });
        mAnswerWidget.clearAnimation();
        mAnswerWidget.setTargetResources(R.array.incoming_hangout_widget_2way_targets);
        mAnswerWidget.setTargetDescriptionsResourceId(R.array.incoming_hangout_widget_2way_target_descriptions);
        mAnswerWidget.setDirectionDescriptionsResourceId(R.array.incoming_hangout_widget_2way_direction_descriptions);
        mAnswerWidget.reset(false);
        mHandler.postDelayed(mAnswerWidgetPingRunnable, 1000L);
        if(!mIsHangoutLite)
            getSupportLoaderManager().initLoader(0, null, mPersonLoaderCallbacks);
        mVibrator = (Vibrator)getSystemService("vibrator");
        mNotificationManager = (NotificationManager)getSystemService("notification");
        if(mCallTimeoutRunnable == null) {
        	mCallTimeoutRunnable = new Runnable() {

                public final void run()
                {
                    exit(RingStatus.TIMED_OUT);
                    createMissedHangoutNotification();
                }

            };
            mHandler.postDelayed(mCallTimeoutRunnable, 30000L);
            Uri uri;
            if(mRingtone == null)
            {
                resources1 = getResources();
                s1 = resources1.getString(R.string.hangout_ringtone_setting_key);
                s2 = resources1.getString(R.string.hangout_ringtone_setting_default_value);
                uri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(this).getString(s1, s2));
                mRingtone = RingtoneManager.getRingtone(this, uri);
            } else
            {
                uri = null;
            }
            if(mRingtone != null) {
            	if(!mRingtone.isPlaying()) {
            		mRingtone.setStreamType(2);
                    mRingtone.play();
                    resources = getResources();
                    s = resources.getString(R.string.hangout_vibrate_setting_key);
                    flag = resources.getBoolean(R.bool.hangout_vibrate_setting_default_value);
                    if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(s, flag) && mVibratorThread == null)
                    {
                        mContinueVibrating = true;
                        mVibratorThread = new VibratorThread();
                        mVibratorThread.start();
                    }
            	}
            	
            } else { 
            	Log.error((new StringBuilder("Cannot get a ringtone for ")).append(uri).toString());
            	resources = getResources();
                s = resources.getString(R.string.hangout_vibrate_setting_key);
                flag = resources.getBoolean(R.bool.hangout_vibrate_setting_default_value);
                if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(s, flag) && mVibratorThread == null)
                {
                    mContinueVibrating = true;
                    mVibratorThread = new VibratorThread();
                    mVibratorThread.start();
                }
            }
            
            (new IntentFilter("com.google.android.c2dm.intent.RECEIVE")).addCategory("com.google.android.apps.hangout.NOTIFICATION");
            GCommApp.getInstance(this).registerForEvents(this, mHangoutRingingEventHandler, false);
            sRingingActivity = this;
            mPhoneStateChangeListener = new PhoneStateChangeListener();
            GCommApp.getInstance(this).getApp().registerReceiver(mPhoneStateChangeListener, new IntentFilter("android.intent.action.PHONE_STATE"));
        }
        
        toggleAudioMuteMenuButton = (ImageButton)findViewById(R.id.hangout_menu_toggle_audio_mute);
        toggleAudioMuteMenuButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                GCommApp gcommapp = GCommApp.getInstance(HangoutRingingActivity.this);
                boolean flag2;
                if(!GCommApp.getInstance(HangoutRingingActivity.this).isAudioMute())
                    flag2 = true;
                else
                    flag2 = false;
                gcommapp.setAudioMute(flag2);
            }

        });
        toggleVideoMuteMenuButton = (ImageButton)findViewById(R.id.hangout_menu_toggle_video_mute);
        toggleVideoMuteMenuButton.setOnClickListener(new android.view.View.OnClickListener() {

            public final void onClick(View view)
            {
                GCommApp.sendEmptyMessage(HangoutRingingActivity.this, 202);
            }
        });
    }

    protected void onPause()
    {
        super.onPause();
        if(sRingingActivity != null)
        {
            Notification notification = new Notification(R.drawable.ic_stat_gplus, getString(R.string.hangout_ringing_incoming), System.currentTimeMillis());
            Context context = getApplicationContext();
            String s = getString(R.string.hangout_ringing_incoming);
            String s1 = mInviterName;
            EsAccount esaccount = mAccount;
            String _tmp = mInviteId;
            PendingIntent pendingintent = PendingIntent.getActivity(this, 0, Intents.getHangoutRingingActivityIntent(this, esaccount, mInviterId, mInviterName, mHangoutInfo, mIsHangoutLite), 0);
            notification.flags = 16;
            notification.setLatestEventInfo(context, s, s1, pendingintent);
            mNotificationManager.notify(buildNotificationTag(this, mAccount), 3, notification);
        }
        mSelfVideoView.onPause();
    }

    protected void onResume()
    {
        super.onResume();
        removeStatusBarNotification();
        mSelfVideoView.onResume();
    }

    protected void onStart()
    {
        super.onStart();
        mSelfVideoView = new SelfVideoView(this, null);
        android.widget.FrameLayout.LayoutParams layoutparams = new android.widget.FrameLayout.LayoutParams(-1, -1);
        mSelfVideoView.setLayoutParams(layoutparams);
        mSelfVideoView.turnOffFlashLightSupport();
        mSelfVideoView.setLayoutMode(SelfVideoView.LayoutMode.FIT);
        mSelfVideoView.setVerticalGravity(mSelfVideoVerticalGravity);
        mSelfVideoViewContainer.addView(mSelfVideoView);
    }
    
    
    
    static void access$1000(HangoutRingingActivity hangoutringingactivity)
    {
        if(hangoutringingactivity.mPackedCircleIds != null && hangoutringingactivity.mCircleNameResolver.isLoaded() && hangoutringingactivity.mInviterCircleNamesTextView != null)
        {
            hangoutringingactivity.mInviterCircleNamesTextView.setText(hangoutringingactivity.mCircleNameResolver.getCircleNamesForPackedIds(hangoutringingactivity.mPackedCircleIds));
            hangoutringingactivity.mInviterCircleNamesTextView.setVisibility(0);
        }
        return;
    }
    
    static void access$1100(HangoutRingingActivity hangoutringingactivity)
    {
        if(!hangoutringingactivity.mHasActed)
        {
            hangoutringingactivity.mHasActed = true;
            Log.debug("Accepted invitation");
            if(GCommApp.getInstance(hangoutringingactivity).getGCommNativeWrapper().getCurrentState() == GCommNativeWrapper.GCommAppState.SIGNED_IN)
            {
                hangoutringingactivity.sendHangoutRingStatus(RingStatus.ACCEPTED);
                hangoutringingactivity.exit(RingStatus.ACCEPTED);
            } else
            {
                hangoutringingactivity.mPendingFinishStatus = RingStatus.ACCEPTED;
                Log.debug("Not yet signed in. Will send finish once signed in.");
                hangoutringingactivity.stopRingTone();
            }
            hangoutringingactivity.startActivity(Intents.getHangoutActivityIntent(hangoutringingactivity, hangoutringingactivity.mAccount, hangoutringingactivity.mHangoutInfo, true, null));
        }
        return;
    }
    
    static void access$1200(HangoutRingingActivity hangoutringingactivity)
    {
        if(!hangoutringingactivity.mHasActed)
        {
            hangoutringingactivity.mHasActed = true;
            Log.debug("Rejected invitation");
            if(GCommApp.getInstance(hangoutringingactivity).getGCommNativeWrapper().getCurrentState() == GCommNativeWrapper.GCommAppState.SIGNED_IN)
            {
                hangoutringingactivity.sendHangoutRingStatus(RingStatus.IGNORED);
                hangoutringingactivity.exit(RingStatus.IGNORED);
            } else
            {
                hangoutringingactivity.mPendingFinishStatus = RingStatus.IGNORED;
                Log.debug("Not yet signed in. Will send finish once signed in.");
                hangoutringingactivity.stopRingTone();
            }
        }
        return;
    }
    
    static String access$1902(HangoutRingingActivity hangoutringingactivity, String s)
    {
        hangoutringingactivity.mPackedCircleIds = s;
        return s;
    }
    
    static RingStatus access$302(HangoutRingingActivity hangoutringingactivity, RingStatus ringstatus)
    {
        hangoutringingactivity.mPendingFinishStatus = null;
        return null;
    }
    
    //=============================================================================
    //
    //=============================================================================
    private static enum RingStatus {
    	ACCEPTED,
    	IGNORED,
    	TIMED_OUT;
    }
    
    private final class HangoutRingingActivityEventHandler extends GCommEventHandler
    {

        public final void onAudioMuteStateChanged(MeetingMember meetingmember, boolean flag)
        {
            if(meetingmember == null || meetingmember.isSelf())
                if(flag)
                {
                    toggleAudioMuteMenuButton.setImageResource(R.drawable.hangout_ic_menu_audio_unmute);
                    toggleAudioMuteMenuButton.setContentDescription(getResources().getString(R.string.hangout_menu_audio_unmute));
                } else
                {
                    toggleAudioMuteMenuButton.setImageResource(R.drawable.hangout_ic_menu_audio_mute);
                    toggleAudioMuteMenuButton.setContentDescription(getResources().getString(R.string.hangout_menu_audio_mute));
                }
        }

        public final void onSignedIn(String s)
        {
            super.onSignedIn(s);
            if(mHasActed && mPendingFinishStatus != null)
            {
                sendHangoutRingStatus(mPendingFinishStatus);
                RingStatus ringstatus = mPendingFinishStatus;
                mPendingFinishStatus = null;
                exit(ringstatus);
            }
            Log.debug((new StringBuilder("Signed in! User jid = ")).append(s).toString());
        }

        public final void onVideoMuteChanged(boolean flag)
        {
            if(flag)
            {
                toggleVideoMuteMenuButton.setImageResource(R.drawable.hangout_ic_menu_video_unmute);
                toggleVideoMuteMenuButton.setContentDescription(getResources().getString(R.string.hangout_menu_video_unmute));
            } else
            {
                toggleVideoMuteMenuButton.setImageResource(R.drawable.hangout_ic_menu_video_mute);
                toggleVideoMuteMenuButton.setContentDescription(getResources().getString(R.string.hangout_menu_video_mute));
            }
        }
    }
    
	private final class PersonLoaderCallbacks implements android.support.v4.app.LoaderManager.LoaderCallbacks {

		public final Loader onCreateLoader(int i, Bundle bundle) {
			Object obj;
			if (mAccount == null || mInviterId == null) {
				obj = null;
			} else {
				final HangoutRingingActivity hangoutringingactivity = HangoutRingingActivity.this;
				Uri uri = EsProvider.appendAccountParameter(EsProvider.CONTACTS_URI, mAccount);
				String as[] = HangoutRingingActivity.INVITER_PROJECTION;
				String as1[] = new String[1];
				as1[0] = mInviterId;
				obj = new EsCursorLoader(hangoutringingactivity, uri, as, "person_id=?", as1, null) {

					public final Cursor esLoadInBackground() {
						EsPeopleData.ensurePeopleSynced(hangoutringingactivity, mAccount);
						return super.esLoadInBackground();
					}
				};
			}
			return ((Loader) (obj));
		}

		public final void onLoadFinished(Loader loader, Object obj) {
			Cursor cursor = (Cursor) obj;
			if (cursor != null && cursor.moveToFirst()) {
				mPackedCircleIds = cursor.getString(0);
				HangoutRingingActivity.access$1000(HangoutRingingActivity.this);
			}
		}

		public final void onLoaderReset(Loader loader) {
		}
	}

	private final class PhoneStateChangeListener extends BroadcastReceiver {

		public final void onReceive(Context context, Intent intent) {
			String s = intent.getStringExtra("state");
			if (TelephonyManager.EXTRA_STATE_RINGING.equals(s)) {
				Log.debug("Received incoming phone call. Stopping hangout ring...");
				HangoutRingingActivity.stopRingActivity();
			}
		}

	}

	private final class VibratorThread extends Thread {

		public final void run() {
			while (mContinueVibrating) {
				mVibrator.vibrate(1000L);
				SystemClock.sleep(2000L);
			}
		}

	}
}
