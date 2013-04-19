/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.ParticipantHelper;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsConversationsData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.hangout.HangoutTile;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class ParticipantListActivity extends EsFragmentActivity implements
		LoaderCallbacks, OnClickListener {

	private EsAccount mAccount;
    private long mConversationRowId;
    private boolean mIsGroup;
    private boolean mNeedToInviteParticipants;
    private int mParticipantCount;
    private Collection mParticipantList;
    private AudienceData mResultAudience;
    
    public ParticipantListActivity()
    {
    }

    private void inviteMoreParticipants()
    {
        if(mParticipantList != null)
        {
            ParticipantHelper.inviteMoreParticipants(this, mParticipantList, mIsGroup, mAccount, HangoutTile.class.getName().equals(getIntent().getStringExtra("tile")));
            mNeedToInviteParticipants = false;
        } else
        {
            mNeedToInviteParticipants = true;
        }
    }

    private void setParticipantCount(int i)
    {
        mParticipantCount = i;
        updateSubtitle();
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

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.CONVERSATION_PARTICIPANT_LIST;
    }

    public void onActivityResult(int i, int j, Intent intent)
    {
        if(i == 1)
        {
            if(j == -1 && intent != null)
            {
                mResultAudience = (AudienceData)intent.getParcelableExtra("audience");
                if(mResultAudience != null && EsLog.isLoggable("ParticipantList", 3))
                    Log.d("ParticipantList", (new StringBuilder("got audience ")).append(mResultAudience).toString());
            }
        } else
        {
            super.onActivityResult(i, j, intent);
        }
    }

    public void onClick(View view)
    {
        if(view.getId() == R.id.title_button_1)
            inviteMoreParticipants();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.participant_list_activity);
        mAccount = (EsAccount)getIntent().getExtras().get("account");
        mIsGroup = getIntent().getBooleanExtra("is_group", false);
        mConversationRowId = getIntent().getLongExtra("conversation_row_id", -1L);
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else
        {
            showTitlebar(true);
            createTitlebarButtons(R.menu.participant_list_activity_menu);
        }
        getSupportLoaderManager().restartLoader(1, null, this);
        getSupportLoaderManager().restartLoader(2, null, this);
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
        boolean flag;
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            getMenuInflater().inflate(R.menu.participant_list_activity_menu, menu);
            flag = true;
        } else
        {
            flag = false;
        }
        return flag;
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        if(loader.getId() != 1) {
        	if(loader.getId() == 2)
            {
                mParticipantList = new ArrayList();
                Data.Participant participant;
                for(; cursor.moveToNext(); mParticipantList.add(participant))
                {
                    Data.Participant.Builder builder = Data.Participant.newBuilder();
                    String s = cursor.getString(3);
                    if(s != null)
                        builder.setFirstName(s);
                    participant = builder.setFullName(cursor.getString(2)).setParticipantId(cursor.getString(1)).setType(EsConversationsData.convertParticipantType(cursor.getInt(4))).build();
                }

                if(cursor != null && cursor.moveToFirst())
                    setParticipantCount(cursor.getCount());
                else
                    setParticipantCount(0);
                if(mNeedToInviteParticipants)
                {
                    ParticipantHelper.inviteMoreParticipants(this, mParticipantList, mIsGroup, mAccount, HangoutTile.class.getName().equals(getIntent().getStringExtra("tile")));
                    mNeedToInviteParticipants = false;
                }
            }
        } else { 
        	if(cursor != null && cursor.moveToFirst())
            {
                String s1 = cursor.getString(1);
                if(s1 == null)
                    s1 = cursor.getString(2);
                if(android.os.Build.VERSION.SDK_INT < 11)
                {
                    showTitlebar(true);
                    setTitlebarTitle(s1);
                } else
                {
                    getActionBar().setTitle(s1);
                }
                updateSubtitle();
            }
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        int i = menuitem.getItemId();
        if(R.id.realtimechat_conversation_invite_menu_item == i) {
        	inviteMoreParticipants();
        	return false;
        } else if(0x102002c == i) {
        	goHome(mAccount);
        	return true;
        } else {
        	return false;
        }
        
    }

    protected void onPause()
    {
        super.onPause();
        RealTimeChatService.allowDisconnect(this, mAccount);
    }

    protected void onResume()
    {
        super.onResume();
        if(isIntentAccountActive())
        {
            RealTimeChatService.connectAndStayConnected(this, mAccount);
            if(mResultAudience != null)
            {
                RealTimeChatService.inviteParticipants(this, mAccount, mConversationRowId, mResultAudience);
                mResultAudience = null;
            }
        } else
        {
            finish();
        }
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
            "is_group", "name", "generated_name"
        };

    }

    public static interface ParticipantsQuery
    {

        public static final String PROJECTION[] = {
            "_id", "participant_id", "full_name", "first_name", "type"
        };

    }
}
