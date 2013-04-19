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
import com.galaxy.meetup.server.client.domain.request.ReportProfileRequest;
import com.galaxy.meetup.server.client.domain.response.ReportProfileResponse;

/**
 * 
 * @author sihai
 *
 */
public class ReportProfileAbuseOperation extends PlusiOperation {

	private final String mAbuseType;
    private final String mGaiaId;
    
    public ReportProfileAbuseOperation(Context context, EsAccount esaccount, String s, String s1, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, esaccount, "reportprofile", null, null, ReportProfileResponse.class);
        mGaiaId = s;
        mAbuseType = s1;
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
    }

    protected final GenericJson populateRequest()
    {
        ReportProfileRequest reportprofilerequest = new ReportProfileRequest();
        reportprofilerequest.ownerId = mGaiaId;
        reportprofilerequest.abuseReport = new DataAbuseReport();
        reportprofilerequest.abuseReport.abuseType = mAbuseType;
        return reportprofilerequest;
    }
}
