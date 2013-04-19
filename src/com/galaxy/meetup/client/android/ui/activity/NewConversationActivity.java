/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.Toast;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzActions;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.hangout.GCommApp;
import com.galaxy.meetup.client.android.hangout.HangoutTile;
import com.galaxy.meetup.client.android.hangout.Log;
import com.galaxy.meetup.client.android.realtimechat.Client;
import com.galaxy.meetup.client.android.realtimechat.CreateConversationOperation;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceListener;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatServiceResult;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.ui.fragments.AudienceFragment;
import com.galaxy.meetup.client.android.ui.fragments.ComposeMessageFragment;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.util.ImageUtils;
import com.galaxy.meetup.client.util.ImageUtils.InsertCameraPhotoDialogDisplayer;

/**
 * 
 * @author sihai
 *
 */
public class NewConversationActivity extends EsFragmentActivity implements
		InsertCameraPhotoDialogDisplayer {

	private static int sInstanceCount;
    private EsAccount mAccount;
    private AudienceFragment mAudienceFragment;
    private ComposeMessageFragment mComposeMessageFragment;
    private Integer mCreateConversationRequestId;
    private final RTCServiceListener mRTCServiceListener = new RTCServiceListener();
	
	public NewConversationActivity()
    {
        mCreateConversationRequestId = null;
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

    private void updateAllowSendMessage()
    {
        if(mComposeMessageFragment != null)
        {
            ComposeMessageFragment composemessagefragment = mComposeMessageFragment;
            boolean flag;
            if(mAudienceFragment != null && !mAudienceFragment.isAudienceEmpty())
                flag = true;
            else
                flag = false;
            composemessagefragment.setAllowSendMessage(flag);
        }
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.CONVERSATION_START_NEW;
    }

    public final void hideInsertCameraPhotoDialog()
    {
        dismissDialog(0x7f0a003e);
    }

    public final void onAttachFragment(Fragment fragment) {
        if(!(fragment instanceof ComposeMessageFragment)) {
        	mComposeMessageFragment = (ComposeMessageFragment)fragment;
            updateAllowSendMessage();
            mComposeMessageFragment.allowSendingImages(false);
            mComposeMessageFragment.setListener(new ComposeMessageFragment.Listener() {
            	public final void onSendPhoto(String paramString, int paramInt)
                {
                }

                public final void onSendTextMessage(String paramString)
                {
                  NewConversationActivity.access$300(NewConversationActivity.this, paramString);
                }

                public final void onTypingStatusChanged(Client.Typing.Type paramType)
                {
                }
            });
        } else { 
        	if(fragment instanceof AudienceFragment)
            {
                mAudienceFragment = (AudienceFragment)fragment;
                mAudienceFragment.setCirclesUsageType(6);
                mAudienceFragment.setIncludePhoneOnlyContacts(true);
                mAudienceFragment.setIncludePlusPages(false);
                mAudienceFragment.setPublicProfileSearchEnabled(true);
                mAudienceFragment.setShowSuggestedPeople(true);
                updateAllowSendMessage();
                mAudienceFragment.setAudienceChangedCallback(new Runnable() {

                    public final void run()
                    {
                        if(mComposeMessageFragment != null)
                        {
                            ComposeMessageFragment composemessagefragment = mComposeMessageFragment;
                            boolean flag;
                            if(!mAudienceFragment.isAudienceEmpty())
                                flag = true;
                            else
                                flag = false;
                            composemessagefragment.setAllowSendMessage(flag);
                        }
                    }

                });
            }
        }
    }

    public void onBackPressed()
    {
        recordUserAction(OzActions.CONVERSATION_ABORT_NEW);
        onBackPressed();
    }

    protected void onCreate(Bundle bundle)
    {
        onCreate(bundle);
        setContentView(R.layout.new_conversation_activity);
        mAccount = EsService.getActiveAccount(this);
        if(android.os.Build.VERSION.SDK_INT < 11)
        {
            showTitlebar(true);
            setTitlebarTitle(getString(R.string.new_huddle_label));
        }
        int i;
        if(bundle != null)
            if(bundle.containsKey("requestId"))
                mCreateConversationRequestId = Integer.valueOf(bundle.getInt("requestId"));
            else
                mCreateConversationRequestId = null;
        i = 1 + sInstanceCount;
        sInstanceCount = i;
        if(i > 1)
            Log.error((new StringBuilder("NewConversationActivity onCreate instanceCount out of sync: ")).append(sInstanceCount).toString());
    }

    public Dialog onCreateDialog(int i, Bundle bundle)
    {
        Dialog dialog;
        if(i == 0x7f0a003e)
            dialog = ImageUtils.createInsertCameraPhotoDialog(this);
        else
            dialog = onCreateDialog(i, bundle);
        return dialog;
    }

    protected void onDestroy()
    {
        onDestroy();
        int i = -1 + sInstanceCount;
        sInstanceCount = i;
        if(i < 0)
        {
            Log.error((new StringBuilder("NewConversationActivity onDestroy instanceCount out of sync: ")).append(sInstanceCount).toString());
            sInstanceCount = 0;
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(menuitem.getItemId() == 0x102002c)
        {
            goHome(mAccount);
            flag = true;
        } else
        {
            flag = false;
        }
        return flag;
    }

    protected void onPause()
    {
        onPause();
        RealTimeChatService.unregisterListener(mRTCServiceListener);
        RealTimeChatService.allowDisconnect(this, mAccount);
    }

    protected void onResume()
    {
        onResume();
        if(isIntentAccountActive())
        {
            RealTimeChatService.registerListener(mRTCServiceListener);
            RealTimeChatService.connectAndStayConnected(this, mAccount);
        } else
        {
            finish();
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        onSaveInstanceState(bundle);
        if(mCreateConversationRequestId != null)
            bundle.putInt("requestId", mCreateConversationRequestId.intValue());
    }

    protected void onStart()
    {
        onStart();
        if(android.os.Build.VERSION.SDK_INT >= 11)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected final void onTitlebarLabelClick()
    {
        goHome(mAccount);
    }

    public final void showInsertCameraPhotoDialog()
    {
        showDialog(0x7f0a003e);
    }

    static void access$300(NewConversationActivity newconversationactivity, String s)
    {
        AudienceData audiencedata = newconversationactivity.mAudienceFragment.getAudience();
        newconversationactivity.mCreateConversationRequestId = Integer.valueOf(RealTimeChatService.createConversation(newconversationactivity, newconversationactivity.mAccount, audiencedata, s));
        return;
    }
    
	//==================================================================================================================
    //									Inner class
    //==================================================================================================================
	private final class RTCServiceListener extends RealTimeChatServiceListener
    {

        public final void onConversationCreated(int i, CreateConversationOperation.ConversationResult conversationresult, RealTimeChatServiceResult realtimechatserviceresult)
        {
            boolean flag = true;
            if(i == mCreateConversationRequestId.intValue())
                if(realtimechatserviceresult.getErrorCode() == 1)
                {
                    Intent intent;
                    if(conversationresult.mConversation == null || conversationresult.mConversation.getParticipantCount() <= 1)
                        flag = false;
                    if(HangoutTile.class.getName().equals(getIntent().getStringExtra("tile")))
                    {
                        GCommApp.getInstance(NewConversationActivity.this).exitMeeting();
                        intent = Intents.getConversationActivityHangoutTileIntent(NewConversationActivity.this, mAccount, conversationresult.mConversationRowId.longValue(), flag);
                    } else
                    {
                        intent = Intents.getConversationActivityIntent(NewConversationActivity.this, mAccount, conversationresult.mConversationRowId.longValue(), flag);
                    }
                    startActivity(intent);
                    finish();
                } else
                if(realtimechatserviceresult.getErrorCode() == 4)
                    Toast.makeText(NewConversationActivity.this, R.string.conversation_too_large, 0).show();
                else
                    Toast.makeText(NewConversationActivity.this, R.string.error_creating_conversation, 0).show();
        }

    }
}
