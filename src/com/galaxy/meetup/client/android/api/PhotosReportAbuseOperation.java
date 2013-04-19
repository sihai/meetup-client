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
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.ReportAbusePhotoRequest;
import com.galaxy.meetup.server.client.domain.response.ReportAbusePhotoResponse;

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

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
    }

    protected final GenericJson populateRequest()
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
