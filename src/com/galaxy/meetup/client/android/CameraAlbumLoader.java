/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.HashSet;
import java.util.Set;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.util.MediaStoreUtils;

/**
 * 
 * @author sihai
 *
 */
public class CameraAlbumLoader extends EsCursorLoader implements Pageable {

	protected static final Uri sMediaStoreUri[];
    private int mExcludedCount;
    private Set mExcludedUris;
    private boolean mHasMore;
    private int mLoadLimit;
    private boolean mPageable;

    static 
    {
        Uri auri[] = new Uri[4];
        auri[0] = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        auri[1] = MediaStoreUtils.PHONE_STORAGE_IMAGES_URI;
        auri[2] = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        auri[3] = MediaStoreUtils.PHONE_STORAGE_VIDEO_URI;
        sMediaStoreUri = auri;
    }

    public CameraAlbumLoader(Context context, EsAccount esaccount)
    {
        this(context, true, 1);
    }

    public CameraAlbumLoader(Context context, EsAccount esaccount, MediaRef amediaref[])
    {
        this(context, esaccount);
        if(mExcludedUris != null)
        {
            mExcludedUris.clear();
            if(amediaref == null)
                mExcludedUris = null;
        }
        if(amediaref != null && amediaref.length > 0)
        {
            if(mExcludedUris == null)
                mExcludedUris = new HashSet(amediaref.length);
            int i = amediaref.length;
            for(int j = 0; j < i; j++)
            {
                MediaRef mediaref = amediaref[j];
                if(mediaref.hasLocalUri())
                    mExcludedUris.add(mediaref.getLocalUri().toString());
            }

        }
    }

    private CameraAlbumLoader(Context context, boolean flag, int i) {
    	super(context, null);
        int j = 16;
        mLoadLimit = j;
        mPageable = true;
        if(!mPageable)
            j = -1;
        mLoadLimit = j;
    }

    protected Cursor buildMatrixCursor(Context context, Cursor acursor[], Uri auri[]) {
        // TODO
    	return null;
    }

    public final Cursor esLoadInBackground() {
        // TODO
    	return null;
    }

    public final int getCurrentPage()
    {
        int i = -1;
        if(mPageable && mLoadLimit != i)
            i = mLoadLimit / 16;
        return i;
    }

    public final int getExcludedCount()
    {
        return mExcludedCount;
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
        return false;
    }

    public final void loadMore()
    {
        if(hasMore())
        {
            mLoadLimit = 48 + mLoadLimit;
            onContentChanged();
        }
    }

    public final void setLoadingListener(Pageable.LoadingListener loadinglistener)
    {
    }
    
    //=====================================================================================
    //								Inner class
    //=====================================================================================
    private static interface CorrectedMediaStoreColumn {

        public static final String DATE_TAKEN = String.format("case when (datetaken >= %1$d and datetaken < %2$d) then datetaken * 1000 when (datetaken >= %3$d and datetaken < %4$d) then datetaken when (datetaken >= %5$d and datetaken < %6$d) then datetaken / 1000 else 0 end as %7$s", 
        		Long.valueOf(0x9660180L),
        		Long.valueOf(0x70c81200L),
        		Long.valueOf(0x24b675dc00L),
        		Long.valueOf(0x1b88d865000L),
        		Long.valueOf(0x8f68bc636000L),
        		Long.valueOf(0x6b8e8d4a88000L),
        		"corrected_date_taken");
    }

    protected static interface PhotoQuery {

        public static final String[] PROJECTION = new String[]{"_id", CorrectedMediaStoreColumn.DATE_TAKEN, "_display_name"};
    }
}
