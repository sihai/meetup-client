/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.realtimechat.ParticipantUtils;
import com.galaxy.meetup.client.android.service.Hangout;
import com.galaxy.meetup.client.android.ui.fragments.EsFragmentActivity;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class HangoutParticipantListActivity extends EsFragmentActivity
		implements OnClickListener {

	private Hangout.Info hangoutInfo;
    private EsAccount mAccount;
    private Collection mParticipantList;
    private ToastsView toastsView;
    
    public HangoutParticipantListActivity()
    {
    }

    private boolean canInviteMoreParticipants()
    {
        boolean flag;
        if(hangoutInfo != null && EsLog.ENABLE_DOGFOOD_FEATURES)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void inviteMoreParticipants()
    {
        String s = getResources().getString(R.string.realtimechat_conversation_invite_menu_item_text);
        ArrayList arraylist = new ArrayList();
        for(Iterator iterator = mParticipantList.iterator(); iterator.hasNext(); arraylist.add(ParticipantUtils.makePersonFromParticipant((Data.Participant)iterator.next())));
        AudienceData audiencedata = new AudienceData(arraylist, null);
        startActivityForResult(Intents.getEditAudienceActivityIntent(this, mAccount, s, audiencedata, 5, false, false, true, true), 0);
    }

    protected final EsAccount getAccount()
    {
        return mAccount;
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.HANGOUT_PARTICIPANTS;
    }

    public void onActivityResult(int i, int j, Intent intent)
    {
        if(i == 0 && j == -1 && intent != null)
        {
            AudienceData audiencedata = (AudienceData)intent.getParcelableExtra("audience");
            GCommApp.getInstance(this).getGCommNativeWrapper().inviteToMeeting(audiencedata, "HANGOUT", false, true);
            toastsView.addToast(R.string.hangout_invites_sent);
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
        toastsView = (ToastsView)findViewById(R.id.toasts_view);
        mAccount = (EsAccount)getIntent().getExtras().get("account");
        hangoutInfo = GCommApp.getInstance(this).getGCommNativeWrapper().getHangoutInfo();
        showTitlebar(true);
        createTitlebarButtons(R.menu.hangout_participant_list_activity_menu);
        mParticipantList = (ArrayList)getIntent().getSerializableExtra("hangout_participants");
        String s = getResources().getString(R.string.hangout_participants_title);
        showTitlebar(true);
        setTitlebarTitle(s);
        mParticipantList.size();
        Resources resources = getResources();
        int i = R.string.participant_count;
        Object aobj[] = new Object[1];
        aobj[0] = Integer.valueOf(mParticipantList.size());
        String s1 = resources.getString(i, aobj);
        showTitlebar(true);
        setTitlebarSubtitle(s1);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
        int i = menuitem.getItemId();
        if(i == R.id.hangout_invite_menu_item) {
        	inviteMoreParticipants();
        	return false;
        } else if(0x102002c == i) {
        	goHome(mAccount);
        	return true;
        }

        return false;
    }

    protected void onPause()
    {
        super.onPause();
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return false;
    }

    public final void onPrepareTitlebarButtons(Menu menu)
    {
        menu.findItem(R.id.hangout_invite_menu_item).setVisible(canInviteMoreParticipants());
    }

    protected void onResume()
    {
        super.onResume();
        if(!isIntentAccountActive())
            finish();
    }

    protected final void onTitlebarLabelClick()
    {
        goHome(mAccount);
    }

}
