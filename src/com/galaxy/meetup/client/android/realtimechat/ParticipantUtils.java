/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.realtimechat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;

/**
 * 
 * @author sihai
 *
 */
public class ParticipantUtils {

	public static String getParticipantIdFromPerson(PersonData persondata) {
        String s = persondata.getObfuscatedId();
        if(!TextUtils.isEmpty(s)) {
        	return (new StringBuilder("g:")).append(s).toString();
        }
        
        boolean flag = TextUtils.isEmpty(persondata.getEmail());
        if(!flag)
        {
            String s2 = persondata.getEmail();
            if(s2.startsWith("p:"))
                return (new StringBuilder("p:")).append(PhoneNumberUtils.stripSeparators(s2.substring(2))).toString();
            else
                return (new StringBuilder("e:")).append(s2).toString();
        }
        return null;
    }

    public static List getParticipantListFromAudience(Context context, EsAccount esaccount, AudienceData audiencedata) {
        CircleData acircledata[];
        int k;
        int l;
        HashSet hashset = new HashSet();
        ArrayList arraylist = new ArrayList();
        PersonData apersondata[] = audiencedata.getUsers();
        int i = apersondata.length;
        int j = 0;
        while(j < i) 
        {
            PersonData persondata = apersondata[j];
            String s1 = getParticipantIdFromPerson(persondata);
            Data.Participant participant1;
            if(s1 != null)
            {
                String s2 = persondata.getName();
                String as1[] = s2.split(" ");
                if(as1.length > 0)
                    s2 = as1[0];
                participant1 = Data.Participant.newBuilder().setFullName(persondata.getName()).setFirstName(s2).setParticipantId(s1).build();
            } else
            {
                participant1 = null;
            }
            if(participant1 != null && !hashset.contains(participant1.getParticipantId()))
            {
                hashset.add(participant1.getParticipantId());
                arraylist.add(participant1);
            }
            j++;
        }
        acircledata = audiencedata.getCircles();
        k = acircledata.length;
        for(l = 0; l < k; l++) {
            Cursor cursor = null;
            CircleData circledata = acircledata[l];
            try {
	            cursor = EsPeopleData.getPeople(context, esaccount, circledata.getId(), null, new String[] {
	                "name", "person_id"
	            }, null, null);
	            do
	            {
	                if(!cursor.moveToNext())
	                    break;
	                String s = cursor.getString(0);
	                String as[] = s.split(" ");
	                if(as.length > 0)
	                    s = as[0];
	                Data.Participant participant = Data.Participant.newBuilder().setFullName(cursor.getString(0)).setFirstName(s).setParticipantId(cursor.getString(1)).build();
	                if(!hashset.contains(participant.getParticipantId()))
	                {
	                    hashset.add(participant.getParticipantId());
	                    arraylist.add(participant);
	                }
	            } while(true);
            } finally {
            	if(null != cursor) {
            		cursor.close();
            	}
            }
        }
        
        return arraylist;

    }

    public static PersonData makePersonFromParticipant(Data.Participant participant)
    {
        PersonData persondata;
        if(participant != null)
        {
            String s = participant.getParticipantId();
            if(!s.startsWith("g:"))
                throw new IllegalArgumentException();
            persondata = new PersonData(s.substring(2), participant.getFullName(), null);
        } else
        {
            persondata = null;
        }
        return persondata;
    }
}
