/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.ParticipantHelper;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ShakeDetector;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsConversationsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.hangout.GCommApp;
import com.galaxy.meetup.client.android.hangout.HangoutPhoneTile;
import com.galaxy.meetup.client.android.hangout.HangoutTabletTile;
import com.galaxy.meetup.client.android.hangout.HangoutTile;
import com.galaxy.meetup.client.android.hangout.HangoutTile.HangoutTileActivity;
import com.galaxy.meetup.client.android.hangout.Log;
import com.galaxy.meetup.client.android.realtimechat.Client;
import com.galaxy.meetup.client.android.realtimechat.CreateConversationOperation;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.realtimechat.ParticipantUtils;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceListener;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceResult;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.ui.fragments.ComposeMessageFragment;
import com.galaxy.meetup.client.android.ui.fragments.ConversationRenameDialog;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.MessageListFragment;
import com.galaxy.meetup.client.android.ui.fragments.ParticipantsGalleryFragment;
import com.galaxy.meetup.client.android.ui.view.ConversationTile;
import com.galaxy.meetup.client.android.ui.view.ParticipantsGalleryView;
import com.galaxy.meetup.client.android.ui.view.Tile;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.ImageUtils.InsertCameraPhotoDialogDisplayer;

/**
 * 
 * @author sihai
 *
 */
