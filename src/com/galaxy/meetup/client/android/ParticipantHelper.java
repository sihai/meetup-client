/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.hangout.HangoutTile;
import com.galaxy.meetup.client.android.realtimechat.Data;

/**
 * 
 * @author sihai
 *
 */
public class ParticipantHelper {

	public static void inviteMoreParticipants(Activity activity, Collection collection, boolean flag, EsAccount esaccount, boolean flag1)
    {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = collection.iterator();
        while(iterator.hasNext()) 
        {
            Data.Participant participant = (Data.Participant)iterator.next();
            String s = participant.getParticipantId();
            String s1 = null;
            String s2;
            if(s.startsWith("g:"))
                s2 = EsPeopleData.extractGaiaId(s);
            else
            if(s.startsWith("e:"))
            {
                s1 = s.substring(2);
                s2 = null;
            } else
            {
                boolean flag2 = s.startsWith("p:");
                s1 = null;
                s2 = null;
                if(flag2)
                {
                    s1 = s;
                    s2 = null;
                }
            }
            arraylist.add(new PersonData(s2, participant.getFullName(), s1));
        }
        AudienceData audiencedata = new AudienceData(arraylist, null);
        if(flag)
        {
            activity.startActivityForResult(Intents.getEditAudienceActivityIntent(activity, esaccount, activity.getString(R.string.realtimechat_edit_audience_activity_title), audiencedata, 6, true, true, true, false), 1);
        } else
        {
            Intent intent = Intents.getNewConversationActivityIntent(activity, esaccount, audiencedata);
            if(flag1)
                intent.putExtra("tile", HangoutTile.class.getName());
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
