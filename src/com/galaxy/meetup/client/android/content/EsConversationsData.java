/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.galaxy.meetup.client.android.realtimechat.Client;
import com.galaxy.meetup.client.android.realtimechat.Data;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatOperationState;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class EsConversationsData {

	
	public static void cleanupData(SQLiteDatabase sqlitedatabase)
    {
        if(EsLog.isLoggable("EsConversationsData", 3))
            Log.d("EsConversationsData", "cleanupData");
        sqlitedatabase.delete("participants", "(SELECT COUNT(participant_id) FROM conversation_participants WHERE participants.participant_id=conversation_participants.participant_id)=0", null);
    }
	
	public static Bundle createConversationLocally(Context context, EsAccount esaccount, Client.ClientConversation clientconversation, String s, boolean flag, RealTimeChatOperationState realtimechatoperationstate) {
		// TODO
		return null;
    }
	
	public static final int convertParticipantType(Data.Participant participant)
    {
		// TODO
		return 0;
    }
	
	
	public static final Data.Participant.Type convertParticipantType(int i) {
		// TODO
		Data.Participant.Type t = Data.Participant.Type.valueOf(i);
		return null == t ? Data.Participant.Type.ANDROID : t;
	}
}
