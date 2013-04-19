/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.EmbedsPerson;
import com.galaxy.meetup.server.client.domain.Invitee;
import com.galaxy.meetup.server.client.domain.InviteeSummary;
import com.galaxy.meetup.server.client.domain.PlusEvent;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class EventInviteeListLoader extends EsCursorLoader {

	private static final String INVITEE_PROJECTION[] = {
        "is_header", "_id", "person_id", "gaia_id", "name", "email", "packed_circle_ids", "numaddguests", "blacklisted", "rsvp", 
        "is_past", "invitee_count"
    };
    private final EsAccount mAccount;
    private final String mEventId;
    private final String mOwnerId;
    
    private static interface InviteeQuery
    {

        public static final String PROJECTION[] = {
            "event_data", "invitee_roster"
        };

    }


    public EventInviteeListLoader(Context context, EsAccount esaccount, String s, String s1)
    {
        super(context, EsProvider.EVENTS_ALL_URI);
        mEventId = s;
        mOwnerId = s1;
        mAccount = esaccount;
    }

    private void insertInviteeGroup(PlusEvent plusevent, String s, List list, EsMatrixCursor esmatrixcursor, HashMap hashmap)
    {
        InviteeSummary inviteesummary = EsEventData.getInviteeSummary(plusevent, s);
        int i = 0;
        for(int j = 0; j < list.size(); j++)
        {
            Invitee invitee1 = (Invitee)list.get(j);
            if(isPersonVisible(invitee1.invitee))
                i += 1 + PrimitiveUtils.safeInt(invitee1.numAdditionalGuests);
        }

        int i1;
        if(inviteesummary != null)
        {
            i1 = inviteesummary.count.intValue();
        } else
        {
            int k = 0;
            for(int l = 0; l < list.size(); l++)
                k += 1 + PrimitiveUtils.safeInt(((Invitee)list.get(l)).numAdditionalGuests);

            i1 = k;
        }
        if(i1 > 0)
        {
            boolean flag = EsEventData.isEventOver(plusevent, System.currentTimeMillis());
            Object aobj[] = new Object[INVITEE_PROJECTION.length];
            aobj[0] = Integer.valueOf(0);
            aobj[1] = Integer.valueOf(esmatrixcursor.getCount());
            aobj[9] = s;
            aobj[11] = Integer.valueOf(i1);
            int j1;
            if(flag)
                j1 = 1;
            else
                j1 = 0;
            aobj[10] = Integer.valueOf(j1);
            esmatrixcursor.addRow(aobj);
            if(list != null)
            {
                Iterator iterator = list.iterator();
                do
                {
                    if(!iterator.hasNext())
                        break;
                    Invitee invitee = (Invitee)iterator.next();
                    if(invitee.invitee != null && isPersonVisible(invitee.invitee))
                    {
                        String s1 = invitee.invitee.ownerObfuscatedId;
                        boolean flag1;
                        Object aobj2[];
                        String s2;
                        int l1;
                        int i2;
                        if(invitee.isAdminBlacklisted != null && invitee.isAdminBlacklisted.booleanValue())
                            flag1 = true;
                        else
                            flag1 = false;
                        aobj2 = new Object[INVITEE_PROJECTION.length];
                        aobj2[0] = Integer.valueOf(1);
                        aobj2[1] = Integer.valueOf(esmatrixcursor.getCount());
                        if(s1 != null)
                            s2 = (new StringBuilder("g:")).append(s1).toString();
                        else
                            s2 = null;
                        aobj2[2] = s2;
                        aobj2[3] = s1;
                        aobj2[4] = invitee.invitee.name;
                        aobj2[5] = invitee.invitee.email;
                        if(TextUtils.equals(invitee.rsvpType, "ATTENDING"))
                            l1 = PrimitiveUtils.safeInt(invitee.numAdditionalGuests);
                        else
                            l1 = 0;
                        aobj2[7] = Integer.valueOf(l1);
                        aobj2[6] = hashmap.get(s1);
                        if(flag1)
                            i2 = 1;
                        else
                            i2 = 0;
                        aobj2[8] = Integer.valueOf(i2);
                        esmatrixcursor.addRow(aobj2);
                    }
                } while(true);
            }
            int k1 = i1 - i;
            if(k1 > 0)
            {
                Object aobj1[] = new Object[INVITEE_PROJECTION.length];
                aobj1[0] = Integer.valueOf(2);
                aobj1[1] = Integer.valueOf(esmatrixcursor.getCount());
                aobj1[11] = Integer.valueOf(k1);
                esmatrixcursor.addRow(aobj1);
            }
        }
    }

    private static boolean isPersonVisible(EmbedsPerson embedsperson)
    {
        boolean flag;
        if(embedsperson != null && (!TextUtils.isEmpty(embedsperson.email) || !TextUtils.isEmpty(embedsperson.name)))
            flag = true;
        else
            flag = false;
        return flag;
    }

    private HashMap queryCirclesForPeople(List list)
    {
        HashMap hashmap;
        Cursor cursor = null;
        hashmap = new HashMap();
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            Invitee invitee = (Invitee)iterator.next();
            if(invitee.invitee != null && invitee.invitee.ownerObfuscatedId != null)
                hashmap.put(invitee.invitee.ownerObfuscatedId, null);
        } while(true);
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("gaia_id IN(");
        for(int i = 0; i < hashmap.size(); i++)
        {
            if(i > 0)
                stringbuilder.append(',');
            stringbuilder.append('?');
        }

        stringbuilder.append(')');
        String s = stringbuilder.toString();
        String as[] = (String[])hashmap.keySet().toArray(new String[0]);
        try {
        	cursor = EsPeopleData.getPeople(getContext(), mAccount, null, null, CircleQuery.PROJECTION, s, as);
        	if(null != cursor) {
        		for(; cursor.moveToNext(); hashmap.put(cursor.getString(0), cursor.getString(1)));
        	}
        	return hashmap;
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }

    public final Cursor esLoadInBackground()
    {
    	if(null == mEventId || null == mOwnerId) {
    		return null;
    	}
    	
    	PlusEvent plusevent = null;
		EsEventData.InviteeList inviteelist = null;
    	Cursor cursor = null;
    	try {
    		cursor = EsEventData.getEvent(getContext(), mAccount, mEventId, InviteeQuery.PROJECTION);
    		if(cursor.moveToFirst()) {
    			plusevent = (PlusEvent)JsonUtil.fromByteArray(cursor.getBlob(0), PlusEvent.class);
                byte abyte0[] = cursor.getBlob(1);
                inviteelist = null;
                if(abyte0 != null)
                    inviteelist = (EsEventData.InviteeList)JsonUtil.fromByteArray(abyte0, EsEventData.InviteeList.class);
    		}
    	} finally {
    		if(null != cursor) {
    			cursor.close();
    		}
    	}
        
        if(inviteelist == null)
        {
            return null;
        } else
        {
            ArrayList arraylist = new ArrayList();
            ArrayList arraylist1 = new ArrayList();
            ArrayList arraylist2 = new ArrayList();
            ArrayList arraylist3 = new ArrayList();
            ArrayList arraylist4 = new ArrayList();
            for(Iterator iterator = inviteelist.invitees.iterator(); iterator.hasNext();)
            {
                Invitee invitee = (Invitee)iterator.next();
                if(invitee.isAdminBlacklisted != null && invitee.isAdminBlacklisted.booleanValue())
                {
                    arraylist4.add(invitee);
                } else
                {
                    String s = invitee.rsvpType;
                    if("ATTENDING".equals(s) || "CHECKIN".equals(s))
                        arraylist.add(invitee);
                    else
                    if("NOT_ATTENDING".equals(s))
                        arraylist2.add(invitee);
                    else
                    if("MAYBE".equals(s))
                        arraylist1.add(invitee);
                    else
                        arraylist3.add(invitee);
                }
            }

            HashMap hashmap = queryCirclesForPeople(inviteelist.invitees);
            EsMatrixCursor em = new EsMatrixCursor(INVITEE_PROJECTION);
            insertInviteeGroup(plusevent, "ATTENDING", arraylist, ((EsMatrixCursor) (em)), hashmap);
            insertInviteeGroup(plusevent, "MAYBE", arraylist1, ((EsMatrixCursor) (em)), hashmap);
            insertInviteeGroup(plusevent, "NOT_ATTENDING", arraylist2, ((EsMatrixCursor) (em)), hashmap);
            insertInviteeGroup(plusevent, "NOT_RESPONDED", arraylist3, ((EsMatrixCursor) (em)), hashmap);
            insertInviteeGroup(plusevent, "REMOVED", arraylist4, ((EsMatrixCursor) (em)), hashmap);
            
            return em;
        }
    }
    
	private static interface CircleQuery {

		public static final String PROJECTION[] = { "gaia_id",
				"packed_circle_ids" };

	}
}