public class ConversationActivity extends EsFragmentActivity implements
		LoaderCallbacks, OnClickListener, HangoutTileActivity,
		MessageListFragment.LeaveConversationListener,
		InsertCameraPhotoDialogDisplayer {

	static final boolean $assertionsDisabled;
    private static int sInstanceCount;
    private final Tile.ParticipantPresenceListener conversationParticipantPresenceListener = new ConversationParticipantPresenceListener();
    private EsAccount mAccount;
    private boolean mAdvancedHangoutsEnabled;
    private boolean mCheckExtraTile;
    private ComposeMessageFragment mComposeMessageFragment;
    private ParticipantsGalleryFragment mConversationHeader;
    private String mConversationId;
    private String mConversationName;
    private Long mConversationRowId;
    private ConversationTile mConversationTile;
    private int mCreateConversationRequestId;
    private Tile mCurrentTile;
    private long mEarliestEventTimestamp;
    private long mFirstEventTimestamp;
    private int mFirstHangoutMenuItemIndex;
    private HangoutTile mHangoutTile;
    private boolean mIsConversationLoaded;
    private boolean mIsGroup;
    private boolean mIsMuted;
    private int mLastHangoutMenuItemIndex;
    private MessageListFragment mMessageListFragment;
    private boolean mNeedToInviteParticipants;
    private int mParticipantCount;
    private HashMap mParticipantList;
    private final RTCServiceListener mRealTimeChatListener = new RTCServiceListener();
    private AudienceData mResultAudience;
    private View mRootView;
    private boolean mShakeDetectorWasRunning;
    private Data.Participant mSingleParticipant;
    private LinearLayout mTileContainer;
    private MenuItem mTileSelectorMenuItem;

    static 
    {
        boolean flag;
        if(!ConversationActivity.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
	
	public ConversationActivity()
    {
    }

    public static boolean hasInstance()
    {
        boolean flag;
        if(sInstanceCount > 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void initHangoutTile() {
    	
    	try {
    		Hangout.Info info;
	    	if(null == mHangoutTile) {
	    		Hangout.SupportStatus supportstatus = Hangout.getSupportedStatus(this, mAccount);
	            if(mConversationId.startsWith("c:") || supportstatus != Hangout.SupportStatus.SUPPORTED) 
	            	return; 
	            else {
	            	GCommApp.getInstance(this).getGCommNativeWrapper().getCurrentState();
	                android.widget.LinearLayout.LayoutParams layoutparams;
	                if(mAdvancedHangoutsEnabled)
	                    mHangoutTile = new HangoutTabletTile(this);
	                else
	                    mHangoutTile = (new HangoutPhoneTile(this)).setInnerActionBarEnabled(false);
	                mHangoutTile.onCreate(null);
	                layoutparams = new android.widget.LinearLayout.LayoutParams(-1, -1);
	                mHangoutTile.setLayoutParams(layoutparams);
	                mTileContainer.addView(mHangoutTile);
	                mHangoutTile.addParticipantPresenceListener(conversationParticipantPresenceListener);
	                mHangoutTile.onStart();
	                if(android.os.Build.VERSION.SDK_INT >= 11)
	                    invalidateOptionsMenu();
	                else
	                    createTitlebarButtons(R.menu.conversation_activity_menu);
	            }
	    	}
	    	
	    	info = new Hangout.Info(Hangout.RoomType.EXTERNAL, null, "messenger", mConversationId, null, Hangout.LaunchSource.Messenger, false);
	        mHangoutTile.setHangoutInfo(mAccount, info, new ArrayList(mParticipantList.values()), false, false);
	        if(mCheckExtraTile)
	        {
	            mCheckExtraTile = false;
	            if(shouldShowHangoutTile() && mCurrentTile == mConversationTile || mCurrentTile == mHangoutTile)
	                toggleTiles();
	        }
    	} catch (LinkageError linkageerror) {
    		Log.error("Could not load hangout native library");
            linkageerror.printStackTrace();
    	}
    }

    private void initialize()
    {
        Log.error("initialize");
        mParticipantList = null;
        mConversationId = null;
        mCheckExtraTile = true;
        mConversationRowId = null;
        mIsConversationLoaded = false;
        Intent intent = getIntent();
        mAccount = (EsAccount)intent.getParcelableExtra("account");
        mIsGroup = intent.getBooleanExtra("is_group", false);
        if(!$assertionsDisabled && mConversationHeader == null)
            throw new AssertionError();
        mConversationHeader.setAccount(mAccount);
        long l = intent.getLongExtra("conversation_row_id", -1L);
        if(l != -1L)
            mConversationRowId = Long.valueOf(l);
        mSingleParticipant = (Data.Participant)intent.getSerializableExtra("participant");
        if(mSingleParticipant == null) {
        	getSupportLoaderManager().restartLoader(1, null, this);
            getSupportLoaderManager().restartLoader(2, null, this);
            mConversationTile.setConversationRowId(mConversationRowId);
            if(mConversationHeader != null)
                mConversationHeader.setParticipantListButtonVisibility(true);
            if(mComposeMessageFragment != null)
                mComposeMessageFragment.allowSendingImages(true);
        } else { 
        	mParticipantList = new HashMap();
            mParticipantList.put(mSingleParticipant.getParticipantId(), mSingleParticipant);
            displayParticipantsInTray();
            setConversationLabel(mSingleParticipant.getFullName());
            if(mConversationHeader != null)
                mConversationHeader.setParticipantListButtonVisibility(false);
            if(mComposeMessageFragment != null)
                mComposeMessageFragment.allowSendingImages(false);
        }
        
        mConversationHeader.setCommandListener(new ParticipantsCommandListener(mConversationHeader.getParticipantsGalleryView()));
        return;
    }

    private void inviteMoreParticipants()
    {
        OzActions ozactions;
        if(mIsGroup)
            ozactions = OzActions.GROUP_CONVERSATION_ADD_PEOPLE;
        else
            ozactions = OzActions.ONE_ON_ONE_CONVERSATION_ADD_PEOPLE;
        recordUserAction(ozactions);
        if(mParticipantList != null && mIsConversationLoaded)
        {
            java.util.Collection collection = mParticipantList.values();
            boolean flag = mIsGroup;
            EsAccount esaccount = mAccount;
            boolean flag1;
            if(mCurrentTile == mHangoutTile)
                flag1 = true;
            else
                flag1 = false;
            ParticipantHelper.inviteMoreParticipants(this, collection, flag, esaccount, flag1);
            mNeedToInviteParticipants = false;
        } else
        {
            mNeedToInviteParticipants = true;
        }
    }

    private MenuItem prepareToggleTilesMenu(Menu menu)
    {
        MenuItem menuitem = menu.findItem(R.id.realtimechat_conversation_toggle_tile_menu_item);
        if(mHangoutTile != null)
        {
            boolean flag;
            int i;
            if(mCurrentTile == mHangoutTile)
                flag = true;
            else
                flag = false;
            if(flag)
                i = R.drawable.ic_speech_bubble;
            else
                i = R.drawable.ic_menu_hangout;
            menuitem.setIcon(i);
            menuitem.setVisible(true);
            menuitem.setEnabled(true);
            if(!flag)
            {
                for(int j = mFirstHangoutMenuItemIndex; j < mLastHangoutMenuItemIndex; j++)
                    menu.getItem(j).setVisible(false);

            }
        } else
        {
            menuitem.setEnabled(false);
            menuitem.setVisible(false);
        }
        if(android.os.Build.VERSION.SDK_INT < 11)
            menuitem = null;
        return menuitem;
    }

    private void setConversationLabel(String s)
    {
        if(android.os.Build.VERSION.SDK_INT < 11)
        {
            showTitlebar(true);
            setTitlebarTitle(s);
        } else
        {
            getActionBar().setTitle(s);
        }
        mConversationName = s;
    }

    private boolean shouldShowHangoutTile()
    {
        boolean flag;
        if(getIntent().hasExtra("tile") && HangoutTile.class.getName().equals(getIntent().getStringExtra("tile")))
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void updateSubtitle()
    {
        boolean flag = mIsGroup;
        String s = null;
        if(flag)
        {
            int i = mParticipantCount;
            s = null;
            if(i > 0)
            {
                Resources resources = getResources();
                int j = R.string.participant_count;
                Object aobj[] = new Object[1];
                aobj[0] = Integer.valueOf(mParticipantCount);
                s = resources.getString(j, aobj);
            }
        }
        if(android.os.Build.VERSION.SDK_INT < 11)
        {
            showTitlebar(true);
            setTitlebarSubtitle(s);
        } else
        {
            getActionBar().setSubtitle(s);
        }
    }

    public final void blockPerson(Serializable serializable)
    {
        if(mHangoutTile != null)
            mHangoutTile.blockPerson(serializable);
    }

    public final void displayParticipantsInTray()
    {
        if(mParticipantList != null)
            if(mCurrentTile == mConversationTile)
            {
                HashSet hashset = new HashSet();
                if(mHangoutTile != null)
                    hashset = mHangoutTile.getActiveParticipantIds();
                mConversationHeader.setParticipants(mParticipantList, mConversationTile.getActiveParticipantIds(), hashset);
            } else
            {
                mHangoutTile.setParticipants(mParticipantList, mConversationTile.getActiveParticipantIds());
            }
    }

    public final EsAccount getAccount()
    {
        return mAccount;
    }

    public final Intent getGreenRoomParticipantListActivityIntent(List arraylist)
    {
        return getParticipantListActivityIntent();
    }

    public final Intent getHangoutNotificationIntent()
    {
        return Intents.getConversationActivityHangoutTileIntent(this, mAccount, mConversationRowId.longValue(), mIsGroup);
    }

    public final Intent getParticipantListActivityIntent()
    {
        EsAccount esaccount = mAccount;
        long l = mConversationRowId.longValue();
        String s = mConversationName;
        boolean flag = mIsGroup;
        boolean flag1;
        if(mCurrentTile == mHangoutTile)
            flag1 = true;
        else
            flag1 = false;
        return Intents.getParticipantListActivityIntent(this, esaccount, l, s, flag, flag1);
    }

    public final OzViews getViewForLogging()
    {
        OzViews ozviews;
        if(getIntent().getExtras().getBoolean("is_group"))
            ozviews = OzViews.CONVERSATION_GROUP;
        else
            ozviews = OzViews.CONVERSATION_ONE_ON_ONE;
        return ozviews;
    }

    public final void hideInsertCameraPhotoDialog()
    {
        dismissDialog(0x7f0a003e);
    }

    public final void leaveConversation()
    {
        if(!$assertionsDisabled && mConversationId == null)
            throw new AssertionError();
        GCommApp gcommapp = GCommApp.getInstance(this);
        if(gcommapp.isInHangout(new Hangout.Info(Hangout.RoomType.EXTERNAL, null, "messenger", mConversationId, null, Hangout.LaunchSource.Messenger, false)))
            gcommapp.exitMeeting();
    }

    protected final boolean needsAsyncData()
    {
        return true;
    }

    public void onActivityResult(int i, int j, Intent intent)
    {
        if(i == 1)
        {
            if(j == -1 && intent != null)
                mResultAudience = (AudienceData)intent.getParcelableExtra("audience");
        } else
        {
            super.onActivityResult(i, j, intent);
        }
    }

    public final void onAttachFragment(Fragment fragment) {
    	
        if((fragment instanceof ComposeMessageFragment)) {
        	mComposeMessageFragment = (ComposeMessageFragment)fragment;
            if(mConversationRowId == null)
                mComposeMessageFragment.allowSendingImages(false);
            else
                mComposeMessageFragment.allowSendingImages(true);
            mComposeMessageFragment.setListener(new ComposeMessageFragment.Listener() {

                public final void onSendPhoto(String s, int i) {
                	ConversationActivity conversationactivity = ConversationActivity.this;
            		OzActions ozactions;
                	if(1 == i) {
                        if(mIsGroup)
                            ozactions = OzActions.GROUP_CONVERSATION_CHOOSE_PHOTO;
                        else
                            ozactions = OzActions.ONE_ON_ONE_CONVERSATION_CHOOSE_PHOTO;
                        conversationactivity.recordUserAction(ozactions);
                	} else if(2 == i) {
                        if(mIsGroup)
                        	ozactions = OzActions.GROUP_CONVERSATION_TAKE_PHOTO;
                        else
                        	ozactions = OzActions.ONE_ON_ONE_CONVERSATION_TAKE_PHOTO;
                        conversationactivity.recordUserAction(ozactions);
                	}
                	
                    if(s.startsWith("content://"))
                        RealTimeChatService.sendLocalPhoto(ConversationActivity.this, mAccount, mConversationRowId.longValue(), s);
                    else
                        RealTimeChatService.sendMessage(ConversationActivity.this, mAccount, mConversationRowId.longValue(), null, s);
                    return;
                	
                }

                public final void onSendTextMessage(String s)
                {
                    ConversationActivity conversationactivity = ConversationActivity.this;
                    OzActions ozactions;
                    if(mIsGroup)
                        ozactions = OzActions.GROUP_CONVERSATION_POST;
                    else
                        ozactions = OzActions.ONE_ON_ONE_CONVERSATION_POST;
                    conversationactivity.recordUserAction(ozactions);
                    if(mConversationRowId != null)
                    {
                        RealTimeChatService.sendMessage(ConversationActivity.this, mAccount, mConversationRowId.longValue(), s, null);
                    } else
                    {
                        AudienceData audiencedata = new AudienceData(ParticipantUtils.makePersonFromParticipant(mSingleParticipant));
                        mCreateConversationRequestId = RealTimeChatService.createConversation(ConversationActivity.this, mAccount, audiencedata, s);
                    }
                }

                public final void onTypingStatusChanged(Client.Typing.Type type)
                {
                    if(mConversationRowId != null)
                        RealTimeChatService.sendTypingRequest(ConversationActivity.this, mAccount, mConversationRowId.longValue(), type);
                }

            });
        } else if(fragment instanceof MessageListFragment) { 
        	mMessageListFragment = (MessageListFragment)fragment;
            mMessageListFragment.setLeaveConversationListener(this);
            if(mConversationId != null)
                mMessageListFragment.setConversationInfo(mConversationId, mFirstEventTimestamp, mEarliestEventTimestamp);
            mMessageListFragment.setParticipantList(mParticipantList);
        } else if(fragment instanceof ParticipantsGalleryFragment) {
        	if(fragment instanceof ParticipantsGalleryFragment)
            {
                mConversationHeader = (ParticipantsGalleryFragment)fragment;
                if(!$assertionsDisabled && mAccount != null)
                    throw new AssertionError();
            }
        }
    }

    public final void onBlockCompleted(boolean flag)
    {
    }

    public void onClick(View view)
    {
        if(view.getId() == R.id.title_button_1)
            toggleTiles();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mAdvancedHangoutsEnabled = Hangout.isAdvancedUiSupported(this);
        FrameLayout framelayout = new FrameLayout(this) {

            protected void onMeasure(int j, int k)
            {
            	access$600(ConversationActivity.this, k);
                super.onMeasure(j, k);
            }

        };
        framelayout.setLayoutParams(new android.view.ViewGroup.LayoutParams(-1, -1));
        getLayoutInflater().inflate(R.layout.conversation_activity, framelayout, true);
        mRootView = framelayout;
        setContentView(mRootView);
        Log.debug("ConversationActivity.onCreate");
        mTileContainer = (LinearLayout)findViewById(R.id.tile_container);
        mConversationTile = (ConversationTile)findViewById(R.id.conversation_tile);
        mConversationTile.addParticipantPresenceListener(conversationParticipantPresenceListener);
        mCurrentTile = mConversationTile;
        mParticipantCount = 0;
        ShakeDetector shakedetector;
        int i;
        if(android.os.Build.VERSION.SDK_INT < 11)
        {
            showTitlebar(true);
            createTitlebarButtons(R.menu.conversation_activity_menu);
        } else
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        shakedetector = ShakeDetector.getInstance(getApplicationContext());
        if(shakedetector != null)
            mShakeDetectorWasRunning = shakedetector.stop();
        initialize();
        i = 1 + sInstanceCount;
        sInstanceCount = i;
        if(i > 1)
            Log.error((new StringBuilder("ConversationActivity onCreate instanceCount out of sync: ")).append(sInstanceCount).toString());
    }

    public Dialog onCreateDialog(int i, Bundle bundle)
    {
        Dialog dialog;
        if(i == 0x7f0a003e)
            dialog = ImageUtils.createInsertCameraPhotoDialog(this);
        else
            dialog = super.onCreateDialog(i, bundle);
        return dialog;
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        Log.debug((new StringBuilder("ConversationActivity.onCreateLoader: ")).append(i).toString());
        EsCursorLoader escursorloader;
        if(i == 1)
        {
            android.net.Uri uri1 = EsProvider.appendAccountParameter(EsProvider.CONVERSATIONS_URI, mAccount);
            String as2[] = ConversationQuery.PROJECTION;
            String as3[] = new String[1];
            as3[0] = String.valueOf(mConversationRowId);
            escursorloader = new EsCursorLoader(this, uri1, as2, "_id=?", as3, null);
        } else
        if(i == 2)
        {
            android.net.Uri uri = EsProvider.buildParticipantsUri(mAccount, mConversationRowId.longValue());
            String as[] = ParticipantsQuery.PROJECTION;
            String as1[] = new String[1];
            as1[0] = mAccount.getRealTimeChatParticipantId();
            escursorloader = new EsCursorLoader(this, uri, as, "participant_id!=? AND active=1", as1, "first_name ASC");
        } else
        {
            escursorloader = null;
        }
        return escursorloader;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuinflater = getMenuInflater();
        menuinflater.inflate(R.menu.conversation_activity_menu, menu);
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            menu.findItem(R.id.realtimechat_conversation_toggle_tile_menu_item).setShowAsAction(2);
            menu.findItem(R.id.realtimechat_conversation_invite_menu_item).setShowAsAction(2);
        }
        mFirstHangoutMenuItemIndex = menu.size();
        if(mHangoutTile != null)
            mHangoutTile.onCreateOptionsMenu(menu, menuinflater);
        mLastHangoutMenuItemIndex = menu.size();
        return true;
    }

    protected void onDestroy()
    {
        super.onDestroy();
        if(mShakeDetectorWasRunning)
        {
            ShakeDetector shakedetector = ShakeDetector.getInstance(getApplicationContext());
            if(shakedetector != null)
                shakedetector.start();
        }
        int i = -1 + sInstanceCount;
        sInstanceCount = i;
        if(i < 0)
            Log.error((new StringBuilder("ConversationActivity onDestroy instanceCount out of sync: ")).append(sInstanceCount).toString());
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        boolean flag;
        Cursor cursor;
        flag = true;
        cursor = (Cursor)obj;
        if(loader.getId() != 1) {
        	if(loader.getId() == 2)
            {
                mParticipantCount = cursor.getCount();
                updateSubtitle();
                mParticipantList = new HashMap();
                Data.Participant participant;
                for(; cursor.moveToNext(); mParticipantList.put(participant.getParticipantId(), participant))
                {
                    Data.Participant.Builder builder = Data.Participant.newBuilder();
                    String s = cursor.getString(3);
                    if(s != null)
                        builder.setFirstName(s);
                    participant = builder.setFullName(cursor.getString(2)).setParticipantId(cursor.getString(1)).setType(EsConversationsData.convertParticipantType(cursor.getInt(4))).build();
                }

                if(mConversationId != null)
                    initHangoutTile();
                displayParticipantsInTray();
                if(mNeedToInviteParticipants)
                {
                    java.util.Collection collection = mParticipantList.values();
                    boolean flag1 = mIsGroup;
                    EsAccount esaccount = mAccount;
                    if(mCurrentTile != mHangoutTile)
                        flag = false;
                    ParticipantHelper.inviteMoreParticipants(this, collection, flag1, esaccount, flag);
                    mNeedToInviteParticipants = false;
                }
                if(mMessageListFragment != null)
                    mMessageListFragment.setParticipantList(mParticipantList);
            } 
        } else { 
        	if(cursor != null && cursor.moveToFirst())
            {
                String s1 = cursor.getString(3);
                if(s1 == null)
                    s1 = cursor.getString(4);
                boolean flag2;
                boolean flag3;
                final int fatalErrorType;
                if(cursor.getInt(2) != 0)
                    flag2 = flag;
                else
                    flag2 = false;
                mIsGroup = flag2;
                if(cursor.getInt(1) != 0)
                    flag3 = flag;
                else
                    flag3 = false;
                mIsMuted = flag3;
                mConversationId = cursor.getString(0);
                mFirstEventTimestamp = cursor.getLong(5);
                mEarliestEventTimestamp = cursor.getLong(6);
                setConversationLabel(s1);
                updateSubtitle();
                fatalErrorType = cursor.getInt(7);
                if(fatalErrorType != 0)
                    (new Handler(Looper.getMainLooper())).post(new Runnable() {

                        public final void run()
                        {
                            LoaderManager loadermanager = getSupportLoaderManager();
                            if(loadermanager != null)
                                loadermanager.destroyLoader(1);
                            mMessageListFragment.handleFatalError(fatalErrorType);
                        }
                    });
                mIsConversationLoaded = flag;
                if(android.os.Build.VERSION.SDK_INT >= 11)
                    invalidateOptionsMenu();
                if(mNeedToInviteParticipants)
                    inviteMoreParticipants();
                mConversationHeader.setParticipantListButtonVisibility(flag);
                if(mMessageListFragment != null)
                    mMessageListFragment.setConversationInfo(mConversationId, mFirstEventTimestamp, mEarliestEventTimestamp);
                if(mParticipantList != null)
                    initHangoutTile();
            }
        }

    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onMeetingMediaStarted()
    {
        HashMap hashmap = new HashMap();
        hashmap.put("AUTHOR_PROFILE_ID", mAccount.getPersonId());
        RealTimeChatService.sendTileEvent(this, mAccount, mConversationId, "com.google.hangouts", 0, "JOIN_HANGOUT", hashmap);
    }

    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.debug("ConversationActivity.onNewIntent");
        initialize();
        mMessageListFragment.reinitialize();
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
        int i = menuitem.getItemId();
        if(0x102002c == i) {
        	goHome(mAccount);
        	return true;
        } else if(R.id.realtimechat_conversation_mute_menu_item == i) {
        	OzActions ozactions1;
            if(mIsGroup)
                ozactions1 = OzActions.GROUP_CONVERSATION_MUTE;
            else
                ozactions1 = OzActions.ONE_ON_ONE_CONVERSATION_MUTE;
            recordUserAction(ozactions1);
            RealTimeChatService.setConversationMuted(this, mAccount, mConversationRowId.longValue(), true);
            mIsMuted = true;
            getIntent().putExtra("conversation_is_muted", true);
            return false;
        } else if(R.id.realtimechat_conversation_unmute_menu_item == i) {
        	OzActions ozactions;
            if(mIsGroup)
                ozactions = OzActions.GROUP_CONVERSATION_UNMUTE;
            else
                ozactions = OzActions.ONE_ON_ONE_CONVERSATION_UNMUTE;
            recordUserAction(ozactions);
            RealTimeChatService.setConversationMuted(this, mAccount, mConversationRowId.longValue(), false);
            mIsMuted = false;
            getIntent().putExtra("conversation_is_muted", false);
            return false;
        } else if(R.id.realtimechat_conversation_leave_menu_item == i) {
        	if(mMessageListFragment != null)
                mMessageListFragment.displayLeaveConversationDialog();
        	return false;
        } else if(R.id.realtimechat_conversation_toggle_tile_menu_item == i) {
                toggleTiles();
                return false;
        } else if(R.id.realtimechat_conversation_edit_name_menu_item == i) {
                (new ConversationRenameDialog(mConversationName, mConversationRowId.longValue())).show(getSupportFragmentManager(), "rename_conversation");
                return false;
        } else if(R.id.realtimechat_conversation_invite_menu_item == i) {
                inviteMoreParticipants();
                return false;
        } else {
        	if(mHangoutTile == null) {
        		return false;
        	}
        	
        	return mHangoutTile.onOptionsItemSelected(menuitem);
        }
    }

    public void onPause()
    {
        Log.debug("ConversationActivity.onPause");
        RealTimeChatService.registerListener(mRealTimeChatListener);
        mCurrentTile.onTilePause();
        mConversationTile.onPause();
        if(mHangoutTile != null)
            mHangoutTile.onPause();
        super.onPause();
        RealTimeChatService.allowDisconnect(this, mAccount);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
    	
        if(!mIsConversationLoaded) {
        	return false;
        }
        
        if(null == mConversationRowId) {
        	menu.findItem(R.id.realtimechat_conversation_invite_menu_item).setVisible(true);
            menu.findItem(R.id.realtimechat_conversation_mute_menu_item).setVisible(false);
            menu.findItem(R.id.realtimechat_conversation_unmute_menu_item).setVisible(false);
            menu.findItem(R.id.realtimechat_conversation_edit_name_menu_item).setVisible(false);
            menu.findItem(R.id.realtimechat_conversation_leave_menu_item).setVisible(false);
            menu.findItem(R.id.realtimechat_conversation_toggle_tile_menu_item).setVisible(false);
            return true;
        } else {
        	menu.findItem(R.id.realtimechat_conversation_invite_menu_item).setVisible(false);
            MenuItem menuitem = menu.findItem(R.id.realtimechat_conversation_mute_menu_item);
            boolean flag2;
            if(!mIsMuted)
                flag2 = true;
            else
                flag2 = false;
            menuitem.setVisible(flag2);
            menu.findItem(R.id.realtimechat_conversation_unmute_menu_item).setVisible(mIsMuted);
            menu.findItem(R.id.realtimechat_conversation_edit_name_menu_item).setVisible(mIsGroup);
            menu.findItem(R.id.realtimechat_conversation_leave_menu_item).setVisible(true);
            if(android.os.Build.VERSION.SDK_INT >= 11)
            {
                mTileSelectorMenuItem = prepareToggleTilesMenu(menu);
            } else
            {
                menu.findItem(R.id.realtimechat_conversation_invite_menu_item).setVisible(false);
                menu.findItem(R.id.realtimechat_conversation_toggle_tile_menu_item).setVisible(false);
            }
            if(mHangoutTile != null)
                mHangoutTile.onPrepareOptionsMenu(menu);
            return true;
        }

    }

    public final void onPrepareTitlebarButtons(Menu menu)
    {
        menu.findItem(R.id.realtimechat_conversation_invite_menu_item).setVisible(true);
        prepareToggleTilesMenu(menu);
        menu.findItem(R.id.realtimechat_conversation_mute_menu_item).setVisible(false);
        menu.findItem(R.id.realtimechat_conversation_unmute_menu_item).setVisible(false);
        menu.findItem(R.id.realtimechat_conversation_edit_name_menu_item).setVisible(false);
        menu.findItem(R.id.realtimechat_conversation_leave_menu_item).setVisible(false);
    }

    public void onResume()
    {
        super.onResume();
        Log.debug("ConversationActivity.onResume");
        RealTimeChatService.registerListener(mRealTimeChatListener);
        mConversationTile.onResume();
        if(mHangoutTile != null)
            mHangoutTile.onResume();
        mCurrentTile.onTileResume();
        if(isIntentAccountActive())
        {
            if(mComposeMessageFragment != null)
                mComposeMessageFragment.setAllowSendMessage(true);
            if(mResultAudience != null)
            {
                RealTimeChatService.inviteParticipants(this, mAccount, mConversationRowId.longValue(), mResultAudience);
                mResultAudience = null;
            }
            RealTimeChatService.connectAndStayConnected(this, mAccount);
            displayParticipantsInTray();
        } else
        {
            finish();
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
    }

    protected void onStart()
    {
        super.onStart();
        Log.debug("ConversationActivity.onStart");
        mConversationTile.onStart();
        if(mHangoutTile != null)
            mHangoutTile.onStart();
        mCurrentTile.onTileStart();
        mComposeMessageFragment.requestFocus();
    }

    public void onStop()
    {
        super.onStop();
        Log.debug("ConversationActivity.onStop");
        mCurrentTile.onTileStop();
        mConversationTile.onStop();
        if(mHangoutTile != null)
            mHangoutTile.onStop();
    }

    protected final void onTitlebarLabelClick()
    {
        goHome(mAccount);
    }

    public void onWindowFocusChanged(boolean flag)
    {
        if(flag)
        {
            RealTimeChatService.setCurrentConversationRowId(mConversationRowId);
            if(!shouldShowHangoutTile() && mConversationRowId != null)
                RealTimeChatService.markConversationRead(this, mAccount, mConversationRowId.longValue());
        } else
        {
            RealTimeChatService.setCurrentConversationRowId(null);
        }
    }

    public final void showInsertCameraPhotoDialog()
    {
        showDialog(0x7f0a003e);
    }

    public final void stopHangoutTile()
    {
        if(mCurrentTile == mHangoutTile)
            toggleTiles();
    }

    public final void toggleTiles() {
    	
        if(!$assertionsDisabled && mHangoutTile == null)
            throw new AssertionError();
        
        if(null == mHangoutTile) {
        	return;
        }
        
        Object obj;
        if(mCurrentTile == mHangoutTile)
            obj = mConversationTile;
        else
            obj = mHangoutTile;
        mCurrentTile.setVisibility(8);
        mCurrentTile.onTilePause();
        mCurrentTile.onTileStop();
        ((Tile) (obj)).setVisibility(0);
        ((Tile) (obj)).onTileStart();
        ((Tile) (obj)).onTileResume();
        mCurrentTile = ((Tile) (obj));
        displayParticipantsInTray();
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            if(mTileSelectorMenuItem != null)
            {
                int i;
                if(mCurrentTile == mHangoutTile)
                    i = R.drawable.ic_speech_bubble;
                else
                    i = R.drawable.ic_menu_hangout;
                mTileSelectorMenuItem.setIcon(getResources().getDrawable(i));
            }
            invalidateOptionsMenu();
            if(obj == mConversationTile)
            {
            	ActionBar actionbar = getActionBar();
                if(actionbar != null)
                    actionbar.show();
            }
        } else
        {
            createTitlebarButtons(R.menu.conversation_activity_menu);
        }
    }
    
    static void access$600(ConversationActivity conversationactivity, int i)
    {
        int j = conversationactivity.getWindowManager().getDefaultDisplay().getHeight();
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            j -= conversationactivity.getActionBar().getHeight();
            android.widget.FrameLayout.LayoutParams layoutparams = new android.widget.FrameLayout.LayoutParams(-1, -1);
            conversationactivity.mRootView.setLayoutParams(layoutparams);
            if(conversationactivity.mAdvancedHangoutsEnabled && conversationactivity.mCurrentTile == conversationactivity.mHangoutTile)
                conversationactivity.mRootView.setPadding(0, 0, 0, 0);
            else
                conversationactivity.mRootView.setPadding(0, conversationactivity.getActionBar().getHeight(), 0, 0);
        }
        if((double)android.view.View.MeasureSpec.getSize(i) < 0.80000000000000004D * (double)j)
            conversationactivity.mConversationHeader.getView().setVisibility(8);
        else
            conversationactivity.mConversationHeader.getView().setVisibility(0);
        return;
    }
	
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
	
	private final class ConversationParticipantPresenceListener implements Tile.ParticipantPresenceListener {

	    public final void onParticipantPresenceChanged() {
	        displayParticipantsInTray();
	    }

	}

	public static interface ConversationQuery
	{
	
	    public static final String PROJECTION[] = {
	        "conversation_id", "is_muted", "is_group", "name", "generated_name", "first_event_timestamp", "earliest_event_timestamp", "fatal_error_type"
	    };
	
	}

	private final class ParticipantsCommandListener extends ParticipantsGalleryView.SimpleCommandListener
	{
	
	    public final void onShowParticipantList()
	    {
	        if(mParticipantList != null && mIsConversationLoaded)
	            startActivity(getParticipantListActivityIntent());
	    }
	
	
	    ParticipantsCommandListener(ParticipantsGalleryView participantsgalleryview)
	    {
	        super(participantsgalleryview, mAccount);
	    }
	}

	public static interface ParticipantsQuery
	{
	
	    public static final String PROJECTION[] = {
	        "_id", "participant_id", "full_name", "first_name", "type"
	    };
	
	}

	private final class RTCServiceListener extends RealTimeChatServiceListener {

	    public final void onConversationCreated(int i, CreateConversationOperation.ConversationResult conversationresult, RealTimeChatServiceResult realtimechatserviceresult)
	    {
	        boolean flag = true;
	        if(i == mCreateConversationRequestId)
	            if(realtimechatserviceresult.getErrorCode() == 1)
	            {
	                Intent intent;
	                if(conversationresult.mConversation == null || conversationresult.mConversation.getParticipantCount() <= 1)
	                    flag = false;
	                if(HangoutTile.class.getName().equals(getIntent().getStringExtra("tile")))
	                {
	                    GCommApp.getInstance(ConversationActivity.this).exitMeeting();
	                    intent = Intents.getConversationActivityHangoutTileIntent(ConversationActivity.this, mAccount, conversationresult.mConversationRowId.longValue(), flag);
	                } else
	                {
	                    intent = Intents.getConversationActivityIntent(ConversationActivity.this, mAccount, conversationresult.mConversationRowId.longValue(), flag);
	                }
	                startActivity(intent);
	            } else
	            if(realtimechatserviceresult.getErrorCode() == 4)
	                Toast.makeText(ConversationActivity.this, R.string.conversation_too_large, 0).show();
	            else
	                Toast.makeText(ConversationActivity.this, R.string.error_creating_conversation, 0).show();
	    }
	}
}
