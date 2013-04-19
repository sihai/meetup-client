/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.io.Serializable;
import java.util.LinkedList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.MenuItem;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsConversationsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.ui.fragments.BlockFragment;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.android.ui.fragments.ParticipantsGalleryFragment;
import com.galaxy.meetup.client.android.ui.fragments.BlockFragment.Listener;
import com.galaxy.meetup.client.android.ui.fragments.BlockPersonDialog.PersonBlocker;
import com.galaxy.meetup.client.android.ui.view.ParticipantsGalleryView;

/**
 * 
 * @author sihai
 *
 */
public class InvitationActivity extends EsFragmentActivity implements
		LoaderCallbacks, Listener, PersonBlocker {

	static final boolean $assertionsDisabled;
    EsAccount mAccount;
    String mConversationName;
    long mConversationRowId;
    String mInviterId;
    String mInviterName;
    boolean mIsGroup;
    private ParticipantsGalleryFragment mParticipantsGalleryFragment;

    static 
    {
        boolean flag;
        if(!InvitationActivity.class.desiredAssertionStatus())
            flag = true;
        else
            flag = false;
        $assertionsDisabled = flag;
    }
    
    public InvitationActivity()
    {
    }

    private void initialize(Intent intent)
    {
        mAccount = (EsAccount)intent.getParcelableExtra("account");
        mConversationRowId = intent.getLongExtra("conversation_row_id", 0L);
        mInviterId = intent.getStringExtra("inviter_id");
        mIsGroup = intent.getBooleanExtra("is_group", false);
        mParticipantsGalleryFragment.setAccount(mAccount);
        mParticipantsGalleryFragment.setCommandListener(new ParticipantsGalleryView.SimpleCommandListener(mParticipantsGalleryFragment.getParticipantsGalleryView(), mAccount));
        mParticipantsGalleryFragment.setParticipantListButtonVisibility(false);
        getSupportLoaderManager().restartLoader(1, null, this);
        getSupportLoaderManager().restartLoader(2, null, this);
        mParticipantsGalleryFragment.getView().setVisibility(0);
        RealTimeChatService.markConversationNotificationsSeen(this, mAccount, mConversationRowId);
    }

    public final void blockPerson(Serializable serializable)
    {
        BlockFragment.getInstance(this, mAccount, mInviterId, mInviterName, false, true).show(this);
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.CONVERSATION_INVITE;
    }

    public final void onAttachFragment(Fragment fragment)
    {
        if(fragment instanceof ParticipantsGalleryFragment)
        {
            mParticipantsGalleryFragment = (ParticipantsGalleryFragment)fragment;
            if(!$assertionsDisabled && mAccount != null)
                throw new AssertionError();
        }
    }

    public final void onBlockCompleted(boolean flag)
    {
        if(flag)
        {
            RealTimeChatService.leaveConversation(this, mAccount, mConversationRowId);
            finish();
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.invitation_activity);
        initialize(getIntent());
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
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
            android.net.Uri uri = EsProvider.buildParticipantsUri(mAccount, mConversationRowId);
            String as[] = ConversationActivity.ParticipantsQuery.PROJECTION;
            String as1[] = new String[1];
            as1[0] = mAccount.getRealTimeChatParticipantId();
            escursorloader = new EsCursorLoader(this, uri, as, "participant_id!=?", as1, "first_name");
        } else
        {
            escursorloader = null;
        }
        return escursorloader;
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        if(loader.getId() != 1) {
        	if(loader.getId() == 2)
            {
                LinkedList linkedlist = new LinkedList();
                Data.Participant.Builder builder;
                for(; cursor.moveToNext(); linkedlist.add(builder.setFullName(cursor.getString(2)).setParticipantId(cursor.getString(1)).setType(EsConversationsData.convertParticipantType(cursor.getInt(4))).build()))
                {
                    builder = Data.Participant.newBuilder();
                    String s = cursor.getString(3);
                    if(s != null)
                        builder.setFirstName(s);
                }

                mParticipantsGalleryFragment.removeAllParticipants();
                mParticipantsGalleryFragment.addParticipants(linkedlist);
            }
        } else { 
        	if(cursor != null && cursor.moveToFirst())
            {
                mConversationName = cursor.getString(2);
                mInviterName = cursor.getString(3);
                String s1 = mInviterName;
                if(android.os.Build.VERSION.SDK_INT < 11)
                {
                    showTitlebar(true);
                    setTitlebarTitle(s1);
                } else
                {
                    getActionBar().setTitle(s1);
                }
            }
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        initialize(intent);
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

    protected void onStart()
    {
        super.onStart();
        if(android.os.Build.VERSION.SDK_INT >= 11)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected final void onTitlebarLabelClick()
    {
        goHome(mAccount);
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    public static interface ConversationQuery
    {

        public static final String PROJECTION[] = {
            "_id", "is_group", "generated_name", "inviter_full_name"
        };

    }
}
