/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.DataAbuseReport;
import com.galaxy.meetup.server.client.domain.request.ReportAbusePhotoRequest;
import com.galaxy.meetup.server.client.domain.response.ReportAbusePhotoResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class PhotosReportAbuseOperation extends PlusiOperation {
	
	private final String mOwnerId;
    private final long mPhotoId;
    
	public PhotosReportAbuseOperation(Context context, EsAccount esaccount, long l, String s, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "reportabusephoto", intent, operationlistener, ReportAbusePhotoResponse.class);
        mPhotoId = l;
        mOwnerId = s;
    }

    protected final void handleResponse(Response response) throws IOException
    {
    }

    protected final Request populateRequest()
    {
        ReportAbusePhotoRequest reportabusephotorequest = new ReportAbusePhotoRequest();
        reportabusephotorequest.photoId = Long.valueOf(mPhotoId);
        reportabusephotorequest.ownerId = mOwnerId;
        DataAbuseReport dataabusereport = new DataAbuseReport();
        dataabusereport.abuseType = "SPAM";
        reportabusephotorequest.abuseReport = dataabusereport;
        return reportabusephotorequest;
    }

}
