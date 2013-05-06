/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.PeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.DataMemberToBlock;
import com.galaxy.meetup.server.client.domain.DataMembersToBlock;
import com.galaxy.meetup.server.client.domain.request.BlockUserRequest;
import com.galaxy.meetup.server.client.domain.response.BlockUserResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class BlockUserOperation extends PlusiOperation {

	private static Factory sFactory = new Factory();
    private final PeopleData mDb;
    private boolean mIsBlocked;
    private String mName;
    private String mPersonId;
    
    BlockUserOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, PeopleData peopledata)
    {
        super(context, esaccount, "blockuser", intent, operationlistener, BlockUserResponse.class);
        mDb = peopledata;
    }

    public static Factory getFactory()
    {
        return sFactory;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        mDb.setBlockedState(mPersonId, mName, mIsBlocked);
    }

    @Override
    protected final Request populateRequest()
    {
        BlockUserRequest blockuserrequest = new BlockUserRequest();
        blockuserrequest.membersToBlock = new DataMembersToBlock();
        blockuserrequest.membersToBlock.block = Boolean.valueOf(mIsBlocked);
        blockuserrequest.membersToBlock.members = new ArrayList();
        DataMemberToBlock datamembertoblock = new DataMemberToBlock();
        datamembertoblock.memberId = EsPeopleData.getCircleMemberId(mPersonId);
        datamembertoblock.name = mName;
        blockuserrequest.membersToBlock.members.add(datamembertoblock);
        return blockuserrequest;
    }
    
    public final void startThreaded(String s, String s1, boolean flag)
    {
        mPersonId = s;
        mName = s1;
        mIsBlocked = flag;
        startThreaded();
    }
    
    public static final class Factory
    {

        public static BlockUserOperation build(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, PeopleData peopledata)
        {
            return new BlockUserOperation(context, esaccount, intent, operationlistener, peopledata);
        }

        Factory()
        {
        }

    }
}
