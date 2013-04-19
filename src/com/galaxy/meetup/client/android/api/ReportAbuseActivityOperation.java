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
import com.galaxy.meetup.client.android.content.EsPhotosDataApiary;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.DataAbuseReport;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.domain.request.ReportAbuseActivityRequest;
import com.galaxy.meetup.server.client.domain.response.ReportAbuseActivityResponse;

/**
 * 
 * @author sihai
 *
 */
public class ReportAbuseActivityOperation extends PlusiOperation {

	private final String mAbuseType;
    private final String mActivityId;
    private final String mCommentId;
    private final boolean mDeleteComment;
    private final boolean mIsUndo;
    private final String mSourceStreamId;
    
	public ReportAbuseActivityOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1, String s2)
    {
        this(context, esaccount, intent, operationlistener, s, s1, null, false, s2, false);
    }

    private ReportAbuseActivityOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1, String s2, 
            boolean flag, String s3, boolean flag1)
    {
        super(context, esaccount, "reportabuseactivity", intent, operationlistener, ReportAbuseActivityResponse.class);
        mActivityId = s;
        mSourceStreamId = s1;
        mCommentId = s2;
        mAbuseType = s3;
        mDeleteComment = flag;
        mIsUndo = flag1;
    }

    public ReportAbuseActivityOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, String s1, boolean flag, 
            boolean flag1)
    {
        this(context, esaccount, intent, operationlistener, s, null, s1, flag, "SPAM", flag1);
    }

    protected final void handleResponse(GenericJson genericjson) throws IOException
    {
        if(mCommentId != null) {
        	if(mDeleteComment)
            {
                EsPostsData.deleteComment(mContext, mAccount, mCommentId);
                EsPhotosDataApiary.deletePhotoComment(mContext, mAccount, mCommentId);
            } 
        } else { 
        	EsPostsData.deleteActivity(mContext, mAccount, mActivityId);
        }
    }

    protected final GenericJson populateRequest()
    {
        ReportAbuseActivityRequest reportabuseactivityrequest = new ReportAbuseActivityRequest();
        ArrayList arraylist = new ArrayList(1);
        if(mCommentId == null)
            arraylist.add(mActivityId);
        else
            arraylist.add(mCommentId);
        reportabuseactivityrequest.itemId = arraylist;
        reportabuseactivityrequest.isUndo = Boolean.valueOf(mIsUndo);
        reportabuseactivityrequest.abuseReport = new DataAbuseReport();
        reportabuseactivityrequest.abuseReport.abuseType = mAbuseType;
        reportabuseactivityrequest.abuseReport.destinationStreamId = mSourceStreamId;
        return reportabuseactivityrequest;
    }

}
