/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.service.AndroidNotification;
import com.galaxy.meetup.client.android.service.CircleMembershipManager;
import com.galaxy.meetup.server.client.domain.DataCircleMemberToAdd;
import com.galaxy.meetup.server.client.domain.DataCircleMembershipModificationParams;
import com.galaxy.meetup.server.client.domain.DataCirclePerson;
import com.galaxy.meetup.server.client.domain.request.ModifyMembershipsRequest;
import com.galaxy.meetup.server.client.domain.response.ModifyMembershipsResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class SetCircleMembershipOperation extends PlusiOperation {

	private final String mCirclesToAdd[];
    private final String mCirclesToRemove[];
    private final boolean mFireAndForget;
    private final String mPersonId;
    private final String mPersonName;
    private final boolean mUpdateNotification;
    
    public SetCircleMembershipOperation(Context context, EsAccount esaccount, String s, String s1, String as[], String as1[], boolean flag, 
            boolean flag1, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "modifymemberships", intent, operationlistener, ModifyMembershipsResponse.class);
        mPersonId = s;
        mPersonName = s1;
        mCirclesToAdd = as;
        mCirclesToRemove = as1;
        mFireAndForget = flag;
        mUpdateNotification = flag1;
    }

    protected final void handleResponse(Response response) throws IOException
    {
        ModifyMembershipsResponse modifymembershipsresponse = (ModifyMembershipsResponse)response;
        if(mFireAndForget)
            CircleMembershipManager.setCircleMembershipResult(mContext, mAccount, mPersonId, mPersonName, true);
        List list = modifymembershipsresponse.circlePerson;
        DataCirclePerson datacircleperson = null;
        if(list != null)
        {
            int i = modifymembershipsresponse.circlePerson.size();
            datacircleperson = null;
            if(i > 0)
                datacircleperson = (DataCirclePerson)modifymembershipsresponse.circlePerson.get(0);
        }
        EsPeopleData.setCircleMembership(mContext, getAccount(), mPersonId, datacircleperson, mCirclesToAdd, mCirclesToRemove);
        if(mUpdateNotification)
            AndroidNotification.update(mContext, mAccount);
        if(mFireAndForget)
            CircleMembershipManager.showToastIfNeeded(mContext, getAccount());
    }

    public final void onHttpReadErrorFromStream(InputStream inputstream, String s, int i, Header aheader[], int j) throws IOException
    {
        CircleMembershipManager.setCircleMembershipResult(mContext, mAccount, mPersonId, mPersonName, false);
        super.onHttpReadErrorFromStream(inputstream, s, i, aheader, j);
    }

    public final Request populateRequest()
    {
        int i = 0;
        ModifyMembershipsRequest modifymembershipsrequest = new ModifyMembershipsRequest();
        modifymembershipsrequest.circleMembershipModificationParams = new DataCircleMembershipModificationParams();
        modifymembershipsrequest.circleMembershipModificationParams.person = new ArrayList();
        DataCircleMemberToAdd datacirclemembertoadd = new DataCircleMemberToAdd();
        datacirclemembertoadd.memberId = EsPeopleData.getCircleMemberId(mPersonId);
        datacirclemembertoadd.displayName = mPersonName;
        modifymembershipsrequest.circleMembershipModificationParams.person.add(datacirclemembertoadd);
        if(mCirclesToAdd != null && mCirclesToAdd.length > 0)
        {
            modifymembershipsrequest.circleToAdd = new ArrayList();
            String as1[] = mCirclesToAdd;
            int k = as1.length;
            for(int l = 0; l < k; l++)
            {
                String s1 = as1[l];
                modifymembershipsrequest.circleToAdd.add(EsPeopleData.buildCircleId(s1));
            }

        }
        if(mCirclesToRemove != null && mCirclesToRemove.length > 0)
        {
            modifymembershipsrequest.circleToRemove = new ArrayList();
            String as[] = mCirclesToRemove;
            for(int j = as.length; i < j; i++)
            {
                String s = as[i];
                modifymembershipsrequest.circleToRemove.add(EsPeopleData.buildCircleId(s));
            }

        }
        return modifymembershipsrequest;
    }
}
