/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.realtimechat;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsConversationsData;
import com.galaxy.meetup.client.util.StringUtils;

/**
 * 
 * @author sihai
 *
 */
public class CreateConversationOperation extends RealTimeChatOperation {

	AudienceData mAudience;
    Client.ClientConversation mConversation;
    Long mConversationRowId;
    long mMessageRowId;
    String mMessageText;
    int mResultCode;


    public CreateConversationOperation(Context context, EsAccount esaccount, AudienceData audiencedata, String s)
    {
        super(context, esaccount);
        mAudience = audiencedata;
        mMessageText = s;
        mResultCode = 1;
    }

    public final void execute() {
        boolean flag;
        List list;
        flag = true;
        ConnectivityManager connectivitymanager = (ConnectivityManager)mContext.getSystemService("connectivity");
        if(connectivitymanager != null && (connectivitymanager.getActiveNetworkInfo() == null || !connectivitymanager.getActiveNetworkInfo().isConnectedOrConnecting()))
            flag = false;
        list = ParticipantUtils.getParticipantListFromAudience(mContext, mAccount, mAudience);
        if(list.size() <= 100) {
        	Client.ClientConversation.Builder builder = Client.ClientConversation.newBuilder();
            for(Iterator iterator = list.iterator(); iterator.hasNext(); builder.addParticipant((Data.Participant)iterator.next()));
            Bundle bundle;
            if(list.size() > 1)
                builder.setType(Data.ConversationType.GROUP);
            else
                builder.setType(Data.ConversationType.ONE_TO_ONE);
            builder.setId((new StringBuilder("c:")).append(StringUtils.randomString(32)).toString());
            mConversation = builder.build();
            bundle = EsConversationsData.createConversationLocally(mContext, mAccount, mConversation, mMessageText, flag, mOperationState);
            mConversationRowId = Long.valueOf(bundle.getLong("conversation_row_id"));
            mMessageRowId = bundle.getLong("message_row_id");
            if(flag)
            {
                CheckIfFailedRunnable checkiffailedrunnable = new CheckIfFailedRunnable();
                (new Handler(Looper.getMainLooper())).postDelayed(checkiffailedrunnable, 10000L);
            } 
        } else { 
        	mResultCode = 4;
        }
        
        return;
    }

    public final int getResultCode()
    {
        return mResultCode;
    }

    public final Object getResultValue()
    {
        return new ConversationResult(mConversationRowId, mConversation);
    }
    
    //================================================================================
    //
    //================================================================================
    
    private final class CheckIfFailedRunnable implements Runnable {

	    public final void run()
	    {
	        RealTimeChatService.checkMessageSent(mContext, mAccount, mMessageRowId, 0);
	    }
    }
    
    public final class ConversationResult {

        public Client.ClientConversation mConversation;
        public Long mConversationRowId;

        ConversationResult(Long long1, Client.ClientConversation clientconversation)
        {
            super();
            mConversationRowId = long1;
            mConversation = clientconversation;
        }
    }
}
