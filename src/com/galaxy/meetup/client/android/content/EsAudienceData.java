/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.galaxy.meetup.client.android.realtimechat.Client;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class EsAudienceData {

	public static void processSuggestionsResponse(Context context, EsAccount esaccount, Client.SuggestionsResponse suggestionsresponse)
    {
        if(EsLog.isLoggable("EsConversationsData", 3))
            Log.d("EsConversationsData", "processSuggestionsResponse");
        SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
        try {
        	sqlitedatabase.beginTransaction();
        	StringBuilder stringbuilder = new StringBuilder();
        	ContentValues contentvalues = new ContentValues();
        	int i = 0;
        	int j;
	        for(Iterator iterator = suggestionsresponse.getSuggestionList().iterator(); iterator.hasNext();)
	        {
	            Iterator iterator1 = ((Client.Suggestion)iterator.next()).getSuggestedUserList().iterator();
	            int k;
	            for(j = i; iterator1.hasNext(); j = k)
	            {
	                Data.Participant participant = (Data.Participant)iterator1.next();
	                if(j > 0)
	                    stringbuilder.append(',');
	                stringbuilder.append("'").append(participant.getParticipantId()).append("'");
	                contentvalues.clear();
	                contentvalues.put("full_name", participant.getFullName());
	                contentvalues.put("first_name", participant.getFirstName());
	                contentvalues.put("participant_id", participant.getParticipantId());
	                k = j + 1;
	                contentvalues.put("sequence", Integer.valueOf(j));
	                sqlitedatabase.insertWithOnConflict("hangout_suggestions", null, contentvalues, 5);
	            }
	
	            i = j;
	        }
	        sqlitedatabase.delete("hangout_suggestions", (new StringBuilder("participant_id NOT IN (")).append(stringbuilder.toString()).append(")").toString(), null);
	        sqlitedatabase.setTransactionSuccessful();
        } finally {
        	sqlitedatabase.endTransaction();
        }
    }
}
