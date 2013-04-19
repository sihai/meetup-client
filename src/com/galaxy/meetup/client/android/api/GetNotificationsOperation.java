/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.DataNotificationsData;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.NotificationsResponseOptions;
import com.galaxy.meetup.server.client.domain.request.GetNotificationsRequest;
import com.galaxy.meetup.server.client.domain.response.GetNotificationsResponse;

/**
 * 
 * @author sihai
 *
 */
public class GetNotificationsOperation extends PlusiOperation {

	public static final List TYPE_GROUP_TO_FETCH = Arrays.asList(new String[] {
        "BASELINE_STREAM", "BASELINE_CIRCLE", "BASELINE_PHOTOS", "BASELINE_EVENTS", "BASELINE_SQUARE", "CIRCLE_SUGGESTIONS_GROUP"
    });
    private List mActors;
    private String mContinuationToken;
    private double mLastNotificationTime;
    private Double mLastReadTime;
    private List mNotifications;
	    
	public GetNotificationsOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "getnotifications", null, operationlistener, GetNotificationsResponse.class);
    }

    public final String getContinuationToken()
    {
        return mContinuationToken;
    }

    public final List getDataActors()
    {
        return mActors;
    }

    public final Double getLastReadTime()
    {
        return mLastReadTime;
    }

    public final List getNotifications()
    {
        return mNotifications;
    }

    public final void getNotifications(double d, String s)
    {
        mLastNotificationTime = d;
        mContinuationToken = s;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        GetNotificationsResponse getnotificationsresponse = (GetNotificationsResponse)genericjson;
        onStartResultProcessing();
        DataNotificationsData datanotificationsdata = getnotificationsresponse.notificationsData;
        if(datanotificationsdata != null)
        {
            if(PrimitiveUtils.safeBoolean(datanotificationsdata.hasMoreNotifications))
                mContinuationToken = datanotificationsdata.continuationToken;
            mLastReadTime = datanotificationsdata.lastReadTime;
            mNotifications = datanotificationsdata.coalescedItem;
            mActors = datanotificationsdata.actor;
        }
    }

    protected final GenericJson populateRequest()
    {
        GetNotificationsRequest getnotificationsrequest = new GetNotificationsRequest();
        getnotificationsrequest.maxResults = Long.valueOf(15L);
        getnotificationsrequest.renderContextLocation = "NOTIFICATION_MOBILE";
        if(mContinuationToken != null)
            getnotificationsrequest.continuationToken = mContinuationToken;
        if(mLastNotificationTime > 0.0D)
            getnotificationsrequest.oldestNotificationTimeUsec = BigDecimal.valueOf(mLastNotificationTime).toBigInteger();
        ArrayList arraylist = new ArrayList();
        arraylist.add("PLAIN");
        getnotificationsrequest.summarySnippets = arraylist;
        getnotificationsrequest.setPushEnabled = Boolean.valueOf(true);
        getnotificationsrequest.typeGroupToFetch = TYPE_GROUP_TO_FETCH;
        NotificationsResponseOptions notificationsresponseoptions = new NotificationsResponseOptions();
        notificationsresponseoptions.includeFullActorDetails = Boolean.valueOf(true);
        notificationsresponseoptions.includeFullEntityDetails = Boolean.valueOf(true);
        notificationsresponseoptions.includeFullRootDetails = Boolean.valueOf(true);
        notificationsresponseoptions.numPhotoEntities = Integer.valueOf(1);
        notificationsresponseoptions.includeActorMap = Boolean.valueOf(true);
        getnotificationsrequest.notificationsResponseOptions = notificationsresponseoptions;
        return getnotificationsrequest;
    }

}
