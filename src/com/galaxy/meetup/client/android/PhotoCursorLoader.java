/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.ui.fragments.HostedEventFragment;

/**
 * 
 * @author sihai
 *
 */
public class PhotoCursorLoader extends EsCursorLoader implements Pageable {

	private final EsAccount mAccount;
    private final String mAlbumId;
    private final String mAuthkey;
    private int mCircleOffset;
    private boolean mDataSourceIsLoading;
    private final String mEventId;
    private String mEventResumeToken;
    Handler mHandler;
    private boolean mHasMore;
    private final int mInitialPageCount;
    private boolean mIsLoadingMore;
    private int mLoadLimit;
    private boolean mNetworkRequestMade;
    private final android.support.v4.content.Loader.ForceLoadContentObserver mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
    private boolean mObserverRegistered;
    private final String mOwnerGaiaId;
    private boolean mPageable;
    Pageable.LoadingListener mPageableLoadingListener;
    private final boolean mPaging;
    private final String mPhotoOfUserGaiaId;
    private final String mPhotoUrl;
    private final String mStreamId;
    
    public PhotoCursorLoader(Context context, EsAccount esaccount, String s, String s1, String s2, String s3, String s4, 
            String s5, boolean flag, int i, String s6)
    {
    	super(context, getNotificationUri(s, s1, s2, s3, s4, s5));
        int j = -1;
        mHandler = new Handler(Looper.getMainLooper());
        mLoadLimit = 16;
        mAccount = esaccount;
        mAuthkey = s6;
        mOwnerGaiaId = s;
        mAlbumId = s1;
        mEventId = s4;
        mPhotoOfUserGaiaId = s2;
        mStreamId = s3;
        mPhotoUrl = s5;
        mPaging = flag;
        mPageable = flag;
        mInitialPageCount = i;
        if(mPageable && i != j)
            j = i * 16;
        mLoadLimit = j;
    }

    private void doNetworkRequest()
    {
        // TODO
    	
    }

    private static Uri getNotificationUri(String s, String s1, String s2, String s3, String s4, String s5) {
        // TODO
    	return null;
    }

    private void invokeLoadingListener(final boolean isActive)
    {
        mHandler.post(new Runnable() {

            public final void run()
            {
                mDataSourceIsLoading = isActive;
                if(mPageableLoadingListener != null)
                    mPageableLoadingListener.onDataSourceLoading(isActive);
            }
        });
    }

    private void updateEventResumeToken()
    {
        Cursor cursor = EsEventData.getEvent(getContext(), mAccount, mEventId, HostedEventFragment.DetailsQuery.PROJECTION);
        if(cursor != null && cursor.moveToFirst())
            mEventResumeToken = cursor.getString(3);
    }

    public Cursor esLoadInBackground() {
    	if(null == getUri()) {
    		Object obj;
            Log.w("PhotoCursorLoader", "load NULL URI; return empty cursor");
            return new EsMatrixCursor(getProjection());
    	}
    	
    	boolean flag = true;
    	Object obj;
    	String s2;
        int i;
        boolean flag1;
        String s;
        StringBuilder stringbuilder;
        if(mEventId != null)
            if(mIsLoadingMore && !TextUtils.isEmpty(mEventResumeToken))
                doNetworkRequest();
            else
                updateEventResumeToken();
        i = mLoadLimit;
        if(mPageable && mLoadLimit != -1)
            flag1 = flag;
        else
            flag1 = false;
        s = getSortOrder();
        if(s != null) {
        	int j;
            boolean flag2;
            boolean flag3;
            if(flag1)
            {
                String s1 = getSortOrder();
                stringbuilder = new StringBuilder();
                if(s1 == null)
                    s1 = "";
                setSortOrder(stringbuilder.append(s1).append(" LIMIT 0, ").append(i).toString());
            }
            obj = super.esLoadInBackground();
            if(obj != null)
                j = ((Cursor) (obj)).getCount();
            else
                j = 0;
            if(j == i)
                flag2 = flag;
            else
                flag2 = false;
            if(mPageable && (flag2 || !TextUtils.isEmpty(mEventResumeToken)))
                flag3 = flag;
            else
                flag3 = false;
            mHasMore = flag3;
            mIsLoadingMore = false;
            if(j == 0)
            {
                ((Cursor) (obj)).close();
                obj = null;
            }
            if(obj == null)
            {
                mCircleOffset = j;
                doNetworkRequest();
                obj = super.esLoadInBackground();
                int k;
                boolean flag4;
                boolean flag5;
                if(obj != null)
                    k = ((Cursor) (obj)).getCount();
                else
                    k = 0;
                if(k == i)
                    flag4 = flag;
                else
                    flag4 = false;
                if(k != mCircleOffset || !TextUtils.isEmpty(mEventResumeToken))
                    flag5 = flag;
                else
                    flag5 = false;
                mPageable = flag5;
                if(!mPageable || !flag4 && TextUtils.isEmpty(mEventResumeToken))
                    flag = false;
                mHasMore = flag;
            }
            if(flag1)
                setSortOrder(s);
            return ((Cursor) (obj));
        } else { 
        	// TODO
        	return null;
        }
    	
    }

    public final int getCurrentPage()
    {
        int i = -1;
        if(mPageable && mLoadLimit != i)
            i = mLoadLimit / 16;
        return i;
    }

    final Uri getLoaderUri()
    {
        Uri uri = getNotificationUri(mOwnerGaiaId, mAlbumId, mPhotoOfUserGaiaId, mStreamId, mEventId, mPhotoUrl);
        Uri uri1;
        if(uri != null)
            uri1 = EsProvider.appendAccountParameter(uri, mAccount);
        else
            uri1 = null;
        return uri1;
    }

    public final boolean hasMore()
    {
        boolean flag;
        if(mPageable && mHasMore)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isDataSourceLoading()
    {
        return mDataSourceIsLoading;
    }

    public final void loadMore()
    {
        if(mPageable && mHasMore)
        {
            mLoadLimit = 48 + mLoadLimit;
            mIsLoadingMore = true;
            onContentChanged();
        }
    }

    protected final void onAbandon()
    {
        if(mObserverRegistered)
        {
            getContext().getContentResolver().unregisterContentObserver(mObserver);
            mObserverRegistered = false;
        }
        super.onAbandon();
    }

    protected final void onStartLoading()
    {
        if(!mObserverRegistered)
        {
            getContext().getContentResolver().registerContentObserver(EsProvider.PHOTO_URI, false, mObserver);
            mObserverRegistered = true;
        }
        super.onStartLoading();
    }

    public final void setLoadingListener(Pageable.LoadingListener loadinglistener)
    {
        mPageableLoadingListener = loadinglistener;
    }

}
