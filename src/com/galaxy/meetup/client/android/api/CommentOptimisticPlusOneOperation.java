/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPhotosDataApiary;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.server.client.domain.DataPlusOne;

/**
 * 
 * @author sihai
 *
 */
public class CommentOptimisticPlusOneOperation extends PlusOneOperation {

	private String mActivityId;
    private long mPhotoId;
    
    public CommentOptimisticPlusOneOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, String s, long l, 
            String s1, boolean flag)
    {
        super(context, esaccount, intent, operationlistener, "TACO_COMMENT", s1, flag);
        mActivityId = s;
        mPhotoId = l;
    }

    protected final void onFailure()
    {
        boolean flag = true;
        Context context;
        EsAccount esaccount;
        String s;
        String s1;
        if(mActivityId != null)
        {
            Context context1 = mContext;
            EsAccount esaccount1 = mAccount;
            String s2 = mActivityId;
            String s3 = mItemId;
            boolean flag1;
            if(!mIsPlusOne)
                flag1 = flag;
            else
                flag1 = false;
            EsPostsData.plusOneComment(context1, esaccount1, s2, s3, flag1);
        }
        if(mPhotoId != 0L)
        {
            context = mContext;
            esaccount = mAccount;
            s = Long.toString(mPhotoId);
            s1 = mItemId;
            if(mIsPlusOne)
                flag = false;
            EsPhotosDataApiary.updatePhotoCommentPlusOne(context, esaccount, s, s1, flag);
        }
    }

    protected final void onPopulateRequest()
    {
        if(mActivityId != null)
            EsPostsData.plusOneComment(mContext, mAccount, mActivityId, mItemId, mIsPlusOne);
        if(mPhotoId != 0L)
            EsPhotosDataApiary.updatePhotoCommentPlusOne(mContext, mAccount, Long.toString(mPhotoId), mItemId, mIsPlusOne);
    }

    protected final void onSuccess(DataPlusOne dataplusone)
    {
        if(dataplusone != null && mActivityId != null)
            EsPostsData.updateCommentPlusOneId(mContext, mAccount, mActivityId, mItemId, dataplusone.id);
        if(dataplusone != null && mPhotoId != 0L)
            EsPhotosDataApiary.updatePhotoCommentPlusOne(mContext, mAccount, Long.toString(mPhotoId), mItemId, dataplusone, true);
    }
}
