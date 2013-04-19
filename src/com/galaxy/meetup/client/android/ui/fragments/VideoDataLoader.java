/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.util.MediaStoreUtils;
import com.galaxy.meetup.server.client.domain.DataVideo;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class VideoDataLoader extends CursorLoader {

	private final EsAccount mAccount;
    private final Uri mLocalUri;
    private final long mPhotoId;
    private final String mPhotoUrl;
    
    public VideoDataLoader(Context context, EsAccount esaccount, String s, long l, Uri uri)
    {
        super(context);
        mAccount = esaccount;
        mPhotoUrl = s;
        mPhotoId = l;
        mLocalUri = uri;
    }

    public final Cursor loadInBackground()
    {
    	Cursor cursor = null;
        ContentResolver contentresolver = getContext().getContentResolver();
        if(mPhotoId == 0L) { 
        	Uri uri;
        	cursor = new EsMatrixCursor(PhotoQuery.PROJECTION);
        	DataVideo datavideo = null;
        	if(mPhotoUrl == null) {
        		uri = mLocalUri;
                datavideo = null;
                if(uri != null)
                    datavideo = MediaStoreUtils.toVideoData(getContext(), mLocalUri);
        	} else { 
        		datavideo = MediaStoreUtils.toVideoData(getContext(), Uri.parse(mPhotoUrl));
        	}
        	
            byte abyte0[];
            Object aobj[];
            if(datavideo != null)
                abyte0 = JsonUtil.toByteArray(datavideo);
            else
                abyte0 = null;
            aobj = new Object[PhotoQuery.PROJECTION.length];
            aobj[0] = abyte0;
            ((EsMatrixCursor)cursor).addRow(aobj);
            return cursor;
        	
        } else { 
        	return contentresolver.query(EsProvider.appendAccountParameter(ContentUris.withAppendedId(EsProvider.PHOTO_BY_PHOTO_ID_URI, mPhotoId), mAccount), PhotoQuery.PROJECTION, null, null, null);
        }
    }

    public static interface PhotoQuery
    {

        public static final String PROJECTION[] = {
            "video_data"
        };

    }

}
