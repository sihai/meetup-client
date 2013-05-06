/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.content.EsDatabaseHelper;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.MemberList;
import com.galaxy.meetup.server.client.domain.MemberListQuery;
import com.galaxy.meetup.server.client.domain.SquareMember;
import com.galaxy.meetup.server.client.domain.request.ReadSquareMembersOzRequest;
import com.galaxy.meetup.server.client.domain.response.ReadSquareMembersOzResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class ReadSquareMembersOperation extends PlusiOperation {

	private final String mContinuationtoken;
    private final String mSquareId;
    private AudienceData mSquareMembers;
    
	public ReadSquareMembersOperation(Context context, EsAccount esaccount, String s, String s1, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "readsquaremembers", intent, operationlistener, ReadSquareMembersOzResponse.class);
        mSquareId = s;
        mContinuationtoken = s1;
    }

    public final AudienceData getSquareMembers()
    {
        return mSquareMembers;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        int i = 0;
        ReadSquareMembersOzResponse readsquaremembersozresponse = (ReadSquareMembersOzResponse)response;
        int j;
        List list;
        ArrayList arraylist;
        SQLiteDatabase sqlitedatabase;
        int k;
        SquareMember squaremember;
        if(readsquaremembersozresponse.memberList != null && readsquaremembersozresponse.memberList.size() > 0)
        {
            MemberList memberlist = (MemberList)readsquaremembersozresponse.memberList.get(0);
            List list1 = memberlist.member;
            int l = PrimitiveUtils.safeInt(memberlist.totalMembers);
            list = list1;
            j = l;
        } else
        {
            j = 0;
            list = null;
        }
        arraylist = new ArrayList();
        if(null != list) {
        	sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(mContext, mAccount).getWritableDatabase();
        	try {
	            sqlitedatabase.beginTransaction();
	            for(k = list.size(); i < k; i++)
	            {
	                squaremember = (SquareMember)list.get(i);
	                arraylist.add(new PersonData(squaremember.obfuscatedGaiaId, squaremember.displayName, null, EsAvatarData.compressAvatarUrl(squaremember.photoUrl)));
	                EsPeopleData.replaceUserInTransaction(sqlitedatabase, squaremember.obfuscatedGaiaId, squaremember.displayName, squaremember.photoUrl);
	            }
	            sqlitedatabase.setTransactionSuccessful();
        	} finally {
        		sqlitedatabase.endTransaction();
        	}
        }
        mSquareMembers = new AudienceData(arraylist, null, j);
        
    }

    protected final Request populateRequest()
    {
        ReadSquareMembersOzRequest readsquaremembersozrequest = new ReadSquareMembersOzRequest();
        MemberListQuery memberlistquery = new MemberListQuery();
        memberlistquery.membershipStatus = "MEMBER";
        memberlistquery.pageLimit = Integer.valueOf(100);
        memberlistquery.continuationToken = mContinuationtoken;
        readsquaremembersozrequest.obfuscatedSquareId = mSquareId;
        readsquaremembersozrequest.memberListQuery = new ArrayList();
        readsquaremembersozrequest.memberListQuery.add(memberlistquery);
        return readsquaremembersozrequest;
    }

}
